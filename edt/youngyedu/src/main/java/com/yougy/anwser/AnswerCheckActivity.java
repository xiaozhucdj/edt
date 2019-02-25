package com.yougy.anwser;


import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bumptech.glide.Glide;
import com.frank.etude.pageable.PageBtnBarAdapter;
import com.google.gson.Gson;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.DialogManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.bean.QuestionReplyDetail;
import com.yougy.message.YXClient;
import com.yougy.message.attachment.ExitAnswerCheckAttachment;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnswerCheckBinding;
import com.yougy.view.dialog.HintDialog;
import com.yougy.view.dialog.LoadingProgressDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by FH on 2018/3/7.
 * 问答批改界面 问答自评、互评，只有客观题，主观题自动批改了，不会展示在这里
 */

public class AnswerCheckActivity extends BaseActivity implements View.OnClickListener {
    ActivityAnswerCheckBinding binding;
    //当前展示的学生答案的页码(从0开始)
    private int currentShowReplyPageIndex = 0;
    //当前展示的解析的页码(从0开始)
    private int currentShowAnalysisPageIndex = 0;

    //byte数组集合，（用来保存每一页书写的笔记数据）
    private ArrayList<byte[]> bytesList = new ArrayList<>();
    //存储每一页截屏图片地址
    private ArrayList<String> pathList = new ArrayList<>();
    private QuestionReplyDetail questionReplyDetail;
    //该题教师给出的评分 默认-1。3个状态0，50,100
    private int score = -1;
    //某一题图片上传成功后，统计上传数据到集合中，方便将该信息提交到服务器
    private ArrayList<STSResultbean> stsResultbeanArrayList = new ArrayList<>();

    @Override
    public void init() {
        YXClient.getInstance().with(this).addOnNewCommandCustomMsgListener(new YXClient.OnMessageListener() {
            @Override
            public void onNewMessage(IMMessage message) {
                if (message.getAttachment() instanceof ExitAnswerCheckAttachment){
                    myLeaveScribbleMode();
                    finish();
                }
            }
        });


        binding.pageBtnBar.setPageBarAdapter(new PageBtnBarAdapter(getApplicationContext()) {
            @Override
            public int getPageBtnCount() {
                if (binding.questionBodyBtn.isSelected()) {
                    return binding.contentDisplayer.getContentAdapter().getPageCountBaseOnBaseLayer("question");
                } else if (binding.answerAnalysisBtn.isSelected()) {
                    return binding.contentDisplayer.getContentAdapter().getPageCountBaseOnBaseLayer("analysis");
                }
                return 0;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn) {

                myLeaveScribbleMode();
                if (binding.questionBodyBtn.isSelected()) {

                    currentShowReplyPageIndex = btnIndex;
                    binding.contentDisplayer.toPage("question", currentShowReplyPageIndex, true);


                } else if (binding.answerAnalysisBtn.isSelected()) {
                    currentShowAnalysisPageIndex = btnIndex;
                    binding.contentDisplayer.toPage("analysis", currentShowAnalysisPageIndex, true);
                }
            }

        });

        binding.wrongBtn.setOnClickListener(this);
        binding.halfCorrectBtn.setOnClickListener(this);
        binding.correctBtn.setOnClickListener(this);
        binding.answerAnalysisBtn.setOnClickListener(this);
        binding.questionBodyBtn.setOnClickListener(this);
        binding.llLastStudent.setOnClickListener(this);
        binding.llNextStudent.setOnClickListener(this);
        binding.imageRefresh.setOnClickListener(this);
    }

    @Override
    protected void initLayout() {

    }

