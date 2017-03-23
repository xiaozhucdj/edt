package com.inkscreen.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xcz on 2016/11/16.
 */
public class RecordInfo implements Serializable{

    int ret_code;
    String ret_msg;

    public Ret ret;

    public static class Ret{
        String clazzCount;
        public History history;
        int total;
        int totalPage;
        int pageSize;
        int currentPage;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public static class History{


        }

        String historyCount;

        public List<Items> items;

        public static class Items{

            boolean autoManualAllCorrected;
            Integer completionCount;
            int completionRate;
            String completionRateTitle;
            boolean corrected;
            long createAt;
            int halfWrongCount;

            public HomeWork homework;

            public static class HomeWork{

                boolean allCommitMement;
                String correctingCount;
                String correctingType;
                int courseId;
                long createAt;
                String deadTime;
                long deadline;
                String delStatus;
                float difficulty;
                String distributeCount;
                String exerciseId;
                int halfWrongCount;
                boolean hasQuestionAnswering;
                String homeworkClazzId;
                int homeworkTime;
                String id;
                String name;
                String questionCount;
                int rightCount;
                String rightRateTitle;
                String sectionName;
                boolean showIssue;
                long startTime;
                String status;
                String statusName;
                long textbookCode;
                int type;
                int wrongCount;





                public List<KnowledgePoint> knowledgePoint;


                public static class  KnowledgePoint{

                    String code;
                    String difficulty;
                    String focalDifficult;
                    String name;
                    String pcode;
                    int phaseCode;
                    String status;
                    String studyDifficulty;
                    int subjectCode;

                    public String getCode() {
                        return code;
                    }

                    public void setCode(String code) {
                        this.code = code;
                    }

                    public String getDifficulty() {
                        return difficulty;
                    }

                    public void setDifficulty(String difficulty) {
                        this.difficulty = difficulty;
                    }

                    public String getFocalDifficult() {
                        return focalDifficult;
                    }

