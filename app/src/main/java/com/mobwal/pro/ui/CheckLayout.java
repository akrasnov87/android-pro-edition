package com.mobwal.pro.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.mobwal.pro.R;
import com.mobwal.pro.models.db.Point;
import com.mobwal.pro.utilits.ActivityUtil;

/**
 * Модуль проверки результата
 */
public class CheckLayout extends LinearLayout {

    /**
     * заголовок
     */
    private final TextView mLabel;

    /**
     * Комментарий
     */
    private final EditText mComment;

    public CheckLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.check_layout, this, true);

        mComment = findViewById(R.id.check_comment);
        mLabel = findViewById(R.id.check_label);
    }

    private void onCheckedChange(boolean isChecked) {
        if(isChecked) {
            mLabel.setText(R.string.check);
            mLabel.setTextColor(ActivityUtil.getColor(getContext(), android.R.attr.textColorSecondary));
        } else {
            mLabel.setText(R.string.uncheck);
            mLabel.setTextColor(ActivityUtil.getColor(getContext(), R.attr.colorError));
        }
    }

    /**
     * Привязка данных
     * @param point точка маршрута
     */
    public void bind(@Nullable Point point) {
        if(point != null) {
            onCheckedChange(point.b_check);

            mComment.setText(point.c_comment);
        }
    }

    private String getComment() {
        return mComment.getText().toString();
    }
}