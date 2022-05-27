package com.mobwal.pro.ui.global;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobwal.pro.ManualSynchronization;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.databinding.FragmentSynchronizationBinding;

import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.authorization.Authorization;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.socket.OnSocketListeners;
import ru.mobnius.core.data.socket.SocketManager;
import com.mobwal.pro.data.IProgress;
import com.mobwal.pro.data.OnSynchronizationListeners;
import com.mobwal.pro.data.utils.transfer.Transfer;
import com.mobwal.pro.data.utils.transfer.TransferProgress;
import ru.mobnius.core.utils.HardwareUtil;

public class SynchronizationFragment extends Fragment
    implements View.OnClickListener, OnSocketListeners {
    private FragmentSynchronizationBinding binding;

    private SocketManager socketManager;

    public SynchronizationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSynchronizationBinding.inflate(inflater, container, false);
        binding.syncAction.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        socketManager.destroy();
        socketManager = null;
        binding = null;
    }

    @Override
    public void onClick(View v) {
        BasicCredentials credentials = Authorization.getInstance().getUser().getCredentials();
        socketManager = SocketManager.createInstance(GlobalSettings.getConnectUrl(), credentials, HardwareUtil.getNumber(requireContext()));
        socketManager.open(this);
    }

    @Override
    public void onPushMessage(String type, byte[] buffer) {

    }

    @Override
    public void onPushDelivered(byte[] buffer) {

    }

    @Override
    public void onPushUnDelivered(byte[] buffer) {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onRegistry() {
        ManualSynchronization synchronization = ManualSynchronization.getInstance(WalkerApplication.getWalkerSQLContext(requireContext()), PreferencesManager.getInstance().getZip());
        synchronization.start(requireActivity(), new IProgress() {
            @Override
            public void onStart(OnSynchronizationListeners synchronization) {

            }

            @Override
            public void onStop(OnSynchronizationListeners synchronization) {

            }

            @Override
            public void onProgress(OnSynchronizationListeners synchronization, int step, String message, String tid) {

            }

            @Override
            public void onError(OnSynchronizationListeners synchronization, int step, String message, String tid) {

            }

            @Override
            public void onStartTransfer(String tid, Transfer transfer) {

            }

            @Override
            public void onRestartTransfer(String tid, Transfer transfer) {

            }

            @Override
            public void onPercentTransfer(String tid, TransferProgress progress, Transfer transfer) {

            }

            @Override
            public void onStopTransfer(String tid, Transfer transfer) {

            }

            @Override
            public void onEndTransfer(String tid, Transfer transfer, Object data) {

            }

            @Override
            public void onErrorTransfer(String tid, String message, Transfer transfer) {

            }
        });
    }

    @Override
    public void onDisconnect() {

    }
}