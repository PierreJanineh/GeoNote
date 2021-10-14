package com.pierrejanineh.geonote.UI.navigation.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pierrejanineh.geonote.Model.Note;
import com.pierrejanineh.geonote.R;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {

    private final Note[] notes;
    private final View.OnClickListener onClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View mainView;
        private final TextView title;
        private final TextView body;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            title = view.findViewById(R.id.title);
            body = view.findViewById(R.id.body);
            mainView = view;
        }

        public TextView getTitle() {
            return title;
        }

        public TextView getBody() {
            return body;
        }

        public View getMainView() {
            return mainView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param notesArr Note[] containing the data of the notes to populate views to be used by RecyclerView.
     */
    public NotesRecyclerViewAdapter(Note[] notesArr, View.OnClickListener clickListener) {
        notes = notesArr;
        onClickListener = clickListener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.note_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTitle().setText(notes[position].getTitle());
        viewHolder.getBody().setText(notes[position].getBody());

        viewHolder.getMainView().setTag(notes[position].getUid());

        viewHolder.getMainView().setOnClickListener(onClickListener);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return notes.length;
    }
}
