package com.mobwal.pro.ui.global;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobwal.android.library.FileManager;
import com.mobwal.android.library.FragmentRunnable;
import com.mobwal.android.library.PrefManager;
import com.mobwal.android.library.data.sync.Entity;
import com.mobwal.android.library.data.sync.util.transfer.DownloadTransfer;
import com.mobwal.android.library.data.sync.util.transfer.TransferListeners;
import com.mobwal.android.library.data.sync.util.transfer.UploadTransfer;
import com.mobwal.android.library.util.LogUtilSingleton;
import com.mobwal.pro.ManualSynchronization;
import com.mobwal.pro.Names;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.adapter.SyncLogAdapter;
import com.mobwal.pro.databinding.FragmentSynchronizationBinding;

import com.mobwal.android.library.authorization.BasicAuthorizationSingleton;

import com.mobwal.android.library.authorization.credential.BasicCredential;
import com.mobwal.android.library.socket.OnSocketListeners;
import com.mobwal.android.library.socket.SocketManager;
import com.mobwal.android.library.data.sync.ProgressListeners;
import com.mobwal.android.library.data.sync.OnSynchronizationListeners;
import com.mobwal.android.library.data.sync.util.transfer.Transfer;
import com.mobwal.android.library.data.sync.util.transfer.TransferProgress;
import com.mobwal.pro.sync.SynchronizationLogItem;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class SynchronizationFragment extends Fragment
    implements View.OnClickListener, OnSocketListeners {

    private FragmentSynchronizationBinding binding;

    private SocketManager socketManager;
    private ManualSynchronization synchronization;

    private List<SynchronizationLogItem> mLogList;
    private SyncLogAdapter mSyncLogAdapter;

    public SynchronizationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSynchronizationBinding.inflate(inflater, container, false);
        binding.synchronizationAction.setOnClickListener(this);
        PrefManager prefManager = new PrefManager(requireContext());
        mLogList = new ArrayList<>();

        if(prefManager.get("debug", false)) {
            binding.synchronizationLogs.setVisibility(View.VISIBLE);
        }

        synchronization = ManualSynchronization.getInstance(WalkerApplication.getWalkerSQLContext(requireContext()), false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Toast.makeText(requireContext(), "Синхронизация завершена вручную!", Toast.LENGTH_SHORT).show();

        if(synchronization != null) {
            synchronization.destroy();
        }

        if(socketManager != null) {
            socketManager.destroy();
        }

        binding = null;
    }

    @Override
    public void onClick(View v) {
        binding.synchronizationInfo.setVisibility(View.GONE);

        if(synchronization.isRunning()) {
            setLogMessage("Синхронизация завершена принудительно!", true);
            synchronization.stop();
            binding.synchronizationAction.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_play_arrow_24, null));
            Toast.makeText(requireContext(), "Синхронизация завершена вручную!", Toast.LENGTH_SHORT).show();
        } else {
            binding.synchronizationAction.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_stop_24, null));

            mLogList.clear();
            mSyncLogAdapter = new SyncLogAdapter(requireContext(), mLogList);
            binding.synchronizationLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.synchronizationLogs.setAdapter(mSyncLogAdapter);

            BasicCredential credentials = BasicAuthorizationSingleton.getInstance().getUser().getCredential();
            socketManager = new SocketManager(Names.getConnectUrl(), credentials, "");
            if(socketManager.isRegistered()) {
                onRegistry();
            } else {
                socketManager.open(this);
            }
        }
    }

    @Override
    public void onConnect() {
        requireActivity().runOnUiThread(new FragmentRunnable(SynchronizationFragment.this) {
            @Override
            public void beforeRun() {
                setLogMessage("Соединение с сервером создано", false);
            }
        });
    }

    @Override
    public void onRegistry() {
        synchronization.start(socketManager, new ProgressListeners() {
            @Override
            public void onStart(OnSynchronizationListeners synchronization) {
                requireActivity().runOnUiThread(new FragmentRunnable(SynchronizationFragment.this) {
                    @Override
                    public void beforeRun() {
                        binding.synchronizationDataCategory.updateProgressBarColor(TransferListeners.START);
                        binding.synchronizationFileCategory.updateProgressBarColor(TransferListeners.START);

                        binding.synchronizationDataCategory.setVisibility(View.VISIBLE);
                        binding.synchronizationFileCategory.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onStop(OnSynchronizationListeners synchronization) {
                binding.synchronizationAction.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_play_arrow_24, null));

                socketManager.destroy();

                // останавливаем индикацию
                requireActivity().runOnUiThread(new FragmentRunnable(SynchronizationFragment.this) {
                    @Override
                    public void beforeRun() {

                        binding.synchronizationDataCategory.setVisibility(View.GONE);
                        binding.synchronizationFileCategory.setVisibility(View.GONE);

                        try {
                            Toast.makeText(requireContext(), "Синхронизация завершена успешно!", Toast.LENGTH_SHORT).show();
                            FileManager.getInstance().deleteFolder(FileManager.PHOTOS);
                        } catch (FileNotFoundException e) {
                            LogUtilSingleton.getInstance().writeText("Ошибка удаление изображений после синхронизации.", e);
                        }
                    }
                });
            }

            @Override
            public void onProgress(OnSynchronizationListeners synchronization, int step, String message, String tid) {
                if (!message.isEmpty()) {
                    requireActivity().runOnUiThread(new FragmentRunnable(SynchronizationFragment.this) {
                        @Override
                        public void beforeRun() {
                            setLogMessage(message, false);
                        }
                    });
                }
            }

            @Override
            public void onError(OnSynchronizationListeners synchronization, int step, String message, String tid) {
                requireActivity().runOnUiThread(new FragmentRunnable(SynchronizationFragment.this) {
                    @Override
                    public void beforeRun() {
                        setLogMessage(message, true);
                    }
                });
            }

            @Override
            public void onStartTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                String category = getCategoryByTid(tid);

                requireActivity().runOnUiThread(new FragmentRunnable(SynchronizationFragment.this) {
                    @Override
                    public void beforeRun() {
                        if (category.equals("DATA")) {
                            binding.synchronizationDataCategory.updatePercent(0, 0);
                        } else if (category.equals("FILES")) {
                            binding.synchronizationFileCategory.updatePercent(0, 0);
                        }
                    }
                });
            }

            @Override
            public void onRestartTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                String category = getCategoryByTid(tid);

                requireActivity().runOnUiThread(new FragmentRunnable(SynchronizationFragment.this) {
                    @Override
                    public void beforeRun() {
                        if (category.equals("DATA")) {
                            binding.synchronizationDataCategory.updateProgressBarColor(TransferListeners.RESTART);
                        } else if (category.equals("FILES")) {
                            binding.synchronizationFileCategory.updateProgressBarColor(TransferListeners.RESTART);
                        }
                    }
                });
            }

            @Override
            public void onPercentTransfer(@NonNull String tid, @NonNull TransferProgress progress, @NonNull Transfer transfer) {
                String category = getCategoryByTid(tid);

                requireActivity().runOnUiThread(new FragmentRunnable(SynchronizationFragment.this) {
                    @Override
                    public void beforeRun() {
                        if (transfer instanceof UploadTransfer && isAdded()) {
                            if (category.equals("DATA")) {
                                binding.synchronizationDataCategory.updatePercent(progress.getPercent(), 0);
                                binding.synchronizationDataCategory.updateStatus(progress);
                            } else if (category.equals("FILES")) {
                                binding.synchronizationFileCategory.updatePercent(progress.getPercent(), 0);
                                binding.synchronizationFileCategory.updateStatus(progress);
                            }
                        }

                        if (transfer instanceof DownloadTransfer && isAdded()) {
                            if (category.equals("DATA")) {
                                binding.synchronizationDataCategory.updatePercent(100, progress.getPercent());
                                binding.synchronizationDataCategory.updateStatus(progress);
                            } else if (category.equals("FILES")) {
                                binding.synchronizationFileCategory.updatePercent(100, progress.getPercent());
                                binding.synchronizationFileCategory.updateStatus(progress);
                            }
                        }
                    }
                });
            }

            @Override
            public void onStopTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                String category = getCategoryByTid(tid);

                requireActivity().runOnUiThread(new FragmentRunnable(SynchronizationFragment.this) {
                    @Override
                    public void beforeRun() {
                        if (category.equals("DATA")) {
                            binding.synchronizationDataCategory.updateProgressBarColor(TransferListeners.STOP);
                        } else if (category.equals("FILES")) {
                            binding.synchronizationFileCategory.updateProgressBarColor(TransferListeners.STOP);
                        }
                    }
                });
            }

            @Override
            public void onEndTransfer(@NonNull String tid, @NonNull Transfer transfer, @NonNull Object data) {

            }

            @Override
            public void onErrorTransfer(@NonNull String tid, @NonNull String message, @NonNull Transfer transfer) {
                String category = getCategoryByTid(tid);

                requireActivity().runOnUiThread(new FragmentRunnable(SynchronizationFragment.this) {
                    @Override
                    public void beforeRun() {
                        if (category.equals("DATA")) {
                            binding.synchronizationDataCategory.updateProgressBarColor(TransferListeners.ERROR);
                        } else if (category.equals("FILES")) {
                            binding.synchronizationFileCategory.updateProgressBarColor(TransferListeners.ERROR);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onDisconnect() {
        requireActivity().runOnUiThread(new FragmentRunnable(SynchronizationFragment.this) {
            @Override
            public void beforeRun() {
                setLogMessage("Соединение с сервером разорвано", false);
            }
        });
    }

    // Privates
    private String getCategoryByTid(@NonNull String tid) {
        Entity[] entities = synchronization.getEntities(tid);
        if (entities.length > 0) {
            return entities[0].category;
        } else {
            return "UNKNOWN";
        }
    }

    protected void setLogMessage(@NonNull String message, boolean isError) {
        if(isError) {
            LogUtilSingleton.getInstance().error(message);
        } else {
            LogUtilSingleton.getInstance().debug(message);
        }

        mLogList.add(0, new SynchronizationLogItem(message, isError));
        mSyncLogAdapter.notifyItemInserted(0);
    }
}