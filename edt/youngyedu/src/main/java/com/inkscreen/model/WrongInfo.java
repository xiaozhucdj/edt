package com.inkscreen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xcz on 2016/11/16.
 */
public class WrongInfo implements Serializable{


    int ret_code;
    String ret_msg;

    public Ret ret;


    public static class Ret implements Serializable{
        int currentPage;
        int pageSize;
        int total;
        int totalPage;

        public List<Items> items;

        public static class Items implements Serializable{
            long createAt;
            String doNum;
            String exerciseNum;
            String id;
            String latestAnswerImg;
            String latestResult;
            String mistakeNum;
            String mistakePeople;
            String questionId;
            String source;
            String sourceTitle;
            long updateAt;

            private List<String> latestAnswer = new ArrayList<>();
            private List<String> latestItemResults = new ArrayList<>();



            public List<OcrHisAnswerImgs>  ocrHisAnswerImgs;
            public static class OcrHisAnswerImgs implements Serializable{

            }

            public  Question question;
            public static class Question implements  Serializable{

                public Subject subject;

                public static class Subject implements  Serializable{

                    int phaseCode;
                    int code;
                    String acronym;
                    int sequence;
                    String name;

                    public int getPhaseCode() {
                        return phaseCode;
                    }

                    public void setPhaseCode(int phaseCode) {
                        this.phaseCode = phaseCode;
                    }

                    public int getCode() {
                        return code;
                    }

                    public void setCode(int code) {
                        this.code = code;
                    }

                    public String getAcronym() {
                        return acronym;
                    }

                    public void setAcronym(String acronym) {
                        this.acronym = acronym;
                    }

                    public int getSequence() {
                        return sequence;
                    }

                    public void setSequence(int sequence) {
                        this.sequence = sequence;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }
                }


                boolean inStuFallQuestion;

                public List<Answers> answers;

                public static class Answers implements  Serializable{


                    String questionId;
                    String id;
                    String content;
                    int sequence;

                    public String getQuestionId() {
                        return questionId;
                    }

                    public void setQuestionId(String questionId) {
                        this.questionId = questionId;
                    }

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

                    public String getContent() {
                        return content;
                    }

                    public void setContent(String content) {
                        this.content = content;
                    }

                    public int getSequence() {
                        return sequence;
                    }

                    public void setSequence(int sequence) {
                        this.sequence = sequence;
                    }
                }



                float difficulty;

                String type;
                boolean isCollect;

               String analysis;
                boolean  subFlag;
                String questionSource;
                String id;
                String parentId;
                boolean correctQuestion;
                String checkStatus;
                boolean inQuestionCar;

                public List<NewKnowpoints> newKnowpoints;

                public static class NewKnowpoints implements  Serializable{
                    String status;
                    int sequence;
                    String name;
                    int phaseCode;
                    String studyDifficulty;
                    int subjectCode;
                    String pcode;
                    String code;
                    String focalDifficult;

                    public String getStatus() {
                        return status;
                    }

                    public void setStatus(String status) {
                        this.status = status;
                    }

                    public int getSequence() {
                        return sequence;
                    }

