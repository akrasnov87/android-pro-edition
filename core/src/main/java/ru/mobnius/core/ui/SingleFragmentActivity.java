package ru.mobnius.core.ui;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ru.mobnius.core.R;
import ru.mobnius.core.data.app.OnCoreApplicationListeners;
import ru.mobnius.core.data.network.OnNetworkChangeListeners;
import ru.mobnius.core.data.socket.OnSocketListeners;

public abstract class SingleFragmentActivity extends CoreActivity
        implements OnSocketListeners {

    private Fragment mFragment;

    public SingleFragmentActivity() {
        super();
    }

    public SingleFragmentActivity(boolean isBackToExist) {
        super(isBackToExist);
    }

    public Fragment getFragment() {
        return mFragment;
    }

    protected abstract Fragment createFragment();

    protected int getLayoutResId() {
        return R.layout.master_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());
        FragmentManager fm = getSupportFragmentManager();

        mFragment = fm.findFragmentById(R.id.single_fragment_container);
        if (mFragment == null) {
            mFragment = createFragment();
            if (mFragment != null) {
                fm.beginTransaction()
                        .add(R.id.single_fragment_container, mFragment)
                        .commit();
            }
        }
    }

    /**
     * Получение обработчика изменения сети
     *
     * @return обработчик
     */
    public OnCoreApplicationListeners getNetworkChangeListener() {
        if (getApplication() instanceof OnNetworkChangeListeners) {
            return (OnCoreApplicationListeners) getApplication();
        }

        return null;
    }

    public OnCoreApplicationListeners getNotificationListner() {
        if (getApplication() instanceof OnSocketListeners) {
            return (OnCoreApplicationListeners) getApplication();
        }

        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getNotificationListner() != null) {
            getNotificationListner().addNotificationListener(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getNotificationListner() != null) {
            getNotificationListner().removeNotificationListener(this);
        }
    }


    @Override
    public void onPushMessage(String type, byte[] buffer) {

    }

    @Override
    public void onPushDelivered(byte[] buffer) {
        /*StringMail mail = StringMail.getInstance(buffer);
        if (mail != null) {
            String CHANNEL_ID = "app-android";
            String body = mail.mBody;
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "app", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(notificationChannel);
                }
            }
            Intent i = MainActivity.newNotificationIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setTicker("Появилась новая информация")
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle("Появилась новая информация")
                    .setContentText(body)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
            if (notificationManager != null) {
                int unicNumber =  (int) ((new Date().getTime() / 1000L)% Integer.MAX_VALUE);
                notificationManager.notify(unicNumber, notification);
            }
        }*/
    }

    @Override
    public void onPushUnDelivered(byte[] buffer) {

    }

    /*public static Intent newNotificationIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }*/

    @Override
    public void onConnect() {

    }

    @Override
    public void onRegistry() {

    }

    @Override
    public void onDisconnect() {

    }
}
