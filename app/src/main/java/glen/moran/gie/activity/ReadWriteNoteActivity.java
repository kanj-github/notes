package glen.moran.gie.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import glen.moran.gie.R;
import glen.moran.gie.fragment.ReadWriteNoteFragment;

public class ReadWriteNoteActivity extends AppCompatActivity {
    public static final String EXTRA_NOTE_ID = "EXTRA_NOTE_ID";
    public static final String EXTRA_NOTE_TITLE = "EXTRA_NOTE_TITLE";
    public static final String EXTRA_DOES_EXIST = "EXTRA_DOES_EXIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_read_write_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Fragment frag;

        frag = getSupportFragmentManager().findFragmentByTag("TAG");
        if (frag != null) {
            // show retained fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.frag, frag, "TAG").commit();
        } else {
            Intent i = getIntent();
            boolean doesExist = i.getBooleanExtra(EXTRA_DOES_EXIST, false);
            if (doesExist) {
                frag = ReadWriteNoteFragment.newInstance(
                        i.getLongExtra(EXTRA_NOTE_ID, -1),
                        i.getStringExtra(EXTRA_NOTE_TITLE),
                        true
                );
            } else {
                frag = ReadWriteNoteFragment.newInstance(-1, null, false);
            }

            getSupportFragmentManager().beginTransaction().add(R.id.frag, frag, "TAG").commit();
        }
    }
}