                    public void setSequence(int sequence) {
                        this.sequence = sequence;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public int getPhaseCode() {
                        return phaseCode;
                    }

                    public void setPhaseCode(int phaseCode) {
                        this.phaseCode = phaseCode;
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

                    public String getPcode() {
                        return pcode;
                    }

                    public void setPcode(String pcode) {
                        this.pcode = pcode;
                    }

                    public String getCode() {
                        return code;
                    }

                    public void setCode(String code) {
                        this.code = code;
                    }

                    public String getFocalDifficult() {
                        return focalDifficult;
                    }

                    public void setFocalDifficult(String focalDifficult) {
                        this.focalDifficult = focalDifficult;
                    }
                }

                public  QuestionType questionType;

                public  static  class  QuestionType  implements  Serializable{

                    int subjectCode;
                    String name;
                    int code;

                    public int getSubjectCode() {
                        return subjectCode;
                    }

                    public void setSubjectCode(int subjectCode) {
                        this.subjectCode = subjectCode;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public int getCode() {
                        return code;
                    }

                    public void setCode(int code) {
                        this.code = code;
                    }
                }

                long updateAt;
                String allMetaKnowpoint;
                boolean done;

                private List<String> choices = new ArrayList<>();

                public List<String> getChoices() {
                    return choices;
                }

                public void setChoices(List<String> choices) {
                    this.choices = choices;
                }

                String hint;
                String allNewKnowpoint;
                String code;
                String content;

                public List<MetaKnowpoints> metaKnowpoints;

                public static class MetaKnowpoints implements  Serializable{

                    int subjectCode;
                    String name;
                    int code;

                    public int getSubjectCode() {
                        return subjectCode;
                    }

                    public void setSubjectCode(int subjectCode) {
                        this.subjectCode = subjectCode;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public int getCode() {
                        return code;
                    }

                    public void setCode(int code) {
                        this.code = code;
                    }
                }

                long createAt;
                int answerNumber;

                private  List<String> allAnalysis = new ArrayList<>();

                String source;
                int sequence;


                public TextbookCategory textbookCategory;

                public static  class  TextbookCategory implements  Serializable{
                    int sequence;
                    String name;
                    int code;

                    private List<String> textbooks = new ArrayList<>();


                    public int getSequence() {
                        return sequence;
                    }

                    public void setSequence(int sequence) {
                        this.sequence = sequence;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public int getCode() {
                        return code;
                    }

                    public void setCode(int code) {
                        this.code = code;
                    }

                    public List<String> getTextbooks() {
                        return textbooks;
                    }

                    public void setTextbooks(List<String> textbooks) {
                        this.textbooks = textbooks;
                    }
                }



                private List<String> studentHomeworkAnswers = new ArrayList<>();



                private List<String> allHints = new ArrayList<>();




                public Phase phase;

                public static class Phase implements  Serializable{

                    int code;
                    String acronym;
                    int sequence;
                    String name;

                    public List<Subjects> subjects;
                    public static class Subjects implements  Serializable{

                    }


                    public int getCode() {
                        return code;
                    }

                    public void setCode(int code) {
                        this.code = code;
                    }

                    public String getAcronym() {
                        return acronym;
                    }

                    public void setAcronym(String acronym) {
                        this.acronym = acronym;
                    }

                    public int getSequence() {
                        return sequence;
                    }

                    public void setSequence(int sequence) {
                        this.sequence = sequence;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public List<Subjects> getSubjects() {
                        return subjects;
                    }

                    public void setSubjects(List<Subjects> subjects) {
                        this.subjects = subjects;
                    }
                }


                public Subject getSubject() {
                    return subject;
                }

                public void setSubject(Subject subject) {
                    this.subject = subject;
                }

                public boolean isInStuFallQuestion() {
                    return inStuFallQuestion;
                }

                public void setInStuFallQuestion(boolean inStuFallQuestion) {
                    this.inStuFallQuestion = inStuFallQuestion;
                }

                public List<Answers> getAnswers() {
                    return answers;
                }

                public void setAnswers(List<Answers> answers) {
                    this.answers = answers;
                }


                public float getDifficulty() {
                    return difficulty;
                }

                public void setDifficulty(float difficulty) {
                    this.difficulty = difficulty;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public boolean isCollect() {
                    return isCollect;
                }

                public void setIsCollect(boolean isCollect) {
                    this.isCollect = isCollect;
                }

                public String getAnalysis() {
                    return analysis;
                }

                public void setAnalysis(String analysis) {
                    this.analysis = analysis;
                }

                public boolean isSubFlag() {
                    return subFlag;
                }

                public void setSubFlag(boolean subFlag) {
                    this.subFlag = subFlag;
                }

                public String getQuestionSource() {
                    return questionSource;
                }

                public void setQuestionSource(String questionSource) {
                    this.questionSource = questionSource;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getParentId() {
                    return parentId;
                }

                public void setParentId(String parentId) {
                    this.parentId = parentId;
                }

                public boolean isCorrectQuestion() {
                    return correctQuestion;
                }

                public void setCorrectQuestion(boolean correctQuestion) {
                    this.correctQuestion = correctQuestion;
                }

                public String getCheckStatus() {
                    return checkStatus;
                }

                public void setCheckStatus(String checkStatus) {
                    this.checkStatus = checkStatus;
                }

                public boolean isInQuestionCar() {
                    return inQuestionCar;
                }

                public void setInQuestionCar(boolean inQuestionCar) {
                    this.inQuestionCar = inQuestionCar;
                }

                public List<NewKnowpoints> getNewKnowpoints() {
                    return newKnowpoints;
                }

                public void setNewKnowpoints(List<NewKnowpoints> newKnowpoints) {
                    this.newKnowpoints = newKnowpoints;
                }

                public QuestionType getQuestionType() {
                    return questionType;
                }

                public void setQuestionType(QuestionType questionType) {
                    this.questionType = questionType;
                }

                public long getUpdateAt() {
                    return updateAt;
                }

                public void setUpdateAt(long updateAt) {
                    this.updateAt = updateAt;
                }

                public String getAllMetaKnowpoint() {
                    return allMetaKnowpoint;
                }

                public void setAllMetaKnowpoint(String allMetaKnowpoint) {
                    this.allMetaKnowpoint = allMetaKnowpoint;
                }

                public boolean isDone() {
                    return done;
                }

                public void setDone(boolean done) {
                    this.done = done;
                }



                public String getHint() {
                    return hint;
                }

                public void setHint(String hint) {
                    this.hint = hint;
                }

                public String getAllNewKnowpoint() {
                    return allNewKnowpoint;
                }

                public void setAllNewKnowpoint(String allNewKnowpoint) {
                    this.allNewKnowpoint = allNewKnowpoint;
                }

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public List<MetaKnowpoints> getMetaKnowpoints() {
                    return metaKnowpoints;
                }

                public void setMetaKnowpoints(List<MetaKnowpoints> metaKnowpoints) {
                    this.metaKnowpoints = metaKnowpoints;
                }

                public long getCreateAt() {
                    return createAt;
                }

                public void setCreateAt(long createAt) {
                    this.createAt = createAt;
                }

                public int getAnswerNumber() {
                    return answerNumber;
                }

                public void setAnswerNumber(int answerNumber) {
                    this.answerNumber = answerNumber;
                }

                public List<String> getAllAnalysis() {
                    return allAnalysis;
                }

                public void setAllAnalysis(List<String> allAnalysis) {
                    this.allAnalysis = allAnalysis;
                }

                public String getSource() {
                    return source;
                }

                public void setSource(String source) {
                    this.source = source;
                }

                public int getSequence() {
                    return sequence;
                }

                public void setSequence(int sequence) {
                    this.sequence = sequence;
                }

                public TextbookCategory getTextbookCategory() {
                    return textbookCategory;
                }

                public void setTextbookCategory(TextbookCategory textbookCategory) {
                    this.textbookCategory = textbookCategory;
                }

                public List<String> getStudentHomeworkAnswers() {
                    return studentHomeworkAnswers;
                }

                public void setStudentHomeworkAnswers(List<String> studentHomeworkAnswers) {
                    this.studentHomeworkAnswers = studentHomeworkAnswers;
                }


                public List<String> getAllHints() {
                    return allHints;
                }

                public void setAllHints(List<String> allHints) {
                    this.allHints = allHints;
                }

                public Phase getPhase() {
                    return phase;
                }

                public void setPhase(Phase phase) {
                    this.phase = phase;
                }
            }

            public long getCreateAt() {
                return createAt;
            }

            public void setCreateAt(long createAt) {
                this.createAt = createAt;
            }

            public String getDoNum() {
                return doNum;
            }

            public void setDoNum(String doNum) {
                this.doNum = doNum;
            }

            public String getExerciseNum() {
                return exerciseNum;
            }

            public void setExerciseNum(String exerciseNum) {
                this.exerciseNum = exerciseNum;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLatestAnswerImg() {
                return latestAnswerImg;
            }

            public void setLatestAnswerImg(String latestAnswerImg) {
                this.latestAnswerImg = latestAnswerImg;
            }

            public String getLatestResult() {
                return latestResult;
            }

            public void setLatestResult(String latestResult) {
                this.latestResult = latestResult;
            }

            public String getMistakeNum() {
                return mistakeNum;
            }

            public void setMistakeNum(String mistakeNum) {
                this.mistakeNum = mistakeNum;
            }

            public String getMistakePeople() {
                return mistakePeople;
            }

            public void setMistakePeople(String mistakePeople) {
                this.mistakePeople = mistakePeople;
            }

            public String getQuestionId() {
                return questionId;
            }

            public void setQuestionId(String questionId) {
                this.questionId = questionId;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getSourceTitle() {
                return sourceTitle;
            }

            public void setSourceTitle(String sourceTitle) {
                this.sourceTitle = sourceTitle;
            }

            public long getUpdateAt() {
                return updateAt;
            }

            public void setUpdateAt(long updateAt) {
                this.updateAt = updateAt;
            }

            public List<String> getLatestAnswer() {
                return latestAnswer;
            }

            public void setLatestAnswer(List<String> latestAnswer) {
                this.latestAnswer = latestAnswer;
            }

            public List<String> getLatestItemResults() {
                return latestItemResults;
            }

            public void setLatestItemResults(List<String> latestItemResults) {
                this.latestItemResults = latestItemResults;
            }

            public List<OcrHisAnswerImgs> getOcrHisAnswerImgs() {
                return ocrHisAnswerImgs;
            }

            public void setOcrHisAnswerImgs(List<OcrHisAnswerImgs> ocrHisAnswerImgs) {
                this.ocrHisAnswerImgs = ocrHisAnswerImgs;
            }

            public Question getQuestion() {
                return question;
            }

            public void setQuestion(Question question) {
                this.question = question;
            }
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

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
//    public WrongInfo(String workTitle, String workTime, String workAmount) {
//        this.workTitle = workTitle;
//        this.workTime = workTime;
//        this.workAmount = workAmount;
//    }
//
//    public WrongInfo() {
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
