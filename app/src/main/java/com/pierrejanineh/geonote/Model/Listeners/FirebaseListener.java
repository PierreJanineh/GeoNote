package com.pierrejanineh.geonote.Model.Listeners;

import com.google.android.gms.tasks.Task;

public interface FirebaseListener<T> {
    /**
     * Listens to Firebase task completion.
     * @param task Task returned by Firebase task.
     */
    void onComplete(Task<T> task);
}
