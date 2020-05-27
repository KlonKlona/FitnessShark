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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fitnessshark.model.Exercise;
import com.google.firebase.example.fitnessshark.NotificationFragment;
import com.google.firebase.example.fitnessshark.viewmodel.MainActivityViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;

//implement the interface OnNavigationItemSelectedListener in your activity class
public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;

    private MainActivityViewModel mViewModel;
    private BottomNavigationView mNavigation;
    private FirebaseFirestore mFirestore;

    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGson = new Gson();

        // View model
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Initialize Firestore and the main RecyclerView
        initFirestore();

        //getting bottom navigation view and attaching the listener
        mNavigation = findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (shouldFetchExerciseDatabase()) {
            fetchExerciseDatabase();
        }

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }

        // Complete profile if necessary
        if (shouldCompleteProfile()) {
            completeProfile();
        }

//        WARNING: MOVED TO startCompleteProfile()
//        loading the default fragment
//        loadFragment(new HomeFragment());
    }

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
                fragment = new NotificationFragment();
                break;

            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;

            case R.id.navigation_logout:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }

        return loadFragment(fragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);

            if (resultCode != RESULT_OK && shouldStartSignIn()) {
                startSignIn();
            }
        }
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
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

    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private boolean shouldCompleteProfile() {
        return (!mViewModel.getIsProfileCompleted());
    }

    private boolean shouldFetchExerciseDatabase() {
        return (!mViewModel.getIsExerciseDatabaseFetched());
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

    private void completeProfile() {
        // Check if profile of the user exist and is completed
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String userId = user.getUid();
            mFirestore.collection("users")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                Log.d(TAG, "No Profile in Database! Redirecting to ProfilePage");
                                mNavigation.setSelectedItemId(R.id.navigation_profile);
                                loadFragment(new ProfileFragment());
                                mViewModel.setIsProfileComplete(true);
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    mViewModel.setIsProfileComplete(true);
                                    loadFragment(new HomeFragment());
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            mViewModel.setIsProfileComplete(false);
                            loadFragment(new HomeFragment());
                        }
                    });
        }
    }

    private void fetchExerciseDatabase() {
        mFirestore.collection("exercisesApi")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(task.getResult().isEmpty()) {
                            Log.d(TAG, "No exercises in Database! Fetching...");
                            new WebServiceHandler()
                                    .execute("https://wger.de/api/v2/exercise/?language=2&status=2&limit=200");
                        } else {
                            Log.d(TAG, "Exercises currently in Database!");
                        }
                    } else {
                        Log.d(TAG, "Error getting exercises: ", task.getException());
                    }
                });

    }

    private class WebServiceHandler extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Wait...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();

                InputStream in = new BufferedInputStream(
                        connection.getInputStream());

                return streamToString(in);

            } catch (Exception e) {
                Log.d(MainActivity.class.getSimpleName(), e.toString());
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();

            try {
                JSONObject json = new JSONObject(result);
                Log.d(MainActivity.class.getSimpleName(), "JSON: " + json.toString());
                Exercise exercise;
                JSONArray jsonArray = json.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    Log.d(MainActivity.class.getSimpleName(), "jsonObject" + i + ": "  + jsonObject.toString());
                    exercise = mGson.fromJson(String.valueOf(jsonObject), Exercise.class);
                    exercise.setDescription(exercise.getDescription().replace("<p>", ""));
                    exercise.setDescription(exercise.getDescription().replace("</p>", ""));
                    mFirestore.collection("exercisesApi")
                            .add(exercise);
                }
                mViewModel.setIsExerciseDatabaseFetched(true);

            } catch (Exception e) {
                // obsłuż wyjątek
                Log.d(MainActivity.class.getSimpleName(), e.toString());
            }
        }
    }

    public static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        try {

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            reader.close();

        } catch (IOException e) {
            // obsłuż wyjątek
            Log.d(MainActivity.class.getSimpleName(), e.toString());
        }

        return stringBuilder.toString();
    }

}