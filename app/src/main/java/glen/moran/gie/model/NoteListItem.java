package glen.moran.gie.model;

/**
 * Created by kanj on 3/8/16.
 */
public class NoteListItem {
    public long noteId;
    public String title;
    public String shortDesc;

    public NoteListItem(long noteId, String title, String shortDesc) {
        this.noteId = noteId;
        this.title = title;
        this.shortDesc = shortDesc;
    }
}
