package com.yougy.anwser;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

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
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.message.EndQuestionAttachment;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnsweringBinding;
import com.yougy.view.NoteBookView2;
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
 * Created by FH on 2017/3/22.
 * <p>
 * 问答中界面
 */

public class AnsweringActivity extends AnswerBaseActivity {
    ActivityAnsweringBinding binding;
    ParsedQuestionItem parsedQuestionItem;
    private NoteBookView2 mNbvAnswerBoard;

    //图片地址的集合（用来保存截图生长的图片路径）
    private ArrayList<String> picPathList = new ArrayList<>();
    //byte数组集合，（用来保存每一页书写的笔记数据）
    private ArrayList<byte[]> bytesList = new ArrayList<>();
    //当前页，默认从1开始
    private int position = 1;

    String itemId;
    String fromUserId;
    int examId;

    private Bitmap firstResultBitmap;


    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_answering, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    public void init() {
        Log.v("FH", "AnsweringActivity init " + this.toString());
        itemId = getIntent().getStringExtra("itemId");
        itemId = "73";
        if (TextUtils.isEmpty(itemId)) {
            ToastUtil.showToast(this, "item 为空,开始问答失败");
            Log.v("FH", "item 为空,开始问答失败");
            finish();
        }
        fromUserId = getIntent().getStringExtra("from");
        fromUserId = "10000200";
        if (TextUtils.isEmpty(fromUserId)) {
            ToastUtil.showToast(this, "from userId 为空,开始问答失败");
            Log.v("FH", "from userId 为空,开始问答失败");
            finish();
        }
        examId = getIntent().getIntExtra("examId", -1);
        examId = 148;
        if (examId == -1) {
            ToastUtil.showToast(this, "examId 为空,开始问答失败");
            Log.v("FH", "examId 为空,开始问答失败");
            finish();
        }
    }

