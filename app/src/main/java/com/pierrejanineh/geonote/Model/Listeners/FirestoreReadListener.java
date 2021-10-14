package com.pierrejanineh.geonote.Model.Listeners;

import com.pierrejanineh.geonote.Model.Note;

public interface FirestoreReadListener {

    /**
     * Listens to FirestoreBD readings.
     * @param notes Notes read as array.
     */
    void onComplete(Note[] notes);
}
