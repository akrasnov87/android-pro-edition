package ru.mobnius.core.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

import ru.mobnius.core.R;
import ru.mobnius.core.adapter.BaseSpinnerAdapter;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.data.gallery.BasePhotoManager;
import ru.mobnius.core.data.gallery.PhotoDataManager;
import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.ui.BaseDialogFragment;
import ru.mobnius.core.ui.image.ImageItem;

/**
 * Форма редактирования изображения
 */
public class PhotoChangeDialogFragment extends BaseDialogFragment
        implements View.OnClickListener {

    private AppCompatSpinner sTypeImage;
    private EditText etNotice;
    private ImageView ivImage;

    private ImageItem mImage;
    private BaseSpinnerAdapter mTypeAdapter;
    private BasePhotoManager mPhotoManager;
    private PhotoDataManager mPhotoDataManager;
    private OnPhotoItemListeners mListeners;

    /**
     *
     * @param image текущее обрабатываемое изображение
     * @param manager класс для работы с изображениями
     * @param photoDataListeners объект для работы с данными
     * @param typeAdapter адаптер для получения типов изображений
     */
    public PhotoChangeDialogFragment(ImageItem image, BasePhotoManager manager, PhotoDataManager photoDataListeners, BaseSpinnerAdapter typeAdapter) {
        mImage = image;
        mPhotoManager = manager;
        mPhotoDataManager = photoDataListeners;
        mTypeAdapter = typeAdapter;
    }

    public void setPhotoItemListeners(OnPhotoItemListeners listeners) {
        mListeners = listeners;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_photo, container, false);
        sTypeImage = v.findViewById(R.id.photo_dialog_type);
        etNotice = v.findViewById(R.id.photo_dialog_comment);
        ivImage = v.findViewById(R.id.photo_dialog_image);

        Button btnDone = v.findViewById(R.id.photo_dialog_done);
        btnDone.setOnClickListener(this);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(getDialog())).getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        sTypeImage.setAdapter(mTypeAdapter);
        sTypeImage.setSelection(mTypeAdapter.getPositionById(mImage.getType()));
        sTypeImage.setEnabled(!mImage.isVideo());

        byte[] bytes = mImage.getBytes();
        if(bytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            setPhoto(bitmap);
        }
        setNotice(mImage.getNotice());
    }

    private void setPhoto(Bitmap bitmap) {
        if (bitmap != null) {
            ivImage.setImageBitmap(bitmap);
        }
    }

    private void setNotice(String notice) {
        etNotice.setText(notice);
    }

    @Override
    public void onClick(View v) {
        String notice = Objects.requireNonNull(etNotice.getText()).toString();
        int position = sTypeImage.getSelectedItemPosition();
        long typeSelected = mTypeAdapter.getId(position);

        if (!mPhotoManager.isTempImage(mImage)) {
            mPhotoDataManager.updateAttachment(mImage.getId(), typeSelected, notice);
        }

        mImage.setType(typeSelected);
        mImage.setNotice(notice);

        if(mListeners != null) {
            mListeners.onPhotoItemChanged(mImage);
        }

        this.dismiss();
    }

    @Override
    public int getExceptionCode() {
        return IExceptionCode.PHOTO_CHANGE;
    }

    public interface OnPhotoItemListeners {
        /**
         * ОБработчик изменения изображения
         * @param image изображение
         */
        void onPhotoItemChanged(ImageItem image);
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Logger.error(e);
        }

    }
}
