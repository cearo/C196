package com.cearo.owlganizer.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "notes", foreignKeys =
    @ForeignKey(
        entity = Course.class,
        parentColumns = "course_id",
        childColumns = "parent_course_id",
        onDelete = CASCADE
    )
)
public class Note {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    private long noteId;
    @NonNull
    private String title;
    @NonNull
    @ColumnInfo(name = "message_body")
    private String messageBody;
    @ColumnInfo(name = "parent_course_id")
    private long parentCourseId;

    public Note(){}
    @Ignore
    public Note(@NonNull String title, @NonNull String messageBody, long parentCourseId) {
        this.title = title;
        this.messageBody = messageBody;
        this.parentCourseId = parentCourseId;
        this.noteId = 0;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(@NonNull String messageBody) {
        this.messageBody = messageBody;
    }


    public long getParentCourseId() {
        return parentCourseId;
    }

    public void setParentCourseId(long parentCourseId) {
        this.parentCourseId = parentCourseId;
    }
}
