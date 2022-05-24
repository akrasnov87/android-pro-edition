package ru.mobnius.core.ui;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.abedelazizshe.lightcompressorlibrary.VideoQuality;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.Date;

import ru.mobnius.core.Names;
import ru.mobnius.core.R;
import ru.mobnius.core.adapter.BaseSpinnerAdapter;
import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.camera.CameraManager;
import ru.mobnius.core.data.camera.VideoManager;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.gallery.BasePhotoManager;
import ru.mobnius.core.data.gallery.OnGalleryChangeListeners;
import ru.mobnius.core.data.gallery.OnGalleryListener;
import ru.mobnius.core.data.gallery.PhotoDataManager;
import ru.mobnius.core.data.gallery.OnPhotoItemChangeListener;
import ru.mobnius.core.data.synchronization.FinishStatus;
import ru.mobnius.core.data.synchronization.IProgress;
import ru.mobnius.core.data.synchronization.OnSynchronizationListeners;
import ru.mobnius.core.data.synchronization.utils.transfer.Transfer;
import ru.mobnius.core.data.synchronization.utils.transfer.TransferProgress;
import ru.mobnius.core.ui.fragment.BaseGalleryFragment;
import ru.mobnius.core.ui.fragment.PhotoChangeDialogFragment;
import ru.mobnius.core.ui.image.ImageViewActivity;
import ru.mobnius.core.ui.image.ImageItem;
import ru.mobnius.core.utils.BitmapUtil;
import ru.mobnius.core.utils.GalleryUtil;
import ru.mobnius.core.utils.LocationChecker;
import ru.mobnius.core.utils.LocationUtil;

