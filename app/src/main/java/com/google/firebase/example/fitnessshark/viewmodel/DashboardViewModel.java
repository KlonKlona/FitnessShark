package com.google.firebase.example.fitnessshark.viewmodel;

import androidx.lifecycle.ViewModel;

import com.google.firebase.example.fitnessshark.WorkoutFilters;

public class DashboardViewModel extends ViewModel {

    private WorkoutFilters mFilters;

    public DashboardViewModel() {

        mFilters = WorkoutFilters.getDefault();
    }

    public WorkoutFilters getFilters() {
        return mFilters;
    }

    public void setFilters(WorkoutFilters mFilters) {
        this.mFilters = mFilters;
    }
}