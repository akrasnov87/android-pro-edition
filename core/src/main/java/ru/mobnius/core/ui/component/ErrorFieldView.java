package ru.mobnius.core.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Objects;

import ru.mobnius.core.R;

public class ErrorFieldView extends LinearLayout
        implements View.OnClickListener {

    private final TextView tvMessage;

    public ErrorFieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setVisibility(GONE);

        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Objects.requireNonNull(inflater).inflate(R.layout.error_field, this, true);

        tvMessage = findViewById(R.id.error_field_message);

        ImageButton iBtnClose = findViewById(R.id.error_field_close);
        iBtnClose.setOnClickListener(this);


        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ErrorFieldView, 0, 0);

        if(a.hasValue(R.styleable.ErrorFieldView_errorMessageTextColor)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextColor(a.getColor(R.styleable.ErrorFieldView_errorMessageTextColor, getResources().getColor(android.R.color.holo_red_light, null)));
            } else {
                setTextColor(a.getColor(R.styleable.ErrorFieldView_errorMessageTextColor, Color.RED));
            }
        }

        if(a.hasValue(R.styleable.ErrorFieldView_errorMessageTextSize)) {
            float textSize = a.getDimension(R.styleable.ErrorFieldView_errorMessageTextSize, getResources().getDimension(R.dimen.medium_font_size)) / getResources().getDisplayMetrics().density;
            tvMessage.setTextSize(textSize);
        }
        a.recycle();
    }

    public ErrorFieldView(Context context) {
        this(context, null);
    }

    /**
     * Установка сообщения
     * @param message текст сообщения
     */
    public void setMessage(String message) {
        tvMessage.setText(message);
        setVisibility(VISIBLE);
    }

    /**
     * Установка размера текста
     * @param textSize размер текст
     */
    public void setTextSize(float textSize) {
        tvMessage.setTextSize(textSize);
    }

    /**
     * Установка цвета сообщения
     * @param textColor цвет сообщения
     */
    public void setTextColor(int textColor) {
        tvMessage.setTextColor(textColor);
    }

    @Override
    public void onClick(View v) {
        setVisibility(GONE);
    }
}