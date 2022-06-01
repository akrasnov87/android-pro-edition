package com.mobwal.pro.ui.mail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobwal.pro.Names;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.databinding.FragmentMailBinding;
import com.mobwal.android.library.util.LogUtil;

import java.io.File;
import java.util.List;

/**
 * Принудительная отправка сообщений
 */
public class MailFragment extends Fragment
    implements View.OnClickListener {

    private FragmentMailBinding mBinding;

    public MailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WalkerApplication.Log("Отправка сообщения.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMailBinding.inflate(inflater, container, false);
        mBinding.sendAction.setOnClickListener(this);

        return mBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Names.PREFERENCE_NAME, Context.MODE_PRIVATE);

        boolean useSystemJournal = mBinding.mailJournal.isChecked();

        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_EMAIL, new String[] {
                sharedPreferences.getString("support_email", Names.SUPPORT_EMAIL)
        });
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_mail_subject));
        i.putExtra(Intent.EXTRA_TEXT, mBinding.mailText.getText());

        Intent chooser = Intent.createChooser(i, getString(R.string.send_mail_title));

        if(useSystemJournal) {
            File archive = LogUtil.getArchiveLog(requireContext());
            if(archive != null) {
                i.setType("application/zip");
                Uri fileUri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.mobwal.pro.provider", //(use your app signature + ".provider" )
                        archive);
                i.putExtra(Intent.EXTRA_STREAM, fileUri);

                List<ResolveInfo> resInfoList = requireActivity().getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    requireActivity().grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            } else {
                WalkerApplication.Debug("Не удалось создать системный журнал.");
            }
        }

        startActivity(chooser);

        requireActivity().finish();

        WalkerApplication.Log("Сообщение отправлено.");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mBinding = null;
    }
}