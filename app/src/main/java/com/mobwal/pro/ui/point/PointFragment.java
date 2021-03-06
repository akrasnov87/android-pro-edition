package com.mobwal.pro.ui.point;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobwal.android.library.LogManager;
import com.mobwal.pro.DataManager;
import com.mobwal.pro.Names;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.adapter.PointItemAdapter;
import com.mobwal.pro.databinding.FragmentPointBinding;
import com.mobwal.pro.models.db.complex.PointItem;
import com.mobwal.pro.models.db.complex.ResultTemplate;
import com.mobwal.pro.models.db.Route;
import com.mobwal.pro.ui.BaseFragment;
import com.mobwal.pro.ui.RecycleViewItemListeners;
import com.mobwal.pro.ui.RecycleViewItemRemovable;
import com.mobwal.pro.ui.global.ResultChoiceBottomDialogFragment;

import java.io.FileNotFoundException;

public class PointFragment extends BaseFragment
        implements SearchView.OnQueryTextListener,
        RecycleViewItemListeners, View.OnClickListener {

    private static final String QUERY_NAME = "query";
    private String mQuery;

    private FragmentPointBinding binding;
    private String f_route = null;
    private PointItemAdapter mPointItemAdapter;
    private DataManager mDataManager;
    private SearchView mSearchView;
    private String mRouteName;

    private ResultChoiceBottomDialogFragment mResultChoiceBottomDialogFragment;

    @Nullable
    @Override
    public String getSubTitle() {
        return mRouteName;
    }

    public PointFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogManager.getInstance().info("??????????.");
        setHasOptionsMenu(true);

        mDataManager = new DataManager(requireContext());
        mResultChoiceBottomDialogFragment = new ResultChoiceBottomDialogFragment();

        if(savedInstanceState != null) {
            mQuery = savedInstanceState.getString(QUERY_NAME);
        }

        if(getArguments() != null) {
            f_route = getArguments().getString("f_route");

            Route route = mDataManager.getRoute(f_route);
            if (route != null) {
                mRouteName = route.c_name;
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(QUERY_NAME, mQuery);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPointBinding.inflate(inflater, container, false);
        binding.pointList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.pointListCreate.setOnClickListener(this);

        // https://stackoverflow.com/questions/33985719/android-swipe-to-delete-recyclerview
        new ItemTouchHelper(new SwipeToDeleteCallback(requireContext(), 0, ItemTouchHelper.LEFT) {

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                // ?????????????????? ?????????? ???????? ?????????????? ???? ??????????????????
                if(viewHolder instanceof RecycleViewItemRemovable) {
                    RecycleViewItemRemovable holder = (RecycleViewItemRemovable) viewHolder;
                    if(holder.isRemovable()) {
                        return super.getSwipeDirs(recyclerView, viewHolder);
                    }
                }

                return 0;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(R.string.attention);
                builder.setMessage(R.string.point_remove);

                final int position = viewHolder.getBindingAdapterPosition();
                final PointItem pointItem = mPointItemAdapter.getData().get(position);

                builder.setCancelable(false);
                builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                    // ????
                    try {
                        if(pointItem.b_server ? mDataManager.disabledPoint(pointItem.id) : mDataManager.delPoint(pointItem.id)) {
                            mPointItemAdapter.removeItem(position);
                        } else {
                            Toast.makeText(requireContext(), R.string.remove_point_error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                });

                builder.setNegativeButton(R.string.no, (dialog, which) -> {
                    // ??????
                    mPointItemAdapter.removeItem(position);
                    mPointItemAdapter.restoreItem(pointItem, position);
                    binding.pointList.scrollToPosition(position);
                });

                AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return Names.SWIPE_THRESHOLD;
            }
        }).attachToRecyclerView(binding.pointList);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        PointItem[] items = mDataManager.getPoints(f_route, null);
        setEmptyText(items, false);

        bindAdapter(items);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        if(!TextUtils.isEmpty(mQuery)) {
            final String searchText = mQuery;

            mSearchView.post(() -> {
                mSearchView.onActionViewExpanded();
                mSearchView.setQuery(searchText, false);
            });
        }

        mSearchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mQuery = newText;

        PointItem[] items = mDataManager.getPoints(f_route, mQuery);
        setEmptyText(items, true);
        bindAdapter(items);

        return false;
    }

    @Override
    public void onViewItemInfo(String id) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        Bundle bundle = new Bundle();
        bundle.putString("f_point", id);
        navController.navigate(R.id.nav_point_info, bundle);
    }

    @Override
    public void onViewItemClick(String id) {
        ResultTemplate[] resultTemplates = mDataManager.getResultTemplates(f_route, id);

        if(resultTemplates != null) {
            if(resultTemplates.length > 1) {
                mResultChoiceBottomDialogFragment.setPoint(f_route, id);
                mResultChoiceBottomDialogFragment.show(requireActivity().getSupportFragmentManager(), "choice");
            } else if(resultTemplates.length == 1) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                Bundle bundle = new Bundle();
                bundle.putString("f_route", f_route);
                bundle.putString("f_point", id);

                bundle.putString("f_result", resultTemplates[0].f_result);
                bundle.putString("c_template", resultTemplates[0].c_const);
                bundle.putString("f_template", resultTemplates[0].f_template);

                navController.navigate(R.id.nav_result, bundle);
            }
        }
    }

    private void setEmptyText(@Nullable PointItem[] items, boolean isSearch) {
        binding.pointList.setVisibility(items == null || items.length == 0 ? View.GONE : View.VISIBLE);
        binding.pointListEmpty.setVisibility(items == null || items.length == 0 ? View.VISIBLE : View.GONE);

        if(isSearch) {
            binding.pointListEmpty.setText(R.string.search_not_result);
        } else {
            binding.pointListEmpty.setText(R.string.point_list_empty);
        }
    }

    private void bindAdapter(PointItem[] items) {
        mPointItemAdapter = new PointItemAdapter(requireContext(), items, this);
        binding.pointList.setAdapter(mPointItemAdapter);
    }

    @Override
    public void onClick(View v) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        Bundle bundle = new Bundle();
        bundle.putString("f_route", f_route);
        navController.navigate(R.id.nav_create_point, bundle);
    }
}