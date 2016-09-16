package com.example.android.sunshine.app.Data;

import android.content.Context;

import com.example.android.sunshine.app.util.Constants;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by AdonisArifi on 31.7.2016 - 2016 . PGoCaptureMoment
 */

public class FirebaseData {

    public static FirebaseData firebaseDataInstance;

    public FirebaseApp firebaseApp = FirebaseApp.getInstance();

    public FirebaseDatabase firebaseDatabase;
    //Firebase database
    public DatabaseReference mRootRef;

    public DatabaseReference background_Ref;


    public FirebaseStorage storage;


    // Get a reference to the location where we'll store user_profile photos
    public StorageReference storageRef;

    public FirebaseAuth mAuth;

    private final Context mContext;


    public FirebaseData(Context mContext) {
        this.mContext = mContext;
        firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        firebaseDatabase.setPersistenceEnabled(true);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        background_Ref = mRootRef.child(Constants.FIREBASE_CLASS_BACKGROUND);
        storage = FirebaseStorage.getInstance(firebaseApp);
        storageRef = storage.getReference(Constants.FIREBASE_FOLLDER_PATH_BACKGROUND_PHOTO);
        mAuth = FirebaseAuth.getInstance(firebaseApp);

    }

    public static FirebaseData getFirebaseDataInstance(Context context) {

        if (firebaseDataInstance == null) {
            firebaseDataInstance = new FirebaseData(context);
        }

        return firebaseDataInstance;
    }


}