public abstract class CoreFormActivity extends SingleFragmentActivity
        implements OnGalleryChangeListeners,
        OnPhotoItemChangeListener,
        LocationListener,
        OnGalleryListener,
        LocationChecker.OnLocationAvailable {

    public final static int NONE = 0;
    public final static int NORMAL = 1;
    public final static int GOOD = 2;

    private final static String LOCATION = "location";
    private final static String COUNT = "count";

    protected Location mLocation;
    protected Menu mActionMenu;

    private LocationManager locationManager;
    private CameraManager mCameraManager;
    private VideoManager mVideoManager;
    protected BasePhotoManager mPhotoManager;

    public abstract BasePhotoManager getPhotoManager();

    /**
     * Для лайт синхронизации
     */
    @Deprecated
    public abstract OnSynchronizationListeners getSynchronization();

    public abstract PhotoDataManager getPhotoData();

    public abstract BaseSpinnerAdapter getPhotoTypeAdapter(boolean isVideo);

    public abstract int getMenuItemGeoSignal();

    public abstract boolean isDone();

    /**
     * количество полученных координат
     */
    private int locationCount;

    public int getLocationCount() {
        return locationCount;
    }

    public Location getLocation() {
        if (mLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return getEmptyLocation();
            }
            String providerName = LocationUtil.getProviderName(locationManager, LocationManager.NETWORK_PROVIDER);
            Location location = null;
            if (providerName != null) {
                location = locationManager.getLastKnownLocation(providerName);
            }
            return location != null ? location : getEmptyLocation();
        } else {
            mLocation.setTime(new Date().getTime());
        }
        return mLocation;
    }

    private Location getEmptyLocation() {
        Location location = new Location("none");
        location.setLatitude(0);
        location.setLongitude(0);
        location.setTime(new Date().getTime());
        return location;
    }

    public String getRouteId() {
        return getIntent().getStringExtra(Names.ROUTE_ID);
    }

    public String getPointId() {
        return getIntent().getStringExtra(Names.POINT_ID);
    }

    public String getResultd() {
        return getIntent().getStringExtra(Names.RESULT_ID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPhotoManager = GalleryUtil.deSerializable(getIntent());

        super.onCreate(savedInstanceState);
        mCameraManager = new CameraManager(this);
        mVideoManager = new VideoManager(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean b = super.onCreateOptionsMenu(menu);
        if (locationCount > 0) {
            synchronized (this) {
                --locationCount;
            }

            onLocationChanged(mLocation);
        }

        MenuItem actionGeo = menu.findItem(getMenuItemGeoSignal());
        if (actionGeo != null) {
            mActionMenu = menu;
            onLocationStatusChange(NONE, null);
        }

        return b;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            alert("Нет разрешений на использование геолокации");
            return;
        }
        if (PreferencesManager.getInstance().isGeoCheck()) {
            LocationChecker.start(this);
        }

        if (2 > locationCount) {
            String secondProviderName = LocationUtil.getProviderName(locationManager, PreferencesManager.getInstance().getLocation().equals(LocationManager.GPS_PROVIDER) ? LocationManager.NETWORK_PROVIDER : LocationManager.GPS_PROVIDER);
            String firstProviderName = LocationUtil.getProviderName(locationManager, PreferencesManager.getInstance().getLocation());

            if (firstProviderName != null && secondProviderName != null) {
                locationManager.requestLocationUpdates(firstProviderName, 1000, 1, this);
                locationManager.requestLocationUpdates(secondProviderName, 1000, 1, this);

                if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 1, this);
                }
            }
        }
    }

    @Override
    public void onLocationAvailable(int mode) {
        switch (mode) {
            case LocationChecker.LOCATION_OFF:
                geoAlert("Для работы приложения необходимо включить доступ к геолокации", "Включить геолокацию", null);
                break;
            case LocationChecker.LOCATION_ON_LOW_ACCURACY:
                String message;
                Bitmap icon;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                    message = "На устройстве работет не точный режим определения геокоординат. Необходимо в меню 'Местположение' " +
                            "перейти в подменю 'Геолокация Google' и выбрать 'Определять местополжение точнее'";
                    icon = BitmapFactory.decodeResource(getResources(), R.raw.location_p_q);
                } else {
                    message = "Включен режим определения геолокации \"По спутникам GPS\". Для корректной работы приложения " +
                            "необходимо выбрать режим: \"По всем источникам\" или \"По координатам сети\"";
                    icon = BitmapFactory.decodeResource(getResources(), R.raw.location_below_p);
                }
                geoAlert(message, "Изменить режим", icon);
                break;
        }
    }

    private void geoAlert(String message, String buttonText, Bitmap drawable) {
        View dialogView = getLayoutInflater().inflate(R.layout.geo_alert_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setMessage(message)
                .setCancelable(false)
                .create();
        ImageView pic = dialogView.findViewById(R.id.geo_alert_image);
        Button btn = dialogView.findViewById(R.id.geo_alert_button);
        btn.setText(buttonText);
        btn.setOnClickListener((v) -> {
            try {
                CoreFormActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });
        pic.setImageBitmap(drawable);
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        locationManager.removeUpdates(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(LOCATION, mLocation);
        outState.putInt(COUNT, locationCount);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mLocation = savedInstanceState.getParcelable(LOCATION);
        locationCount = savedInstanceState.getInt(COUNT);
    }

    public void onCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mCameraManager.open(BitmapUtil.IMAGE_QUALITY);
        } else {
            Toast.makeText(this, "Нет требуемых разрешений", Toast.LENGTH_LONG).show();
        }
    }

    public void onVideoCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

            // VERY_HIGH, HIGH, MEDIUM, LOW, VERY_LOW
            mVideoManager.open(PreferencesManager.getInstance().getVideoQuality());
        } else {
            Toast.makeText(this, "Нет требуемых разрешений", Toast.LENGTH_LONG).show();
        }
    }

    public CameraManager getCameraManager() {
        return mCameraManager;
    }

    public VideoManager getVideoManager() {
        return mVideoManager;
    }

    /**
     * ОБработчик изменения изображений
     */
    @Override
    public void onGalleryChange(int type, ImageItem image) {
        if (getFragment() instanceof OnGalleryChangeListeners) {
            ((OnGalleryChangeListeners) getFragment()).onGalleryChange(type, image);
        }

        // тут нужно вывести диалог на редактирование
        if (type == OnGalleryChangeListeners.ADD) {
            PhotoChangeDialogFragment f = new PhotoChangeDialogFragment(image, getPhotoManager(), getPhotoData(), getPhotoTypeAdapter(image.isVideo()));
            f.setPhotoItemListeners(new PhotoChangeDialogFragment.OnPhotoItemListeners() {
                @Override
                public void onPhotoItemChanged(ImageItem image) {
                    FragmentManager fm = getSupportFragmentManager();
                    Fragment fragment = fm.findFragmentById(R.id.single_fragment_container);
                    if (fragment instanceof BaseGalleryFragment) {
                        ((BaseGalleryFragment) fragment).onUpdateGallery(image);
                    }
                }
            });
            f.show(getSupportFragmentManager(), "dialog");
        }
    }

    @Override
    public void onPhotoItemSave(ImageItem imageItem) {
        Fragment fragment = getFragment();
        if (fragment instanceof BaseGalleryFragment) {
            ((BaseGalleryFragment) fragment).onUpdateGallery(imageItem);
        } else if (fragment instanceof CoreFormFragment) {
            CoreFormFragment coreFormFragment = (CoreFormFragment) fragment;
            String mPointId = coreFormFragment.getFormManager().getValue(Names.POINT_ID);
            coreFormFragment.onPhotoGallery(mPointId, imageItem.getResultId());
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragment() != null && getFragment() instanceof CoreFormFragment) {
            if (((CoreFormFragment) getFragment()).onBackPressed(false)) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // удаление изображения из галереи
        if (requestCode == ImageViewActivity.IMAGE_REMOVE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data == null || data.getStringExtra(ImageViewActivity.IMAGE_ID) == null) {
                return;
            }
            final String imageId = data.getStringExtra(ImageViewActivity.IMAGE_ID);
            if (imageId == null) {
                return;
            }
            final ImageItem image = mPhotoManager.getImageById(imageId);
            if (image == null) {
                return;
            }
            mPhotoManager.deletePicture(getPhotoData(), FileManager.getInstance(), image);
            // только после удаления
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.single_fragment_container);
            if (fragment instanceof BaseGalleryFragment) {
                ((BaseGalleryFragment) fragment).onDeletePhoto(image);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        synchronized (this) {
            locationCount++;
        }

        int status;
        if (locationCount == 1) {
            status = NORMAL;
        } else {
            status = GOOD;
            locationManager.removeUpdates(this);
        }

        onLocationStatusChange(status, location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void onLocationStatusChange(int status, Location location) {
        if (mActionMenu != null) {
            int icon;
            String message;
            switch (status) {
                case NONE:
                    icon = R.drawable.ic_gps_off_24px;
                    message = "Местоположение не определено.";
                    break;

                case NORMAL:
                    icon = R.drawable.ic_gps_not_fixed_24px;
                    message = "Координата не является точной.";
                    break;

                default:
                    icon = R.drawable.ic_gps_fixed_24px;
                    message = "Координата получена (" + getLocationCount() + ")";
                    break;
            }

            MenuItem menuItem = mActionMenu.findItem(getMenuItemGeoSignal());
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            menuItem.setIcon(icon);
            menuItem.setTitle(message);
        }
    }

    @Override
    public void onSaveFromGallery() {
        CoreFormFragment fragment = ((CoreFormFragment) getFragment());
        if (fragment.getFormManager().isChanged()) {
            if (fragment.onSaveDocument(false)) {
                finish();
            }
        }
    }

    /**
     * При уничтожении активити удаляем созданные временные файлы, т.к. при сохранении они должны быть перещены в постоянное хранилище
     */
    @Override
    public void onDestroyGallery() {
        BasePhotoManager photoManager = getPhotoManager();
        photoManager.clearTempImage(FileManager.getInstance());
    }

    @Override
    protected void onDestroy() {
        locationManager.removeUpdates(this);
        onDestroyGallery();
        super.onDestroy();
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

}