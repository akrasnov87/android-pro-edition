package com.mobwal.pro.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.mobwal.android.library.data.sync.util.transfer.TransferListeners;
import com.mobwal.android.library.data.sync.util.transfer.TransferProgress;
import com.mobwal.pro.R;

import java.util.Objects;

/**
 * Представление для вывода хода синхронизации
 */
public class SynchronizationProgressView extends LinearLayout {
    
    private final ProgressBar pbProgressBar;
    private final TextView tvLabelView;
    private final TextView tvStatusView;

    /**
     * Имя группы
     */
    private final String mGroupText;

    /**
     * Пользовательское имя группы
     */
    private final String mNameText;

    public SynchronizationProgressView(Context context) {
        this(context, null);
    }

    public SynchronizationProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SynchronizationProgressView, 0, 0);
        mGroupText = a.getString(R.styleable.SynchronizationProgressView_groupText);
        mNameText = a.getString(R.styleable.SynchronizationProgressView_nameText);

        a.recycle();

        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Objects.requireNonNull(inflater).inflate(R.layout.synchronization_progress_layout, this, true);

        tvLabelView = findViewById(R.id.synchronizationLabel);
        tvStatusView = findViewById(R.id.synchronizationStatus);
        pbProgressBar = findViewById(R.id.synchronizationProgress);

        setLabelText(mNameText);

        updateProgressBarColor(TransferListeners.START);
    }

    /**
     * получение имени группы
     * @return группа
     */
    public String getGroupName() {
        return mGroupText;
    }

    public void setLabelText(String text) {
        tvLabelView.setText(text);
    }

    public void setStatusText(String text) {
        tvStatusView.setText(text);
    }

    /**
     * обновление процента выполнения
     *
     * @param percent       процент
     * @param secondPercent процент
     */
    public void updatePercent(double percent, double secondPercent) {
        pbProgressBar.setSecondaryProgress((int) percent);
        pbProgressBar.setProgress((int) secondPercent);
    }

    /**
     * обновление статуса
     *
     * @param progress прогресс
     */
    public void updateStatus(@NonNull TransferProgress progress) {
        setStatusText(progress.toString(getContext()));
        String text = mNameText + " (" + progress.getTransferData().toString(getContext()) + ")";
        setLabelText(text);
    }

    /**
     * Обновление логов
     *
     * @param logs лог
     */
    public void updateLogs(@NonNull String logs) {
        String text = mNameText + " " + logs;
        setLabelText(text);
    }

    /**
     * обновление цвета полосы
     *
     * @param type тип статуса TransferListener
     */
    public void updateProgressBarColor(int type) {
        ColorStateList colorStateList;
        switch (type) {
            case TransferListeners.STOP:
            case TransferListeners.ERROR:
                colorStateList = AppCompatResources.getColorStateList(getContext(), R.color.colorSecondary);
                break;

            default:
                colorStateList = AppCompatResources.getColorStateList(getContext(), R.color.colorSuccess);
                break;
        }
        pbProgressBar.setSecondaryProgressTintList(colorStateList);
        pbProgressBar.setProgressTintList(colorStateList);
    }
}
