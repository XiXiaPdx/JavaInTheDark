package com.blueoxgym.javainthedark.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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

import butterknife.BindView;
import butterknife.ButterKnife;


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
                                        Log.d("Signed IN", "YES DONE");
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
