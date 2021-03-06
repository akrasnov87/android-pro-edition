package com.mobwal.pro.ui.point;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.android.material.snackbar.Snackbar;
import com.mobwal.android.library.LogManager;
import com.mobwal.pro.DataManager;
import com.mobwal.pro.R;
import com.mobwal.pro.adapter.PointInfoItemAdapter;
import com.mobwal.pro.databinding.FragmentPointInfoBinding;
import com.mobwal.pro.models.PointInfo;
import com.mobwal.pro.models.SettingRoute;
import com.mobwal.pro.models.db.Point;
import com.mobwal.pro.models.db.Result;
import com.mobwal.pro.models.db.Template;
import com.mobwal.pro.ui.RecycleViewItemListeners;
import com.mobwal.pro.utilits.ActivityUtil;
import com.mobwal.pro.utilits.OsmDroidUtil;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

/**
 * информация по точке
 */
public class InfoFragment extends Fragment
        implements LocationListener, RecycleViewItemListeners {

    private final static String LOCATION = "location";

    private FragmentPointInfoBinding binding;
    private String f_point = null;
    private String f_result = null;
    private DataManager mDataManager;
    private PointInfoItemAdapter mPointInfoItemAdapter;
    private LocationManager mLocationManager;
    private final List<Marker> mMarkers;
    private Location mLocation;
    private MenuItem mDeleteMenuItem;
    private Template[] mTemplates;

    @Nullable
    private Result[] mResults;

    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    @Nullable
    private final ActivityResultLauncher<String[]> mPermissionActivityResultLauncher;

    public InfoFragment() {
        // Required empty public constructor
        mMarkers = new ArrayList<>();

        mPermissionActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), this::onPermission);
    }

    public void onPermission(Map<String, Boolean> result) {
        boolean areAllGranted = true;
        for (Boolean b : result.values()) {
            areAllGranted = areAllGranted && b;
        }

        if (!areAllGranted) {
            String message = ActivityUtil.getMessageNotGranted(requireContext(), new String[] { requireContext().getString(R.string.location) });
            Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).setAction(requireContext().getString(R.string.more), view -> requireContext().startActivity(ActivityUtil.getIntentApplicationSetting(requireContext()))).show();
        } else {
            startUpdates();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        LogManager.getInstance().info("Точки. Информация.");
        mLocationManager = (LocationManager) requireContext().getSystemService(LOCATION_SERVICE);

        if(savedInstanceState != null) {
            mLocation = savedInstanceState.getParcelable(LOCATION);
        }

        mDataManager = new DataManager(requireContext());

        if (getArguments() != null) {
            f_point = getArguments().getString("f_point");
            f_result = getArguments().getString("f_result");

            mResults = mDataManager.getResults(f_point);

            mTemplates = mDataManager.getTemplates();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(LOCATION, mLocation);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPointInfoBinding.inflate(inflater, container, false);
        binding.pointInfoList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.osmPointInfoListMap.getController().setZoom(18.0f);

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();

        if(f_result == null && mResults != null && mResults.length > 0) {
            inflater.inflate(R.menu.clear_menu, menu);

            mDeleteMenuItem = menu.findItem(R.id.action_result_delete);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_result_delete) {

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(R.string.attention);
            builder.setMessage(R.string.results_remove);

            builder.setCancelable(false);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                // ДА
                if(mResults != null) {
                    boolean b = true;
                    for (Result result : mResults) {
                        if (result.b_server ? mDataManager.disabledResult(result.id) : mDataManager.delResult(result.id)) {
                            List<PointInfo> items = mPointInfoItemAdapter.getData();
                            int i = 0;
                            for (PointInfo pointInfo : items) {
                                if (pointInfo.result != null && pointInfo.result.equals(result.id)) {
                                    mPointInfoItemAdapter.removeItem(i);
                                    break;
                                }
                                i++;
                            }
                        } else {
                            if (b) {
                                b = false;
                            }
                        }
                    }

                    if (!b) {
                        Toast.makeText(requireContext(), R.string.remove_result_error, Toast.LENGTH_SHORT).show();
                    }

                    updateResults();
                    updateLocations(mLocation);
                }
            });
            builder.setNegativeButton(R.string.no, null);

            AlertDialog alert = builder.create();
            alert.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onStart() {
        super.onStart();

        PointInfo[] items = mDataManager.getPointInfo(f_point);

        if (items != null && items.length > 0) {
            updateLocations(mLocation);
        }

        mPointInfoItemAdapter = new PointInfoItemAdapter(requireContext(), items, this);
        binding.pointInfoList.setAdapter(mPointInfoItemAdapter);
    }

    @SuppressLint("MissingPermission")
    private void startUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LogManager.getInstance().debug("Точки. Информация. Доступ к геолокации не предоставлен.");
            if(mPermissionActivityResultLauncher != null) {
                mPermissionActivityResultLauncher.launch(REQUIRED_PERMISSIONS);
            }

            //binding.pointInfoList.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            OsmDroidUtil.enableCompass(requireContext(), binding.osmPointInfoListMap);
            mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 1, this);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        startUpdates();
        Objects.requireNonNull(binding.osmPointInfoListMap).onResume();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onPause() {
        super.onPause();

        mLocationManager.removeUpdates(this);
        Objects.requireNonNull(binding.osmPointInfoListMap).onPause();
    }

    /**
     * Обновление меток на карте
     * @param location текущее местоположение
     */
    private void updateLocations(@Nullable Location location) {
        clearMarkers();

        List<GeoPoint> points = new ArrayList<>();
        Point pointItem = mDataManager.getPoint(f_point);
        Marker pointMarker;

        // точка задания
        if(pointItem != null) {
            GeoPoint point = pointItem.convertToLatLng();
            points.add(point);

            if(point != null) {
                pointMarker = new Marker(binding.osmPointInfoListMap);
                pointMarker.setPosition(point);
                pointMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                pointMarker.setTitle(pointItem.c_address);
                pointMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_location_on_48_red, null));

                binding.osmPointInfoListMap.getOverlays().add(pointMarker);

                mMarkers.add(pointMarker);
            }
        }

        Result[] results = mDataManager.getResults(f_point);
        if(results != null && results.length > 0) {
            for (Result result: results) {
                GeoPoint point = result.convertToLatLng();
                points.add(point);
                if(point != null) {
                    Marker marker = new Marker(binding.osmPointInfoListMap);
                    marker.setPosition(point);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_location_on_48_green, null));

                    String templateName = requireContext().getString(R.string.template_default);
                    for (Template template:
                            mTemplates) {
                        if(template.id.equals(result.fn_template)) {
                            templateName = template.c_name;
                            break;
                        }
                    }

                    marker.setTitle(templateName);

                    binding.osmPointInfoListMap.getOverlays().add(marker);

                    mMarkers.add(marker);
                }
            }
        }

        // текущее местоположение
        if(location != null) {
            GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
            points.add(point);

            Marker marker = new Marker(binding.osmPointInfoListMap);
            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(getString(R.string.my_coordinate));
            marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_location_on_48_blue, null));

            binding.osmPointInfoListMap.getOverlays().add(marker);

            marker.showInfoWindow();

            mMarkers.add(marker);
        }


        if(mMarkers.size() > 1) {
            BoundingBox bb = OsmDroidUtil.toBoxing(points);
            //binding.osmPointInfoListMap.zoomToBoundingBox(OsmDroidUtil.toBoxing(points), true);
            binding.osmPointInfoListMap.getController().setCenter(bb.getCenterWithDateLine());
            binding.osmPointInfoListMap.zoomToBoundingBox(bb, false);
        } else {
            // если одна точка
            if(mMarkers.size() == 1 && pointItem != null) {
                GeoPoint point = pointItem.convertToLatLng();
                mMarkers.get(0).showInfoWindow();

                binding.osmPointInfoListMap.getController().setZoom(18.0f);
                binding.osmPointInfoListMap.getController().setCenter(point);
            }
        }
    }

    /**
     * Обработчик нажатия на результат
     * @param id идентификатор результата
     */
    @Override
    public void onViewItemClick(String id) {
        Result result = mDataManager.getResult(id);
        if(result != null) {
            GeoPoint point = result.convertToLatLng();
            if(point != null) {
                binding.osmPointInfoListMap.getController().setZoom(18.0f);
                binding.osmPointInfoListMap.getController().setCenter(point);
            }
        }
    }

    /**
     * Обработчик удаления
     * @param id позиция в списке
     */
    @Override
    public void onViewItemInfo(String id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.attention);
        builder.setMessage(R.string.result_remove);

        final int position = Integer.parseInt(id);
        final PointInfo pointItem = mPointInfoItemAdapter.getData().get(position);

        builder.setCancelable(false);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            // ДА
            if (pointItem.result != null) {
                if(pointItem.server) {
                    mDataManager.disabledResult(pointItem.result);
                } else {
                    mDataManager.delResult(pointItem.result);
                }

                mPointInfoItemAdapter.removeItem(position);

                updateLocations(mLocation);
                updateResults();
            } else {
                Toast.makeText(requireContext(), R.string.remove_result_error, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.no, null);

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLocation = location;
        updateLocations(mLocation);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    /**
     * Обновление массива с результатами
     */
    private void updateResults() {
        mResults = mDataManager.getResults(f_point);
        if(mDeleteMenuItem != null) {
            if (!(f_result != null && mResults != null && mResults.length > 0)) {
                mDeleteMenuItem.setVisible(false);
            }
        }
    }

    /**
     * Очистка меток на карте
     */
    private void clearMarkers() {
        if(mMarkers.size() > 0) {
            for (Marker marker: mMarkers) {
                marker.remove(binding.osmPointInfoListMap);
            }
            mMarkers.clear();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}