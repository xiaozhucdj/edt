package com.yougy.common.new_network;

import com.yougy.anwser.BaseResult;
import com.yougy.anwser.Content_new;
import com.yougy.anwser.OriginQuestionItem;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.common.utils.LogUtils;
import com.yougy.homework.bean.HomeworkDetail;
import com.yougy.homework.bean.QuestionReplyDetail;
import com.yougy.view.dialog.LoadingProgressDialog;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * User: Axl_Jacobs(Axl.Jacobs@gmail.com)
 * Date: 2016-09-01
 * Time: 20:27
 * FIXME
 * Rx处理服务器返回
 */
public class RxResultHelper {
    public static <T> Observable.Transformer<BaseResult<T>, T> handleResult(final LoadingProgressDialog loadingProgressDialog) {
        LogUtils.e("FH" , "!!!!! handleResult " + loadingProgressDialog);
        return new Observable.Transformer<BaseResult<T>, T>() {
            @Override
            public Observable<T> call(Observable<BaseResult<T>> tObservable) {
                return tObservable.flatMap(
                        new Func1<BaseResult<T>, Observable<? extends T>>() {
                            @Override
                            public Observable<? extends T> call(BaseResult<T> entity) {
                                LogUtils.e("FH", "!!!!! success call  " + loadingProgressDialog);
                                if (loadingProgressDialog != null){
                                    loadingProgressDialog.dismiss();
                                }
                                if (entity.getCode() == 200) {
                                    return createData(entity.getData());
                                } else {
                                    return Observable.error(new ApiException(entity.getCode() + "", entity.getMsg()));
                                }
                            }
                        },
                        new Func1<Throwable, Observable<? extends T>>() {
                            @Override
                            public Observable<? extends T> call(Throwable throwable) {
                                LogUtils.e("FH", "!!!!! error call  " + loadingProgressDialog);
                                if (loadingProgressDialog != null){
                                    loadingProgressDialog.dismiss();
                                }
                                return Observable.error(throwable);
                            }
                        }, new Func0<Observable<? extends T>>() {
                            @Override
                            public Observable<? extends T> call() {
                                return null;
                            }
                        }
                );
            }
        };
    }

