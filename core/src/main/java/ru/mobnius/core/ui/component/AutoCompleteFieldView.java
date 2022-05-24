package ru.mobnius.core.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ru.mobnius.core.Names;
import ru.mobnius.core.R;
import ru.mobnius.core.adapter.BaseSpinnerAdapter;
import ru.mobnius.core.utils.LongUtil;
import ru.mobnius.core.utils.StringUtil;

public class AutoCompleteFieldView extends LinearLayout {

    private TextView tvFieldLabel;
    private AutoCompleteTextView actvFieldValue;
    private ImageButton ibDropDown;
    private ImageButton ibCancel;
    private AutoCompleteFieldAdapter mAdapter;

    private String mValue;
    private OnAutoCompleteSelectedListener mOnAutoCompleteSelectedListener;

    private String savedText;
    private String savedValue;


    public AutoCompleteFieldView(Context context) {
        this(context, null);
    }

    public AutoCompleteFieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.AutoCompleteFieldView, 0, 0);
        String fieldLabel = a.getString(R.styleable.AutoCompleteFieldView_autoCompleteLabel);
        String fieldHint = a.getString(R.styleable.AutoCompleteFieldView_autoCompleteHint);
        a.recycle();

        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Objects.requireNonNull(inflater).inflate(R.layout.auto_complete_field, this, true);

        tvFieldLabel = findViewById(R.id.auto_complete_field_label);
        actvFieldValue = findViewById(R.id.auto_complete_field_value);
        actvFieldValue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("rawtypes")
                HashMap map = (HashMap) parent.getItemAtPosition(position);
                String text = (String)map.get(Names.NAME);

                actvFieldValue.setText(text);
                if(map.get(Names.ID) instanceof Long) {
                    mValue = Long.toString(LongUtil.convertToLong(map.get(Names.ID)));
                } else {
                    mValue = (String) map.get(Names.ID);
                }

                ibCancel.setVisibility(VISIBLE);
                ibDropDown.setVisibility(GONE);
                if(mOnAutoCompleteSelectedListener != null){
                    mOnAutoCompleteSelectedListener.onAutoCompleteSelected(mValue, text);
                }
            }
        });
        actvFieldValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mOnAutoCompleteSelectedListener != null) {
                    mOnAutoCompleteSelectedListener.onAutoCompleteTextChanged(s.toString());
                }
            }
        });
        ibDropDown = findViewById(R.id.auto_complete_field_dropdown);
        ibDropDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actvFieldValue.showDropDown();
            }
        });
        ibCancel = findViewById(R.id.auto_complete_field_cancel);
        ibCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ibCancel.setVisibility(GONE);
                ibDropDown.setVisibility(VISIBLE);
                actvFieldValue.setText("");
                mValue = null;
                if(mOnAutoCompleteSelectedListener != null){
                    mOnAutoCompleteSelectedListener.onAutoCompleteSelected(null, "");
                }
            }
        });

        setFieldLabel(fieldLabel);

        setFieldHint(fieldHint);
    }

    public void setFieldLabel(String value) {
        if(StringUtil.isEmptyOrNull(value)) {
            tvFieldLabel.setText("");
            tvFieldLabel.setVisibility(GONE);
        } else {
            tvFieldLabel.setText(value);
            tvFieldLabel.setVisibility(VISIBLE);
        }
    }

    public void setFieldHint(String value) {
        actvFieldValue.setHint(value);
    }

    public void setFieldText(String text) {
        actvFieldValue.setText(text);
    }

    public void setError(String error) {
        if(StringUtil.isEmptyOrNull(error)) {
            actvFieldValue.setError("", null);
        } else {
            actvFieldValue.setError(error);
        }
    }

    public void setFieldValue(String value) {
        if(mAdapter != null) {
            int position = mAdapter.getPositionById(value);
            mValue = value;
            if(position >= 0) {
                ibCancel.setVisibility(VISIBLE);
                ibDropDown.setVisibility(GONE);
                actvFieldValue.setText(mAdapter.getStringValue(position));
            }
        }
    }

    public void setAdapter(AutoCompleteFieldAdapter adapter) {
        mAdapter = adapter;
        actvFieldValue.setAdapter(adapter);
    }

    public String getValue() {
        String text = actvFieldValue.getText().toString();
        if(actvFieldValue.getText().length() > 0 && mValue == null) {
            for(int i = 0; i < mAdapter.getCount(); i++) {
                @SuppressWarnings("rawtypes")
                HashMap m = (HashMap)mAdapter.getMaps().get(i);
                String result = (String)m.get(Names.NAME);
                assert result != null;
                if(result.equals(text)) {
                    return (String) m.get(Names.ID);
                }
            }
        }
        return mValue;
    }

    public String getFieldText() {
        return actvFieldValue.getText().toString();
    }

    public void setOnAutoCompleteSelected(OnAutoCompleteSelectedListener listener) {
        mOnAutoCompleteSelectedListener = listener;
    }

    public static class AutoCompleteFieldAdapter extends BaseSpinnerAdapter implements Filterable {

        private static String[] from = { Names.NAME };
        private static int[] to = { R.id.simple_type_item_name };

        public AutoCompleteFieldAdapter(Context context) {
            super(context, new ArrayList<Map<String, Object>>(), from, to);
        }

        public void updateItems(ArrayList<Map<String, Object>> items) {
            mMaps.clear();
            mMaps.addAll(items);
            notifyDataSetChanged();
        }

        public int getPositionById(String id) {
            for(int i = 0; i < getCount(); i++) {
                if(mMaps.size() > 0) {
                    @SuppressWarnings("rawtypes")
                    HashMap m = (HashMap) mMaps.get(i);
                    String resultId;
                    if (m.get(Names.ID) instanceof Long) {
                        resultId = Long.toString(LongUtil.convertToLong(m.get(Names.ID)));
                    } else {
                        resultId = (String) m.get(Names.ID);
                    }
                    assert resultId != null;
                    if (resultId.equals(id)) {
                        return i;
                    }
                }
            }

            return  -1;
        }
    }

    public void saveState(){
        savedText = getFieldText();
        savedValue = getValue();
    }

    public void restoreState(ArrayList<Map<String, Object>> items){
        if (savedText !=null&& savedValue !=null) {
            mAdapter.updateItems(items);
            setFieldText(savedText);
            setFieldValue(savedValue);
            savedText = null;
            savedValue = null;
        }
    }

    public AutoCompleteFieldAdapter getAdapter(){
        return mAdapter;
    }

    public interface OnAutoCompleteSelectedListener {
        void onAutoCompleteSelected(String value, String text);
        void onAutoCompleteTextChanged(String text);
    }
}
