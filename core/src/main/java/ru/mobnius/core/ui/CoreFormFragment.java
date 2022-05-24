package ru.mobnius.core.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.core.Names;
import ru.mobnius.core.R;
import ru.mobnius.core.data.DiffValueManager;
import ru.mobnius.core.data.audit.AuditManager;
import ru.mobnius.core.data.audit.OnAuditListeners;
import ru.mobnius.core.data.gallery.BasePhotoManager;
import ru.mobnius.core.data.gallery.OnGalleryChangeListeners;
import ru.mobnius.core.data.gallery.OnGalleryListener;
import ru.mobnius.core.ui.form.OnValidationListeners;
import ru.mobnius.core.ui.form.validator.OnValidatorListeners;
import ru.mobnius.core.ui.image.ImageItem;
import ru.mobnius.core.utils.StringUtil;

public abstract class CoreFormFragment extends BaseFragment
        implements OnStringTextWatcher,
        OnValidationListeners,
        OnGalleryChangeListeners,
        DialogInterface.OnClickListener {

    private OnGalleryListener mOnGalleryListener;
    private List<OnValidatorListeners> mValidModels;
    private boolean mIsGalleryVisible = false;
    protected DiffValueManager mFormManager;

    public DiffValueManager getFormManager() {
        return mFormManager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mValidModels = new ArrayList<>();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnGalleryListener) {
            mOnGalleryListener = (OnGalleryListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        isChangedDocument(getFormManager().isChanged());
        // проверка на валидность
        //onValidate();
    }

    /**
     * Изменение текстового поля
     *
     * @param id        идентификатор
     * @param prevValue пред. значение
     * @param value     значение
     */
    @Override
    public void afterStringTextChanged(String id, String prevValue, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(id, value);
        getFormManager().addValues(bundle);
    }

    @Override
    public void afterTextChanged(Editable s) {
        isChangedDocument(getFormManager().isChanged());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    protected OnGalleryListener getOnGalleryListener() {
        return mOnGalleryListener;
    }

    /**
     * Вывести фотогалерею
     *
     * @param pointId  инден. точки маршрута
     * @param resultId идентификатор результат
     */
    public void onPhotoGallery(String pointId, String resultId) {
        mIsGalleryVisible = true;
    }

    /**
     * Вывод сообщения в случаии не валидности формы
     */
    protected void onValidateMessage() {
        alert(getResources().getString(R.string.un_valid_save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onSaveDocument(true)) {
                    requireActivity().finish();
                }
            }
        });
    }

    public boolean onBackPressed(boolean callParent) {
        if (isGalleryVisible()) {
            // если сейчас выводиться галерея, то должна быть стандартная обработкам назад
            mIsGalleryVisible = false;
            return true;
        }

        if (getFormManager().isChanged() && !callParent) {
            ((CoreActivity)requireActivity()).setBackPressed(false);
            // тут были изменения
            alert(getResources().getString(R.string.is_exit_from_card), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // сбрасываем информацию
                    if (!StringUtil.isEmptyOrNull(getPhotoValidation()) && getFormManager().existsValue(Names.RESULT_ID)) {
                        Toast.makeText(requireActivity(), getPhotoValidation(), Toast.LENGTH_SHORT).show();
                        ((CoreActivity)requireActivity()).setBackPressed(false);
                        dialog.dismiss();
                    } else {
                        AuditManager.getInstance().write("", "EXIT_CARD_NOT_SAVE", OnAuditListeners.Level.HIGH);
                        getFormManager().resetBundle();
                        ((CoreActivity)requireActivity()).setBackPressed(true);
                        requireActivity().onBackPressed();
                    }
                }
            });
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // выполняется для выбора нет
    }

    @Override
    public void addValidator(OnValidatorListeners validator) {
        mValidModels.add(validator);
    }

    @Override
    public void removeValidator(OnValidatorListeners validator) {
        mValidModels.remove(validator);
    }

    @Override
    public void removeValidators() {
        mValidModels.clear();
    }

    @Override
    public OnValidatorListeners[] getValidations() {
        return mValidModels.toArray(new OnValidatorListeners[0]);
    }

    /**
     * Вывод всплывающего сообщения
     *
     * @param title    заголовок
     * @param listener обработчик нажатия на PositiveButton
     */
    private void alert(String title, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
        adb.setPositiveButton(getResources().getString(R.string.yes), listener);

        adb.setNegativeButton(getResources().getString(R.string.no), this);

        AlertDialog alert = adb.create();
        alert.setTitle(title);
        alert.show();
    }

    /**
     * Обработчик валидности формы
     *
     * @return true - форма валидна
     */
    public boolean onValidate() {
        boolean mResult = true;
        for (OnValidatorListeners validator : getValidations()) {
            if (!validator.isValid() && mResult) {
                mResult = false;
            }
        }
        return mResult;
    }

    /**
     * Обработка сохранения формы
     *
     * @param validateIgnore игнорировать проверку на валидность
     * @return результат сохранения
     */
    public abstract boolean onSaveDocument(boolean validateIgnore);

    public abstract void isChangedDocument(boolean changed);

    /**
     * Отображается сейчас галерея или нет
     *
     * @return true - выводиться галарея
     */
    public boolean isGalleryVisible() {
        return mIsGalleryVisible;
    }

    /**
     * ОБработчик изменения изображений
     */
    @Override
    public void onGalleryChange(int type, ImageItem image) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Names.IMAGES, getOnGalleryListener().getPhotoManager().getImages());
        getFormManager().addValues(bundle);
        isChangedDocument(getFormManager().isChanged());
    }


    public CoreFormActivity getFormActivity() {
        return (CoreFormActivity) getActivity();
    }

    public BasePhotoManager getPhotoManager() {
        return mOnGalleryListener.getPhotoManager();
    }

    public Location getLocation() {
        return getFormActivity().getLocation();
    }

    /**
     * Валидация изображения
     *
     * @return
     */
    public abstract String getPhotoValidation();

    public void setIsGalleryVisible(boolean isGalleryVisible){
        mIsGalleryVisible = isGalleryVisible;
    }
}
