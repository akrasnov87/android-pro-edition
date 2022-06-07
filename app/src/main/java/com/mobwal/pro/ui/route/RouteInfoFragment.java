package com.mobwal.pro.ui.route;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobwal.pro.DataManager;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.adapter.RouteInfoCategoryAdapter;
import com.mobwal.pro.databinding.FragmentRouteInfoBinding;
import com.mobwal.pro.models.RouteInfo;

/**
 * Информация по маршруту
 */
public class RouteInfoFragment extends Fragment {

    private FragmentRouteInfoBinding binding;
    @Nullable
    private String f_route = null;
    private RouteInfoCategoryAdapter mRouteInfoCategoryAdapter;
    private DataManager mDataManager;

    public RouteInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        WalkerApplication.Log("Маршруты. Информация.");

        if(getArguments() != null) {
            f_route = getArguments().getString("f_route");
        }

        mDataManager = new DataManager(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRouteInfoBinding.inflate(inflater, container, false);
        binding.routeInfo.setLayoutManager(new LinearLayoutManager(requireContext()));
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        RouteInfo[][] info = mDataManager.getRouteInfo(f_route);

        mRouteInfoCategoryAdapter = new RouteInfoCategoryAdapter(requireContext(), info);
        binding.routeInfo.setAdapter(mRouteInfoCategoryAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        // обновляется история после изменения
        RouteInfo[][] info = mDataManager.getRouteInfo(f_route);
        mRouteInfoCategoryAdapter.updateItem(info[0], 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}