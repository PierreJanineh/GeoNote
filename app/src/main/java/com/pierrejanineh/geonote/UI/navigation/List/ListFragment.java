package com.pierrejanineh.geonote.UI.navigation.List;

import static com.pierrejanineh.geonote.Model.Note.NOTE_UID;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pierrejanineh.geonote.UI.navigation.AddEditNoteActivity;
import com.pierrejanineh.geonote.databinding.FragmentListBinding;

public class ListFragment extends Fragment {

    private ListViewModel listViewModel;
    private FragmentListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewModel = new ViewModelProvider(this).get(ListViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createRecyclerView();

        return root;
    }

    /**
     * Gets Notes and assigns them to the recyclerView.
     */
    private void createRecyclerView() {

        final RecyclerView recyclerView = binding.recyclerView;
        final TextView nullText = binding.nullText;
        final ProgressBar progressBar = binding.progressBar;

        listViewModel.getNotes().observe(getViewLifecycleOwner(), notesArr -> {

            progressBar.setVisibility(View.GONE);

            // Show/Hide Recycler view to indicate Empty list.
            if (notesArr != null && notesArr.length > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                nullText.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                nullText.setVisibility(View.VISIBLE);
            }

            NotesRecyclerViewAdapter notesRecyclerViewAdapter = new NotesRecyclerViewAdapter(notesArr, view -> {
                Intent intent = new Intent(getContext(), AddEditNoteActivity.class);
                intent.putExtra(NOTE_UID, view.getTag().toString());
                startActivity(intent);
            });
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setAdapter(notesRecyclerViewAdapter);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}