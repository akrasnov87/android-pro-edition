package ru.mobnius.core.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import ru.mobnius.core.R;

public class TextFieldView extends LinearLayout {

    private TextView tvLabel;
    private TextView tvValue;

    public TextFieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.text_field, this, true);

        tvLabel = findViewById(R.id.text_field_label);
        tvValue = findViewById(R.id.text_field_value);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TextFieldView, 0, 0);

        String labelText = a.getString(R.styleable.TextFieldView_labelText);
        setLabelText(labelText);

        if(a.hasValue(R.styleable.TextFieldView_labelTextColor)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setLabelTextColor(a.getColor(R.styleable.TextFieldView_labelTextColor, getResources().getColor(android.R.color.darker_gray, null)));
            } else {
                setLabelTextColor(a.getColor(R.styleable.TextFieldView_labelTextColor, Color.GRAY));
            }
        }

        if(a.hasValue(R.styleable.TextFieldView_labelTextSize)) {
            float textSize = a.getDimension(R.styleable.TextFieldView_labelTextSize, getResources().getDimension(R.dimen.small_font_size)) / getResources().getDisplayMetrics().density;
            setLabelTextSize(textSize);
        }

        String valueText = a.getString(R.styleable.TextFieldView_valueText);
        setValueText(valueText);

        if(a.hasValue(R.styleable.TextFieldView_valueTextColor)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setValueTextColor(a.getColor(R.styleable.TextFieldView_valueTextColor, getResources().getColor(android.R.color.black, null)));
            } else {
                setValueTextColor(a.getColor(R.styleable.TextFieldView_valueTextColor, Color.BLACK));
            }
        }

        if(a.hasValue(R.styleable.TextFieldView_valueTextSize)) {
            float textSize = a.getDimension(R.styleable.TextFieldView_valueTextSize, getResources().getDimension(R.dimen.medium_font_size)) / getResources().getDisplayMetrics().density;
            setValueTextSize(textSize);
        }
        a.recycle();
    }

    public TextFieldView(Context context) {
        this(context, null);
    }

    /**
     * ?????????????????? ??????????????????
     * @param text ?????????? ??????????????????
     */
    public void setLabelText(String text) {
        tvLabel.setText(text);
    }

    /**
     * ?????????????????? ?????????????? ????????????
     * @param textSize ???????????? ??????????
     */
    public void setLabelTextSize(float textSize) {
        tvLabel.setTextSize(textSize);
    }

    /**
     * ?????????????????? ?????????? ??????????????????
     * @param textColor ???????? ??????????????????
     */
    public void setLabelTextColor(int textColor) {
        tvLabel.setTextColor(textColor);
    }

    /**
     * ?????????????????? ????????????????
     * @param text ?????????? ????????????????
     */
    public void setValueText(String text) {
        tvValue.setText(text);
    }

    public void setValueHtml(Spanned value) {
        tvValue.setText(value);
    }

    /**
     * ?????????????????? ?????????????? ????????????????
     * @param textSize ???????????? ????????????????
     */
    public void setValueTextSize(float textSize) {
        tvValue.setTextSize(textSize);
    }

    /**
     * ?????????????????? ?????????? ????????????????
     * @param textColor ???????? ????????????????
     */
    public void setValueTextColor(int textColor) {
        tvValue.setTextColor(textColor);
    }

    public void setValueVisible(int visible) {
        tvValue.setVisibility(visible);
    }
}
