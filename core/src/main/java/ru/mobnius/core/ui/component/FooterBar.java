package ru.mobnius.core.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import ru.mobnius.core.R;

/**
 * Панель для вывода нижней панели
 */
public class FooterBar extends LinearLayout {

    private Button btnSave;
    private ImageView ivSecondButton;

    public FooterBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.FooterBar, 0, 0);
        String btnSavetitle = a.getString(R.styleable.FooterBar_saveButtonText);
        boolean cameraVisible = a.getBoolean(R.styleable.FooterBar_secondButtonVisible, false);
        int secondIconButton = a.getResourceId(R.styleable.FooterBar_secondIconButton, R.drawable.ic_baseline_wallpaper_48);

        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.form_footer_bar, this, true);

        btnSave = findViewById(R.id.footer_bar_save);
        ivSecondButton = findViewById(R.id.footer_bar_second_button);
        setSecondIconButton(secondIconButton);

        btnSave.setText(btnSavetitle);
        ivSecondButton.setVisibility(cameraVisible ? ImageView.VISIBLE : ImageView.GONE);

        a.recycle();
    }

    public FooterBar(Context context) {
        this(context, null);
    }

    /**
     * Установка текста кнопки
     * @param value значение
     */
    public void setSaveTitle(String value) {
        btnSave.setText(value);
    }

    /**
     * Уставновка видимости кнопки с переходом на галерею
     * @param visibility статус видимости
     */
    public void setSecondButtonVisible(int visibility) {
        ivSecondButton.setVisibility(visibility);
    }

    public void setSecondIconButton(int drawable) {
        ivSecondButton.setBackgroundResource(drawable);
    }

    /**
     * Установить обработчики нажатия
     * @param listener обработчик
     */
    public void setOnClickListener(OnClickListener listener) {
        btnSave.setOnClickListener(listener);
        ivSecondButton.setOnClickListener(listener);
    }

    /**
     * блокировка кнопки сохранения
     * @param value значение
     */
    public void setSaveEnabled(boolean value) {
        btnSave.setEnabled(value);
    }
}