                    public void setFocalDifficult(String focalDifficult) {
                        this.focalDifficult = focalDifficult;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getPcode() {
                        return pcode;
                    }

                    public void setPcode(String pcode) {
                        this.pcode = pcode;
                    }

                    public int getPhaseCode() {
                        return phaseCode;
                    }

                    public void setPhaseCode(int phaseCode) {
                        this.phaseCode = phaseCode;
                    }

                    public String getStatus() {
                        return status;
                    }

                    public void setStatus(String status) {
                        this.status = status;
                    }

                    public String getStudyDifficulty() {
                        return studyDifficulty;
                    }

                    public void setStudyDifficulty(String studyDifficulty) {
                        this.studyDifficulty = studyDifficulty;
                    }

                    public int getSubjectCode() {
                        return subjectCode;
                    }

                    public void setSubjectCode(int subjectCode) {
                        this.subjectCode = subjectCode;
                    }
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getQuestionCount() {
                    return questionCount;
                }

                public void setQuestionCount(String questionCount) {
                    this.questionCount = questionCount;
                }

                public int getRightCount() {
                    return rightCount;
                }

                public void setRightCount(int rightCount) {
                    this.rightCount = rightCount;
                }

                public String getRightRateTitle() {
                    return rightRateTitle;
                }

                public void setRightRateTitle(String rightRateTitle) {
                    this.rightRateTitle = rightRateTitle;
                }

                public String getSectionName() {
                    return sectionName;
                }

                public void setSectionName(String sectionName) {
                    this.sectionName = sectionName;
                }

                public boolean isShowIssue() {
                    return showIssue;
                }

                public void setShowIssue(boolean showIssue) {
                    this.showIssue = showIssue;
                }

                public long getStartTime() {
                    return startTime;
                }

                public void setStartTime(long startTime) {
                    this.startTime = startTime;
                }

                public String getStatus() {
                    return status;
                }

                public void setStatus(String status) {
                    this.status = status;
                }

                public String getStatusName() {
                    return statusName;
                }

                public void setStatusName(String statusName) {
                    this.statusName = statusName;
                }

                public long getTextbookCode() {
                    return textbookCode;
                }

                public void setTextbookCode(long textbookCode) {
                    this.textbookCode = textbookCode;
                }

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                public int getWrongCount() {
                    return wrongCount;
                }

                public void setWrongCount(int wrongCount) {
                    this.wrongCount = wrongCount;
                }

                public boolean isAllCommitMement() {
                    return allCommitMement;
                }

                public void setAllCommitMement(boolean allCommitMement) {
                    this.allCommitMement = allCommitMement;
                }

                public String getCorrectingCount() {
                    return correctingCount;
                }

                public void setCorrectingCount(String correctingCount) {
                    this.correctingCount = correctingCount;
                }

                public String getCorrectingType() {
                    return correctingType;
                }

                public void setCorrectingType(String correctingType) {
                    this.correctingType = correctingType;
                }

                public int getCourseId() {
                    return courseId;
                }

                public void setCourseId(int courseId) {
                    this.courseId = courseId;
                }

                public long getCreateAt() {
                    return createAt;
                }

                public void setCreateAt(long createAt) {
                    this.createAt = createAt;
                }

                public String getDeadTime() {
                    return deadTime;
                }

                public void setDeadTime(String deadTime) {
                    this.deadTime = deadTime;
                }

                public long getDeadline() {
                    return deadline;
                }

                public void setDeadline(long deadline) {
                    this.deadline = deadline;
                }

                public String getDelStatus() {
                    return delStatus;
                }

                public void setDelStatus(String delStatus) {
                    this.delStatus = delStatus;
                }

                public float getDifficulty() {
                    return difficulty;
                }

                public void setDifficulty(float difficulty) {
                    this.difficulty = difficulty;
                }

                public String getDistributeCount() {
                    return distributeCount;
                }

                public void setDistributeCount(String distributeCount) {
                    this.distributeCount = distributeCount;
                }

                public String getExerciseId() {
                    return exerciseId;
                }

                public void setExerciseId(String exerciseId) {
                    this.exerciseId = exerciseId;
                }

                public int getHalfWrongCount() {
                    return halfWrongCount;
                }

                public void setHalfWrongCount(int halfWrongCount) {
                    this.halfWrongCount = halfWrongCount;
                }

                public boolean isHasQuestionAnswering() {
                    return hasQuestionAnswering;
                }

                public void setHasQuestionAnswering(boolean hasQuestionAnswering) {
                    this.hasQuestionAnswering = hasQuestionAnswering;
                }

                public String getHomeworkClazzId() {
                    return homeworkClazzId;
                }

                public void setHomeworkClazzId(String homeworkClazzId) {
                    this.homeworkClazzId = homeworkClazzId;
                }

                public int getHomeworkTime() {
                    return homeworkTime;
                }

                public void setHomeworkTime(int homeworkTime) {
                    this.homeworkTime = homeworkTime;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public List<KnowledgePoint> getKnowledgePoint() {
                    return knowledgePoint;
                }

                public void setKnowledgePoint(List<KnowledgePoint> knowledgePoint) {
                    this.knowledgePoint = knowledgePoint;
                }
            }

            String homeworkId;
            String id;
            int rank;
            int rightCount;
            String rightRateTitle;
            String status;
            String statusName;
            boolean studentCorrected;
            String studentId;
            long updateAt;
            int wrongCount;


            public boolean isAutoManualAllCorrected() {
                return autoManualAllCorrected;
            }

            public void setAutoManualAllCorrected(boolean autoManualAllCorrected) {
                this.autoManualAllCorrected = autoManualAllCorrected;
            }

            public Integer getCompletionCount() {
                return completionCount;
            }

            public void setCompletionCount(Integer completionCount) {
                this.completionCount = completionCount;
            }

            public int getCompletionRate() {
                return completionRate;
            }

            public void setCompletionRate(int completionRate) {
                this.completionRate = completionRate;
            }

            public String getCompletionRateTitle() {
                return completionRateTitle;
            }

            public void setCompletionRateTitle(String completionRateTitle) {
                this.completionRateTitle = completionRateTitle;
            }

            public boolean isCorrected() {
                return corrected;
            }

            public void setCorrected(boolean corrected) {
                this.corrected = corrected;
            }

            public long getCreateAt() {
                return createAt;
            }

            public void setCreateAt(long createAt) {
                this.createAt = createAt;
            }

            public int getHalfWrongCount() {
                return halfWrongCount;
            }

            public void setHalfWrongCount(int halfWrongCount) {
                this.halfWrongCount = halfWrongCount;
            }

            public HomeWork getHomeWork() {
                return homework;
            }

            public void setHomeWork(HomeWork homeWork) {
                this.homework = homeWork;
            }

            public String getHomeworkId() {
                return homeworkId;
            }

            public void setHomeworkId(String homeworkId) {
                this.homeworkId = homeworkId;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public int getRank() {
                return rank;
            }

            public void setRank(int rank) {
                this.rank = rank;
            }

            public int getRightCount() {
                return rightCount;
            }

            public void setRightCount(int rightCount) {
                this.rightCount = rightCount;
            }

            public String getRightRateTitle() {
                return rightRateTitle;
            }

            public void setRightRateTitle(String rightRateTitle) {
                this.rightRateTitle = rightRateTitle;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getStatusName() {
                return statusName;
            }

            public void setStatusName(String statusName) {
                this.statusName = statusName;
            }

            public boolean isStudentCorrected() {
                return studentCorrected;
            }

            public void setStudentCorrected(boolean studentCorrected) {
                this.studentCorrected = studentCorrected;
            }

            public String getStudentId() {
                return studentId;
            }

            public void setStudentId(String studentId) {
                this.studentId = studentId;
            }

            public long getUpdateAt() {
                return updateAt;
            }

            public void setUpdateAt(long updateAt) {
                this.updateAt = updateAt;
            }

            public int getWrongCount() {
                return wrongCount;
            }

            public void setWrongCount(int wrongCount) {
                this.wrongCount = wrongCount;
            }
        }

        public String getClazzCount() {
            return clazzCount;
        }

        public void setClazzCount(String clazzCount) {
            this.clazzCount = clazzCount;
        }

        public History getHistory() {
            return history;
        }

        public void setHistory(History history) {
            this.history = history;
        }

        public String getHistoryCount() {
            return historyCount;
        }

        public void setHistoryCount(String historyCount) {
            this.historyCount = historyCount;
        }

//        public List<Todo> getTodo() {
//            return todo;
//        }
//
//        public void setTodo(List<Todo> todo) {
//            this.todo = todo;
//        }


        public List<Items> getItems() {
            return items;
        }

        public void setItems(List<Items> items) {
            this.items = items;
        }
    }

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public String getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public Ret getRet() {
        return ret;
    }

    public void setRet(Ret ret) {
        this.ret = ret;
    }


//    String  workTitle;
//    String workTime;
//    String workAmount;
//
//    public WorkInfo(String workTitle, String workTime, String workAmount) {
//        this.workTitle = workTitle;
//        this.workTime = workTime;
//        this.workAmount = workAmount;
//    }
//
//    public WorkInfo() {
//
//    }
//
//    public String getWorkTitle() {
//        return workTitle;
//    }
//
//    public void setWorkTitle(String workTitle) {
//        this.workTitle = workTitle;
//    }
//
//    public String getWorkTime() {
//        return workTime;
//    }
//
//    public void setWorkTime(String workTime) {
//        this.workTime = workTime;
//    }
//
//    public String getWorkAmount() {
//        return workAmount;
//    }
//
//    public void setWorkAmount(String workAmount) {
//        this.workAmount = workAmount;
//    }
}
