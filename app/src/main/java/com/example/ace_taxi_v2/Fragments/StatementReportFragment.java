package com.example.ace_taxi_v2.Fragments;

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

import com.example.ace_taxi_v2.Fragments.Adapters.StatementAdapter;
import com.example.ace_taxi_v2.Logic.GetStatementsApi;
import com.example.ace_taxi_v2.Models.Reports.StatementItem;
import com.example.ace_taxi_v2.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class StatementReportFragment extends Fragment {

    private TextView totalEarn;
    private RecyclerView recyclerView;
    private List<StatementItem> statementList = new ArrayList<>();

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
        MaterialToolbar toolbar = view.findViewById(R.id.header_toolbar);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load Data Initially
        fetchStatements(view);

        // Toolbar Navigation Click Listener
        toolbar.setNavigationOnClickListener(v -> navigateToReportPage());
    }

    private void fetchStatements(View view) {
        GetStatementsApi getStatementsApi = new GetStatementsApi(getContext());
        getStatementsApi.getStatements(view, recyclerView, new GetStatementsApi.statementListener() {
            @Override
            public void onSuccess(List<StatementItem> items) {
                if (items != null && !items.isEmpty()) {
                    statementList.clear();
                    statementList.addAll(items);
                    calculateTotalEarnings(items);
                } else {
                    Toast.makeText(getContext(), "No statements found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getContext(), "Failed to fetch statements: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateTotalEarnings(List<StatementItem> items) {
        double total = 0;
        for (StatementItem item : items) {
            total += item.getTotalEarned(); // Ensure `getAmount()` returns a double
        }
        totalEarn.setText(String.format("Total: £%.2f", total));
    }

    private void navigateToReportPage() {
        Fragment selectedFragment = new ReportPageFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
        fragmentTransaction.addToBackStack(null); // Allows back navigation
        fragmentTransaction.commit();
    }
}
