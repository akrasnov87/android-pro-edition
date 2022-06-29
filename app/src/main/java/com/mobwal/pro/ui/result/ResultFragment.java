package com.mobwal.pro.ui.result;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.UUID;

import com.mobwal.android.library.LogManager;
import com.mobwal.android.library.authorization.BasicAuthorizationSingleton;
import com.mobwal.android.library.data.DbOperationType;
import com.mobwal.pro.DataManager;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.databinding.FragmentResultBinding;
import com.mobwal.pro.models.LocationInfo;
import com.mobwal.pro.models.PointBundle;
import com.mobwal.pro.models.SettingRoute;
import com.mobwal.pro.models.db.Attachment;
import com.mobwal.pro.models.db.Point;
import com.mobwal.pro.models.db.Result;
import com.mobwal.pro.models.db.Route;
import com.mobwal.pro.models.db.Template;
import com.mobwal.pro.ui.BaseFragment;
import com.mobwal.pro.ui.global.OsmMapBottomDialogFragment;
import com.mobwal.pro.ui.GeoLocationLayout;
import com.mobwal.pro.ui.global.WalkerLocationListeners;
import com.mobwal.android.library.util.JsonUtil;

public class ResultFragment extends BaseFragment
        implements WalkerLocationListeners, View.OnClickListener {

    private Attachment[] mItems;

    private ActivityResultLauncher<Intent> mChoiceActivityResultLauncher;
    private final ActivityResultLauncher<String[]> mPermissionGalleryActivityResultLauncher;
    private final ActivityResultLauncher<String[]> mPermissionLocationActivityResultLauncher;

    private FragmentResultBinding binding;
    private DataManager mDataManager;
    private OsmMapBottomDialogFragment mGoogleMapBottomDialogFragment;

    private boolean mLocationRequire = false;
    private boolean mImageRequire = false;
    private boolean mCameraOnly = false;
    private boolean mPhotoLabel = false;

    private String mLocationLevel = GeoLocationLayout.LEVEL;
    @Nullable
    private Location mLocation;

    private String mAttachmentFileName;

    private String f_route;
    private String f_point;
    @Nullable
    private Point mPoint;
    private String f_result;

    @Nullable
    private Result mResult;

    private String c_template;

    private String f_template;

    @Nullable
    private Template mTemplate;

    private final String id = UUID.randomUUID().toString();

    @Nullable
    @Override
    public String getSubTitle() {
        return mTemplate != null ? mTemplate.c_name : null;
    }

    public ResultFragment() {
        // Required empty public constructor

        // разрешения для фото
        mPermissionGalleryActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> binding.createResultGallery.onPermission(result));

        // разрешения для геолокации
        mPermissionLocationActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> binding.createResultLocation.onPermission(result));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogManager.getInstance().info("Результат.");
        setHasOptionsMenu(true);

        if(savedInstanceState != null) {
            mLocation = savedInstanceState.getParcelable("location");
            mItems = (Attachment[]) savedInstanceState.getSerializable("items");
            mAttachmentFileName = savedInstanceState.getString("fileName");
        }

        mDataManager = new DataManager(requireContext());
        mGoogleMapBottomDialogFragment = new OsmMapBottomDialogFragment();

        if(getArguments() != null) {
            f_route = getArguments().getString("f_route");
            f_point = getArguments().getString("f_point");
            f_result = getArguments().getString("f_result");
            c_template = getArguments().getString("c_template");
            f_template = getArguments().getString("f_template");

            SettingRoute settingRoute = new SettingRoute(mDataManager.getRouteSettings());
            mLocationRequire = settingRoute.geo;
            mLocationLevel = settingRoute.geo_quality;
            mImageRequire = settingRoute.image;
            mCameraOnly = settingRoute.camera_only;
            mPhotoLabel = settingRoute.photo_label;

            mTemplate = mDataManager.getTemplate(c_template);
            mPoint = mDataManager.getPoint(f_point);
            mResult = mDataManager.getResult(f_result);
        }

        mChoiceActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> binding.createResultGallery.onActivityResult(result));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("location", mLocation);

        outState.putSerializable("items", mItems);
        outState.putString("fileName", mAttachmentFileName);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);
        binding.createResultSave.setOnClickListener(this);
        binding.createResultGallery.setActivityResultLauncherChoice(mChoiceActivityResultLauncher);
        binding.createResultGallery.setActivityResultLauncherPermission(mPermissionGalleryActivityResultLauncher);
        binding.createResultGallery.FileName = mAttachmentFileName;
        binding.createResultGallery.IsCameraOnly = mCameraOnly;
        binding.createResultGallery.IsPhotoLabel = mPhotoLabel;
        if(mPoint != null) {
            binding.createResultGallery.Address = mPoint.c_address;
        }

        binding.createResultLocation.setActivityResultLauncherPermission(mPermissionLocationActivityResultLauncher);
        binding.createResultLocation.setOnLocationListeners(this);
        binding.createResultLocation.setLevel(mLocationLevel);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(mResult != null && mDataManager.getResult(f_result) == null) {
            // Значит результат удален
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.popBackStack();
        }

        if(mTemplate != null) {
            binding.createResultForm.init(mTemplate.c_layout, new Hashtable<>());
        }

        binding.createResultSave.setEnabled(mPoint != null);
        binding.createResultCheck.bind(mPoint);

        binding.createResultGallery.setData(mItems == null ? mDataManager.getAttachments(f_result) : mItems);
        binding.createResultGallery.setPointBundle(new PointBundle(f_route, f_point, f_result));

        if(mResult != null) {
            binding.createResultLocation.setVisibility(View.GONE);
            binding.createResultForm.setValues(JsonUtil.toHashObject(mResult.jb_data));
        }

        if(mPoint != null) {
            binding.createResultCheck.setVisibility(mPoint.b_check ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        binding.createResultLocation.onStart(mLocation);
        putMapLocation(mPoint, mLocation);
    }

    @Override
    public void onStop() {
        super.onStop();

        binding.createResultLocation.onStop();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.info_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_info) {

            mItems = binding.createResultGallery.getData();
            mAttachmentFileName = binding.createResultGallery.FileName;

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            Bundle bundle = new Bundle();
            bundle.putString("f_point", f_point);
            bundle.putString("f_result", f_result);
            navController.navigate(R.id.nav_point_info, bundle);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(mPoint == null) {
            return;
        }

        if(mImageRequire && binding.createResultGallery.getData().length == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(R.string.attention);
            builder.setMessage(R.string.result_save_error4);

            builder.setCancelable(false);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                // ДА
                binding.createResultGallery.onLongClick(null);
            });
            builder.setNegativeButton(R.string.no, null);

            AlertDialog alert = builder.create();
            alert.show();

            return;
        }

        if(mLocationRequire && mLocation == null) {
            Toast.makeText(requireContext(), R.string.result_save_error5, Toast.LENGTH_LONG).show();
            return;
        }

        // получение данных с формы
        String jb_data = JsonUtil.toString(binding.createResultForm.getValues());

        Result item = mResult == null
                ? new Result(id, f_route, f_point, c_template, mLocation, mPoint)
                : mResult;
        item.fn_user = BasicAuthorizationSingleton.getInstance().getUser().getUserId();
        item.fn_template = f_template;
        item.__OBJECT_OPERATION_TYPE = item.b_server ? DbOperationType.UPDATED : DbOperationType.CREATED;
        item.__IS_SYNCHRONIZATION = false;

        item.jb_data = jb_data;

        String txt = "";

        binding.createResultSave.setEnabled(false);

        if(mDataManager.addResult(item)) {
            if (binding.createResultGallery.saveData(item.id)) {
                requireActivity().onBackPressed();
            } else {
                txt = getString(R.string.result_save_error1);
            }
        } else {
            txt = getString(R.string.result_save_error2);
        }

        if(!TextUtils.isEmpty(txt)) {
            Toast.makeText(requireContext(), txt, Toast.LENGTH_SHORT).show();
            binding.createResultSave.setEnabled(true);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLocation = location;
        putMapLocation(mPoint, location);
    }

    @Override
    public void onLocationClick(View v) {
        mGoogleMapBottomDialogFragment.show(requireActivity().getSupportFragmentManager(), "map");
    }

    /**
     * Установка гелокации на карте
     * @param point точка маршрута
     * @param location геолокация
     */
    private void putMapLocation(@Nullable Point point, @Nullable Location location) {
        if(point != null) {
            LocationInfo locationInfo = new LocationInfo(location);
            locationInfo.taskLatitude = point.n_latitude;
            locationInfo.taskLongitude = point.n_longitude;

            mGoogleMapBottomDialogFragment.addLocation(locationInfo);
            binding.createResultGallery.setLocationInfo(locationInfo);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding.createResultForm.onDestroy();
        binding = null;
    }
}