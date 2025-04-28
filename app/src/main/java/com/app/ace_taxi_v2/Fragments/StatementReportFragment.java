package com.app.ace_taxi_v2.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.ace_taxi_v2.Fragments.Adapters.StatementAdapter;
import com.app.ace_taxi_v2.Logic.GetStatementsApi;
import com.app.ace_taxi_v2.Models.Reports.StatementItem;
import com.app.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatementReportFragment extends Fragment {

    private TextView totalEarn;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialTextView noDataText;
    private List<StatementItem> statementList = new ArrayList<>();
    private StatementAdapter statementAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statement_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI Components
        recyclerView = view.findViewById(R.id.recycler_view);
        totalEarn = view.findViewById(R.id.totalAmount);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        noDataText = view.findViewById(R.id.no_data_text);
        MaterialToolbar toolbar = view.findViewById(R.id.header_toolbar);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        statementAdapter = new StatementAdapter(getContext(), statementList);
        recyclerView.setAdapter(statementAdapter);

        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::fetchStatements);

        // Set toolbar navigation
        toolbar.setNavigationOnClickListener(v -> navigateToReportPage());

        // Load Data Initially
        fetchStatements();
    }

    private void fetchStatements() {
        swipeRefreshLayout.setRefreshing(true);

        GetStatementsApi getStatementsApi = new GetStatementsApi(getContext());
        getStatementsApi.getStatements(new GetStatementsApi.statementListener() {
            @Override
            public void onSuccess(List<StatementItem> items) {
                statementAdapter.updateData(items);
                calculateTotalEarnings(items);
                updateEmptyView();
                if (items == null || items.isEmpty()) {
                    Toast.makeText(getContext(), "No statements found", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getContext(), "Failed to fetch statements: " + error, Toast.LENGTH_SHORT).show();
                statementAdapter.updateData(null);
                calculateTotalEarnings(null);
                updateEmptyView();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void calculateTotalEarnings(List<StatementItem> items) {
        double total = 0;
        if (items != null) {
            for (StatementItem item : items) {
                total += item.getTotalEarned();
            }
        }
        totalEarn.setText(String.format(Locale.getDefault(), "£%.2f", total));
    }

    private void updateEmptyView() {
        if (statementAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            noDataText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noDataText.setVisibility(View.GONE);
        }
    }

    private void navigateToReportPage() {
        Fragment selectedFragment = new ReportPageFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}