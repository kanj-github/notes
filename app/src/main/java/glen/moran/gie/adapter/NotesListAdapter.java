package glen.moran.gie.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import glen.moran.gie.R;
import glen.moran.gie.db.DbManager;
import glen.moran.gie.model.NoteListItem;

/**
 * Created by kanj on 3/8/16.
 */
public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NoteViewHolder>{
    private ArrayList<NoteListItem> data;
    private Context mContext;
    private OnNoteSelectedListener mListener;

    public NotesListAdapter(ArrayList<NoteListItem> data, Context mContext, OnNoteSelectedListener mListener) {
        this.data = data;
        this.mContext = mContext;
        this.mListener = mListener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new NoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        NoteListItem item = data.get(position);
        holder.title.setText(item.title);
        holder.shortDesc.setText(item.shortDesc);
        holder.noteId = item.noteId;
        holder.noteTitle = item.title;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void deleteNote(long id) {
        DbManager.getInstance(mContext).deleteNote(id);
        File noteFile = new File(mContext.getFilesDir().getPath() + "/notes/" + Long.toString(id));
        if (noteFile.exists()) {
            noteFile.delete();
        }
    }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            PopupMenu.OnMenuItemClickListener {
        LinearLayout layout;
        TextView title, shortDesc;
        ImageView threeDots;
        long noteId;
        String noteTitle;

        public NoteViewHolder(View v) {
            super(v);

            layout = (LinearLayout) v.findViewById(R.id.layout);
            title = (TextView) v.findViewById(R.id.title);
            shortDesc = (TextView) v.findViewById(R.id.short_desc);
            threeDots = (ImageView) v.findViewById(R.id.three_dots);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onNoteSelected(noteId, noteTitle);
                    }
                }
            });

            threeDots.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (R.id.three_dots == view.getId()) {
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.inflate(R.menu.note_menu);
                popup.setOnMenuItemClickListener(this);
                popup.show();
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.delete:
                    deleteNote(noteId);
                    data.remove(getLayoutPosition());
                    notifyDataSetChanged();
                    break;
                case R.id.other:
                    Toast.makeText(mContext, "Other feature not implemented", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    }

    public interface OnNoteSelectedListener {
        void onNoteSelected(long noteId, String title);
    }
}
