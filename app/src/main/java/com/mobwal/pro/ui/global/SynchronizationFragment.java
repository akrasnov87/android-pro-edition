package com.mobwal.pro.ui.global;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobwal.pro.ManualSynchronization;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.databinding.FragmentSynchronizationBinding;

import com.mobwal.android.library.authorization.BasicAuthorizationSingleton;

import com.mobwal.android.library.authorization.credential.BasicCredential;
import com.mobwal.android.library.socket.OnSocketListeners;
import com.mobwal.android.library.socket.SocketManager;
import com.mobwal.android.library.data.sync.ProgressListeners;
import com.mobwal.android.library.data.sync.OnSynchronizationListeners;
import com.mobwal.android.library.data.sync.util.transfer.Transfer;
import com.mobwal.android.library.data.sync.util.transfer.TransferProgress;

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
        BasicCredential credentials = BasicAuthorizationSingleton.getInstance().getUser().getCredential();
        //socketManager = SocketManager.createInstance(GlobalSettings.getConnectUrl(), credentials, "");
        socketManager = SocketManager.createInstance("", credentials, "");
        if(socketManager.isRegistered()) {
            onRegistry();
        } else {
            socketManager.open(this);
        }
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
        ManualSynchronization synchronization = ManualSynchronization.getInstance(WalkerApplication.getWalkerSQLContext(requireContext()), false);
        synchronization.start(requireActivity(), new ProgressListeners() {
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