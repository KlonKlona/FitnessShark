/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.google.firebase.example.fitnessshark;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.example.fitnessshark.model.Category;
import com.google.firebase.example.fitnessshark.model.Equipment;
import com.google.firebase.example.fitnessshark.model.Exercise;
import com.google.firebase.example.fitnessshark.model.Rating;

import java.util.Collections;

/**
 * Dialog Fragment containing Exercise form.
 */
public class ExerciseDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "ExerciseDialog";

    private EditText mDescription;
    private EditText mName;
    private EditText mSets;
    private EditText mReps;
    private EditText mWeight;
    private Spinner mCategory;
    private Spinner mEquipment;

    interface ExerciseListener {

        void onExercise(Exercise exercise);

    }

    private ExerciseListener mExerciseListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_exercise, container, false);
        mName = v.findViewById(R.id.exercise_form_name);
        mDescription = v.findViewById(R.id.exercise_form_desription);
        mSets = v.findViewById(R.id.exercise_form_sets);
        mReps = v.findViewById(R.id.exercise_form_reps);
        mWeight = v.findViewById(R.id.exercise_form_weight);

        mCategory = v.findViewById(R.id.exercise_spinner_category);
        mEquipment = v.findViewById(R.id.exercise_spinner_equipment);

        v.findViewById(R.id.exercise_form_button).setOnClickListener(this);
        v.findViewById(R.id.exercise_form_cancel).setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ExerciseListener) {
            mExerciseListener = (ExerciseListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exercise_form_button:
                onSubmitClicked(v);
                break;
            case R.id.exercise_form_cancel:
                onCancelClicked(v);
                break;
        }
    }

    public void onSubmitClicked(View view) {
        Exercise exercise = new Exercise(mName.getText().toString(),
                mDescription.getText().toString(),
                Category.valueOf(mCategory.getSelectedItem().toString()),
                Collections.singletonList(Equipment.valueOf(mEquipment.getSelectedItem().toString())),
                Integer.parseInt(mSets.getText().toString()),
                Integer.parseInt(mReps.getText().toString()),
                Double.parseDouble(mWeight.getText().toString()));

        if (mExerciseListener != null) {
            mExerciseListener.onExercise(exercise);
        }

        dismiss();
    }

    public void onCancelClicked(View view) {
        dismiss();
    }
}
