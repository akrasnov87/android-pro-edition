package com.mobwal.pro.ui.mail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobwal.android.library.PrefManager;
import com.mobwal.android.library.authorization.BasicAuthorizationSingleton;
import com.mobwal.android.library.exception.ExceptionInterceptFragment;
import com.mobwal.android.library.exception.FaceExceptionSingleton;
import com.mobwal.pro.Codes;
import com.mobwal.pro.MailActivity;
import com.mobwal.pro.MainActivity;
import com.mobwal.pro.Names;
import com.mobwal.pro.R;
import com.mobwal.pro.SecurityActivity;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.databinding.FragmentMailBinding;
import com.mobwal.android.library.util.LogUtil;

import java.io.File;
import java.util.List;

/**
 * Принудительная отправка сообщений
 */
public class MailFragment extends ExceptionInterceptFragment
    implements View.OnClickListener {

    private FragmentMailBinding mBinding;

    private String mode;

    public MailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMailBinding.inflate(inflater, container, false);
        mBinding.sendAction.setOnClickListener(this);

        Intent intent = requireActivity().getIntent();
        ActionBar actionBar = ((AppCompatActivity)requireActivity()).getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            mode = intent.getStringExtra("mode");

            if(mode.equals(MailActivity.EXCEPTION)) {
                actionBar.setSubtitle(R.string.send_error);
            }
        }

        LogUtil.writeText(requireContext(), "Отправка сообщения в режиме: " + mode);

        return mBinding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            if(BasicAuthorizationSingleton.getInstance().isAuthorized()) {
                LogUtil.debug(requireContext(), "Переход на главый экран");
                startActivity(MainActivity.getIntent(requireContext()));
            } else {
                LogUtil.debug(requireContext(), "Переход на экран авторизации");
                startActivity(SecurityActivity.getIntent(requireContext()));
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        PrefManager prefManager = new PrefManager(requireContext());

        boolean useSystemJournal = mBinding.mailJournal.isChecked();

        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_EMAIL, new String[] {
                prefManager.get("support_email", Names.SUPPORT_EMAIL)
        });
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_mail_subject));
        i.putExtra(Intent.EXTRA_TEXT, mBinding.mailText.getText());

        Intent chooser = Intent.createChooser(i, getString(R.string.send_mail_title));

        File archive = LogUtil.getArchiveLog(requireContext(), useSystemJournal || mode.equals(MailActivity.EXCEPTION));

        if(useSystemJournal) {
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
                LogUtil.debug(requireContext(),"Не удалось создать системный журнал.");
            }
        }

        startActivity(chooser);

        requireActivity().finish();

        FaceExceptionSingleton.getInstance(requireContext()).clearAll();
        LogUtil.clear(requireContext(), true);

        LogUtil.debug(requireContext(),"Сообщение отправлено в режиме: " + mode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mBinding = null;
    }

    @Override
    public int getExceptionCode() {
        return Codes.MAIL_FRAGMENT;
    }
}