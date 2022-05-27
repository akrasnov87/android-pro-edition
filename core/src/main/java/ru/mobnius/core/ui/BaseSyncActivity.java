package ru.mobnius.core.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.mobnius.core.R;
import ru.mobnius.core.adapter.SyncLogAdapter;
import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.Version;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.data.exception.IExceptionGroup;
import ru.mobnius.core.data.logger.Logger;
/*import ru.mobnius.core.data.synchronization.Entity;
import ru.mobnius.core.data.synchronization.IProgress;
import ru.mobnius.core.data.synchronization.OnSynchronizationListeners;
import com.mobwal.pro.data.utils.SocketStatusReader;
import com.mobwal.pro.data.utils.transfer.DownloadTransfer;
import com.mobwal.pro.data.utils.transfer.Transfer;
import com.mobwal.pro.data.utils.transfer.TransferListener;
import com.mobwal.pro.data.utils.transfer.TransferProgress;
import com.mobwal.pro.data.utils.transfer.UploadTransfer;*/
import ru.mobnius.core.model.LogItemModel;
import ru.mobnius.core.ui.fragment.SynchronizationPartFragment;
import ru.mobnius.core.utils.NetworkInfoUtil;
import ru.mobnius.core.utils.VersionUtil;

public abstract class BaseSyncActivity extends CoreActivity
        implements View.OnClickListener {

    private Button btnStart;
    private Button btnCancel;

    private RecyclerView rvLogs;
    private List<LogItemModel> mLogList;
    private SyncLogAdapter mSyncLogAdapter;
    private boolean mIsSyncSuccess = true;

    /**
     * хранилище фрагментов транспортировки
     */
    private HashMap<String, SynchronizationPartFragment> transferFragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronization);

        mLogList = new ArrayList<>();
        transferFragments = new HashMap<>();

        rvLogs = findViewById(R.id.sync_logs);
        btnStart = findViewById(R.id.sync_start);
        btnCancel = findViewById(R.id.sync_cancel);

        btnStart.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String currentVersion = VersionUtil.getVersionName(this);
        if (PreferencesManager.getInstance() != null && PreferencesManager.getInstance().isDebug()
                || new Version().getVersionState(currentVersion) == Version.ALPHA
                || new Version().getVersionState(currentVersion) == Version.BETA) {
            rvLogs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getExceptionCode() {
        return IExceptionCode.SYNCHRONIZATION;
    }

    @Override
    public String getExceptionGroup() {
        return IExceptionGroup.SYNCHRONIZATION;
    }

    @Override
    public void onClick(View v) {
        mIsSyncSuccess = true;
        int id = v.getId();
        if (id == R.id.sync_start) {
            if (NetworkInfoUtil.isNetworkAvailable(this)) {
                onSyncStart();
                btnCancel.setVisibility(View.VISIBLE);
                btnStart.setEnabled(false);

                mLogList.clear();
                mSyncLogAdapter = new SyncLogAdapter(this, mLogList);
                rvLogs.setLayoutManager(new LinearLayoutManager(this));
                rvLogs.setAdapter(mSyncLogAdapter);
                start();
            } else {
                setErrorMessage(getString(R.string.network_error));
            }
        } else if (id == R.id.sync_cancel) {
            stop();
            onSyncStop();
            setLogMessage("Синхронизация завершена принудительно", true);
            btnStart.setEnabled(true);
            btnCancel.setVisibility(View.GONE);
        }
    }

    //public abstract OnSynchronizationListeners getSynchronization();

    /**
     * остановка выполнения синхронизации
     */
    void stop() {
        for (String tid : transferFragments.keySet()) {
            removeSynchronizationPart(tid);
        }
        transferFragments.clear();
        //getSynchronization().stop();
    }

    /**
     * запуск выполнения синхронизации
     */
    private void start() {
        try {
            stop();
            final List<Boolean> success = new ArrayList<>();

            /*getSynchronization().start(this, new IProgress() {
                @Override
                public void onStartTransfer(String tid, Transfer transfer) {
                    if (transfer instanceof UploadTransfer) {
                        addSynchronizationPart(tid);
                        success.add(true);
                    }
                }

                @Override
                public void onRestartTransfer(String tid, Transfer transfer) {
                    updateSynchronizationPartStatus(tid, TransferListener.RESTART);
                }

                @Override
                public void onPercentTransfer(String tid, TransferProgress progress, Transfer transfer) {
                    if (transfer instanceof UploadTransfer) {
                        updateSynchronizationPart(tid, progress.getPercent(), 0, progress);
                    }

                    if (transfer instanceof DownloadTransfer) {
                        updateSynchronizationPart(tid, 100, progress.getPercent(), progress);
                    }
                }

                @Override
                public void onStopTransfer(String tid, Transfer transfer) {
                    updateSynchronizationPartStatus(tid, TransferListener.STOP);
                }

                @Override
                public void onEndTransfer(String tid, Transfer transfer, Object data) {
                    if (transfer instanceof DownloadTransfer) {
                        removeSynchronizationPart(tid);
                        success.remove(0);

                        if (success.size() == 0) {
                            btnCancel.setVisibility(View.GONE);
                            btnStart.setEnabled(true);
                        }
                    }
                }

                @Override
                public void onErrorTransfer(String tid, String message, Transfer transfer) {
                    updateSynchronizationPartStatus(tid, TransferListener.ERROR);
                }

                @Override
                public void onStart(OnSynchronizationListeners synchronization) {

                }

                @Override
                public void onStop(OnSynchronizationListeners synchronization) {
                    try {
                        if (mIsSyncSuccess) {
                            FileManager.getInstance().deleteFolder(FileManager.PHOTOS);
                        }
                    } catch (FileNotFoundException e) {
                        Logger.error(e);
                    }

                    onSynced(mIsSyncSuccess);
                }

                @Override
                public void onProgress(OnSynchronizationListeners synchronization, int step, String message, String tid) {
                    updateSynchronizationPartLogs(tid, message);

                    if (!message.isEmpty()) {
                        setLogMessage(message, false);
                    }
                }

                @Override
                public void onError(OnSynchronizationListeners synchronization, int step, String message, String tid) {
                    setLogMessage(message, true);
                }
            });
            */
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    protected void setLogMessage(String message, boolean isError) {
        if (isError) {
            mIsSyncSuccess = false;
        }
        mLogList.add(0, new LogItemModel(message, isError));
        mSyncLogAdapter.notifyDataSetChanged();
    }

    private void addSynchronizationPart(String tid) {

        /*Entity[] entities = getSynchronization().getEntities(tid);
        String name;
        if (entities.length > 0) {
            name = entities[0].nameEntity;
        } else {
            name = "Неизвестно";
        }

        SynchronizationPartFragment synchronizationPartFragment = new SynchronizationPartFragment();

        Bundle bundle = new Bundle();
        bundle.putString(SynchronizationPartFragment.DATA_TYPE, name);
        synchronizationPartFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.sync_progress, synchronizationPartFragment);
        fragmentTransaction.commit();
        transferFragments.put(tid, synchronizationPartFragment);*/
    }

    /*private void updateSynchronizationPart(String tid, double progress, double secondProgress, TransferProgress transferProgress) {
        SynchronizationPartFragment fragment = transferFragments.get(tid);
        if (fragment != null) {
            fragment.updatePercent(progress, secondProgress);
            fragment.updateStatus(transferProgress);
        }
    }

    private void updateSynchronizationPartStatus(String tid, int type) {
        if (!isFinishing()) {
            SynchronizationPartFragment fragment = transferFragments.get(tid);
            if (fragment != null) {
                fragment.updateProgressBarColor(type);
            }
        }
    }

    private void updateSynchronizationPartLogs(String tid, String message) {
        SocketStatusReader reader = SocketStatusReader.getInstance(message);
        if (reader != null) {
            SynchronizationPartFragment fragment = transferFragments.get(tid);
            if (fragment != null) {
                fragment.updateLogs(reader.getParams()[0]);
            }
        }
    }*/

    /**
     * удаление фрагмента для вывода progressbar
     *
     * @param tid иден.
     */
    private void removeSynchronizationPart(String tid) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        SynchronizationPartFragment fragment = transferFragments.get(tid);

        if (fragment != null && !getSupportFragmentManager().isDestroyed()) {
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    /**
     * Обработчик завершения синхронизации
     *
     * @param success статус завершения, true - завершено удачно
     */
    protected abstract void onSynced(boolean success);

    /**
     * запуск синхронизации
     */
    protected abstract void onSyncStart();

    /**
     * принудительная остановка синхронизации
     */
    protected abstract void onSyncStop();
}
