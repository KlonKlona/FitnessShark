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
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.example.fitnessshark.adapter.RestaurantAdapter;
import com.google.firebase.example.fitnessshark.model.UserProfile;
import com.google.firebase.example.fitnessshark.model.WorkoutPlan;
import com.google.firebase.example.fitnessshark.util.WorkoutPlanUtil;
import com.google.firebase.example.fitnessshark.viewmodel.MainActivityViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Collections;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GithubAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PlayGamesAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.TimeUnit;


import com.google.firebase.example.fitnessshark.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment implements
        View.OnClickListener,
        FilterDialogFragment.FilterListener,
        RestaurantAdapter.OnRestaurantSelectedListener {

    private static final String TAG = "HomeFragment";

    private static final int RC_SIGN_IN = 9001;

    private static final int LIMIT = 50;

    public static final int RESULT_OK = -1;

    private Toolbar mToolbar;
    private CardView mFilterBar;
    private ImageView mButtonClearFilter;
    private TextView mCurrentSearchView;
    private TextView mCurrentSortByView;
    private RecyclerView mRestaurantsRecycler;
    private ViewGroup mEmptyView;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private FilterDialogFragment mFilterDialog;
    private RestaurantAdapter mAdapter;

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
        mRestaurantsRecycler = v.findViewById(R.id.recycler_restaurants);
        mEmptyView = v.findViewById(R.id.view_empty);

        mFilterBar = v.findViewById(R.id.filter_bar);
        mFilterBar.setOnClickListener(this);

        mButtonClearFilter = v.findViewById(R.id.button_clear_filter);
        mButtonClearFilter.setOnClickListener(this);

        // View model
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

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

    // Get the 50 highest rated restaurants
    mQuery = mFirestore.collection("workoutPlans")
            .orderBy("avgRating", Query.Direction.DESCENDING)
            .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new RestaurantAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mRestaurantsRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRestaurantsRecycler.setVisibility(View.VISIBLE);
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

         mRestaurantsRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity()));
         mRestaurantsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }

        // Complete profile if necessary
        if (shouldCompleteProfile()) {
            startCompleteProfile();
            return;
        }

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

    private void onAddItemsClicked() {
        // Get a reference to the restaurants collection
        CollectionReference workoutPlans = mFirestore.collection("workoutPlans");

        for (int i = 0; i < 10; i++) {
            // Get a random Restaurant POJO
            WorkoutPlan workoutPlan = WorkoutPlanUtil.getRandom(this.getActivity());

            // Add a new document to the restaurants collection
            workoutPlans.add(workoutPlan);
        }
    }

    @Override
    public void onFilter(Filters filters) {
        // Construct query basic query
        Query query = mFirestore.collection("workoutPlans");

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_items:
                onAddItemsClicked();
                break;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this.getActivity());
                startSignIn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);

            if (resultCode != this.getActivity().RESULT_OK && shouldStartSignIn()) {
                startSignIn();
            }
        }
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

        onFilter(Filters.getDefault());
    }

    @Override
    public void onRestaurantSelected(DocumentSnapshot restaurant) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this.getContext(), WorkoutPlanDetailActivity.class);
        intent.putExtra(WorkoutPlanDetailActivity.KEY_RESTAURANT_ID, restaurant.getId());

        startActivity(intent);
    }

    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private boolean shouldCompleteProfile() {
        return (!mViewModel.getIsProfileCompleted());
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intentAuth = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intentAuth, RC_SIGN_IN);

        mViewModel.setIsSigningIn(true);
    }

    private void startCompleteProfile() {
    // Check if profile of the user exist and is completed
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
        final String userId = user.getUid();
        mFirestore.collection("users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                customizeParameters(userId);
                                mViewModel.setIsProfileComplete(true);
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    mViewModel.setIsProfileComplete(true);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            mViewModel.setIsProfileComplete(false);
                        }
                    }
                });
    }
}

    private void customizeParameters(String userId) {
        // Open training parameters
        Intent intentUser = new Intent(this.getActivity(), ProfileActivity.class);
        intentUser.putExtra("USER_ID", userId);
        startActivity(intentUser);
    }
}

