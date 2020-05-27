package com.google.firebase.example.fitnessshark;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.example.fitnessshark.adapter.WorkoutAdapter;
import com.google.firebase.example.fitnessshark.model.WorkoutPlan;
import com.google.firebase.example.fitnessshark.util.WorkoutPlanUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;


import com.google.firebase.example.fitnessshark.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment implements
        View.OnClickListener,
        FilterDialogFragment.FilterListener,
        WorkoutAdapter.OnWorkoutSelectedListener {

    private static final String TAG = "HomeFragment";

    private static final int LIMIT = 50;

    private Toolbar mToolbar;
    private CardView mFilterBar;
    private ImageView mButtonClearFilter;
    private TextView mCurrentSearchView;
    private TextView mCurrentSortByView;
    private RecyclerView mWorkoutRecycler;
    private ViewGroup mEmptyView;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private FilterDialogFragment mFilterDialog;
    private WorkoutAdapter mAdapter;

    private HomeViewModel mViewModel;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.home_fragment, container, false);

        mToolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        mCurrentSearchView = v.findViewById(R.id.text_current_search);
        mCurrentSortByView = v.findViewById(R.id.text_current_sort_by);
        mWorkoutRecycler = v.findViewById(R.id.recycler_workouts);
        mEmptyView = v.findViewById(R.id.view_empty);

        mFilterBar = v.findViewById(R.id.filter_bar);
        mFilterBar.setOnClickListener(this);

        mButtonClearFilter = v.findViewById(R.id.button_clear_filter);
        mButtonClearFilter.setOnClickListener(this);

        // View model
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Initialize Firestore and the main RecyclerView
        initFirestore();
        initRecyclerView();

        // Filter Dialog
        mFilterDialog = new FilterDialogFragment();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initFirestore() {
    mFirestore = FirebaseFirestore.getInstance();

    mQuery = mFirestore.collection("workouts")
            .orderBy("name", Query.Direction.DESCENDING)
            .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new WorkoutAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mWorkoutRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mWorkoutRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
//              Show a snackbar on errors
                Snackbar.make(getView().findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

         mWorkoutRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity()));
         mWorkoutRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Apply filters
        onFilter(mViewModel.getFilters());

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onFilter(WorkoutFilters filters) {
        // Construct query basic query
        Query query = mFirestore.collection("workouts");

        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereEqualTo("category", filters.getCategory());
        }

        // City (equality filter)
        if (filters.hasDifficulty()) {
            query = query.whereEqualTo("difficulty", filters.getDifficulty());
        }

        // Price (equality filter)
        if (filters.hasDuration()) {
            query = query.whereGreaterThan("duration", filters.getDuration());
        }

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        // Limit items
        query = query.limit(LIMIT);

        // Update the query
        mQuery = query;
        mAdapter.setQuery(query);

        // Set header
        mCurrentSearchView.setText(Html.fromHtml(filters.getSearchDescription(this.getActivity())));
        mCurrentSortByView.setText(filters.getOrderDescription(this.getActivity()));

        // Save filters
        mViewModel.setFilters(filters);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter_bar:
                onFilterClicked();
                break;
            case R.id.button_clear_filter:
                onClearFilterClicked();
        }
    }

    public void onFilterClicked() {
        // Show the dialog containing filter options
        mFilterDialog.show(this.getActivity().getSupportFragmentManager(), FilterDialogFragment.TAG);
    }

    public void onClearFilterClicked() {
        mFilterDialog.resetFilters();

        onFilter(WorkoutFilters.getDefault());
    }

    @Override
    public void onWorkoutSelected(DocumentSnapshot workoutPlan) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this.getContext(), WorkoutDetailActivity.class);
        intent.putExtra(WorkoutDetailActivity.KEY_WORKOUT_PLAN_ID, workoutPlan.getId());

        startActivity(intent);
    }
}
