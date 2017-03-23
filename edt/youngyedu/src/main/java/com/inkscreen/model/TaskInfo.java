package com.inkscreen.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xcz on 2016/12/7.
 */
public class TaskInfo {

    int ret_code;
    String ret_msg;

    public Ret ret;

    public static class Ret{
        public StudentHomeWork studentHomework;

        public static class StudentHomeWork{
            int rightCount;
            int wrongCount;
            String status;
            boolean corrected;
            String statusName;
            String completionRateTitle;
            int homeworkTime;
            boolean studentCorrected;
            String id;
            int completionCount;
            boolean autoManualAllCorrected;
            int rank;
            long createAt;
            String homeworkId;
            String studentId;
            String rightRateTitle;
            int completionRate;
            int halfWrongCount;

            public Homework homework;
            public static class Homework{
                boolean showIssue;
                int wrongCount;
                boolean hasQuestionAnswering;
                String statusName;
                String sectionName;
                float difficulty;
                String courseId;
                int  type;
                long startTime;
                String id;
                String correctingType;
                String correctingCount;
                String rightRateTitle;
                String name;
                int textbookCode;
                String delStatus;
                boolean allCommitMement;
                int rightCount;
                String status;
                String homeworkClazzId;
                int homeworkTime;
                String distributeCount;
                String commitCount;
                String exerciseId;
                String questionCount;
                long deadline;
                String deadTime;
                int halfWrongCount;
                long createAt;

                public boolean isShowIssue() {
                    return showIssue;
                }

                public void setShowIssue(boolean showIssue) {
                    this.showIssue = showIssue;
                }

                public int getWrongCount() {
                    return wrongCount;
                }

                public void setWrongCount(int wrongCount) {
                    this.wrongCount = wrongCount;
                }

                public boolean isHasQuestionAnswering() {
                    return hasQuestionAnswering;
                }

                public void setHasQuestionAnswering(boolean hasQuestionAnswering) {
                    this.hasQuestionAnswering = hasQuestionAnswering;
                }

                public String getStatusName() {
                    return statusName;
                }

                public void setStatusName(String statusName) {
                    this.statusName = statusName;
                }

                public String getSectionName() {
                    return sectionName;
                }

                public void setSectionName(String sectionName) {
                    this.sectionName = sectionName;
                }

                public float getDifficulty() {
                    return difficulty;
                }

                public void setDifficulty(float difficulty) {
                    this.difficulty = difficulty;
                }

                public String getCourseId() {
                    return courseId;
                }

                public void setCourseId(String courseId) {
                    this.courseId = courseId;
                }

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                public long getStartTime() {
                    return startTime;
                }

