package com.pierrejanineh.geonote.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pierrejanineh.geonote.Model.Listeners.FirebaseListener;
import com.pierrejanineh.geonote.Model.Listeners.FirestoreReadListener;
import com.pierrejanineh.geonote.UI.LoginLogoutActivity;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {

    public static final String USERS_COLLECTION = "users";
    public static final String NOTES_COLLECTION = "notes";

    private static final FirebaseManager firebase = new FirebaseManager();
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    private final MutableLiveData<Note[]> notes = new MutableLiveData<>(new Note[]{});

    /**
     * Creates a Firebase Manager to manage all Firebase/Firestore methods.
     */
    private FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Get Singleton instance.
     * @return FirebaseManager only instance.
     */
    public static FirebaseManager getInstance() { return firebase; }

    /**
     * Gets current user from Firebase.
     * @return FirebaseUser current logged-in Firebase user.
     */
    public FirebaseUser getUser() { return auth.getCurrentUser(); }

    /**
     * Checks if user is logged in by checking if getUser() returns an actual user.
     * @return boolean to indicate whether any user is logged-in.
     */
    public boolean checkIfUserIsLoggedIn() { return getUser() != null; }

    /**
     * Signs up a new user on Firebase.
     * @param name User's name.
     * @param email User's E-mail Address.
     * @param password User's Password.
     * @param firebaseListener FirebaseListener for handling onComplete CallBack.
     */
    public void signUp(String name, String email, String password, FirebaseListener firebaseListener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateUserName(name, firebaseListener);
                    } else {
                        firebaseListener.onComplete(task);
                    }
                });
    }

    /**
     * Updates Current User's Name on Firebase.
     * @param name User's new Name.
     * @param firebaseListener FirebaseListener for handling onComplete CallBack.
     */
    private void updateUserName(String name, FirebaseListener<Void> firebaseListener) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        getUser().updateProfile(profileChangeRequest)
                .addOnCompleteListener(task -> {
                    firebaseListener.onComplete(task);
                });
    }

    /**
     * Logs-in a Firebase User.
     * @param email User's E-mail.
     * @param password User's Password.
     * @param firebaseListener FirebaseListener for handling onComplete CallBack.
     */
    public void login(String email, String password, FirebaseListener<AuthResult> firebaseListener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    firebaseListener.onComplete(task);
                });
    }

    /**
     * Logs-out a Firebase User.
     * @param activity Current Activity to finish and start new Activity.
     * @param context Current Contect to create an Intent.
     */
    public void logout(Activity activity, Context context) {
        auth.signOut();
        activity.finish();

        Intent intent = new Intent(context, LoginLogoutActivity.class);
        activity.startActivity(intent);
    }

    /**
     * Requests Notes array from Firestore.
     * @param firestoreReadListener FirestoreReadListener for handling onComplete CallBack.
     */
    public void getNotesFromDB(FirestoreReadListener firestoreReadListener){
        final CollectionReference collRef = db.collection(USERS_COLLECTION).document(getUser().getUid()).collection(NOTES_COLLECTION);
        collRef.addSnapshotListener((value, error) -> {
            if (error != null){
                Log.d("FIRESTORE_ERROR", error.getMessage());
                return;
            }

            if (value != null){
                List<Note> list = new ArrayList<>();
                for (QueryDocumentSnapshot val : value) {
                    Note note = val.toObject(Note.class);
                    note.setUid(val.getId());
                    list.add(note);
                }
                Note[] array = new Note[list.size()];
                list.toArray(array);
                notes.postValue(array);
                firestoreReadListener.onComplete(array);
            }
        });
    }

    /**
     * Adds/Updates Note on Firestore.
     * @param note Note to be Added/Updated.
     * @param successListener OnSuccessListener for handling Success CallBack.
     * @param failureListener OnFailureListener for handling Failure CallBack.
     */
    public void setNoteOnDB(Note note, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        final DocumentReference docRef;

        if (note.getUid() == null) {
            docRef = db.collection(USERS_COLLECTION).document(getUser().getUid()).collection(NOTES_COLLECTION).document();
        }else {
            docRef = db.collection(USERS_COLLECTION).document(getUser().getUid()).collection(NOTES_COLLECTION).document(note.getUid());
        }

        docRef.set(note)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Deletes Notes on Firestore.
     * @param note Note to be deleted.
     * @param successListener OnSuccessListener for handling Success CallBack.
     * @param failureListener OnFailureListener for handling Failure CallBack.
     */
    public void deleteNoteOnDB(Note note, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        final CollectionReference collRef = db.collection(USERS_COLLECTION).document(getUser().getUid()).collection(NOTES_COLLECTION);
        collRef.document(note.getUid())
                .delete()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Get Notes as MutableLiveData.
     * @return MutableLiveData with Notes array.
     */
    public MutableLiveData<Note[]> getNotes() {
        return notes;
    }
}
