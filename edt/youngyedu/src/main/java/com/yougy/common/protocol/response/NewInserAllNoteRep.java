package com.yougy.common.protocol.response;

import java.util.List;

/**
 * Created by Administrator on 2017/5/22.
 */

public class NewInserAllNoteRep extends NewBaseRep {
    private List<Data>  data ;

    public List<Data> getData() {
        return data;
    }

    public static class  Data{
        private int noteId ;

        public int getNoteId() {
            return noteId;
        }
    }
}
