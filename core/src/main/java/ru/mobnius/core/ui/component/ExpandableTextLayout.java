package ru.mobnius.core.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ru.mobnius.core.R;
import ru.mobnius.core.utils.StringUtil;

public class ExpandableTextLayout extends LinearLayout
        implements View.OnClickListener {

    private TextView tvTitle;
    private TextView tvContent;

    public ExpandableTextLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.ExpandableTextLayout, 0, 0);
        String titleText = a.getString(R.styleable.ExpandableTextLayout_titleText);
        String contentText = a.getString(R.styleable.ExpandableTextLayout_contentText);
        boolean expanded = a.getBoolean(R.styleable.ExpandableTextLayout_expanded, true);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandable_text_layout, this, true);

        tvTitle = findViewById(R.id.expandable_layout_title);
        setTitle(titleText);

        tvContent = findViewById(R.id.expandable_layout_content);
        setContent(contentText);
        setExpanded(expanded);

        tvTitle.setOnClickListener(this);
    }

    public void setTitle(String title) {
        if (StringUtil.isEmptyOrNull(title)) {
            return;
        }
        tvTitle.setText(title);
    }

    public void setContent(String content) {
        if (StringUtil.isEmptyOrNull(content)) {
            return;
        }
        tvContent.setText(Html.fromHtml(content));
    }

    public void setContent(List<OnExpandableItem> items) {
        String HTML_BR = "<br />";
        StringBuilder stringBuilder = new StringBuilder();

        for (OnExpandableItem item : items) {
            stringBuilder.append(item.toHtml()).append(HTML_BR);
        }
        if (stringBuilder.length() > 0) {
            int lenght = stringBuilder.length();
            setContent(stringBuilder.substring(0, lenght - HTML_BR.length()));
        }
    }
    public void setAltContent(String content) {
        if (StringUtil.isEmptyOrNull(content)) {
            return;
        }
        tvContent.setTextColor(getResources().getColor(R.color.colorPrimaryText));
        tvContent.setTextSize(14);
        tvContent.setText(Html.fromHtml(content));
    }

    public void setAltContent(List<OnExpandableItem> items) {
        String HTML_BR = "<br />";
        StringBuilder stringBuilder = new StringBuilder();

        for (OnExpandableItem item : items) {
            stringBuilder.append(item.toAltHtml()).append(HTML_BR);
        }
        if (stringBuilder.length() > 0) {
            tvContent.setTextColor(getResources().getColor(R.color.colorPrimaryText));
            tvContent.setTextSize(14);
            int lenght = stringBuilder.length();
            setContent(stringBuilder.substring(0, lenght - HTML_BR.length()));
        }
    }

    public void setExpanded(boolean expanded) {
        updateDrawable(expanded);
        tvContent.setVisibility(expanded ? VISIBLE : GONE);
    }

    @Override
    public void onClick(View v) {
        tvContent.setVisibility(tvContent.getVisibility() == VISIBLE ? GONE : VISIBLE);
        updateDrawable(tvContent.getVisibility() == VISIBLE);
    }

    private void updateDrawable(boolean expanded) {
        tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                expanded ? R.drawable.ic_baseline_keyboard_arrow_up_24 : R.drawable.ic_baseline_keyboard_arrow_down_24,
                0);
    }

    public interface OnExpandableItem {
        String toHtml();
        String toAltHtml();
    }
}
