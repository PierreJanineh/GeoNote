package com.pierrejanineh.geonote.UI.navigation.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pierrejanineh.geonote.Model.FirebaseManager;
import com.pierrejanineh.geonote.Model.Note;

public class ListViewModel extends ViewModel {

    private final MutableLiveData<Note[]> notes;

    public ListViewModel() {
        notes = new MutableLiveData<>();
        FirebaseManager.getInstance().getNotesFromDB(notesArr -> notes.postValue(notesArr));
    }

    public MutableLiveData<Note[]> getNotes() {
        return notes;
    }
}