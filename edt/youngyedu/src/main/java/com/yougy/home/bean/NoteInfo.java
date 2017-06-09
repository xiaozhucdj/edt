package com.yougy.home.bean;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

import java.io.Serializable;


/**
 * Created by Administrator on 2016/11/9.
 * 获取服务器的笔记列表详情信息
 * 注释:
 * 1.当noteOwner参数与当前用户编码一致时，此笔记才是笔记原作。
 * 当前，需要使用不到  2016.11/9
 * (..........................................................)
 * 2.当noteCreator参数与当前用户编码一致时，才允许删除此笔记。
 * noteCreator == userid 表示自己创建的 ，可以删除
 * (..........................................................)
 * 3.bookId，categoryId参数都存在时，该笔记是与固定图书绑定的独立笔记。
 * 按照学科 有服务器创建的笔记在笔记列表可以获取（非在书上书写的笔记）
 * ((..........................................................)
 * 4.仅有bookId参数，该笔记是与固定图书绑定的内部笔记。
 * 在“书”上所使用的笔记
 * ((..........................................................)
 * 5.仅有categoryId参数，该笔记是与固定科目绑定的独立笔记。
 * categoryId代表学科 ，笔记未绑定图书
 * (..........................................................)
 * 6.termIndex参数为0,将追加用户本学期笔记。目前仅支持参数0。
 * 传-1 那么服务器将图书存放在全部课本里
 * (..........................................................)
 * <p>
 * //服务器还需提供参数列表：
 * 1.笔记样式 : noteContent：暂时作为提供样式使用  0 1 2 3
 */
public class NoteInfo extends DataSupport implements Serializable{
    ////////////////////////////////////服务器提供参数//////////////////////////////////////////////////
    /**
     * 笔记id
     */
    private int noteId = -1;
    /***
     * 笔记名称
     */
    private String noteTitle;
    /**
     * 笔记内容
     */
    private String noteContent;
    /**
     * 笔记所有者(用户编码)
     */
    private int noteAuthor = -1;
    /**
     * 笔记创建者(用户编码)
     */
    private int noteCreator = -1;
    /**
     * 笔记匹配年级索引
     */
    private int noteFitGradeId = -1;
    /**
     * 笔记匹配年级名
     */
    private String noteFitGradeName;
    /***
     * 笔记匹配学科索
     */
    private int noteFitSubjectId = -1;
    /***
     * 笔记匹配学科名称
     */
    private String noteFitSubjectName;
    /***
     * 关联图书编码
     */
    @SerializedName("courseBookId")
    private int bookId = -1;
    /**
     * 关联分类编码
     */
    @SerializedName("noteFitCourseId")
    private int bookCategory = -1;
    /**
     * 关联学期索引
     */
    private int termIndex = 0;

    /**
     * 笔记类型 ,1 内部笔记 ，2外部对应书笔记
     */
    private int noteType = 0;


    ///////////////////////////////////本地参数///////////////////////////////////////////////
    /**
     * 笔记的类型 ,用于创建笔记显示判断
     */
    private NoteStyleOption noteStyleOption = NoteStyleOption.NOTE_TYPE_BLANK;
    /**
     * 最后一个item 创建笔记
     */
    private boolean isAddView;

//--------------------------------------------------------------------------------------------

    /**笔记标记 (内部笔记ID)*/
    private   long noteMark =-1		;
    /**笔记样式*/
    private  int noteStyle ;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteInfo info = (NoteInfo) o;

