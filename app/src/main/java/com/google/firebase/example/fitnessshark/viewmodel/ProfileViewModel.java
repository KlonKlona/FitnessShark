package com.google.firebase.example.fitnessshark.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.example.fitnessshark.model.UserProfile;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<UserProfile> userProfile;
    private boolean mDoesProfileExists;

    public ProfileViewModel() {
        mDoesProfileExists = false;
    }

    public boolean getDoesProfileExists() {
        return mDoesProfileExists;
    }

    public void setDoesProfileExists(boolean mDoesProfileExists) {
        this.mDoesProfileExists = mDoesProfileExists;
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
