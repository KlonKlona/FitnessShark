///**
// * Copyright 2017 Google Inc. All Rights Reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
// package com.google.firebase.example.fitnessshark;

package com.google.firebase.example.fitnessshark;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.example.fitnessshark.ui.notifications.NotificationsFragment;

//implement the interface OnNavigationItemSelectedListener in your activity class
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //loading the default fragment
        loadFragment(new HomeFragment());

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;

            case R.id.navigation_dashboard:
                fragment = new DashboardFragment();
                break;

            case R.id.navigation_notifications:
                fragment = new NotificationsFragment();
                break;

            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
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
//
//    private void showTodoToast() {
//        Toast.makeText(this, "TODO: Implement", Toast.LENGTH_SHORT).show();
//    }
//
//    public void getUserProfile() {
//        // [START get_user_profile]
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            // Name, email address, and profile photo Url
//            String name = user.getDisplayName();
//            String email = user.getEmail();
//            Uri photoUrl = user.getPhotoUrl();
//
//            // Check if user's email is verified
//            boolean emailVerified = user.isEmailVerified();
//
//            // The user's ID, unique to the Firebase project. Do NOT use this value to
//            // authenticate with your backend server, if you have one. Use
//            // FirebaseUser.getIdToken() instead.
//            String uid = user.getUid();
//        }
//        // [END get_user_profile]
//    }
//
//    public void getProviderData() {
//        // [START get_provider_data]
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            for (UserInfo profile : user.getProviderData()) {
//                // Id of the provider (ex: google.com)
//                String providerId = profile.getProviderId();
//
//                // UID specific to the provider
//                String uid = profile.getUid();
//
//                // Name, email address, and profile photo Url
//                String name = profile.getDisplayName();
//                String email = profile.getEmail();
//                Uri photoUrl = profile.getPhotoUrl();
//            }
//        }
//        // [END get_provider_data]
//    }
//
//    public void updateProfile() {
//        // [START update_profile]
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                .setDisplayName("Jane Q. User")
//                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
//                .build();
//
//        user.updateProfile(profileUpdates)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User profile updated.");
//                        }
//                    }
//                });
//        // [END update_profile]
//    }
//
//    public void updateEmail() {
//        // [START update_email]
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        user.updateEmail("user@example.com")
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User email address updated.");
//                        }
//                    }
//                });
//        // [END update_email]
//    }
//
//    public void updatePassword() {
//        // [START update_password]
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String newPassword = "SOME-SECURE-PASSWORD";
//
//        user.updatePassword(newPassword)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User password updated.");
//                        }
//                    }
//                });
//        // [END update_password]
//    }
//
//    public void sendEmailVerification() {
//        // [START send_email_verification]
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        FirebaseUser user = auth.getCurrentUser();
//
//        user.sendEmailVerification()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "Email sent.");
//                        }
//                    }
//                });
//        // [END send_email_verification]
//    }
//
//    public void sendEmailVerificationWithContinueUrl() {
//        // [START send_email_verification_with_continue_url]
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        FirebaseUser user = auth.getCurrentUser();
//
//        String url = "http://www.example.com/verify?uid=" + user.getUid();
//        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
//                .setUrl(url)
//                .setIOSBundleId("com.example.ios")
//                // The default for this is populated with the current android package name.
//                .setAndroidPackageName("com.example.android", false, null)
//                .build();
//
//        user.sendEmailVerification(actionCodeSettings)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "Email sent.");
//                        }
//                    }
//                });
//
//        // [END send_email_verification_with_continue_url]
//        // [START localize_verification_email]
//        auth.setLanguageCode("fr");
//        // To apply the default app language instead of explicitly setting it.
//        // auth.useAppLanguage();
//        // [END localize_verification_email]
//    }
//
//    public void sendPasswordReset() {
//        // [START send_password_reset]
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        String emailAddress = "user@example.com";
//
//        auth.sendPasswordResetEmail(emailAddress)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "Email sent.");
//                        }
//                    }
//                });
//        // [END send_password_reset]
//    }
//
//    public void deleteUser() {
//        // [START delete_user]
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        user.delete()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User account deleted.");
//                        }
//                    }
//                });
//        // [END delete_user]
//    }
//
//    public void reauthenticate() {
//        // [START reauthenticate]
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        // Get auth credentials from the user for re-authentication. The example below shows
//        // email and password credentials but there are multiple possible providers,
//        // such as GoogleAuthProvider or FacebookAuthProvider.
//        AuthCredential credential = EmailAuthProvider
//                .getCredential("user@example.com", "password1234");
//
//        // Prompt the user to re-provide their sign-in credentials
//        user.reauthenticate(credential)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.d(TAG, "User re-authenticated.");
//                    }
//                });
//        // [END reauthenticate]
//    }
//
//    public void authWithGithub() {
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//        // [START auth_with_github]
//        String token = "<GITHUB-ACCESS-TOKEN>";
//        AuthCredential credential = GithubAuthProvider.getCredential(token);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
//
//                        // If sign in fails, display a message to the user. If sign in succeeds
//                        // the auth state listener will be notified and logic to handle the
//                        // signed in user can be handled in the listener.
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "signInWithCredential", task.getException());
//                            Toast.makeText(MainActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//
//                        // ...
//                    }
//                });
//        // [END auth_with_github]
//    }
//
//
//    public void linkAndMerge(AuthCredential credential) {
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//        // [START auth_link_and_merge]
//        FirebaseUser prevUser = FirebaseAuth.getInstance().getCurrentUser();
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        FirebaseUser currentUser = task.getResult().getUser();
//                        // Merge prevUser and currentUser accounts and data
//                        // ...
//                    }
//                });
//        // [END auth_link_and_merge]
//    }
//
//
//    public void unlink(String providerId) {
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//        // [START auth_unlink]
//        mAuth.getCurrentUser().unlink(providerId)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Auth provider unlinked from account
//                            // ...
//                        }
//                    }
//                });
//        // [END auth_unlink]
//    }
//
//    public void buildActionCodeSettings() {
//        // [START auth_build_action_code_settings]
//        ActionCodeSettings actionCodeSettings =
//                ActionCodeSettings.newBuilder()
//                        // URL you want to redirect back to. The domain (www.example.com) for this
//                        // URL must be whitelisted in the Firebase Console.
//                        .setUrl("https://www.example.com/finishSignUp?cartId=1234")
//                        // This must be true
//                        .setHandleCodeInApp(true)
//                        .setIOSBundleId("com.example.ios")
//                        .setAndroidPackageName(
//                                "com.example.android",
//                                true, /* installIfNotAvailable */
//                                "12"    /* minimumVersion */)
//                        .build();
//        // [END auth_build_action_code_settings]
//    }
//
//    public void sendSignInLink(String email, ActionCodeSettings actionCodeSettings) {
//        // [START auth_send_sign_in_link]
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.sendSignInLinkToEmail(email, actionCodeSettings)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "Email sent.");
//                        }
//                    }
//                });
//        // [END auth_send_sign_in_link]
//    }
//
//    public void verifySignInLink() {
//        // [START auth_verify_sign_in_link]
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        Intent intent = getIntent();
//        String emailLink = intent.getData().toString();
//
//        // Confirm the link is a sign-in with email link.
//        if (auth.isSignInWithEmailLink(emailLink)) {
//            // Retrieve this from wherever you stored it
//            String email = "someemail@domain.com";
//
//            // The client SDK will parse the code from the link for you.
//            auth.signInWithEmailLink(email, emailLink)
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                Log.d(TAG, "Successfully signed in with email link!");
//                                AuthResult result = task.getResult();
//                                // You can access the new user via result.getUser()
//                                // Additional user info profile *not* available via:
//                                // result.getAdditionalUserInfo().getProfile() == null
//                                // You can check if the user is new or existing:
//                                // result.getAdditionalUserInfo().isNewUser()
//                            } else {
//                                Log.e(TAG, "Error signing in with email link", task.getException());
//                            }
//                        }
//                    });
//        }
//        // [END auth_verify_sign_in_link]
//    }
//
//    public void linkWithSignInLink(String email, String emailLink) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//
//        // [START auth_link_with_link]
//        // Construct the email link credential from the current URL.
//        AuthCredential credential =
//                EmailAuthProvider.getCredentialWithLink(email, emailLink);
//
//        // Link the credential to the current user.
//        auth.getCurrentUser().linkWithCredential(credential)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "Successfully linked emailLink credential!");
//                            AuthResult result = task.getResult();
//                            // You can access the new user via result.getUser()
//                            // Additional user info profile *not* available via:
//                            // result.getAdditionalUserInfo().getProfile() == null
//                            // You can check if the user is new or existing:
//                            // result.getAdditionalUserInfo().isNewUser()
//                        } else {
//                            Log.e(TAG, "Error linking emailLink credential", task.getException());
//                        }
//                    }
//                });
//        // [END auth_link_with_link]
//    }
//
//    public void reauthWithLink(String email, String emailLink) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//
//        // [START auth_reauth_with_link]
//        // Construct the email link credential from the current URL.
//        AuthCredential credential =
//                EmailAuthProvider.getCredentialWithLink(email, emailLink);
//
//        // Re-authenticate the user with this credential.
//        auth.getCurrentUser().reauthenticateAndRetrieveData(credential)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // User is now successfully reauthenticated
//                        } else {
//                            Log.e(TAG, "Error reauthenticating", task.getException());
//                        }
//                    }
//                });
//        // [END auth_reauth_with_link]
//    }
//
//    public void differentiateLink(String email) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//
//        // [START auth_differentiate_link]
//        auth.fetchSignInMethodsForEmail(email)
//                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
//                        if (task.isSuccessful()) {
//                            SignInMethodQueryResult result = task.getResult();
//                            List<String> signInMethods = result.getSignInMethods();
//                            if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
//                                // User can sign in with email/password
//                            } else if (signInMethods.contains(EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD)) {
//                                // User can sign in with email/link
//                            }
//                        } else {
//                            Log.e(TAG, "Error getting sign in methods for user", task.getException());
//                        }
//                    }
//                });
//        // [END auth_differentiate_link]
//    }
//
//    public void getGoogleCredentials() {
//        String googleIdToken = "";
//        // [START auth_google_cred]
//        AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken, null);
//        // [END auth_google_cred]
//    }
//
//    public void getEmailCredentials() {
//        String email = "";
//        String password = "";
//        // [START auth_email_cred]
//        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
//        // [END auth_email_cred]
//    }
//
//    public void signOut() {
//        // [START auth_sign_out]
//        FirebaseAuth.getInstance().signOut();
//        // [END auth_sign_out]
//    }
//
//    public void testPhoneVerify() {
//        // [START auth_test_phone_verify]
//        String phoneNum = "+16505554567";
//        String testVerificationCode = "123456";
//
//        // Whenever verification is triggered with the whitelisted number,
//        // provided it is not set for auto-retrieval, onCodeSent will be triggered.
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNum, 30L /*timeout*/, TimeUnit.SECONDS,
//                this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//                    @Override
//                    public void onCodeSent(String verificationId,
//                                           PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                        // Save the verification id somewhere
//                        // ...
//
//                        // The corresponding whitelisted code above should be used to complete sign-in.
//                        MainActivity.this.enableUserManuallyInputCode();
//                    }
//
//                    @Override
//                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                        // Sign in with the credential
//                        // ...
//                    }
//
//                    @Override
//                    public void onVerificationFailed(FirebaseException e) {
//                        // ...
//                    }
//
//                });
//        // [END auth_test_phone_verify]
//    }
//
//    private void enableUserManuallyInputCode() {
//        // No-op
//    }
//
//    public void testPhoneAutoRetrieve() {
//        // [START auth_test_phone_auto]
//        // The test phone number and code should be whitelisted in the console.
//        String phoneNumber = "+16505554567";
//        String smsCode = "123456";
//
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();
//
//        // Configure faking the auto-retrieval with the whitelisted numbers.
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, smsCode);
//
//        PhoneAuthProvider phoneAuthProvider = PhoneAuthProvider.getInstance();
//        phoneAuthProvider.verifyPhoneNumber(
//                phoneNumber,
//                60L,
//                TimeUnit.SECONDS,
//                this, /* activity */
//                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                    @Override
//                    public void onVerificationCompleted(PhoneAuthCredential credential) {
//                        // Instant verification is applied and a credential is directly returned.
//                        // ...
//                    }
//
//                    // [START_EXCLUDE]
//                    @Override
//                    public void onVerificationFailed(FirebaseException e) {
//
//                    }
//                    // [END_EXCLUDE]
//                });
//        // [END auth_test_phone_auto]
//    }
//
//    public void gamesMakeGoogleSignInOptions() {
//        // [START games_google_signin_options]
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
//                .requestServerAuthCode(getString(R.string.default_web_client_id))
//                .build();
//        // [END games_google_signin_options]
//    }
//
//    // [START games_auth_with_firebase]
//    // Call this both in the silent sign-in task's OnCompleteListener and in the
//    // Activity's onActivityResult handler.
//    private void firebaseAuthWithPlayGames(GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithPlayGames:" + acct.getId());
//
//        final FirebaseAuth auth = FirebaseAuth.getInstance();
//        AuthCredential credential = PlayGamesAuthProvider.getCredential(acct.getServerAuthCode());
//        auth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = auth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(MainActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
//    }
//    // [END games_auth_with_firebase]
//
//    private void gamesGetUserInfo() {
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//        // [START games_get_user_info]
//        FirebaseUser user = mAuth.getCurrentUser();
//        String playerName = user.getDisplayName();
//
//        // The user's Id, unique to the Firebase project.
//        // Do NOT use this value to authenticate with your backend server, if you
//        // have one; use FirebaseUser.getIdToken() instead.
//        String uid = user.getUid();
//        // [END games_get_user_info]
//    }
//
//    private void updateUI(@Nullable FirebaseUser user) {
//        // No-op
//    }
//}
