package ru.mobnius.core.ui.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import ru.mobnius.core.R;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.ui.BaseDialogFragment;

public class HelpDialogFragment extends BaseDialogFragment
        implements View.OnClickListener {

    private TextView tvDescription;
    private TextView tvSync;
    private TextView tvTitle;

    private String mTitle;
    private String mText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_help, container, false);

        tvSync = v.findViewById(R.id.help_no_sync);
        tvDescription = v.findViewById(R.id.help_txt);
        tvTitle = v.findViewById(R.id.help_title_text);

        setCancelable(false);

        ImageButton ibClose = v.findViewById(R.id.help_close);
        ibClose.setOnClickListener(this);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(getDialog())).getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        tvSync.setVisibility(mTitle == null ? View.VISIBLE : View.GONE);
        if(mTitle != null) {
            tvTitle.setText(mTitle);
            tvDescription.setText((Html.fromHtml(mText)));
        }
    }

    public void bind(String title, String text, String date) {
        mTitle = title;
        mText = text;
    }

    @Override
    public int getExceptionCode() {
        return IExceptionCode.HELP;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}

