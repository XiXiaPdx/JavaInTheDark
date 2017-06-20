package com.blueoxgym.javainthedark.Services;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.blueoxgym.javainthedark.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Guest on 6/20/17.
 */

public class FireBaseService {
    private static Boolean userLoggedIn;

    public static FirebaseAuth firebaseAuth = makeFireBaseAuth();

    public static FirebaseAuth makeFireBaseAuth(){
        return FirebaseAuth.getInstance();
    }


    public static Boolean isUserLogged() {
        if (firebaseAuth.getCurrentUser() != null) {
            return true;
        } else {
            return false;
        }
    }


}
