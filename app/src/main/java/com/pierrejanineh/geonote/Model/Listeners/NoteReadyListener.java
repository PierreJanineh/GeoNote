package com.pierrejanineh.geonote.Model.Listeners;

import com.pierrejanineh.geonote.Model.Note;

public interface NoteReadyListener {

    /**
     * Waits and Listens to Note to be ready to Add/Edit on DB.
     * @param note Note being ready.
     */
    void OnReady(Note note);
}
