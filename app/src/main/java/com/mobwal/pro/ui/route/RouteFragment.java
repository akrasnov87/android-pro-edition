package com.mobwal.pro.ui.route;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.text.HtmlCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.text.MessageFormat;

import com.mobwal.android.library.LogManager;
import com.mobwal.pro.DataManager;
import com.mobwal.pro.Names;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.adapter.RouteItemAdapter;
import com.mobwal.pro.databinding.FragmentRouteBinding;
import com.mobwal.pro.models.db.complex.RouteItem;
import com.mobwal.pro.ui.BaseFragment;
import com.mobwal.pro.ui.RecycleViewItemListeners;

public class RouteFragment extends BaseFragment
        implements SearchView.OnQueryTextListener,
        RecycleViewItemListeners {

    private static final String QUERY_NAME = "query";
    private String mQuery;

    private FragmentRouteBinding binding;
    private RouteItemAdapter mRouteItemAdapter;
    private DataManager mDataManager;
    private SearchView mSearchView;

    public RouteFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        LogManager.getInstance().info("Маршруты.");
        if(savedInstanceState != null) {
            mQuery = savedInstanceState.getString(QUERY_NAME);
        }

        mDataManager = new DataManager(requireContext());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(QUERY_NAME, mQuery);
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

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRouteBinding.inflate(inflater, container, false);
        binding.routeList.setLayoutManager(new LinearLayoutManager(requireContext()));

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        updateRoutes();
    }

    private void updateRoutes() {
        RouteItem[] items = mDataManager.getRoutes(null);
        setEmptyText(items, false);

        bindAdapter(items);
    }

    private void setEmptyText(@Nullable RouteItem[] items, boolean isSearch) {
        binding.routeList.setVisibility(items == null || items.length == 0 ? View.GONE : View.VISIBLE);
        binding.routeListEmpty.setVisibility(items == null || items.length == 0 ? View.VISIBLE : View.GONE);

        if(isSearch) {
            binding.routeListEmpty.setText(R.string.search_not_result);
        } else {
            String html = getString(R.string.route_list_empty) + "<p>" + MessageFormat.format(getString(R.string.create_route_docs), "<a href=\""+Names.ROUTE_DOCS+"\">" + Names.HOME_PAGE + "</a>") + "</p>";
            binding.routeListEmpty.setText(HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY));
            binding.routeListEmpty.setMovementMethod(LinkMovementMethod.getInstance());
        }
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
        RouteItem[] items = mDataManager.getRoutes(mQuery);
        setEmptyText(items, true);
        bindAdapter(items);

        return false;
    }

    @Override
    public void onViewItemInfo(String id) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        Bundle bundle = new Bundle();
        bundle.putString("f_route", id);
        navController.navigate(R.id.nav_route_info, bundle);
    }

    @Override
    public void onViewItemClick(String id) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        Bundle bundle = new Bundle();
        bundle.putString("f_route", id);
        navController.navigate(R.id.nav_point, bundle);
    }

    private void bindAdapter(@Nullable RouteItem[] items) {
        mRouteItemAdapter = new RouteItemAdapter(requireContext(), this, items);
        binding.routeList.setAdapter(mRouteItemAdapter);
    }
}