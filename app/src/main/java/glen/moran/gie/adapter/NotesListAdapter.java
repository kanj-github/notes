package glen.moran.gie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import glen.moran.gie.R;
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

    class NoteViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView title, shortDesc;
        long noteId;
        String noteTitle;

        public NoteViewHolder(View v) {
            super(v);

            layout = (LinearLayout) v.findViewById(R.id.layout);
            title = (TextView) v.findViewById(R.id.title);
            shortDesc = (TextView) v.findViewById(R.id.short_desc);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onNoteSelected(noteId, noteTitle);
                    }
                }
            });
        }
    }

    public interface OnNoteSelectedListener {
        void onNoteSelected(long noteId, String title);
    }
}
