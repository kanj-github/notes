package glen.moran.gie.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import glen.moran.gie.R;
import glen.moran.gie.db.DbManager;
import glen.moran.gie.file.ReadNoteFileTask;
import glen.moran.gie.util.ProgressIndicator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReadWriteNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadWriteNoteFragment extends Fragment {
    private static final String ARG_NOTE_ID = "NOTE_ID";
    private static final String ARG_NOTE_TITLE = "NOTE_TITLE";
    private static final String ARG_DOES_EXIST = "DOES_EXIST";

    private static final String EDIT_TITLE_TEXT = "EDIT_TITLE_TEXT";
    private static final String EDIT_NOTE_TEXT = "EDIT_NOTE_TEXT";

    private long noteId;
    private String noteTitle;
    private boolean doesExist;
    private LinearLayout readLayout, writeLayout;
    private EditText etTitle, etText;
    private TextView tvTitle, tvText;
    private Button button;
    private boolean isEditing;
    private ReadNoteFileTask readNoteFileTask;

    public ReadWriteNoteFragment() {
        // Required empty public constructor
    }

    public static ReadWriteNoteFragment newInstance(long noteId, String title, boolean doesExist) {
        ReadWriteNoteFragment fragment = new ReadWriteNoteFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_NOTE_ID, noteId);
        args.putString(ARG_NOTE_TITLE, title);
        args.putBoolean(ARG_DOES_EXIST, doesExist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            noteId = getArguments().getLong(ARG_NOTE_ID);
            noteTitle = getArguments().getString(ARG_NOTE_TITLE);
            doesExist = getArguments().getBoolean(ARG_DOES_EXIST);
        }
        if (doesExist) {
            isEditing = false;
        } else {
            isEditing = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_read_write_note, container, false);

        readLayout = (LinearLayout) v.findViewById(R.id.read);
        writeLayout = (LinearLayout) v.findViewById(R.id.write);
        button = (Button) v.findViewById(R.id.button);
        etTitle = (EditText) v.findViewById(R.id.et_title);
        etText = (EditText) v.findViewById(R.id.et_text);
        tvTitle = (TextView) v.findViewById(R.id.tv_title);
        tvText = (TextView) v.findViewById(R.id.tv_text);

        if (isEditing) {
            readLayout.setVisibility(View.GONE);
            if (savedInstanceState != null) {
                etTitle.setText(savedInstanceState.getString(EDIT_TITLE_TEXT));
                etText.setText(savedInstanceState.getString(EDIT_NOTE_TEXT));
            } else {
                // This should happen only when new note is opened
                Log.w("Kanj", "no saved bundle but isEditing true in onCreateView");
            }
            button.setText("save");
        } else {
            writeLayout.setVisibility(View.GONE);
            tvTitle.setText(noteTitle);
            readNoteFileTask = new ReadNoteFileTask(getContext(), tvText);
            readNoteFileTask.execute(noteId);
            button.setText("edit");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleButtonClick();
            }
        });
        return v;
    }

    private void handleButtonClick() {
        if (isEditing) {
            ProgressIndicator.showProgressBar(getContext(), getContext().getString(R.string.saving));
            noteTitle = etTitle.getText().toString();
            final String text = etText.getText().toString();
            final String desc = (text.length() > 50) ? text.substring(0, 51) : text;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Context mContext = ReadWriteNoteFragment.this.getContext();
                    boolean status = true;
                    // Update in db
                    if (doesExist) {
                        DbManager.getInstance(mContext).updateNote(
                                noteId,
                                noteTitle,
                                desc
                        );
                    } else {
                        noteId = DbManager.getInstance(mContext).createNote(
                                noteTitle,
                                desc
                        );
                        if (noteId == -1) {
                            status = false;
                        } else {
                            doesExist = true;
                        }
                    }

                    // Write the entire text to file
                    File directory = new File(mContext.getFilesDir().getPath() + "/notes");

                    if (!directory.exists()) {
                        directory.mkdir();
                    }

                    File noteFile = new File(directory, Long.toString(noteId));
                    Log.v("Kanj", "Write to file " + noteFile.getAbsolutePath());
                    try {
                        OutputStreamWriter outStream = new OutputStreamWriter(new FileOutputStream(noteFile));
                        outStream.write(text, 0, text.length());
                        outStream.flush();
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        status = false;
                    }

                    onEditSaved(status);
                }
            }).start();
        } else {
            // Begin editing
            isEditing = true;
            button.setText("save");
            String text = tvText.getText().toString();
            readLayout.setVisibility(View.GONE);
            writeLayout.setVisibility(View.VISIBLE);
            etTitle.setText(noteTitle);
            etText.setText(text);
        }
    }

    private void onEditSaved(final boolean status) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isEditing = false;
                button.setText("edit");
                String text = etText.getText().toString();
                readLayout.setVisibility(View.VISIBLE);
                writeLayout.setVisibility(View.GONE);
                tvTitle.setText(noteTitle);
                tvText.setText(text);
                ProgressIndicator.hideProgressBar();
                String msg;
                if (status) {
                    msg = "Saved";
                } else {
                    msg = "Error occured";
                }
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (readNoteFileTask != null && !readNoteFileTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
            readNoteFileTask.cancel(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isEditing) {
            // Need to save user input
            outState.putString(EDIT_TITLE_TEXT, etTitle.getText().toString());
            outState.putString(EDIT_NOTE_TEXT, etText.getText().toString());
        }
    }
}
