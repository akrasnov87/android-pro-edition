package com.mobwal.pro.ui.point;

import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobwal.android.library.LogManager;
import com.mobwal.pro.DataManager;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.databinding.FragmentCreatePointBinding;
import com.mobwal.pro.models.LocationInfo;
import com.mobwal.pro.models.SettingRoute;
import com.mobwal.pro.ui.global.OsmMapBottomDialogFragment;
import com.mobwal.pro.ui.GeoLocationLayout;
import com.mobwal.pro.ui.global.WalkerLocationListeners;

/**
 * Создание точки в маршруте
 */
public class CreateFragment extends Fragment
    implements WalkerLocationListeners, View.OnClickListener {

    private FragmentCreatePointBinding binding;
    private String f_route = null;
    private DataManager mDataManager;
    private OsmMapBottomDialogFragment mOsmMapBottomDialogFragment;
    @Nullable
    private Location mLocation;

    private boolean mLocationRequire = false;
    private String mLocationLevel = GeoLocationLayout.LEVEL;
    private final ActivityResultLauncher<String[]> mPermissionLocationActivityResultLauncher;

    public CreateFragment() {
        // Required empty public constructor
        mPermissionLocationActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            binding.createPointLocation.onPermission(result);
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("location", mLocation);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogManager.getInstance().info("Создание точки.");
        setHasOptionsMenu(true);

        if(savedInstanceState != null) {
            mLocation = savedInstanceState.getParcelable("location");
        }

        mOsmMapBottomDialogFragment = new OsmMapBottomDialogFragment();
        mDataManager = new DataManager(requireContext());

        SettingRoute settingRoute = new SettingRoute(mDataManager.getRouteSettings());
        mLocationRequire = settingRoute.geo;
        mLocationLevel = settingRoute.geo_quality;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCreatePointBinding.inflate(inflater, container, false);
        binding.createPointSave.setOnClickListener(this);

        binding.createPointLocation.setActivityResultLauncherPermission(mPermissionLocationActivityResultLauncher);
        binding.createPointLocation.setOnLocationListeners(this);
        binding.createPointLocation.setLevel(mLocationLevel);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        binding.createPointLocation.onStart(mLocation);

        if(TextUtils.isEmpty(f_route)) {
            binding.createPointSave.setEnabled(false);
            Toast.makeText(requireContext(), R.string.global_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.createPointLocation.onStop();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLocation = location;
        mOsmMapBottomDialogFragment.addLocation(new LocationInfo(location));
    }

    @Override
    public void onLocationClick(View v) {
        mOsmMapBottomDialogFragment.show(requireActivity().getSupportFragmentManager(), "map");
    }

    @Override
    public void onClick(View v) {
        Editable name = binding.createPointName.getText();
        Editable desc = binding.createPointDesc.getText();

        if(name != null && name.length() > 0) {
            if(mLocationRequire && mLocation == null) {
                Toast.makeText(requireContext(), R.string.create_point_error2, Toast.LENGTH_SHORT).show();
            } else {
                String nameTxt = name.toString();

                String descTxt = null;
                if (desc != null) {
                    descTxt = desc.toString();
                }

                binding.createPointSave.setEnabled(false);

                if (!mDataManager.createPoint(f_route, nameTxt, descTxt, mLocation)) {
                    binding.createPointSave.setEnabled(true);
                    Toast.makeText(requireContext(), R.string.create_point_error, Toast.LENGTH_SHORT).show();
                } else {
                    requireActivity().onBackPressed();
                }
            }
        } else {
            binding.createPointName.setError(getString(R.string.validate_empty));
        }
    }
}