//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Html;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.firebase.ui.auth.AuthUI;
//import com.google.android.material.snackbar.Snackbar;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.example.fitnessshark.adapter.RestaurantAdapter;
//import com.google.firebase.example.fitnessshark.model.UserProfile;
//import com.google.firebase.example.fitnessshark.model.WorkoutPlan;
//import com.google.firebase.example.fitnessshark.util.WorkoutPlanUtil;
//import com.google.firebase.example.fitnessshark.viewmodel.MainActivityViewModel;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.Query;
//
//import java.util.Collections;
//
//import android.net.Uri;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.FirebaseException;
//import com.google.firebase.auth.ActionCodeSettings;
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.EmailAuthProvider;
//import com.google.firebase.auth.FirebaseAuthSettings;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.GithubAuthProvider;
//import com.google.firebase.auth.GoogleAuthProvider;
//import com.google.firebase.auth.PhoneAuthCredential;
//import com.google.firebase.auth.PhoneAuthProvider;
//import com.google.firebase.auth.PlayGamesAuthProvider;
//import com.google.firebase.auth.SignInMethodQueryResult;
//import com.google.firebase.auth.UserInfo;
//import com.google.firebase.auth.UserProfileChangeRequest;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//
//public class MainActivity extends AppCompatActivity implements
//        View.OnClickListener,
//        FilterDialogFragment.FilterListener,
//        RestaurantAdapter.OnRestaurantSelectedListener {
//
//    private static final String TAG = "MainActivity";
//
//    private static final int RC_SIGN_IN = 9001;
//
//    private static final int LIMIT = 50;
//
//    private Toolbar mToolbar;
//    private TextView mCurrentSearchView;
//    private TextView mCurrentSortByView;
//    private RecyclerView mRestaurantsRecycler;
//    private ViewGroup mEmptyView;
//
//    private FirebaseFirestore mFirestore;
//    private Query mQuery;
//
//    private FilterDialogFragment mFilterDialog;
//    private RestaurantAdapter mAdapter;
//
//    private MainActivityViewModel mViewModel;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        mToolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
//
//        mCurrentSearchView = findViewById(R.id.text_current_search);
//        mCurrentSortByView = findViewById(R.id.text_current_sort_by);
//        mRestaurantsRecycler = findViewById(R.id.recycler_restaurants);
//        mEmptyView = findViewById(R.id.view_empty);
//
//        findViewById(R.id.filter_bar).setOnClickListener(this);
//        findViewById(R.id.button_clear_filter).setOnClickListener(this);
//
//        // View model
//        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
//
//        // Enable Firestore logging
//        FirebaseFirestore.setLoggingEnabled(true);
//
//        // Initialize Firestore and the main RecyclerView
//        initFirestore();
//        initRecyclerView();
//
//        // Filter Dialog
//        mFilterDialog = new FilterDialogFragment();
//
//    }
//
//    private void initFirestore() {
//        mFirestore = FirebaseFirestore.getInstance();
//
//        // Get the 50 highest rated restaurants
//        mQuery = mFirestore.collection("workouts")
//                .orderBy("avgRating", Query.Direction.DESCENDING)
//                .limit(LIMIT);
//    }
//
//    private void initRecyclerView() {
//        if (mQuery == null) {
//            Log.w(TAG, "No query, not initializing RecyclerView");
//        }
//
//        mAdapter = new RestaurantAdapter(mQuery, this) {
//
//            @Override
//            protected void onDataChanged() {
//                // Show/hide content if the query returns empty.
//                if (getItemCount() == 0) {
//                    mRestaurantsRecycler.setVisibility(View.GONE);
//                    mEmptyView.setVisibility(View.VISIBLE);
//                } else {
//                    mRestaurantsRecycler.setVisibility(View.VISIBLE);
//                    mEmptyView.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            protected void onError(FirebaseFirestoreException e) {
//                // Show a snackbar on errors
//                Snackbar.make(findViewById(android.R.id.content),
//                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
//            }
//        };
//
//        mRestaurantsRecycler.setLayoutManager(new LinearLayoutManager(this));
//        mRestaurantsRecycler.setAdapter(mAdapter);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // Start sign in if necessary
//        if (shouldStartSignIn()) {
//            startSignIn();
//            return;
//        }
//
//        // Complete profile if necessary
//        if (shouldCompleteProfile()) {
//            startCompleteProfile();
//            return;
//        }
//
//        // Apply filters
//        onFilter(mViewModel.getFilters());
//
//        // Start listening for Firestore updates
//        if (mAdapter != null) {
//            mAdapter.startListening();
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAdapter != null) {
//            mAdapter.stopListening();
//        }
//    }
//
//    private void onAddItemsClicked() {
//        // Get a reference to the restaurants collection
//        CollectionReference workoutPlans = mFirestore.collection("workoutPlans");
//
//        for (int i = 0; i < 10; i++) {
//            // Get a random Restaurant POJO
//            WorkoutPlan workoutPlan = WorkoutPlanUtil.getRandom(this);
//
//            // Add a new document to the restaurants collection
//            workoutPlans.add(workoutPlan);
//        }
//    }
//
//    @Override
//    public void onFilter(Filters filters) {
//        // Construct query basic query
//        Query query = mFirestore.collection("workoutPlans");
//
//        // Category (equality filter)
//        if (filters.hasCategory()) {
//            query = query.whereEqualTo("category", filters.getCategory());
//        }
//
//        // City (equality filter)
//        if (filters.hasDifficulty()) {
//            query = query.whereEqualTo("difficulty", filters.getDifficulty());
//        }
//
//        // Price (equality filter)
//        if (filters.hasDuration()) {
//            query = query.whereGreaterThan("duration", filters.getDuration());
//        }
//
//        // Sort by (orderBy with direction)
//        if (filters.hasSortBy()) {
//            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
//        }
//
//        // Limit items
//        query = query.limit(LIMIT);
//
//        // Update the query
//        mQuery = query;
//        mAdapter.setQuery(query);
//
//        // Set header
//        mCurrentSearchView.setText(Html.fromHtml(filters.getSearchDescription(this)));
//        mCurrentSortByView.setText(filters.getOrderDescription(this));
//
//        // Save filters
//        mViewModel.setFilters(filters);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_add_items:
//                onAddItemsClicked();
//                break;
//            case R.id.menu_sign_out:
//                AuthUI.getInstance().signOut(this);
//                startSignIn();
//                break;
//            case R.id.menu_parameters:
//                // customizeParameters();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//            mViewModel.setIsSigningIn(false);
//
//            if (resultCode != RESULT_OK && shouldStartSignIn()) {
//                startSignIn();
//            }
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.filter_bar:
//                onFilterClicked();
//                break;
//            case R.id.button_clear_filter:
//                onClearFilterClicked();
//        }
//    }
//
//    public void onFilterClicked() {
//        // Show the dialog containing filter options
//        mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
//    }
//
//    public void onClearFilterClicked() {
//        mFilterDialog.resetFilters();
//
//        onFilter(Filters.getDefault());
//    }
//
//    @Override
//    public void onRestaurantSelected(DocumentSnapshot restaurant) {
//        // Go to the details page for the selected restaurant
//        Intent intent = new Intent(this, WorkoutPlanDetailActivity.class);
//        intent.putExtra(WorkoutPlanDetailActivity.KEY_RESTAURANT_ID, restaurant.getId());
//
//        startActivity(intent);
//    }
//
//    private boolean shouldStartSignIn() {
//        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
//    }
//
//    private boolean shouldCompleteProfile() {
//        return (!mViewModel.getIsProfileCompleted());
//    }
//
//    private void startSignIn() {
//        // Sign in with FirebaseUI
//        Intent intentAuth = AuthUI.getInstance().createSignInIntentBuilder()
//                .setAvailableProviders(Collections.singletonList(
//                        new AuthUI.IdpConfig.EmailBuilder().build()))
//                .setIsSmartLockEnabled(false)
//                .build();
//
//        startActivityForResult(intentAuth, RC_SIGN_IN);
//
//        mViewModel.setIsSigningIn(true);
//    }
//
//    private void startCompleteProfile() {
//        // Check if profile of the user exist and is completed
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            final String userId = user.getUid();
//            mFirestore.collection("users")
//                    .whereEqualTo("userId", userId)
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                if (task.getResult().isEmpty()) {
//                                    customizeParameters(userId);
//                                    mViewModel.setIsProfileComplete(true);
//                                } else {
//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        Log.d(TAG, document.getId() + " => " + document.getData());
//                                        mViewModel.setIsProfileComplete(true);
//                                    }
//                                }
//                            } else {
//                                Log.d(TAG, "Error getting documents: ", task.getException());
//                                mViewModel.setIsProfileComplete(false);
//                            }
//                        }
//                    });
//        }
//    }
//
//    private void customizeParameters(String userId) {
//        // Open training parameters
//        Intent intentUser = new Intent(this, ProfileActivity.class);
//        intentUser.putExtra("USER_ID", userId);
//        startActivity(intentUser);
//    }