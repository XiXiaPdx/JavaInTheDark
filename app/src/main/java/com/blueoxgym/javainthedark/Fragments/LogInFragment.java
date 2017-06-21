package com.blueoxgym.javainthedark.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blueoxgym.javainthedark.MainActivity;
import com.blueoxgym.javainthedark.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogInFragment extends Fragment implements View.OnClickListener{
    @BindView(R.id.emailView)
    EditText email;
    @BindView(R.id.passwordView) EditText password;
    @BindView(R.id.confirmPasswordView) EditText confirmPassword;
    @BindView (R.id.logInButton)
    Button loginButton;
    public FirebaseAuth firebaseAuth;
    public Toolbar toolbar;
    public ArrayList testArray;



    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);
        ButterKnife.bind(this, view);
        loginButton.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
         toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        testArray = new ArrayList<String>(Arrays.asList("A", "B", "C"));

        return view;
    }

    public static LogInFragment newInstance(){
        LogInFragment newFragment = new LogInFragment();
        return newFragment;
    }

    public Boolean validate (String newPass, String confirmPass) {
        Boolean valid = false;

        if (newPass.equals(confirmPass)){
            valid = true;
        }
        return valid;
    }

    Observable<String> observable = Observable.just("First event");
    Observable<Integer> oneToFiveObservable = Observable.range(0,5);
    Observable<ArrayList> observableArray = Observable.fromArray(testArray);


    @Override
    public void onClick(View v) {
        if (v == loginButton) {
                String newEmail = email.getText().toString().trim();
                String newPassword = password.getText().toString().trim();
                String confirmPassword = password.getText().toString().trim();
                if(validate(newPassword, confirmPassword)) {

                    firebaseAuth.signInWithEmailAndPassword(newEmail, newPassword)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        toolbar.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(getActivity(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }

    }
}
