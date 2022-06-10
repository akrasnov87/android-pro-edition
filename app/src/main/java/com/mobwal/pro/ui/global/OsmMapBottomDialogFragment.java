package com.mobwal.pro.ui.global;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobwal.android.library.LogManager;
import com.mobwal.pro.R;
import com.mobwal.pro.databinding.OsmMapBottomSheetBinding;
import com.mobwal.pro.models.LocationInfo;
import com.mobwal.pro.utilits.OsmDroidUtil;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Objects;

/**
 * Вывод Google карты
 */
public class OsmMapBottomDialogFragment extends BottomSheetDialogFragment {

    private final static String LOCATION_NAME = "location";

    private OsmMapBottomSheetBinding binding;
    private LocationInfo mLocationInfo;
    private Marker mMarker;

    public void addLocation(@NonNull LocationInfo locationInfo) {
        mLocationInfo = locationInfo;

        if(locationInfo.isValidLocations() && isAdded()) {
            addMarkerAndMove(locationInfo);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogManager.getInstance().info("Вывод всплывающей карты.");

        if(savedInstanceState != null) {
            mLocationInfo = (LocationInfo) savedInstanceState.getSerializable(LOCATION_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = OsmMapBottomSheetBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Objects.requireNonNull(binding.bottomOsm).setVisibility(View.VISIBLE);
            OsmDroidUtil.enableCompass(requireContext(), binding.bottomOsm);

            if(mLocationInfo != null && mLocationInfo.isValidLocations()) {
                addMarkerAndMove(mLocationInfo);
            }
        } else {
            Objects.requireNonNull(binding.bottomOsm).setVisibility(View.GONE);
            LogManager.getInstance().info("Доступ к геолокации не предоставлен.");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(LOCATION_NAME, mLocationInfo);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Добавление точек на карту
     * @param locationInfo геолокации
     */
    private void addMarkerAndMove(@NonNull LocationInfo locationInfo) {
        if(mMarker != null) {
            mMarker.remove(binding.bottomOsm);
            mMarker = null;
        }

        GeoPoint geoPoint = locationInfo.convertToLatLng(LocationInfo.MY);
        mMarker = createOnMarker(requireContext(),
                binding.bottomOsm,
                geoPoint,
                getString(R.string.my_coordinate)
        );
    }

    /**
     * Создание одиночной метки на карте
     * @param context контекст
     * @param view карта
     * @param point точка
     * @param title заголовок
     * @return метка
     */

    @Nullable
    private Marker createOnMarker(@NonNull Context context, @NonNull MapView view, GeoPoint point, String title) {
        if(point == null) {
            return null;
        }

        Marker marker = new Marker(view);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        marker.setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_baseline_location_on_48_blue, null));

        marker.showInfoWindow();

        view.getOverlays().add(marker);

        view.getController().setZoom(18.0f);
        view.getController().setCenter(point);

        return marker;
    }
}