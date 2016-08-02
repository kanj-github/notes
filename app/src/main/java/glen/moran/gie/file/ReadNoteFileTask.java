package glen.moran.gie.file;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.Scanner;

import glen.moran.gie.R;

/**
 * Created by voldemort on 2/8/16.
 */
public class ReadNoteFileTask extends AsyncTask<Long, Void, String> {
    private Context mContext;
    private final WeakReference<TextView> textViewReference;

    public ReadNoteFileTask(Context mContext, TextView tv) {
        this.mContext = mContext;
        textViewReference = new WeakReference<TextView>(tv);
    }

    @Override
    protected String doInBackground(Long... longs) {
        File directory = new File(mContext.getFilesDir().getPath() + "/notes");

        if (!directory.exists()) {
            return null;
        }

        if (directory.isDirectory()) {
            File noteFile = new File(directory, Long.toString(longs[0]));
            String text;

            try {
                Scanner scanner = new Scanner(new FileInputStream(noteFile)).useDelimiter("\\A");
                text = scanner.next();
                scanner.close();
                return text;
            } catch (FileNotFoundException fnfe) {
                return null;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        final TextView tv = textViewReference.get();
        if (tv != null) {
            if (s != null) {
                tv.setText(s);
            } else {
                tv.setText(R.string.err);
            }
        }
    }
}
