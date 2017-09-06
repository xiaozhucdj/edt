package com.yougy.anwser;

import android.text.TextUtils;

import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

/**
 * Created by FH on 2017/8/18.
 */

/**
 * 题目的整体,包含题干,答案,和题目类型
 */
public class OriginQuestionItem {
    private int itemId;
    private List<Object> answer;
    private List<Object> notation;
    private List<Object> question;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public List<Object> getAnswer() {
        return answer;
    }

    public void setAnswer(List<Object> answer) {
        this.answer = answer;
    }

    public List<Object> getNotation() {
        return notation;
    }

    public void setNotation(List<Object> notation) {
        this.notation = notation;
    }

    public List<Object> getQuestion() {
        return question;
    }

    public void setQuestion(List<Object> question) {
        this.question = question;
    }




    public ParsedQuestionItem parseQuestion(){
        ParsedQuestionItem parsedQuestionItem = new ParsedQuestionItem();
        parsedQuestionItem.itemId = String.valueOf(getItemId());
        for (Object obj : getQuestion()) {
            LinkedTreeMap questionLinkedTreeMap = (LinkedTreeMap) obj;
            String questionTypeString = (String)questionLinkedTreeMap.get("questionType");
            if (TextUtils.isEmpty(questionTypeString)){
                continue;
            }
            List<LinkedTreeMap> questionContentTreeMapList = (List<LinkedTreeMap>) questionLinkedTreeMap.get("questionContent");
            ParsedQuestionItem.Question question = null;
            for (LinkedTreeMap questionContentTreeMap : questionContentTreeMapList) {
                String format = (String) questionContentTreeMap.get("format");
                if (format.equals("ATCH/???") || format.equals("ATCH/HTM")){
                    if (questionContentTreeMap.get("remote") != null
                            && !TextUtils.isEmpty((String)questionContentTreeMap.get("remote"))){
                        String questionUrl = "http://question.learningpad.cn/" + questionContentTreeMap.get("remote");
                        if (questionUrl.endsWith(".gif")
                                || questionUrl.endsWith(".jpg")
                                || questionUrl.endsWith(".png")
                                ){
                            question = new ParsedQuestionItem.ImgQuestion(questionTypeString , questionUrl);
                            //暂时只取第一个值,其他值忽略
                            break;
                        }
                        else if (questionUrl.endsWith(".htm")){
                            question = new ParsedQuestionItem.HtmlQuestion(questionTypeString , questionUrl);
                            //暂时只取第一个值,其他值忽略
                            break;
                        }
                    }
                }
                else if (format.equals("TEXT")){
                    String questionText = (String) questionContentTreeMap.get("value");
                    question = new ParsedQuestionItem.TextQuestion(questionTypeString , questionText);
                    //暂时只取第一个值,其他值忽略
                    break;
                }
            }
            if (question != null){
                parsedQuestionItem.questionList.add(question);
            }
        }
        if (parsedQuestionItem.questionList.size() == 0){
            return null;
        }
        for (Object obj : getAnswer()) {
            LinkedTreeMap answerLinkedTreeMap = (LinkedTreeMap) obj;
            ParsedQuestionItem.Answer answer = null;
            String answerType = (String) answerLinkedTreeMap.get("answerType");
            if (TextUtils.isEmpty(answerType)){
                continue;
            }
            List<LinkedTreeMap> answerContentTreeMapList = (List<LinkedTreeMap>) answerLinkedTreeMap.get("answerContent");
            for (LinkedTreeMap answerContentTreeMap : answerContentTreeMapList) {
                String format = (String) answerContentTreeMap.get("format");
                if (format.equals("ATCH/???") || format.equals("ATCH/HTM")) {
                    if (answerContentTreeMap.get("remote") != null
                            && !TextUtils.isEmpty((String)answerContentTreeMap.get("remote"))){
                        String answerUrl = "http://question.learningpad.cn/" + answerContentTreeMap.get("remote");
                        if (answerUrl.endsWith(".gif")
                                || answerUrl.endsWith(".jpg")
                                || answerUrl.endsWith(".png")
                                ){
                            answer = new ParsedQuestionItem.ImgAnswer(answerType , answerUrl);
                            //暂时只取第一个值,其他值忽略
                            break;
                        }
                        else if (answerUrl.endsWith(".htm")){
                            answer = new ParsedQuestionItem.HtmlAnswer(answerType , answerUrl);
                            //暂时只取第一个值,其他值忽略
                            break;
                        }
                    }
                }
                else if (format.equals("TEXT")){
                    String answerText = "" + answerContentTreeMap.get("value");
                    answer = new ParsedQuestionItem.TextAnswer(answerType , answerText);
                    //暂时只取第一个值,其他值忽略
                    break;
                }
            }
            if (answer != null){
                parsedQuestionItem.answerList.add(answer);
            }
        }
        for (Object obj : getNotation()) {
            LinkedTreeMap notationLinkedTreeMap = (LinkedTreeMap) obj;
            String notationType = (String) notationLinkedTreeMap.get("notationType");
            if (notationType.equals("难度")){
                List<LinkedTreeMap> notationContentTreeMapList = (List<LinkedTreeMap>) notationLinkedTreeMap.get("notationContent");
                for (LinkedTreeMap notationContentTreeMap : notationContentTreeMapList) {
                    if (notationContentTreeMap.containsKey("value")){
                        parsedQuestionItem.difficulty = notationContentTreeMap.get("value").toString();
                        //暂时只取第一个值,其他值忽略
                        break;
                    }
                }
            }
            else if (notationType.equals("知识点")){
                List<LinkedTreeMap> notationContentTreeMapList = (List<LinkedTreeMap>) notationLinkedTreeMap.get("notationContent");
                for (LinkedTreeMap notationContentTreeMap : notationContentTreeMapList) {
                    if (notationContentTreeMap.containsKey("value")){
                        parsedQuestionItem.knowledgePoint = notationContentTreeMap.get("value").toString();
                        //暂时只取第一个值,其他值忽略
                        break;
                    }
                }
            }
            else if (notationType.equals("解析")){
                ParsedQuestionItem.Analysis analysis = null;
                List<LinkedTreeMap> notationContentTreeMapList = (List<LinkedTreeMap>) notationLinkedTreeMap.get("notationContent");
                for (LinkedTreeMap notationContentTreeMap : notationContentTreeMapList) {
                    String notationFormat = (String) notationContentTreeMap.get("format");
                    if (notationFormat.equals("ATCH/???") || notationFormat.equals("ATCH/HTM")) {
                        if (notationContentTreeMap.get("remote") != null
                                && !TextUtils.isEmpty((String)notationContentTreeMap.get("remote"))){
                            String analysisUrl = "http://question.learningpad.cn/" + notationContentTreeMap.get("remote");
                            if (analysisUrl.endsWith(".gif")
                                    || analysisUrl.endsWith(".jpg")
                                    || analysisUrl.endsWith(".png")
                                    ){
                                analysis = new ParsedQuestionItem.ImgAnalysis(analysisUrl);
                                //暂时只取第一个值,其他值忽略
                                break;
                            }
                            else if (analysisUrl.endsWith(".htm")){
                                analysis = new ParsedQuestionItem.HtmlAnalysis(analysisUrl);
                                //暂时只取第一个值,其他值忽略
                                break;
                            }
                        }
                    }
                    else if (notationFormat.equals("TEXT")){
                        String answerText = "" + notationContentTreeMap.get("value");
                        analysis = new ParsedQuestionItem.TextAnalysis(answerText);
                        //暂时只取第一个值,其他值忽略
                        break;
                    }
                }
                if (analysis != null){
                    parsedQuestionItem.analysisList.add(analysis);
                }
            }
        }
        return parsedQuestionItem;
    }
}
