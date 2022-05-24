package ru.mobnius.core.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import java.util.Objects;

import ru.mobnius.core.R;
import ru.mobnius.core.adapter.task.BitmapToStringAsyncTask;
import ru.mobnius.core.adapter.task.StringToBitmapAsyncTask;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.ui.component.signature.OnSignatureListener;
import ru.mobnius.core.ui.component.signature.SignatureDrawingView;
import ru.mobnius.core.utils.BitmapUtil;

public class SignatureActivity extends CoreActivity
        implements SignatureDrawingView.OnDrawingListener,
        BitmapToStringAsyncTask.OnConvertFinishedListener,
        StringToBitmapAsyncTask.OnConvertFinishedListener {

    public final static int SIGNATURE_REQUEST_CODE = 2;

    private SignatureDrawingView mSignatureDrawingView;
    private MenuItem mItemDone;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSignatureDrawingView = new SignatureDrawingView(this);
        setContentView(mSignatureDrawingView);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        String title = getIntent().getStringExtra(OnSignatureListener.TITLE);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);

        if(getIntent().hasExtra(OnSignatureListener.IMAGE)) {
            startProgress();
            new StringToBitmapAsyncTask(this).execute(getIntent().getStringExtra(OnSignatureListener.IMAGE));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signature_menu, menu);
        mItemDone = menu.findItem(R.id.action_signature_done);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_signature_done) {
            Bitmap bitmap = mSignatureDrawingView.getBitmap();
            if (bitmap != null) {
                startProgress();
                new BitmapToStringAsyncTask(this).execute(bitmap);
            }
        } else if (itemId == R.id.action_signature_erase) {
            mSignatureDrawingView.createBitmap(getWindow().getDecorView().getWidth(), getWindow().getDecorView().getHeight());
            mItemDone.setVisible(false);
        } else if (itemId == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWrite() {
        if (!mItemDone.isVisible()) {
            mItemDone.setVisible(true);
        }
    }

    @Override
    public int getExceptionCode() {
        return IExceptionCode.SIGNATURE;
    }

    @Override
    public void onConvertFinished(String base64) {
        stopProgress();
        Intent intent = new Intent();
        intent.putExtra(OnSignatureListener.IMAGE, base64);

        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onConvertFinished(Bitmap bitmap) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        Bitmap resizeBmp = BitmapUtil.scaleToFitWidth(bitmap, width);
        mSignatureDrawingView.setBitmap(resizeBmp);
        stopProgress();
    }
}

