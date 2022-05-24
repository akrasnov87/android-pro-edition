package ru.mobnius.core.ui.component.signature;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import ru.mobnius.core.R;
import ru.mobnius.core.utils.BitmapUtil;
import ru.mobnius.core.utils.StringUtil;

public class SignatureField extends LinearLayout {

    private TextView tvSignatureLabel;
    private ImageView ivSignature;
    private ImageButton btnSignatureClear;
    private OnSignatureListener mListener;
    private String mBase64 = "";
    private int mDefaultSrc;

    public SignatureField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SignatureField, 0, 0);

        String src = a.getString(R.styleable.SignatureField_signatureSrc);
        String label = a.getString(R.styleable.SignatureField_signatureLabel);
        int removeIcon = a.getResourceId(R.styleable.SignatureField_signatureRemoveIcon, R.drawable.ic_close);
        mDefaultSrc = a.getResourceId(R.styleable.SignatureField_signatureDefaultSrc, R.drawable.ic_baseline_create_24);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.signature_field, this, true);

        tvSignatureLabel = findViewById(R.id.signature_label);
        ivSignature = findViewById(R.id.signature_image);
        ivSignature.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.onClickSignature((StringUtil.isEmptyOrNull(mBase64) || mBase64.isEmpty()) ? OnSignatureListener.ADD : OnSignatureListener.UPDATE, mBase64);
                }
            }
        });

        btnSignatureClear = findViewById(R.id.btnSignatureClear);
        btnSignatureClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.onClickSignature(OnSignatureListener.REMOVE, mBase64);
                }
            }
        });

        if(label != null) {
            setLabel(label);
        }

        if(mDefaultSrc != 0) {
            setDefaultSrc(mDefaultSrc);
        }

        if(removeIcon != 0) {
            setRemoveIcon(removeIcon);
        }

        if(src != null) {
            setSrc(src);
        }
    }

    /**
     * Добавление обработчика
     * @param listener обработчик
     */
    public void setOnSignatureListener(OnSignatureListener listener) {
        mListener = listener;
    }

    /**
     * Устнаовка подписи
     * @param base64 подпись в фомате base64
     */
    public void setSrc(String base64) {
        mBase64 = base64;

        if(!StringUtil.isEmptyOrNull(mBase64)) {
            ivSignature.setImageBitmap(BitmapUtil.toBitmap(mBase64));
            btnSignatureClear.setVisibility(VISIBLE);
        } else {
            setDefaultSrc(mDefaultSrc);
            btnSignatureClear.setVisibility(GONE);
        }
    }

    /**
     * Установка наименования поля
     * @param label наименование
     */
    public void setLabel(String label) {
        tvSignatureLabel.setText(label);
    }

    /**
     * Иконка для очистки подписи
     * @param resId иден. подписи
     */
    public void setRemoveIcon(int resId) {
        btnSignatureClear.setImageResource(resId);
    }

    /**
     * Иконка для подписи по умолчанию
     * @param resId иден. ресурса
     */
    public void setDefaultSrc(int resId) {
        ivSignature.setImageResource(resId);
    }
}
