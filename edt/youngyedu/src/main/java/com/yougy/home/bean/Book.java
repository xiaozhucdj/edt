package com.yougy.home.bean;

import android.util.SparseArray;

import org.litepal.crud.DataSupport;

import java.util.List;


/**
 * Created by jiangliang on 2016/8/12.
 */
public class Book extends DataSupport {
    private int id;
    private String name;

    private List<Note> noteList;
    private SparseArray<Note> notesArray = new SparseArray<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Note> getNoteList() {
        if (noteList == null) {
            noteList = DataSupport.findAll(Note.class);
            for (Note note : noteList) {
                notesArray.put(note.getBookPageNum(), note);
            }
            return noteList;
        }
        return noteList;
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }

    public Note getNote(int page) {
        return notesArray.get(page);
    }

    public SparseArray<Note> getNotesArray() {
        if (noteList == null) {
            noteList = DataSupport.where("book_id = ? ", String.valueOf(id)).find(Note.class);
            for (Note note : noteList) {
                notesArray.put(note.getBookPageNum(), note);
            }
        }
        return notesArray;
    }

    public void setNotesArray(SparseArray<Note> notesArray) {
        this.notesArray = notesArray;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", noteList=" + noteList +
                ", notesArray=" + notesArray +
                '}';
    }
}