    @Override
    protected void handleEvent() {
        super.handleEvent();
        tapEventEmitter.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o instanceof IMMessage) {
                            if (((IMMessage) o).getAttachment() instanceof EndQuestionAttachment) {
                                if (((EndQuestionAttachment) ((IMMessage) o).getAttachment()).examID == examId) {
                                    new HintDialog(AnsweringActivity.this, "老师已经结束本次问答", "确定", new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    }).show();
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void loadData() {
        NetWorkManager.queryQuestionItemList(fromUserId, null, itemId, null)
                .subscribe(new Action1<List<ParsedQuestionItem>>() {
                    @Override
                    public void call(List<ParsedQuestionItem> parsedQuestionItems) {
                        Log.v("FH", "call ");
                        if (parsedQuestionItems != null && parsedQuestionItems.size() > 0) {
                            parsedQuestionItem = parsedQuestionItems.get(0);
                            refreshView();
                        } else {
                            ToastUtil.showToast(getApplicationContext(), "获取到的题目为空,开始问答失败");
                            Log.v("FH", "获取到的题目为空,开始问答失败");
                            finish();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });

    }

    @Override
    protected void initLayout() {


        //新建写字板，并添加到界面上
        mNbvAnswerBoard = new NoteBookView2(this);


    }


    public void onClick(View view) {
        EpdController.leaveScribbleMode(mNbvAnswerBoard);
        switch (view.getId()) {
            case R.id.start_answer_btn:
                binding.startAnswerBtn.setVisibility(View.GONE);
                binding.pageBtnLayout.setVisibility(View.VISIBLE);
                binding.rlAnswer.addView(mNbvAnswerBoard);

                bytesList.add(mNbvAnswerBoard.bitmap2Bytes());
                ToastUtil.showToast(this, "请开始作答");

                break;
            case R.id.last_page_btn:
                /*
                上一页 ，下一页逻辑
                1.保存当期页面数据到集合中
                2.清理当前页面数据
                3.将上一页，下一页数据从集合中取出，并回复到页面
                */

                if (position == 1) {
                    firstResultBitmap = saveScreenBitmap();
                }

                bytesList.set(position - 1, mNbvAnswerBoard.bitmap2Bytes());

                if (position == 1) {
                    ToastUtil.showToast(this, "已经是第一页了");
                    return;
                }
                mNbvAnswerBoard.clearAll();

                position--;
                binding.pageNumTv.setText(position + "/" + bytesList.size());


                byte[] tmpBytes = bytesList.get(position - 1);
                mNbvAnswerBoard.drawBitmap(BitmapFactory.decodeByteArray(tmpBytes, 0, tmpBytes.length));
                if (position == 1) {
                    binding.questionContainer.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.next_page_btn:

                if (position == 1) {
                    firstResultBitmap = saveScreenBitmap();
                    binding.questionContainer.setVisibility(View.VISIBLE);
                }
                binding.questionContainer.setVisibility(View.GONE);
                bytesList.set(position - 1, mNbvAnswerBoard.bitmap2Bytes());
                if (position == bytesList.size()) {
                    ToastUtil.showToast(this, "已经是最后一页了");
                    return;
                }
                mNbvAnswerBoard.clearAll();
                position++;
                binding.pageNumTv.setText(position + "/" + bytesList.size());


                tmpBytes = bytesList.get(position - 1);
                mNbvAnswerBoard.drawBitmap(BitmapFactory.decodeByteArray(tmpBytes, 0, tmpBytes.length));

                break;
            case R.id.add_page_btn:
                if (position == 1) {
                    firstResultBitmap = saveScreenBitmap();
                }
                binding.questionContainer.setVisibility(View.GONE);

                bytesList.set(position - 1, mNbvAnswerBoard.bitmap2Bytes());
                mNbvAnswerBoard.clearAll();
                position++;
                bytesList.add(mNbvAnswerBoard.bitmap2Bytes());

                binding.pageNumTv.setText(position + "/" + bytesList.size());


                break;
            case R.id.delete_current_page_btn:
                if (position == 1) {
                    ToastUtil.showToast(this, "第一页不能删除");
                    return;
                }
                mNbvAnswerBoard.clearAll();
                bytesList.remove(position - 1);
                position--;

                binding.pageNumTv.setText(position + "/" + bytesList.size());


                tmpBytes = bytesList.get(position - 1);
                mNbvAnswerBoard.drawBitmap(BitmapFactory.decodeByteArray(tmpBytes, 0, tmpBytes.length));
                if (position == 1) {
                    binding.questionContainer.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.cancle_btn:
//                mNbvAnswerBoard.clearAll();
//                ToastUtil.showToast(this, "清理成功");

                break;
            case R.id.commit_answer_btn:
                if (position == 1) {
                    firstResultBitmap = saveScreenBitmap();
                } else {
                    bytesList.set(position - 1, mNbvAnswerBoard.bitmap2Bytes());
                }

                makePicbyList();


                break;


        }

    }


    /**
     * 保存所有学生答案的图片地址到集合中去
     */
    private void makePicbyList() {


        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {

                String firstFileName = saveBitmapToFile(firstResultBitmap);
                picPathList.add(firstFileName);

                for (int i = 1; i < bytesList.size(); i++) {

                    byte[] mbyte = bytesList.get(i);

                    Bitmap bitmap = BitmapFactory.decodeByteArray(mbyte, 0, mbyte.length);

                    String filePath = saveBitmapToFile(bitmap);
                    picPathList.add(filePath);

                }


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
                            loadingProgressDialog = new LoadingProgressDialog(AnsweringActivity.this);
                            loadingProgressDialog.show();
                            loadingProgressDialog.setTitle("答案生成中...");
                        }

                    }

                    @Override
                    public void onCompleted() {
                        if (loadingProgressDialog != null) {
                            loadingProgressDialog.dismiss();
                            loadingProgressDialog = null;
                        }
                        ToastUtil.showToast(AnsweringActivity.this, "答案生成完毕");
                        getUpLoadInfo();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loadingProgressDialog != null) {
                            loadingProgressDialog.dismiss();
                            loadingProgressDialog = null;
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });


        ToastUtil.showToast(this, "保存完毕");
    }


    @Override
    protected void refreshView() {
        ParsedQuestionItem.Question question = parsedQuestionItem.questionList.get(0);
        binding.questionTypeTextview.setText("题目类型 : " + question.questionType);
        if (question instanceof ParsedQuestionItem.HtmlQuestion) {
            binding.questionContainer.setHtmlUrl(((ParsedQuestionItem.HtmlQuestion) question).htmlUrl);
        } else if (question instanceof ParsedQuestionItem.TextQuestion) {
            binding.questionContainer.setText(((ParsedQuestionItem.TextQuestion) question).text);
        } else if (question instanceof ParsedQuestionItem.ImgQuestion) {
            binding.questionContainer.setImgUrl(((ParsedQuestionItem.ImgQuestion) question).imgUrl);
        }
    }

    public void back(View view) {
        EpdController.leaveScribbleMode(mNbvAnswerBoard);
        ToastUtil.showToast(this,"请完成作答");
        // TODO: 2017/9/13 这里先保留关闭页面，做测试使用 
        finish();
    }

    private void saveResultBitmap(String fileName) {
        binding.rlAnswer.setDrawingCacheEnabled(true);
        Bitmap tBitmap = binding.rlAnswer.getDrawingCache();
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = tBitmap.createBitmap(tBitmap);
        binding.rlAnswer.setDrawingCacheEnabled(false);
        if (tBitmap != null) {
//            ivResult.setImageBitmap(tBitmap);
            saveBitmapToFile(tBitmap);
            Toast.makeText(this, "获取成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap saveScreenBitmap() {
        binding.rlAnswer.setDrawingCacheEnabled(true);
        Bitmap tBitmap = binding.rlAnswer.getDrawingCache();
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = tBitmap.createBitmap(tBitmap);
        binding.rlAnswer.setDrawingCacheEnabled(false);
        return tBitmap;
    }


    public String saveBitmapToFile(Bitmap bitmap) {

        String fileDir = FileUtils.getAppFilesDir() + "/answer_result";
        FileUtils.createDirs(fileDir);


        String bitName = System.currentTimeMillis() + "";
        File f = new File(fileDir, bitName + ".png");
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
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
        NetWorkManager.queryReplyRequest(SpUtil.getUserId() + "")
                .subscribe(new Action1<STSbean>() {
                    @Override
                    public void call(STSbean stSbean) {
                        Log.v("FH", "call ");
                        if (stSbean != null) {
                            upLoadPic(stSbean);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), "获取上传信息失败");
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


        String endpoint = "http://oss-cn-shanghai.aliyuncs.com";


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
        OSS oss = new OSSClient(YougyApplicationManager.getContext(), endpoint, credentialProvider, conf);


        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {

                for (int i = 0; i < picPathList.size(); i++) {

                    String picPath = picPathList.get(i);
                    String picName = picPath.substring(picPath.lastIndexOf("/"));

                    // 构造上传请求
                    PutObjectRequest put = new PutObjectRequest(stSbean.getBucketName(), stSbean.getPath() + picName, picPath);
                    try {
                        PutObjectResult putResult = oss.putObject(put);
                        Log.d("PutObject", "UploadSuccess");
                        Log.d("ETag", putResult.getETag());
                        Log.d("RequestId", putResult.getRequestId());
                    } catch (ClientException e) {
                        // 本地异常如网络异常等
                        e.printStackTrace();
                    } catch (ServiceException e) {
                        // 服务异常
                        Log.e("RequestId", e.getRequestId());
                        Log.e("ErrorCode", e.getErrorCode());
                        Log.e("HostId", e.getHostId());
                        Log.e("RawMessage", e.getRawMessage());
                    }

                }


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
                            loadingProgressDialog = new LoadingProgressDialog(AnsweringActivity.this);
                            loadingProgressDialog.show();
                            loadingProgressDialog.setTitle("答案上传中...");
                        }

                    }

                    @Override
                    public void onCompleted() {
                        if (loadingProgressDialog != null) {
                            loadingProgressDialog.dismiss();
                            loadingProgressDialog = null;
                        }

                        Intent intent = new Intent(AnsweringActivity.this, AnswerResultActivity.class);
                        intent.putExtra("question", parsedQuestionItem);
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loadingProgressDialog != null) {
                            loadingProgressDialog.dismiss();
                            loadingProgressDialog = null;
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });

    }

}