                public void setStartTime(long startTime) {
                    this.startTime = startTime;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getCorrectingType() {
                    return correctingType;
                }

                public void setCorrectingType(String correctingType) {
                    this.correctingType = correctingType;
                }

                public String getCorrectingCount() {
                    return correctingCount;
                }

                public void setCorrectingCount(String correctingCount) {
                    this.correctingCount = correctingCount;
                }

                public String getRightRateTitle() {
                    return rightRateTitle;
                }

                public void setRightRateTitle(String rightRateTitle) {
                    this.rightRateTitle = rightRateTitle;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getTextbookCode() {
                    return textbookCode;
                }

                public void setTextbookCode(int textbookCode) {
                    this.textbookCode = textbookCode;
                }

                public String getDelStatus() {
                    return delStatus;
                }

                public void setDelStatus(String delStatus) {
                    this.delStatus = delStatus;
                }

                public boolean isAllCommitMement() {
                    return allCommitMement;
                }

                public void setAllCommitMement(boolean allCommitMement) {
                    this.allCommitMement = allCommitMement;
                }

                public int getRightCount() {
                    return rightCount;
                }

                public void setRightCount(int rightCount) {
                    this.rightCount = rightCount;
                }

                public String getStatus() {
                    return status;
                }

                public void setStatus(String status) {
                    this.status = status;
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

                public String getDistributeCount() {
                    return distributeCount;
                }

                public void setDistributeCount(String distributeCount) {
                    this.distributeCount = distributeCount;
                }

                public long getCreateAt() {
                    return createAt;
                }

                public void setCreateAt(long createAt) {
                    this.createAt = createAt;
                }

                public String getCommitCount() {
                    return commitCount;
                }

                public void setCommitCount(String commitCount) {
                    this.commitCount = commitCount;
                }

                public String getExerciseId() {
                    return exerciseId;
                }

                public void setExerciseId(String exerciseId) {
                    this.exerciseId = exerciseId;
                }

                public String getQuestionCount() {
                    return questionCount;
                }

                public void setQuestionCount(String questionCount) {
                    this.questionCount = questionCount;
                }

                public long getDeadline() {
                    return deadline;
                }

                public void setDeadline(long deadline) {
                    this.deadline = deadline;
                }

                public String getDeadTime() {
                    return deadTime;
                }

                public void setDeadTime(String deadTime) {
                    this.deadTime = deadTime;
                }

                public int getHalfWrongCount() {
                    return halfWrongCount;
                }

                public void setHalfWrongCount(int halfWrongCount) {
                    this.halfWrongCount = halfWrongCount;
                }
            }

            public Homework getHomework() {
                return homework;
            }

            public void setHomework(Homework homework) {
                this.homework = homework;
            }

            public int getRightCount() {
                return rightCount;
            }

            public void setRightCount(int rightCount) {
                this.rightCount = rightCount;
            }

            public int getWrongCount() {
                return wrongCount;
            }

            public void setWrongCount(int wrongCount) {
                this.wrongCount = wrongCount;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public boolean isCorrected() {
                return corrected;
            }

            public void setCorrected(boolean corrected) {
                this.corrected = corrected;
            }

            public String getStatusName() {
                return statusName;
            }

            public void setStatusName(String statusName) {
                this.statusName = statusName;
            }

            public String getCompletionRateTitle() {
                return completionRateTitle;
            }

            public void setCompletionRateTitle(String completionRateTitle) {
                this.completionRateTitle = completionRateTitle;
            }

            public int getHomeworkTime() {
                return homeworkTime;
            }

            public void setHomeworkTime(int homeworkTime) {
                this.homeworkTime = homeworkTime;
            }

            public boolean isStudentCorrected() {
                return studentCorrected;
            }

            public void setStudentCorrected(boolean studentCorrected) {
                this.studentCorrected = studentCorrected;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public int getCompletionCount() {
                return completionCount;
            }

            public void setCompletionCount(int completionCount) {
                this.completionCount = completionCount;
            }

            public boolean isAutoManualAllCorrected() {
                return autoManualAllCorrected;
            }

            public void setAutoManualAllCorrected(boolean autoManualAllCorrected) {
                this.autoManualAllCorrected = autoManualAllCorrected;
            }

            public int getRank() {
                return rank;
            }

            public void setRank(int rank) {
                this.rank = rank;
            }

            public long getCreateAt() {
                return createAt;
            }

            public void setCreateAt(long createAt) {
                this.createAt = createAt;
            }

            public String getHomeworkId() {
                return homeworkId;
            }

            public void setHomeworkId(String homeworkId) {
                this.homeworkId = homeworkId;
            }

            public String getStudentId() {
                return studentId;
            }

            public void setStudentId(String studentId) {
                this.studentId = studentId;
            }

            public String getRightRateTitle() {
                return rightRateTitle;
            }

            public void setRightRateTitle(String rightRateTitle) {
                this.rightRateTitle = rightRateTitle;
            }

            public int getCompletionRate() {
                return completionRate;
            }

            public void setCompletionRate(int completionRate) {
                this.completionRate = completionRate;
            }

            public int getHalfWrongCount() {
                return halfWrongCount;
            }

            public void setHalfWrongCount(int halfWrongCount) {
                this.halfWrongCount = halfWrongCount;
            }

//            public Homework getHomework() {
//                return homework;
//            }
//
//            public void setHomework(Homework homework) {
//                this.homework = homework;
//            }
        }

     public List<Questions> questions;
        public static class Questions{

//            String[] index;
//
//            public String[] getIndex() {
//                return index;
//            }
//
//            public void setIndex(String[] index) {
//                this.index = index;
//            }

            public  List<DrawPath> leftPath = new ArrayList<>();
            public  List<DrawPath> rightPath = new ArrayList<>();

            public HashMap<String,String> imgMap;
//            public static class DrawPath {
//                public Path path;// 路径  
//                public Paint paint;// 画笔  
//            }


            public HashMap<String, String> getImgMap() {
                return imgMap;
            }

            public void setImgMap(HashMap<String, String> imgMap) {
                this.imgMap = imgMap;
            }

            public List<DrawPath> getRightPath() {
                return rightPath;
            }

            public void setRightPath(List<DrawPath> rightPath) {
                this.rightPath = rightPath;
            }

            public List<DrawPath> getLeftPath() {
                return leftPath;
            }

            public void setLeftPath(List<DrawPath> leftPath) {
                this.leftPath = leftPath;
            }

            public Subject subject;
            public static class Subject{

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
            public static class Answers{
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

            String[] children;
            float difficulty;
            String type;
            boolean isCollect;
            String analysis;
            boolean subFlag;
            String questionSource;
            String id;
            String parentId;
            boolean correctQuestion;
            String checkStatus;
            boolean inQuestionCar;

            public  QuestionType questionType;

            public static class QuestionType{

                int subjectCode;
                String name;
                int  code;

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

            String[] choices;
            String hint;
            String allNewKnowpoint;

            public  StudentHomeworkQuestion studentHomeworkQuestion;
            public static class StudentHomeworkQuestion{

                String studentHomeworkId;
                String id;
                String result;
                String solvingImgId;
                String notationAnswerImgId;
                String questionId;
                String rightRateTitle;
                String notationAnswerImg;
                String solvingImg;
                String comment;
                String answerImg;
                String answerImgId;
                List<String> handWriting;
                ArrayList<String> answerImgIds=new ArrayList<>();

                List<DrawPath> leftList = new ArrayList<>();
                List<DrawPath> rightList = new ArrayList<>();

                public List<DrawPath> getRightList() {
                    return rightList;
                }

                public void setRightList(List<DrawPath> rightList) {
                    this.rightList = rightList;
                }

                public List<DrawPath> getLeftList() {
                    return leftList;
                }

                public void setLeftList(List<DrawPath> leftList) {
                    this.leftList = leftList;
                }

                public Hand handW = new Hand();
                public static class Hand{
                    String path1;
                    String Path2;


                    public String getPath1() {
                        return path1;
                    }

                    public void setPath1(String path1) {
                        this.path1 = path1;
                    }

                    public String getPath2() {
                        return Path2;
                    }

                    public void setPath2(String path2) {
                        Path2 = path2;
                    }
                }


                public Hand getHandW() {
                    return handW;
                }

                public void setHandW(Hand handW) {
                    this.handW = handW;
                }

                public ArrayList<String> getAnswerImgIds() {
                    return answerImgIds;
                }

                public void setAnswerImgIds(ArrayList<String> answerImgIds) {
                    this.answerImgIds = answerImgIds;
                }

                public List<String> getHandWriting() {
                    return handWriting;
                }

                public void setHandWriting(List<String> handWriting) {
                    this.handWriting = handWriting;
                }

                public String getStudentHomeworkId() {
                    return studentHomeworkId;
                }

                public void setStudentHomeworkId(String studentHomeworkId) {
                    this.studentHomeworkId = studentHomeworkId;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getResult() {
                    return result;
                }

                public void setResult(String result) {
                    this.result = result;
                }

                public String getSolvingImgId() {
                    return solvingImgId;
                }

                public void setSolvingImgId(String solvingImgId) {
                    this.solvingImgId = solvingImgId;
                }

                public String getNotationAnswerImgId() {
                    return notationAnswerImgId;
                }

                public void setNotationAnswerImgId(String notationAnswerImgId) {
                    this.notationAnswerImgId = notationAnswerImgId;
                }

                public String getQuestionId() {
                    return questionId;
                }

                public void setQuestionId(String questionId) {
                    this.questionId = questionId;
                }

                public String getRightRateTitle() {
                    return rightRateTitle;
                }

                public void setRightRateTitle(String rightRateTitle) {
                    this.rightRateTitle = rightRateTitle;
                }

                public String getNotationAnswerImg() {
                    return notationAnswerImg;
                }

                public void setNotationAnswerImg(String notationAnswerImg) {
                    this.notationAnswerImg = notationAnswerImg;
                }

                public String getSolvingImg() {
                    return solvingImg;
                }

                public void setSolvingImg(String solvingImg) {
                    this.solvingImg = solvingImg;
                }

                public String getComment() {
                    return comment;
                }

                public void setComment(String comment) {
                    this.comment = comment;
                }

                public String getAnswerImg() {
                    return answerImg;
                }

                public void setAnswerImg(String answerImg) {
                    this.answerImg = answerImg;
                }

                public String getAnswerImgId() {
                    return answerImgId;
                }

                public void setAnswerImgId(String answerImgId) {
                    this.answerImgId = answerImgId;
                }
            }


            String code;
            String content;

            public List<MetaKnowpoints> metaKnowpoints;

            public  static class MetaKnowpoints{
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

           String[] allAnalysis;

            String source;
            int sequence;

            public  TextbookCategory textbookCategory;

            public static class TextbookCategory{
                int sequence;
                String[] textbooks;
                String name;
                int code;

                public int getSequence() {
                    return sequence;
                }

                public void setSequence(int sequence) {
                    this.sequence = sequence;
                }

                public String[] getTextbooks() {
                    return textbooks;
                }

                public void setTextbooks(String[] textbooks) {
                    this.textbooks = textbooks;
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

            public List<StudentHomeworkAnswers> studentHomeworkAnswers;

            public static class StudentHomeworkAnswers{

                String id;
                String content;
                String result;
                String imageContent;
                String contentAscii;
                String answerId;
                String solvingImg;
                String studentHomeworkQuestionId;
                String noLabelContentAscii;
                int sequence;

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

                public String getResult() {
                    return result;
                }

                public void setResult(String result) {
                    this.result = result;
                }

                public String getImageContent() {
                    return imageContent;
                }

                public void setImageContent(String imageContent) {
                    this.imageContent = imageContent;
                }

                public String getContentAscii() {
                    return contentAscii;
                }

                public void setContentAscii(String contentAscii) {
                    this.contentAscii = contentAscii;
                }

                public String getAnswerId() {
                    return answerId;
                }

                public void setAnswerId(String answerId) {
                    this.answerId = answerId;
                }

                public String getSolvingImg() {
                    return solvingImg;
                }

                public void setSolvingImg(String solvingImg) {
                    this.solvingImg = solvingImg;
                }

                public String getStudentHomeworkQuestionId() {
                    return studentHomeworkQuestionId;
                }

                public void setStudentHomeworkQuestionId(String studentHomeworkQuestionId) {
                    this.studentHomeworkQuestionId = studentHomeworkQuestionId;
                }

                public String getNoLabelContentAscii() {
                    return noLabelContentAscii;
                }

                public void setNoLabelContentAscii(String noLabelContentAscii) {
                    this.noLabelContentAscii = noLabelContentAscii;
                }

                public int getSequence() {
                    return sequence;
                }

                public void setSequence(int sequence) {
                    this.sequence = sequence;
                }
            }

            String[] allHints;

            public  Phase phase;

            public static class Phase{
                int code;
                String acronym;
                String[] subjects;
                int sequence;
                String name;
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

            public String[] getChildren() {
                return children;
            }

            public void setChildren(String[] children) {
                this.children = children;
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

            public String[] getChoices() {
                return choices;
            }

            public void setChoices(String[] choices) {
                this.choices = choices;
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

            public StudentHomeworkQuestion getStudentHomeworkQuestion() {
                return studentHomeworkQuestion;
            }

            public void setStudentHomeworkQuestion(StudentHomeworkQuestion studentHomeworkQuestion) {
                this.studentHomeworkQuestion = studentHomeworkQuestion;
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

            public String[] getAllAnalysis() {
                return allAnalysis;
            }

            public void setAllAnalysis(String[] allAnalysis) {
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

            public List<StudentHomeworkAnswers> getStudentHomeworkAnswers() {
                return studentHomeworkAnswers;
            }

            public void setStudentHomeworkAnswers(List<StudentHomeworkAnswers> studentHomeworkAnswers) {
                this.studentHomeworkAnswers = studentHomeworkAnswers;
            }

            public String[] getAllHints() {
                return allHints;
            }

            public void setAllHints(String[] allHints) {
                this.allHints = allHints;
            }

            public Phase getPhase() {
                return phase;
            }

            public void setPhase(Phase phase) {
                this.phase = phase;
            }
        }

       String deadline;


        public StudentHomeWork getStudentHomework() {
            return studentHomework;
        }

        public void setStudentHomework(StudentHomeWork studentHomework) {
            this.studentHomework = studentHomework;
        }

        public List<Questions> getQuestions() {
            return questions;
        }

        public void setQuestions(List<Questions> questions) {
            this.questions = questions;
        }

        public String getDeadline() {
            return deadline;
        }

        public void setDeadline(String deadline) {
            this.deadline = deadline;
        }
    }

    public Ret getRet() {
        return ret;
    }

    public void setRet(Ret ret) {
        this.ret = ret;
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
}
