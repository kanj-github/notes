package glen.moran.gie.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import glen.moran.gie.R;
import glen.moran.gie.activity.ReadWriteNoteActivity;
import glen.moran.gie.adapter.NotesListAdapter;
import glen.moran.gie.db.DBContract;
import glen.moran.gie.db.DbManager;
import glen.moran.gie.model.NoteListItem;

public class ListFragment extends Fragment implements NotesListAdapter.OnNoteSelectedListener {
    private RecyclerView listView;
    private Button createNewButton;
    private PopulateListTask populateListTask;
    private boolean needReload;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        listView = (RecyclerView) v.findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setHasFixedSize(true);
        loadList();

        createNewButton = (Button) v.findViewById(R.id.button);
        createNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                needReload = true;
                Intent i = new Intent(getContext(), ReadWriteNoteActivity.class);
                i.putExtra(ReadWriteNoteActivity.EXTRA_DOES_EXIST, false);
                startActivity(i);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needReload) {
            loadList();
        }
    }

    @Override
    public void onDestroy() {
        // Stop the async task if it's running, it will be recreated in onCreateView
        if (populateListTask != null && !populateListTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
            populateListTask.cancel(true);
        }
        super.onDestroy();
    }

    private void loadList() {
        populateListTask = new PopulateListTask(
                getContext(),
                listView
        );
        populateListTask.execute();
        needReload = false;
    }

    @Override
    public void onNoteSelected(long noteId, String title) {
        needReload = true;
        Intent i = new Intent(getContext(), ReadWriteNoteActivity.class);
        i.putExtra(ReadWriteNoteActivity.EXTRA_DOES_EXIST, true);
        i.putExtra(ReadWriteNoteActivity.EXTRA_NOTE_ID, noteId);
        i.putExtra(ReadWriteNoteActivity.EXTRA_NOTE_TITLE, title);
        startActivity(i);
    }

    class PopulateListTask extends AsyncTask<Void, Void, Void> {
        private Context mContext;
        private WeakReference<RecyclerView> listViewReference;

        private ArrayList<NoteListItem> notes;

        public PopulateListTask(Context mContext, RecyclerView listView) {
            this.mContext = mContext;
            this.listViewReference = new WeakReference<RecyclerView>(listView);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Cursor c = DbManager.getInstance(mContext).getAllNotes();

            if (c == null) {
                return null;
            } else {
                notes = new ArrayList<>();
                c.moveToPosition(-1);
                while (c.moveToNext()) {
                    notes.add(new NoteListItem(
                            c.getLong(c.getColumnIndex(DBContract.COL_NAME_ID)),
                            c.getString(c.getColumnIndex(DBContract.COL_NAME_TITLE)),
                            c.getString(c.getColumnIndex(DBContract.COL_NAME_SHORT_DESC))
                    ));
                }
                c.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            RecyclerView listView = listViewReference.get();
            if (listView != null) {
                listView.setAdapter(new NotesListAdapter(
                        notes,
                        mContext,
                        ListFragment.this
                ));
            }
        }
    }
}