    private void setData() {

        score = -1;
        stsResultbeanArrayList.clear();
        bytesList.clear();
        pathList.clear();
        //设置数据前，先清理掉之前的缓存数据
        binding.contentDisplayer.getLayer2().clearAll();


        //设置需要展示的学生结果数据
        DialogManager.newInstance().showNetConnDialog(getBaseContext());

        NetWorkManager
                .queryQuestions2BeMarked(SpUtils.getUserId() + "")
//                .queryQuestions2BeMarked("1000002597")
                .compose(bindToLifecycle())
                .subscribe(new Action1<List<QuestionReplyDetail>>() {
                               @Override
                               public void call(List<QuestionReplyDetail> questionReplyDetails) {
                                   if (questionReplyDetails == null || questionReplyDetails.size() == 0) {
//                                       ToastUtil.showCustomToast(getBaseContext(), "已经没有需要批改的问答");
                                       finish();
                                       return;
                                   }

                                   //可能会有多个问答,后台直接倒叙排列，直接取第一个即可。
                                   questionReplyDetail = questionReplyDetails.get(0);

                                   binding.contentDisplayer.getContentAdapter().updateDataList("analysis", 0, questionReplyDetail.getParsedQuestionItem().analysisContentList);
                                   binding.contentDisplayer.getContentAdapter().updateDataList("question", 0, questionReplyDetail.getParsedQuestionItem().questionContentList);


                                   List<Content_new> content_news = questionReplyDetail.getParsedReplyContentList();


                                   //设置学生回答数据
                                   binding.contentDisplayer.getContentAdapter().updateDataList("question", 1, (ArrayList<Content_new>) content_news);

                                   //设置教师批改数据
                                   /*ArrayList<Content_new> replyCommentList = (ArrayList<Content_new>) questionReplyDetail.getParsedReplyCommentList();
                                   if (replyCommentList != null && replyCommentList.size() != 0) {
                                       binding.contentDisplayer.getContentAdapter().updateDataList("question", 2, replyCommentList);
                                   } else {
                                       binding.contentDisplayer.getContentAdapter().deleteDataList("question", 2);
                                   }*/


                                   binding.pageBtnBar.setCurrentSelectPageIndex(-1);
                                   binding.questionBodyBtn.setSelected(true);
                                   binding.answerAnalysisBtn.setSelected(false);
                                   currentShowReplyPageIndex = 0;
                                   currentShowAnalysisPageIndex = 0;

                                   int needSaveSize = content_news.size();

                                   for (int i = 0; i < needSaveSize; i++) {

                                       Content_new content_new = content_news.get(i);

                                       if (content_new != null) {
                                           if (content_new.getType() == Content_new.Type.IMG_URL) {
                                               String picPath = content_new.getValue();
                                               String picName = picPath.substring(picPath.lastIndexOf("/") + 1);
                                               pathList.add(picName);
                                               bytesList.add(null);
                                           }
                                       } else {
                                           pathList.add(null);
                                           bytesList.add(null);
                                       }
                                   }


//                                   binding.tvTitle.setText("问答由" + SpUtils.getAccountName() + "批改");
                                   int replyScore = questionReplyDetail.getReplyScore();

                                   /*//是否批改过了
                                   if (replyScore == -1) {

                                       binding.llControlBottom.setVisibility(View.VISIBLE);
                                       binding.rlControlModifyBottom.setVisibility(View.GONE);

                                   } else {
                                       binding.llControlBottom.setVisibility(View.GONE);
                                       binding.rlControlModifyBottom.setVisibility(View.VISIBLE);

                                       if (replyScore == 0) {
                                           binding.ivCheckResult.setImageResource(R.drawable.img_cuowu);
                                       } else if (replyScore == 50) {
                                           binding.ivCheckResult.setImageResource(R.drawable.img_bandui);
                                       } else {
                                           binding.ivCheckResult.setImageResource(R.drawable.img_zhengque);
                                       }
                                   }*/

                                   //如果教师已经批改，学生收到自评互评时，需要展示出来，让学生进行批改，不过批改的数据提交后服务器不覆盖（20190215老大及产品讨论结果）
                                   binding.llControlBottom.setVisibility(View.VISIBLE);
                                   binding.rlControlModifyBottom.setVisibility(View.GONE);

                                   setWcdToQuestionMode();
                                   binding.pageBtnBar.refreshPageBar();

                               }
                           }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                LogUtils.e("FH", "刷新答题情况失败" + throwable.getMessage());
                                throwable.printStackTrace();

                                ToastUtil.showCustomToast(getBaseContext(), "已经没有需要批改的问答");
                                finish();
                            }
                        });
    }


    private void myLeaveScribbleMode() {
        if (binding.contentDisplayer.getLayer1() != null) {
            binding.contentDisplayer.getLayer1().leaveScribbleMode(true);
        }
        if (binding.contentDisplayer.getLayer2() != null) {
            binding.contentDisplayer.getLayer2().leaveScribbleMode(true);
        }
    }


    @Override
    public void onClick(View view) {
        myLeaveScribbleMode();

        switch (view.getId()) {
            case R.id.question_body_btn:
                //学生答案
                if (!binding.questionBodyBtn.isSelected()) {
                    binding.questionBodyBtn.setSelected(true);
                    binding.answerAnalysisBtn.setSelected(false);
                    setWcdToQuestionMode();
                    binding.contentDisplayer.toPage("question", currentShowReplyPageIndex, true);
                    binding.pageBtnBar.setCurrentSelectPageIndex(currentShowReplyPageIndex);
                    binding.pageBtnBar.refreshPageBar();
                }
                break;
            case R.id.answer_analysis_btn:
                //保存没触发前的界面数据
                if (!binding.answerAnalysisBtn.isSelected()) {
                    binding.answerAnalysisBtn.setSelected(true);
                    binding.questionBodyBtn.setSelected(false);
                    setWcdToAnalysisMode();

                    binding.contentDisplayer.toPage("analysis", currentShowAnalysisPageIndex, true);
                    binding.pageBtnBar.setCurrentSelectPageIndex(currentShowAnalysisPageIndex);
                    binding.pageBtnBar.refreshPageBar();
                }
                break;
            case R.id.correct_btn:
                saveCheckData(currentShowReplyPageIndex);
                score = 100;
                getUpLoadInfo();
                break;
            case R.id.half_correct_btn:
                saveCheckData(currentShowReplyPageIndex);
                score = 50;
                getUpLoadInfo();
                break;
            case R.id.wrong_btn:
                saveCheckData(currentShowReplyPageIndex);
                score = 0;
                getUpLoadInfo();
                break;
            case R.id.image_refresh:
                setData();
                break;
        }

    }

    private void setWcdToQuestionMode() {
        int layer0Size = binding.contentDisplayer.getContentAdapter().getLayerPageCount("question", 0);
        int layer1Size = binding.contentDisplayer.getContentAdapter().getLayerPageCount("question", 1);

        //根据第0层和第1层集合大小调整基准层。
        if (layer0Size > layer1Size) {
            binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(0);
        } else {
            binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(1);
        }

        binding.contentDisplayer.getLayer1().setIntercept(true);
        if (binding.llControlBottom.getVisibility() == View.VISIBLE) {
            binding.contentDisplayer.getLayer2().setIntercept(false);
        } else {
            binding.contentDisplayer.getLayer2().setIntercept(true);
        }
    }

    private void setWcdToAnalysisMode() {
        binding.contentDisplayer.getLayer2().setIntercept(true);
        binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(0);
    }


    @Override
    public void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_answer_check, null, false);
        binding.contentDisplayer.setContentAdapter(new WriteableContentDisplayerAdapter() {
            @Override
            public void afterPageCountChanged(String typeKey) {
                if ((typeKey.equals("question") && binding.questionBodyBtn.isSelected())
                        || (typeKey.equals("analysis") && binding.answerAnalysisBtn.isSelected())) {
                    binding.pageBtnBar.refreshPageBar();
                }
            }

            @Override
            public void beforeToPage(String fromTypeKey, int fromPageIndex, String toTypeKey, int toPageIndex) {

                //当切换题目时（update新数据）框架会将fromTypeKey置空，将fromPageIndex置为-1，这里这么处理是为了第一次调用topage时（第一次进入题目）不触发保存的逻辑。
                if (!TextUtils.isEmpty(fromTypeKey) && "question".equals(fromTypeKey)) {
                    //保存没触发前的界面数据
                    saveCheckData(fromPageIndex);
                }
            }

            @Override
            public void afterToPage(String fromTypeKey, int fromPageIndex, String toTypeKey, int toPageIndex) {

                if (!TextUtils.isEmpty(toTypeKey) && "question".equals(toTypeKey)) {
                    getShowCheckDate();
                }

                if (binding.questionBodyBtn.isSelected()) {
                    int layer0Size = binding.contentDisplayer.getContentAdapter().getLayerPageCount("question", 0);
                    int layer1Size = binding.contentDisplayer.getContentAdapter().getLayerPageCount("question", 1);
                    //根据第0层和第1层集合大小调整基准层。
                    if (layer0Size > layer1Size) {
                        binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(0);

                        int newPageCount = binding.contentDisplayer.getContentAdapter().getPageCountBaseOnBaseLayer("question");
                        //获取到最新的页码数后，刷新需要存储数据的集合（笔记，草稿笔记，图片地址），刷新该题的多页角标，展示显示选择页面题目。
                        if (newPageCount > pathList.size()) {
                            //需要添加的页码数目。
                            int newAddPageNum = newPageCount - pathList.size();

                            for (int i = 0; i < newAddPageNum; i++) {
                                bytesList.add(null);
                                pathList.add(null);
                            }
                        }

                        binding.pageBtnBar.refreshPageBar();
                    }
                }

            }
        });

        binding.contentDisplayer.setStatusChangeListener(new WriteableContentDisplayer.StatusChangeListener() {
            @Override
            public void onStatusChanged(WriteableContentDisplayer.LOADING_STATUS newStatus, String typeKey, int pageIndex, WriteableContentDisplayer.ERROR_TYPE errorType, String errorMsg) {
                switch (newStatus) {
                    case LOADING:
                        binding.contentDisplayer.setHintText("加载中");
                        break;
                    case ERROR:
                        binding.contentDisplayer.setHintText(errorMsg);
                        break;
                    case SUCCESS:
                        binding.contentDisplayer.setHintText(null);//设置为null该view会gone
                        break;
                }
                if (binding.questionBodyBtn.isSelected() && binding.llControlBottom.getVisibility() == View.VISIBLE) {
                    if (newStatus == WriteableContentDisplayer.LOADING_STATUS.SUCCESS) {

                        myLeaveScribbleMode();
                        UIUtils.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.contentDisplayer.getLayer2().setIntercept(false);
                            }
                        }, 600);
                    } else {
                        binding.contentDisplayer.getLayer2().setIntercept(true);
                    }
                }
            }
        });


        binding.questionBodyBtn.setSelected(true);
        setContentView(binding.getRoot());
    }

    @Override
    public void loadData() {
        setData();
    }

    @Override
    protected void refreshView() {

    }


    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        myLeaveScribbleMode();
        super.onBackPressed();
    }


    private void saveCheckData(int index) {

        synchronized (this) {
            if (bytesList.size() == 0) {
                return;
            }
            if (pathList.size() == 0) {
                return;
            }
            //保存笔记
            bytesList.set(index, binding.contentDisplayer.getLayer2().bitmap2Bytes());
            //保存图片
            String fileName = pathList.get(index);
            if (!TextUtils.isEmpty(fileName) && fileName.contains("/")) {
                fileName = fileName.substring(fileName.lastIndexOf("/"));
            } else {
                fileName = System.currentTimeMillis() + ".png";
            }
            String filePath = saveBitmapToFile(binding.contentDisplayer.getLayer2().getBitmap(), fileName);
            pathList.set(index, filePath);
            //清除当前页面笔记
            binding.contentDisplayer.getLayer2().clearAll();
        }
    }


    //展示之前保存的笔记数据
    private void getShowCheckDate() {

        if (bytesList.size() > currentShowReplyPageIndex && currentShowReplyPageIndex >= 0) {
            byte[] tmpBytes = bytesList.get(currentShowReplyPageIndex);
            if (tmpBytes != null) {
                binding.contentDisplayer.getLayer2().drawBitmap(BitmapFactory.decodeByteArray(tmpBytes, 0, tmpBytes.length));
            }
        }
    }

    public String saveBitmapToFile(Bitmap bitmap, String bitName) {

        String fileDir = FileUtils.getAppFilesDir() + "/teacher_check_answer_result";
        FileUtils.createDirs(fileDir);


        File f = new File(fileDir, bitName);
        FileOutputStream fOut = null;
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return f.getAbsolutePath();
    }

    /**
     * 获取oss上传所需信息
     */
    private void getUpLoadInfo() {
        DialogManager.newInstance().showNetConnDialog(getBaseContext());
        NetWorkManager.postCommentRequest(questionReplyDetail.getReplyId() + "", SpUtils.getUserId() + "")
                .subscribe(new Action1<STSbean>() {
                    @Override
                    public void call(STSbean stSbean) {
                        LogUtils.e("FH", "call ");
                        if (stSbean != null) {
                            upLoadPic(stSbean);
                        } else {
                            ToastUtil.showCustomToast(getApplicationContext(), "获取上传信息失败");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });

    }

    /**
     * 上传图片，使用同步方法上传
     *
     * @param stSbean
     */
    public void upLoadPic(STSbean stSbean) {

        String endpoint = Commons.ENDPOINT;
        OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {
            @Override
            public OSSFederationToken getFederationToken() {
                return new OSSFederationToken(stSbean.getAccessKeyId(), stSbean.getAccessKeySecret(), stSbean.getSecurityToken(), stSbean.getExpiration());
            }
        };


        //该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        OSS oss = new OSSClient(getBaseContext(), endpoint, credentialProvider, conf);


        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {

                for (int j = 0; j < pathList.size(); j++) {

                    String picPath = pathList.get(j);

                    if (!TextUtils.isEmpty(picPath) && picPath.contains("/")) {


                        String picName = picPath.substring(picPath.lastIndexOf("/"));


                        // 构造上传请求
                        PutObjectRequest put = new PutObjectRequest(stSbean.getBucketName(), stSbean.getPath() + picName, picPath);
                        try {
                            PutObjectResult putResult = oss.putObject(put);
                            LogUtils.e("PutObject", "UploadSuccess");
                            LogUtils.e("ETag", putResult.getETag());
                            LogUtils.e("RequestId", putResult.getRequestId());
                        } catch (ClientException e) {
                            // 本地异常如网络异常等
                            e.printStackTrace();
                        } catch (ServiceException e) {
                            // 服务异常
                            LogUtils.e("RequestId", e.getRequestId());
                            LogUtils.e("ErrorCode", e.getErrorCode());
                            LogUtils.e("HostId", e.getHostId());
                            LogUtils.e("RawMessage", e.getRawMessage());
                        }

                        STSResultbean stsResultbean = new STSResultbean();
                        stsResultbean.setBucket(stSbean.getBucketName());
                        stsResultbean.setRemote(stSbean.getPath() + picName);
                        File picFile = new File(picPath);
                        stsResultbean.setSize(picFile.length());
                        stsResultbeanArrayList.add(stsResultbean);
                        //上传后清理掉本地图片文件
                        picFile.delete();
                    } else {
                        STSResultbean stsResultbean = new STSResultbean();
                        stsResultbean.setBucket(stSbean.getBucketName());
                        stsResultbean.setRemote("");
                        stsResultbean.setSize(0);
                        stsResultbeanArrayList.add(stsResultbean);
                    }

                }
                pathList.clear();


                subscriber.onNext(new Object());//将执行结果返回
                subscriber.onCompleted();//结束异步任务
            }
        })
                .subscribeOn(Schedulers.io())//异步任务在IO线程执行
                .observeOn(AndroidSchedulers.mainThread())//执行结果在主线程运行
                .subscribe(new Subscriber<Object>() {
                    LoadingProgressDialog loadingProgressDialog;

                    @Override
                    public void onStart() {
                        super.onStart();
                        if (loadingProgressDialog == null) {
                            loadingProgressDialog = new LoadingProgressDialog(AnswerCheckActivity.this);
                            loadingProgressDialog.show();
                            loadingProgressDialog.setTitle("批改上传中...");
                        }

                    }

                    @Override
                    public void onCompleted() {
                        if (loadingProgressDialog != null) {
                            loadingProgressDialog.dismiss();
                            loadingProgressDialog = null;
                        }
                        writeInfoToS();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (loadingProgressDialog != null) {
                            loadingProgressDialog.dismiss();
                            loadingProgressDialog = null;
                        }
                        new HintDialog(AnswerCheckActivity.this, "批改轨迹上传oss失败，请退出后重新批改", "确定", new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        }).show();
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });

    }

    /**
     * 将上传信息提交给服务器
     */
    private void writeInfoToS() {

        String content = new Gson().toJson(stsResultbeanArrayList);
        //教师批改直接使用oss覆盖上传，不需要上传content了
//        String content = "";

        String originalReplyCommentator = SpUtils.getUserId() + "";
        if (questionReplyDetail.getReplyCommentator() != 0) {
            originalReplyCommentator = questionReplyDetail.getReplyCommentator() + "";
        }
        NetWorkManager.postComment(questionReplyDetail.getReplyId() + "", score + "", content, SpUtils.getUserId() + "", originalReplyCommentator)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {

                        ToastUtil.showCustomToast(getBaseContext(), "该问答已批改完毕");
                        //再次刷新查看是否任然有需要自评、互评的问答。如果没有，会在setdata方法中finish该界面。
                        setData();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        ToastUtil.showCustomToast(getBaseContext(), "提交批改分数错误了");
                    }
                });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bytesList != null) {
            bytesList.clear();
        }
        if (pathList != null) {
            pathList.clear();
        }
        if (stsResultbeanArrayList != null) {
            stsResultbeanArrayList.clear();
        }

        binding.contentDisplayer.getLayer2().recycle();
        Glide.get(this).clearMemory();
        binding.contentDisplayer.clearCache();
        Runtime.getRuntime().gc();

    }
}
