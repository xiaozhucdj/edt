package com.yougy.task.bean;

public class ReadTimeBean {

    /*
    *  "msg": "success",
    "data": {
        "insert_id": 0,
        "affected_rows": 1,
        "message": "Rows matched: 1  Changed: 1  Warnings: 0",
        "server_status": 34,
        "warning_count": 0
    },
    "code": 200
    * */

    private int insert_id;
    private int affected_rows;
    private String message;
    private int server_status;
    private int warning_count;

    public int getInsert_id() {
        return insert_id;
    }

    public void setInsert_id(int insert_id) {
        this.insert_id = insert_id;
    }

    public int getAffected_rows() {
        return affected_rows;
    }

    public void setAffected_rows(int affected_rows) {
        this.affected_rows = affected_rows;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getServer_status() {
        return server_status;
    }

    public void setServer_status(int server_status) {
        this.server_status = server_status;
    }

    public int getWarning_count() {
        return warning_count;
    }

    public void setWarning_count(int warning_count) {
        this.warning_count = warning_count;
    }
}
