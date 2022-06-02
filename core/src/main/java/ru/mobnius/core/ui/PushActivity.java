package ru.mobnius.core.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import ru.mobnius.core.R;
import ru.mobnius.core.adapter.PushAdapter;
import ru.mobnius.core.adapter.PushAsyncTask;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.model.PushItemModel;
import ru.mobnius.core.ui.component.ErrorFieldView;

/**
 * Уведомления для пользователя
 */
public class PushActivity extends CoreActivity
        implements PushAsyncTask.OnPushListener {

    public static Intent getIntent(Context context) {
        return new Intent(context, PushActivity.class);
    }

    private PushAdapter mPushAdapter;
    private ErrorFieldView mErrorField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        startProgress();

        mPushAdapter = new PushAdapter(this);
        mErrorField = findViewById(R.id.error);
        RecyclerView recyclerView = findViewById(R.id.push_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mPushAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public int getExceptionCode() {
        return IExceptionCode.PUSH;
    }

    @Override
    public void onPushLoaded(List<PushItemModel> list) {
        if (list.size() < 1) {
            mErrorField.setMessage("Нет новых уведомлений");
        }else {
            mErrorField.setVisibility(View.GONE);
        }
        mPushAdapter.updateList(list);
        stopProgress();
    }

    @Override
    public void onPushFailed(Exception e) {
        setErrorMessage(e.getMessage());
    }
}
