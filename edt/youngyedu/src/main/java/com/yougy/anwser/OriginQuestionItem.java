package com.yougy.anwser;

import android.text.TextUtils;

import com.google.gson.internal.LinkedTreeMap;
import com.yougy.common.utils.AliyunUtil;

import java.util.List;

import static android.R.attr.format;

/**
 * Created by FH on 2017/8/18.
 */

/**
 * 题目的整体,包含题干,答案,和题目类型
 */
public class OriginQuestionItem {
    private int itemId;
    private Integer replyItemWeight;
    private List<Object> answer;
    private List<Object> notation;
    private List<Object> question;
    public Integer getReplyItemWeight() {
        return replyItemWeight;
    }

    public void setReplyItemWeight(Integer replyItemWeight) {
        this.replyItemWeight = replyItemWeight;
    }
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


    public ParsedQuestionItem parseQuestion() {
        ParsedQuestionItem parsedQuestionItem = new ParsedQuestionItem();
        parsedQuestionItem.itemId = String.valueOf(getItemId());
        for (Object obj : getQuestion()) {
            LinkedTreeMap questionLinkedTreeMap = (LinkedTreeMap) obj;
            String questionTypeString = (String) questionLinkedTreeMap.get("questionType");
            if (TextUtils.isEmpty(questionTypeString)) {
                continue;
            }
            List<LinkedTreeMap> questionContentTreeMapList = (List<LinkedTreeMap>) questionLinkedTreeMap.get("questionContent");
            ParsedQuestionItem.Question question = null;
            Content_new questionContent = null;
            for (LinkedTreeMap questionContentTreeMap : questionContentTreeMapList) {
                String format = (String) questionContentTreeMap.get("format");
                String bucket = (String) questionContentTreeMap.get("bucket");
                if (format.startsWith("ATCH/")) {
                    if (questionContentTreeMap.get("remote") != null
                            && !TextUtils.isEmpty((String) questionContentTreeMap.get("remote"))) {
                        String questionUrl = "http://" + bucket + AliyunUtil.ANSWER_PIC_HOST + questionContentTreeMap.get("remote");
                        //TODO 测试pdf用,删掉
//                        questionUrl = "http://lovewanwan.top/111.pdf";
                        if (questionUrl.endsWith(".gif")
                                || questionUrl.endsWith(".jpg")
                                || questionUrl.endsWith(".png")
                                ) {
                            questionContent = new Content_new<String>(Content_new.Type.IMG_URL, 1, questionUrl, questionTypeString);
                            question = new ParsedQuestionItem.ImgQuestion(questionTypeString, questionUrl);
                            //暂时只取第一个值,其他值忽略
                            break;
                        } else if (questionUrl.endsWith(".htm")) {
                            questionContent = new Content_new<String>(Content_new.Type.HTML_URL, 1, questionUrl, questionTypeString);
                            question = new ParsedQuestionItem.HtmlQuestion(questionTypeString, questionUrl);
                            //暂时只取第一个值,其他值忽略
                            break;
                        } else if (questionUrl.endsWith(".pdf")) {
                            if ("ATCH/PDF_COLOR".equals(format)) {
                                questionContent = new Content_new<String>(Content_new.Type.PDF, 1, questionUrl, questionTypeString);
//                                questionContent = new Content_new<String>(Content_new.Type.PDF, 1, "http://ocghxr9lf.bkt.clouddn.com/2.pdf", questionTypeString);
                                //暂时只取第一个值,其他值忽略
                                break;
                            }

                            if ("ATCH/PDF".equals(format)) {
                                if (questionContent == null) {
                                    questionContent = new Content_new<String>(Content_new.Type.PDF, 1, questionUrl, questionTypeString);
//                                    questionContent = new Content_new<String>(Content_new.Type.PDF, 1, "http://ocghxr9lf.bkt.clouddn.com/2.pdf", questionTypeString);
                                    //暂时只取第一个值,其他值忽略
                                }
                            }

                        }
                    }
                } else if (format.equals("TEXT")) {
                    String questionText = (String) questionContentTreeMap.get("value");
                    questionContent = new Content_new<String>(Content_new.Type.TEXT, 1, questionText, questionTypeString);
                    question = new ParsedQuestionItem.TextQuestion(questionTypeString, questionText);
                    //暂时只取第一个值,其他值忽略
                    break;
                }
            }
            if (question != null) {
                parsedQuestionItem.questionList.add(question);
            }
            if (questionContent != null) {
                parsedQuestionItem.questionContentList.add(questionContent);
            }
        }
        if (parsedQuestionItem.questionContentList.size() == 0) {
            return null;
        }
        for (Object obj : getAnswer()) {
            LinkedTreeMap answerLinkedTreeMap = (LinkedTreeMap) obj;
            ParsedQuestionItem.Answer answer = null;
            Content_new answerContent = null;
            String answerType = (String) answerLinkedTreeMap.get("answerType");
            if (TextUtils.isEmpty(answerType)) {
                continue;
            }
            List<LinkedTreeMap> answerContentTreeMapList = (List<LinkedTreeMap>) answerLinkedTreeMap.get("answerContent");
            for (LinkedTreeMap answerContentTreeMap : answerContentTreeMapList) {
                String format = (String) answerContentTreeMap.get("format");
                String bucket = (String) answerContentTreeMap.get("bucket");
                if (format.startsWith("ATCH/")) {
                    if (answerContentTreeMap.get("remote") != null
                            && !TextUtils.isEmpty((String) answerContentTreeMap.get("remote"))) {
                        String answerUrl = "http://" + bucket + AliyunUtil.ANSWER_PIC_HOST + answerContentTreeMap.get("remote");
                        //TODO 测试pdf用,删掉
//                        answerUrl= "http://lovewanwan.top/222.pdf";
                        if (answerUrl.endsWith(".gif")
                                || answerUrl.endsWith(".jpg")
                                || answerUrl.endsWith(".png")
                                ) {
                            answerContent = new Content_new<String>(Content_new.Type.IMG_URL, 1, answerUrl, answerType);
                            answer = new ParsedQuestionItem.ImgAnswer(answerType, answerUrl);
                            //暂时只取第一个值,其他值忽略
                            break;
                        } else if (answerUrl.endsWith(".htm")) {
                            answerContent = new Content_new<String>(Content_new.Type.HTML_URL, 1, answerUrl, answerType);
                            answer = new ParsedQuestionItem.HtmlAnswer(answerType, answerUrl);
                            //暂时只取第一个值,其他值忽略
                            break;
                        } else if (answerUrl.endsWith(".pdf")) {
                            if ("ATCH/PDF_COLOR".equals(format)) {
                                answerContent = new Content_new<String>(Content_new.Type.PDF, 1, answerUrl, answerType);
                                //暂时只取第一个值,其他值忽略
                                break;
                            }
                            if ("ATCH/PDF".equals(format)) {
                                if (answerContent == null) {
                                    answerContent = new Content_new<String>(Content_new.Type.PDF, 1, answerUrl, answerType);
                                }
                            }
                        }
                    }
                } else if (format.equals("TEXT")) {
                    String answerText = "" + answerContentTreeMap.get("value");
                    answerContent = new Content_new<String>(Content_new.Type.TEXT, 1, answerText, answerType);
                    answer = new ParsedQuestionItem.TextAnswer(answerType, answerText);
                    //暂时只取第一个值,其他值忽略
                    break;
                }
            }
            if (answer != null) {
                parsedQuestionItem.answerList.add(answer);
            }
            if (answerContent != null) {
                parsedQuestionItem.answerContentList.add(answerContent);
            }
        }
        for (Object obj : getNotation()) {
            LinkedTreeMap notationLinkedTreeMap = (LinkedTreeMap) obj;
            String notationType = (String) notationLinkedTreeMap.get("notationType");
            if (notationType.equals("难度")) {
                List<LinkedTreeMap> notationContentTreeMapList = (List<LinkedTreeMap>) notationLinkedTreeMap.get("notationContent");
                for (LinkedTreeMap notationContentTreeMap : notationContentTreeMapList) {
                    if (notationContentTreeMap.containsKey("value")) {
                        parsedQuestionItem.difficulty = notationContentTreeMap.get("value").toString();
                        //暂时只取第一个值,其他值忽略
                        break;
                    }
                }
            } else if (notationType.equals("知识点")) {
                List<LinkedTreeMap> notationContentTreeMapList = (List<LinkedTreeMap>) notationLinkedTreeMap.get("notationContent");
                for (LinkedTreeMap notationContentTreeMap : notationContentTreeMapList) {
                    if (notationContentTreeMap.containsKey("value")) {
                        parsedQuestionItem.knowledgePoint = notationContentTreeMap.get("value").toString();
                        //暂时只取第一个值,其他值忽略
                        break;
                    }
                }
            } else if (notationType.equals("解析")) {
                ParsedQuestionItem.Analysis analysis = null;
                Content_new analysisContent = null;
                List<LinkedTreeMap> notationContentTreeMapList = (List<LinkedTreeMap>) notationLinkedTreeMap.get("notationContent");
                for (LinkedTreeMap notationContentTreeMap : notationContentTreeMapList) {
                    String notationFormat = (String) notationContentTreeMap.get("format");
                    String bucket = (String) notationContentTreeMap.get("bucket");
                    if (notationFormat.startsWith("ATCH/")) {
                        if (notationContentTreeMap.get("remote") != null
                                && !TextUtils.isEmpty((String) notationContentTreeMap.get("remote"))) {
                            String analysisUrl = "http://" + bucket + AliyunUtil.ANSWER_PIC_HOST + notationContentTreeMap.get("remote");
                            //TODO 测试pdf用,删掉
//                            analysisUrl= "http://lovewanwan.top/222.pdf";
                            if (analysisUrl.endsWith(".gif")
                                    || analysisUrl.endsWith(".jpg")
                                    || analysisUrl.endsWith(".png")
                                    ) {
                                analysisContent = new Content_new(Content_new.Type.IMG_URL, 1, analysisUrl, null);
                                analysis = new ParsedQuestionItem.ImgAnalysis(analysisUrl);
                                //暂时只取第一个值,其他值忽略
                                break;
                            } else if (analysisUrl.endsWith(".htm")) {
                                analysisContent = new Content_new(Content_new.Type.HTML_URL, 1, analysisUrl, null);
                                analysis = new ParsedQuestionItem.HtmlAnalysis(analysisUrl);
                                //暂时只取第一个值,其他值忽略
                                break;
                            } else if (analysisUrl.endsWith(".pdf")) {
                                if ("ATCH/PDF_COLOR".equals(notationFormat)) {
                                    analysisContent = new Content_new(Content_new.Type.PDF, 1, analysisUrl, null);
                                    //暂时只取第一个值,其他值忽略
                                    break;
                                }
                                if ("ATCH/PDF".equals(notationFormat)) {
                                    if (analysisContent == null) {
                                        analysisContent = new Content_new(Content_new.Type.PDF, 1, analysisUrl, null);
                                        //暂时只取第一个值,其他值忽略
                                    }
                                }
                            }
                        }
                    } else if (notationFormat.equals("TEXT")) {
                        String answerText = "" + notationContentTreeMap.get("value");
                        analysisContent = new Content_new(Content_new.Type.TEXT, 1, answerText, null);
                        analysis = new ParsedQuestionItem.TextAnalysis(answerText);
                        //暂时只取第一个值,其他值忽略
                        break;
                    }
                }
                if (analysis != null) {
                    parsedQuestionItem.analysisList.add(analysis);
                }
                if (analysisContent != null) {
                    parsedQuestionItem.analysisContentList.add(analysisContent);
                }
            }
        }
        return parsedQuestionItem;
    }
}
