package ru.mobnius.core.ui.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ru.mobnius.core.R;
//import com.mobwal.pro.data.utils.transfer.TransferListener;
//import com.mobwal.pro.data.utils.transfer.TransferProgress;

public class SynchronizationPartFragment extends Fragment {

    public final static String DATA_TYPE = "description";

    ProgressBar progressBar;
    TextView tvDescription;
    TextView tvStatus;

    public SynchronizationPartFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.template_fragment_synchronization_part, container, false);
        progressBar = v.findViewById(R.id.sync_part_progress);
        tvDescription = v.findViewById(R.id.sync_part_description);
        tvStatus = v.findViewById(R.id.sync_part_status);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle arguments = getArguments();
        tvDescription.setText(Objects.requireNonNull(arguments).getString(DATA_TYPE));

        //updateProgressBarColor(TransferListener.START);
    }

    /**
     * обновление процента выполнения
     *
     * @param percent       процент
     * @param secondPercent процент
     */
    public void updatePercent(double percent, double secondPercent) {
        progressBar.setSecondaryProgress((int) percent);
        progressBar.setProgress((int) secondPercent);
    }

    /**
     * обновление статуса
     *
     * @param progress прогресс
     */
    /*public void updateStatus(TransferProgress progress) {
        tvStatus.setText(progress.toString());
        Bundle arguments = getArguments();
        if (arguments != null) {
            String text = arguments.getString(DATA_TYPE) + " (" + progress.getTransferData().toString() + ")";
            tvDescription.setText(text);
        }
    }*/

    /**
     * Обновление логов
     *
     * @param logs лог
     */
    public void updateLogs(String logs) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            String text = arguments.getString(DATA_TYPE) + " " + logs;
            tvDescription.setText(text);
        }
    }

    /**
     * обновление цвета полосы
     *
     * @param type тип статуса TransferListener
     */
    public void updateProgressBarColor(int type) {
        if (!requireActivity().isFinishing()) {
            ColorStateList colorStateList;
            switch (type) {
                /*case TransferListener.STOP:
                case TransferListener.ERROR:
                    colorStateList = AppCompatResources.getColorStateList(requireContext(), R.color.colorSecondary);
                    break;*/

                default:
                    colorStateList = AppCompatResources.getColorStateList(requireContext(), R.color.colorSuccess);
                    break;
            }
            progressBar.setSecondaryProgressTintList(colorStateList);
            progressBar.setProgressTintList(colorStateList);
        }
    }
}
