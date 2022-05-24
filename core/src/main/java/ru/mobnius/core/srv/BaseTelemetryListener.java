package ru.mobnius.core.srv;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.Date;
import java.util.Objects;

import ru.mobnius.core.utils.DateUtil;
import ru.mobnius.core.utils.HardwareUtil;
import ru.mobnius.core.utils.NetworkInfoUtil;

import static android.content.Context.ACTIVITY_SERVICE;
import static ru.mobnius.core.srv.BaseService.SD_CARD_MEMORY_USAGE;

public abstract class BaseTelemetryListener extends BaseListener {

    public BaseTelemetryListener(Context context, long userId) {
        super(context, userId);
    }

    public MobileIndicator getMobileIndicator() {
        MobileIndicator mobileIndicator = new MobileIndicator();

        ConnectivityManager manager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        mobileIndicator.b_isonline = NetworkInfoUtil.isNetworkAvailable(mContext);
        if(networkInfo != null) {
            mobileIndicator.c_network_type = Objects.requireNonNull(networkInfo).getSubtypeName();
        }

        ActivityManager actManager = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        assert actManager != null;
        actManager.getMemoryInfo(memInfo);

        mobileIndicator.n_ram = memInfo.totalMem;
        mobileIndicator.n_used_ram = memInfo.availMem;

        StatFs stat2 = new StatFs(Environment.getDataDirectory().getPath());
        mobileIndicator.n_phone_memory = stat2.getTotalBytes();
        mobileIndicator.n_used_phone_memory = stat2.getAvailableBytes();
        String state = Environment.getExternalStorageState();
        if (SD_CARD_MEMORY_USAGE && Environment.MEDIA_MOUNTED.equals(state))
        {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                long[] externalSdCardSize = getExternalSdCardSize();

                mobileIndicator.n_sd_card_memory = externalSdCardSize[0];
                mobileIndicator.n_used_sd_card_memory = externalSdCardSize[1];
            }
        }

        mobileIndicator.n_battery_level = HardwareUtil.getBatteryPercentage(mContext);
        mobileIndicator.fn_user = mUserId;
        mobileIndicator.d_date = DateUtil.convertDateToString(new Date());

        return mobileIndicator;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private long[] getExternalSdCardSize() {
        File storage = new File("/storage");
        String external_storage_path = "";
        long[] size = new long[2];

        if (storage.exists()) {
            File[] files = storage.listFiles();

            assert files != null;
            for (File file : files) {
                if (file.exists()) {
                    try {
                        if (Environment.isExternalStorageRemovable(file)) {
                            // storage is removable
                            external_storage_path = file.getAbsolutePath();
                            break;
                        }
                    } catch (Exception e) {
                        Log.e("TAG", e.toString());
                    }
                }
            }
        }

        if (!external_storage_path.isEmpty()) {
            File external_storage = new File(external_storage_path);
            if (external_storage.exists()) {
                size[0] = totalSize(external_storage);
                size[1] = availableSize(external_storage);
            }
        }
        return size;
    }

    private long totalSize(File file) {
        StatFs stat = new StatFs(file.getPath());

        return stat.getTotalBytes();
    }
    private long availableSize(File file) {
        StatFs stat = new StatFs(file.getPath());

        return stat.getAvailableBytes();
    }

    public abstract void run();

}
