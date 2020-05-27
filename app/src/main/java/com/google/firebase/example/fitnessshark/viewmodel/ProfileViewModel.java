package com.google.firebase.example.fitnessshark.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.example.fitnessshark.model.UserProfile;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<UserProfile> userProfile;
    private boolean mDoesProfileExists;
    private boolean mAreExercisesFetched;
    private boolean mIsWorkoutGenerated;


    public ProfileViewModel() {
        this.mAreExercisesFetched = false;
        this.mDoesProfileExists = false;
        this.mIsWorkoutGenerated = false;
    }

    public boolean getDoesProfileExists() {
        return mDoesProfileExists;
    }

    public void setDoesProfileExists(boolean mDoesProfileExists) {
        this.mDoesProfileExists = mDoesProfileExists;
    }

    public boolean getAreExercisesFetched() {
        return mAreExercisesFetched;
    }

    public void setAreExercisesFetched(boolean mAreExercisesFetched) {
        this.mAreExercisesFetched = mAreExercisesFetched;
    }

    public boolean getIsWorkoutGenerated() {
        return mIsWorkoutGenerated;
    }

    public void setIsWorkoutGenerated(boolean isWorkoutGenerated) {
        this.mIsWorkoutGenerated = isWorkoutGenerated;
    }

    public LiveData<UserProfile> getUserProfile() {
        if (userProfile == null) {
            userProfile = new MutableLiveData<UserProfile>();
            loadUsers();
        }
        return userProfile;
    }

    private void loadUsers() {
        // Do an asynchronous operation to fetch users.
    }
}