        if (noteId != info.noteId) return false;
        if (noteAuthor != info.noteAuthor) return false;
        if (noteCreator != info.noteCreator) return false;
        if (noteFitGradeId != info.noteFitGradeId) return false;
        if (noteFitSubjectId != info.noteFitSubjectId) return false;
        if (bookId != info.bookId) return false;
        if (bookCategory != info.bookCategory) return false;
        if (termIndex != info.termIndex) return false;
        if (noteType != info.noteType) return false;
        if (isAddView != info.isAddView) return false;
        if (noteTitle != null ? !noteTitle.equals(info.noteTitle) : info.noteTitle != null)
            return false;
        if (noteContent != null ? !noteContent.equals(info.noteContent) : info.noteContent != null)
            return false;
        if (noteFitGradeName != null ? !noteFitGradeName.equals(info.noteFitGradeName) : info.noteFitGradeName != null)
            return false;
        if (noteFitSubjectName != null ? !noteFitSubjectName.equals(info.noteFitSubjectName) : info.noteFitSubjectName != null)
            return false;
        return noteStyleOption == info.noteStyleOption;

    }

    @Override
    public int hashCode() {
        int result = noteId;
        result = 31 * result + (noteTitle != null ? noteTitle.hashCode() : 0);
        result = 31 * result + (noteContent != null ? noteContent.hashCode() : 0);
        result = 31 * result + noteAuthor;
        result = 31 * result + noteCreator;
        result = 31 * result + noteFitGradeId;
        result = 31 * result + (noteFitGradeName != null ? noteFitGradeName.hashCode() : 0);
        result = 31 * result + noteFitSubjectId;
        result = 31 * result + (noteFitSubjectName != null ? noteFitSubjectName.hashCode() : 0);
        result = 31 * result + bookId;
        result = 31 * result + bookCategory;
        result = 31 * result + termIndex;
        result = 31 * result + noteType;
        result = 31 * result + (noteStyleOption != null ? noteStyleOption.hashCode() : 0);
        result = 31 * result + (isAddView ? 1 : 0);
        return result;
    }

    public int getNoteType() {
        return noteType;
    }
  /*  noteType参数表示笔记类型，1代表内部笔记，2代表独立笔记。*/
    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public int getNoteAuthor() {
        return noteAuthor;
    }

    public void setNoteAuthor(int noteAuthor) {
        this.noteAuthor = noteAuthor;
    }

    public int getNoteCreator() {
        return noteCreator;
    }

    public void setNoteCreator(int noteCreator) {
        this.noteCreator = noteCreator;
    }

    public int getNoteFitGradeId() {
        return noteFitGradeId;
    }

    public void setNoteFitGradeId(int noteFitGradeId) {
        this.noteFitGradeId = noteFitGradeId;
    }

    public String getNoteFitGradeName() {
        return noteFitGradeName;
    }

    public void setNoteFitGradeName(String noteFitGradeName) {
        this.noteFitGradeName = noteFitGradeName;
    }

    public int getNoteFitSubjectId() {
        return noteFitSubjectId;
    }

    public void setNoteFitSubjectId(int noteFitSubjectId) {
        this.noteFitSubjectId = noteFitSubjectId;
    }

    public String getNoteFitSubjectName() {
        return noteFitSubjectName;
    }

    public void setNoteFitSubjectName(String noteFitSubjectName) {
        this.noteFitSubjectName = noteFitSubjectName;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(int bookCategory) {
        this.bookCategory = bookCategory;
    }

    public int getTermIndex() {
        return termIndex;
    }

    public void setTermIndex(int termIndex) {
        this.termIndex = termIndex;
    }

    public NoteStyleOption getNoteStyleOption() {
        return noteStyleOption;
    }

    public int setNoteStyleOption(NoteStyleOption noteTypeOption) {

        this.noteStyleOption = noteTypeOption;

        if (noteTypeOption == NoteStyleOption.NOTE_TYPE_BLANK) {
            noteStyle = 0;
        }

        if (noteTypeOption == NoteStyleOption.NOTE_TYPE_LINE) {
            noteStyle = 1;
        }


        if (noteTypeOption == NoteStyleOption.NOTE_TYPE_GRID) {
            noteStyle = 2;
        }
        return noteStyle ;
    }

    public boolean isAddView() {
        return isAddView;
    }

    public void setAddView(boolean addView) {
        isAddView = addView;
    }


    //////////////////////////////////////内部枚举笔记样式////////////////////////////////////////////////////

    /***
     * 笔记类型 ，英文  白纸等。。
     */
    public enum  NoteStyleOption {
        /**
         * 空白
         */
        NOTE_TYPE_BLANK,

        /**
         * 横线
         */
        NOTE_TYPE_LINE,

        /**
         * 田子格
         */
        NOTE_TYPE_GRID,

    }

    public long getNoteMark() {
        return noteMark;
    }

    public void setNoteMark(long noteMark) {
        this.noteMark = noteMark;
    }

    public int getNoteStyle() {
        return noteStyle;
    }

    public void setNoteStyle(int noteStyle) {
        this.noteStyle = noteStyle;
    }

    @Override
    public String toString() {
        return "NoteInfo{" +
                "noteId=" + noteId +
                ", noteTitle='" + noteTitle + '\'' +
                ", noteContent='" + noteContent + '\'' +
                ", noteAuthor=" + noteAuthor +
                ", noteCreator=" + noteCreator +
                ", noteFitGradeId=" + noteFitGradeId +
                ", noteFitGradeName='" + noteFitGradeName + '\'' +
                ", noteFitSubjectId=" + noteFitSubjectId +
                ", noteFitSubjectName='" + noteFitSubjectName + '\'' +
                ", bookId=" + bookId +
                ", bookCategory=" + bookCategory +
                ", termIndex=" + termIndex +
                ", noteType=" + noteType +
                ", noteStyleOption=" + noteStyleOption +
                ", isAddView=" + isAddView +
                ", noteMark=" + noteMark +
                ", noteStyle=" + noteStyle +
                '}';
    }

    /**
     * 作业ID
     */
  private  int   noteFitHomeworkId  ;

    public int getNoteFitHomeworkId() {
        return noteFitHomeworkId;
    }

    public void setNoteFitHomeworkId(int noteFitHomeworkId) {
        this.noteFitHomeworkId = noteFitHomeworkId;
    }
}
