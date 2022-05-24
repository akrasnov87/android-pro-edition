package ru.mobnius.core.ui.form.validator;

import android.widget.EditText;

public class TextEmptyValidator
        implements OnValidatorListeners<EditText> {

    protected EditText mEditText;

    public TextEmptyValidator(EditText view) {
        mEditText = view;
    }

    @Override
    public EditText getView() {
        return mEditText;
    }

    @Override
    public String getMessage() {
        return "Поле не должно быть пустым";
    }

    @Override
    public boolean isValid() {
        if (mEditText!=null&&!mEditText.isEnabled()){
            return true;
        }
        if(mEditText != null &&
                mEditText.getText().toString().isEmpty()) {
            mEditText.setError(getMessage());
            return false;
        }
        return true;
    }
}
