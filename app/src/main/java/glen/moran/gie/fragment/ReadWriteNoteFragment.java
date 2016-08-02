package glen.moran.gie.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import glen.moran.gie.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReadWriteNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadWriteNoteFragment extends Fragment {
    private static final String ARG_NOTE_ID = "NOTE_ID";
    private static final String ARG_DOES_EXIST = "DOES_EXIST";

    private long noteId;
    private boolean doesExist;

    public ReadWriteNoteFragment() {
        // Required empty public constructor
    }

    public static ReadWriteNoteFragment newInstance(long noteId, boolean doesExist) {
        ReadWriteNoteFragment fragment = new ReadWriteNoteFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_NOTE_ID, noteId);
        args.putBoolean(ARG_DOES_EXIST, doesExist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            noteId = getArguments().getLong(ARG_NOTE_ID);
            doesExist = getArguments().getBoolean(ARG_DOES_EXIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_read_write_note, container, false);
    }

}
