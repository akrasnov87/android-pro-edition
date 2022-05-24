package ru.mobnius.core.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;

import java.io.File;
import java.util.Objects;

import ru.mobnius.core.Names;
import ru.mobnius.core.R;
import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.ui.BaseDialogFragment;
import ru.mobnius.core.ui.DigestsAsyncTask;
import ru.mobnius.core.utils.UpdateUtil;

public class AboutUpdateDialogFragment extends BaseDialogFragment
        implements View.OnClickListener, DigestsAsyncTask.OnDigestsLoadedListener {

    private TextView tvDescription;
    private DigestsAsyncTask mDigestsAsyncTask;
    private ContentLoadingProgressBar clpbDownloadProgress;
    private TextView tvNeedSync;

    private String mVersion;
    private int mUnSyncCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_about_update, container, false);

        tvDescription = v.findViewById(R.id.about_update_txt);
        clpbDownloadProgress = v.findViewById(R.id.about_update_progress_bar);
        tvNeedSync = v.findViewById(R.id.about_update_no_sync);

        final Button buttonUpdate = v.findViewById(R.id.about_update_done);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonUpdate.setEnabled(false);

                String url = GlobalSettings.getConnectUrl() + Names.UPDATE_URL;
                Handler handler = new Handler(Looper.getMainLooper());
                String packageName = requireActivity().getApplicationContext().getPackageName();
                clpbDownloadProgress.setVisibility(View.VISIBLE);
                String[] data = packageName.split("\\.");
                UpdateUtil.getApk(url, "mobnius-" + data[data.length - 1] + ".apk", packageName, getContext(), handler, new UpdateUtil.OnDownloadProgressListener() {
                    @Override
                    public void onProgress(int progress) {
                        if (progress >= 0) {
                            clpbDownloadProgress.setProgress(progress);
                        } else {
                            clpbDownloadProgress.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onErrorProgress(String message) {
                        buttonUpdate.setEnabled(true);
                    }

                    @Override
                    public void onFinishDownload(File video) {
                        buttonUpdate.setEnabled(true);
                    }
                });
            }
        });

        setCancelable(false);

        ImageButton ibClose = v.findViewById(R.id.about_update_close);
        ibClose.setOnClickListener(this);

        if(mUnSyncCount > 0) {
            tvNeedSync.setVisibility(View.VISIBLE);
            buttonUpdate.setEnabled(false);
        } else {
            tvNeedSync.setVisibility(View.GONE);
            buttonUpdate.setEnabled(true);
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(getDialog())).getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tvDescription.setText("Идет загрузка описания...");
        mDigestsAsyncTask = new DigestsAsyncTask(this);
        mDigestsAsyncTask.execute(mVersion);
    }

    public void bind(String version, int unSyncCount) {
        mVersion = version;
        mUnSyncCount = unSyncCount;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mDigestsAsyncTask != null) {
            mDigestsAsyncTask.cancel(true);
        }
    }

    @Override
    public int getExceptionCode() {
        return IExceptionCode.UPDATE_ABOUT;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onDigestsLoaded(String html) {
        tvDescription.setText(Html.fromHtml(html));
    }

}
