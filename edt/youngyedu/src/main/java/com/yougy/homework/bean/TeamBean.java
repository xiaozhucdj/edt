package com.yougy.homework.bean;

public class TeamBean {

        /*"teamStatus": "启用",
        "teamStatusCode": "SO01",
        "teamParent": 46,
        "teamId": 47,
        "teamLevel": 2,
        "teamAdmin": 10000459,
        "teamDisplay": "白银之手",
        "teamName": "白银之手",
        "teamCreateTime": "2018-10-24 10:27:54",
        "teamModifyTime": "2018-10-24 10:27:54"*/



        private String teamStatus;
        private String teamStatusCode;
        private String teamDisplay;
        private String teamName;
        private String teamCreateTime;
        private String teamModifyTime;
        private int teamParent;
        private int teamId;
        private int teamLevel;

    public String getTeamStatus() {
        return teamStatus;
    }

    public void setTeamStatus(String teamStatus) {
        this.teamStatus = teamStatus;
    }

    public String getTeamStatusCode() {
        return teamStatusCode;
    }

    public void setTeamStatusCode(String teamStatusCode) {
        this.teamStatusCode = teamStatusCode;
    }

    public String getTeamDisplay() {
        return teamDisplay;
    }

    public void setTeamDisplay(String teamDisplay) {
        this.teamDisplay = teamDisplay;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamCreateTime() {
        return teamCreateTime;
    }

    public void setTeamCreateTime(String teamCreateTime) {
        this.teamCreateTime = teamCreateTime;
    }

    public String getTeamModifyTime() {
        return teamModifyTime;
    }

    public void setTeamModifyTime(String teamModifyTime) {
        this.teamModifyTime = teamModifyTime;
    }

    public int getTeamParent() {
        return teamParent;
    }

    public void setTeamParent(int teamParent) {
        this.teamParent = teamParent;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getTeamLevel() {
        return teamLevel;
    }

    public void setTeamLevel(int teamLevel) {
        this.teamLevel = teamLevel;
    }
}