    public static <T> Observable.Transformer<BaseResult<T>, BaseResult<T>> dismissDialog(final LoadingProgressDialog loadingProgressDialog) {
        return new Observable.Transformer<BaseResult<T>, BaseResult<T>>() {
            @Override
            public Observable<BaseResult<T>> call(Observable<BaseResult<T>> tObservable) {
                return tObservable.flatMap(
                        new Func1<BaseResult<T>, Observable<BaseResult<T>>>() {
                            @Override
                            public Observable<BaseResult<T>> call(BaseResult<T> entity) {
                                if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
                                    LogUtils.e("FH", "!!!!! success call  " + loadingProgressDialog.toString());
                                    loadingProgressDialog.dismiss();
                                }
                                if (entity.getCode() == 200) {
                                    return createData(entity);
                                } else {
                                    return Observable.error(new ApiException(entity.getCode() + "", entity.getMsg()));
                                }
                            }
                        },
                        new Func1<Throwable, Observable<BaseResult<T>>>() {
                            @Override
                            public Observable<BaseResult<T>> call(Throwable throwable) {
                                if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
                                    LogUtils.e("FH", "!!!!! error call  " + loadingProgressDialog.toString());
                                    loadingProgressDialog.dismiss();
                                }
                                return Observable.error(throwable);
                            }
                        }, new Func0<Observable<BaseResult<T>>>() {
                            @Override
                            public Observable<BaseResult<T>> call() {
                                return null;
                            }
                        }
                );
            }
        };
    }


    public static class ResultHandler<T> implements Func1<BaseResult<T> , Observable<T>>{
        @Override
        public Observable<T> call(BaseResult<T> tBaseResult) {
            if (tBaseResult.getCode() == 200) {
                return createData(tBaseResult.getData());
            } else {
                return Observable.error(new ApiException(tBaseResult.getCode() + "", tBaseResult.getMsg()));
            }
        }
    }

    private static <T> Observable<T> createData(final T t) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(t);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable.Transformer<List<OriginQuestionItem>, List<ParsedQuestionItem>> parseQuestion(){
        return new Observable.Transformer<List<OriginQuestionItem>, List<ParsedQuestionItem>>() {
            @Override
            public Observable<List<ParsedQuestionItem>> call(Observable<List<OriginQuestionItem>> listObservable) {
                return listObservable.map(new Func1<List<OriginQuestionItem>, List<ParsedQuestionItem>>() {
                    @Override
                    public List<ParsedQuestionItem> call(List<OriginQuestionItem> originQuestionItems) {
                        ArrayList<ParsedQuestionItem> paredQuestionList = new ArrayList<ParsedQuestionItem>();
                        for (OriginQuestionItem originQuestionItem : originQuestionItems) {
                            ParsedQuestionItem parsedQuestionItem = originQuestionItem.parseQuestion();
                            if (parsedQuestionItem != null){
                                paredQuestionList.add(parsedQuestionItem);
                            }
                        }
                        return paredQuestionList;
                    }
                });
            }
        };
    }

    public static Observable.Transformer<List<HomeworkDetail> , List<HomeworkDetail>> parseHomeworkQuestion(){
        return new Observable.Transformer<List<HomeworkDetail>, List<HomeworkDetail>>() {
            @Override
            public Observable<List<HomeworkDetail>> call(Observable<List<HomeworkDetail>> listObservable) {
                return listObservable.map(new Func1<List<HomeworkDetail>, List<HomeworkDetail>>() {
                    @Override
                    public List<HomeworkDetail> call(List<HomeworkDetail> homeworkDetails) {
                        for (HomeworkDetail homeworkDetail : homeworkDetails) {
                            for (HomeworkDetail.ExamPaper.ExamPaperContent paperContent: homeworkDetail.getExamPaper().getPaperContent()
                                 ) {
                                for (OriginQuestionItem originQuestionItem :
                                        paperContent.getPaperItemContent()) {
                                    paperContent.getParsedQuestionItemList().add(originQuestionItem.parseQuestion());
                                }
                            }
                        }
                        return homeworkDetails;
                    }
                });
            }
        };
    }

    public static Observable.Transformer<List<QuestionReplyDetail> , List<QuestionReplyDetail>> parseReplyDetail(){
        return new Observable.Transformer<List<QuestionReplyDetail>, List<QuestionReplyDetail>>() {
            @Override
            public Observable<List<QuestionReplyDetail>> call(Observable<List<QuestionReplyDetail>> listObservable) {
                return listObservable.map(new Func1<List<QuestionReplyDetail>, List<QuestionReplyDetail>>() {
                    @Override
                    public List<QuestionReplyDetail> call(List<QuestionReplyDetail> questionReplyDetails) {
                        for (QuestionReplyDetail questionReplyDetail : questionReplyDetails) {
                            questionReplyDetail.parse();
                        }
                        return questionReplyDetails;
                    }
                });
            }
        };
    }

    /**
     * 把题目Answer中的正式类型的答案拼接成一个字符串
     * @param answerContentList
     * @return
     */
    public static String parseAnswerList(ArrayList<Content_new> answerContentList){
        if (!answerContentList.isEmpty()){
            String answerText = "";
            for (int i = 0 ; i < answerContentList.size() ; i++){
                Content_new answerContent = answerContentList.get(i);
                if (answerContent.getExtraData().equals("正式")){
                    answerText = answerText + answerContent.getValue() + "、";
                }
            }
            if (answerText.endsWith("、")){
                return answerText.substring(0 , answerText.length() - 1);
            }
        }
        return "";
    }

}