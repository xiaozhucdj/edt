package com.yougy.shop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.download.DownloadListener;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.DownloadManager;
import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.nohttp.DownInfo;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.AppendBookCartCallBack;
import com.yougy.common.protocol.callback.AppendBookFavorCallBack;
import com.yougy.common.protocol.callback.PromoteBookCallBack;
import com.yougy.common.protocol.callback.QueryShopBookDetailCallBack;
import com.yougy.common.protocol.callback.RequireOrderCallBack;
import com.yougy.common.protocol.request.AppendBookCartRequest;
import com.yougy.common.protocol.request.AppendBookFavorRequest;
import com.yougy.common.protocol.request.PromoteBookRequest;
import com.yougy.common.protocol.request.RequirePayOrderRequest;
import com.yougy.common.protocol.response.AppendBookCartRep;
import com.yougy.common.protocol.response.AppendBookFavorRep;
import com.yougy.common.protocol.response.PromoteBookRep;
import com.yougy.common.protocol.response.QueryShopBookDetailRep;
import com.yougy.common.protocol.response.RequirePayOrderRep;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.activity.MainActivity;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.init.bean.BookInfo;
import com.yougy.shop.adapter.PromoteBookAdapter;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.DividerGridItemDecoration;
import com.yougy.view.dialog.DownBookDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by FH on 2017/6/9.
 * 图书详情页
 */

public class ShopBookDetailsActivity extends ShopBaseActivity implements DownBookDialog.DownBookListener {
    ////////////////////View//////////////////////////////////////////////////////
    @BindView(R.id.btn_left)
    ImageButton mBtnLeft;
    @BindView(R.id.img_btn_right)
    ImageButton mImgBtnRight;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.img_book_icon)
    ImageView mImgBookIcon;
    @BindView(R.id.tv_book_name)
    TextView mTvBookName;
    @BindView(R.id.tv_bookPublisher)
    TextView mTvBookPublisher;
    @BindView(R.id.tv_bookAuthor)
    TextView mTvBookAuthor;
    @BindView(R.id.tv_bookPublishTime)
    TextView mTvBookPublishTime;
    @BindView(R.id.tv_bookDownloadSize)
    TextView mTvBookDownloadSize;
    @BindView(R.id.tv_bookSalePrice)
    TextView mTvBookSalePrice;
    @BindView(R.id.ll_bookInfos)
    LinearLayout mLlBookInfos;
    @BindView(R.id.btn_buy)
    Button mBtnBuy;
    @BindView(R.id.btn_addCar)
    Button mBtnAddCar;
    @BindView(R.id.btn_addFavor)
    Button mBtnAddFavor;
    @BindView(R.id.btn_read)
    Button mBtnRead;
    @BindView(R.id.tv_bookDetails)
    TextView mTvBookDetails;
    @BindView(R.id.recycler_iew)
    RecyclerView mRecyclerView;

    ////////////////////////global Files//////////////////////////////////////////////////
    /**
     * 图书
     */
    private BookInfo mBookInfo;
    /***
     * 判断activity是否隐藏 ，因为 rxbus 会多次发消息
     */
    private boolean mActivityHide;

    /***
     * 设置 推荐的书具体数目
     */
    private final int PROMOTE_BOOK_COUT = 5;
    /**
     * 图书推荐适配器
     */
    private PromoteBookAdapter mPromoteBookAdapter;
    /**
     * 图书推荐数据集
     */
    private List<BookInfo> mBooks = new ArrayList<>();
    private DownBookDialog mDialog;

    private int bookId;


//////////////////////////////////////tag//////////////////
    /**
     * 去获 图书详情,无网络
     */
    private int mTagForRequestDetailsNoNet = 1;
    /**
     * 打开PDF
     */
    private int mTagBookReader = 2;
    /**
     * 没有网络
     */
    private int mTagNoNet = 3;
    /**
     * 获取图书详情失败
     */
    private int mTagForRequestDetailsFail = 4;
//////////////////////////////////////tag//////////////////

    /***
     * 初始化布局
     */
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_shop_new_book_item_details);
    }

    /**
     * 初始化数据
     */
    @Override
    protected void init() {
        bookId = getIntent().getIntExtra(ShopGloble.BOOK_ID, -1);
    }

    /**
     * 初始化 布局 默认值
     */
    @Override
    protected void initLayout() {
        //初始化推荐列表
        initRecycler();
    }

    /***
     * 加载网络数据 或者其他
     */
    @Override
    protected void loadData() {
    }

    private void refreshData() {
        if (NetUtils.isNetConnected()) {
            ProtocolManager.queryShopBookDetailByIdProtocol(SpUtil.getAccountId(), bookId, ProtocolId.PROTOCOL_ID_QUERY_SHOP_BOOK_DETAIL, new QueryShopBookDetailCallBack(this, bookId));
        } else {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForRequestDetailsNoNet);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshData();
    }

    /**
     * 刷新数据在主线程
     */
    @Override
    protected void refreshView() {
    }

    @OnClick({R.id.btn_left, R.id.img_btn_right, R.id.btn_buy, R.id.btn_addCar, R.id.btn_addFavor, R.id.btn_read})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                this.finish();
                break;
            case R.id.img_btn_right:
                //进入购物车
                loadIntent(ShopCartActivity.class);
                break;
            case R.id.btn_buy:
                if (mBookInfo.isBookInShelf()) {
                    //这本书已经购买过 ,打开PDF
                    showReaderForPackage();
                } else {
                    requestOrder();
                }
                break;
            case R.id.btn_addCar:
                if (mBookInfo.isBookInCart()) {
                    //书加入了购物车
                    showCenterDetermineDialog(R.string.books_already_add_car);
                } else if (mBookInfo.isBookInShelf()) {
                    showReaderForPackage();
                } else {
                    addBooksToCar(new ArrayList<BookInfo>() {{
                        add(mBookInfo);
                    }});
                }
                break;
            case R.id.btn_addFavor:
                if (mBookInfo.isBookInFavor()) {
                    showCenterDetermineDialog(R.string.books_already_add_collection);
                } else {
                    addBooksToFavor(new ArrayList<BookInfo>() {{
                        add(mBookInfo);
                    }});
                }
                break;
            case R.id.btn_read:
                LogUtils.i("aaaaaaaaaaaa");
                if (mBookInfo.isBookInShelf()) {
                    showReaderForPackage();
                    LogUtils.i("bbbbbbbbb");
                    return;
                }
                //跳转在线试读
                String probationUrl = FileUtils.getProbationBookFilesDir() + ShopGloble.probationToken + mBookInfo.getBookId() + ".pdf";
                if (FileUtils.exists(probationUrl)) {
                    LogUtils.i("cccccccccc");
                    jumpProbationActivity();
                } else {
                    LogUtils.i("试读文件不存在");
                    LogUtils.i("dddddddddd");
                    downBookDialog();
                }
                break;
        }
    }

    private void showReaderForPackage() {
        String filePath = FileUtils.getTextBookFilesDir() + mBookInfo.getBookId() + ".pdf";
        if (FileUtils.exists(filePath)) {
            showTagCancelAndDetermineDialog(R.string.books_already_buy, R.string.cancel, R.string.books_reader, mTagBookReader);
        }else{
            showTagCancelAndDetermineDialog(R.string.books_already_buy, R.string.cancel, R.string.play_package, mTagBookReader);
        }
    }

    private void downBookDialog() {
        if (NetUtils.isNetConnected()) {
            if (mDialog == null) {
                mDialog = new DownBookDialog(this);
                mDialog.setListener(this);
            }
            mDialog.show();
            mDialog.getBtnConfirm().setVisibility(View.VISIBLE);
            mDialog.setTitle(UIUtils.getString(R.string.down_book_defult));
        } else {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagNoNet);
        }
    }

    private void jumpProbationActivity() {
        Intent intent = new Intent(BaseActivity.getCurrentActivity(), ProbationReadBookActivity.class);
        intent.putExtra(ShopGloble.BOOK_INFO, mBookInfo);
        startActivity(intent);
    }

    /***
     * 下载图片
     * @param view
     * @param url
     */
    private void refreshImg(ImageView view, String url) {
        ImageLoaderManager.getInstance().loadImageActivity(ShopBookDetailsActivity.this,
                url,
                R.drawable.img_book_cover,
                R.drawable.img_book_cover,
                200,
                267,
                view);
    }

    /***
     * 初始化 推荐列表
     */
    private void initRecycler() {
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
        CustomGridLayoutManager layout = new CustomGridLayoutManager(this, PROMOTE_BOOK_COUT);
        layout.setScrollEnabled(true);
        mRecyclerView.setLayoutManager(layout);

        mPromoteBookAdapter = new PromoteBookAdapter(this, mBooks);
        mRecyclerView.setAdapter(mPromoteBookAdapter);
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                PromoteBookAdapter.HolerPromoteBook bookHolder = (PromoteBookAdapter.HolerPromoteBook) vh;
                int position = bookHolder.getAdapterPosition();
                itemClick(position);
            }
        });
        mPromoteBookAdapter.notifyDataSetChanged();
    }

    /**
     * 点击holder  item
     *
     * @param position
     */
    private void itemClick(int position) {
        if (mBooks.size() > 0) {
            loadIntentWithExtra(ShopBookDetailsActivity.class, ShopGloble.BOOK_ID, mBooks.get(position).getBookId());
        }
    }

    @Override
    protected void handleEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (!mActivityHide) {
                    if (o instanceof AppendBookCartRep) {
                        AppendBookCartRep rep = (AppendBookCartRep) o;
                        if (rep.getCode() == 200) {
                            showCenterDetermineDialog(R.string.books_add_car_success);
                            mBookInfo.setBookInCart(true);
                            setBtnCarState();
                        } else {
                            showCenterDetermineDialog(R.string.books_add_car_fail);
                        }
                    } else if (o instanceof AppendBookFavorRep) {
                        AppendBookFavorRep rep = (AppendBookFavorRep) o;
                        if (rep.getCode() == 200) {
                            showCenterDetermineDialog(R.string.books_add_collection_success);
                            mBookInfo.setBookInFavor(true);
                            setBtnFavorState();
                        } else {
                            showCenterDetermineDialog(R.string.books_add_collection_fail);
                        }
                    } else if (o instanceof QueryShopBookDetailRep) {
                        QueryShopBookDetailRep rep = (QueryShopBookDetailRep) o;
                        if (rep.getData() != null) {
                            mBookInfo = rep.getData().get(0);
                            if (mBooks == null || mBooks.size() == 0) {
                                requestPromoteBook();
                            }
                            //图片
                            refreshImg(mImgBookIcon, mBookInfo.getBookCover());
                            //标题
                            mTvTitle.setText("图书详情");
                            //图书名称
                            mTvBookName.setText(mBookInfo.getBookTitle());
                            //出版社
                            mTvBookPublisher.setText("出版社 : " + mBookInfo.getBookPublisherName());
                            //作者
                            mTvBookAuthor.setText("作者 : " + mBookInfo.getBookAuthor());
                            //出版时间
                            mTvBookPublishTime.setText("出版时间 : " + mBookInfo.getBookPublishTime());
                            mTvBookDownloadSize.setText("文件大小 :");
                            //价格
                            mTvBookSalePrice.setText("￥" + mBookInfo.getBookSalePrice());
                            //购买按钮价格
                            mBtnBuy.setText("￥" + mBookInfo.getBookSalePrice() + "购买");
                            //图书详情
                            mTvBookDetails.setText(mBookInfo.getBookSummary());
                            //在线试读是否可点
                            if (TextUtils.isEmpty(mBookInfo.getBookPreview())) {
                                mBtnRead.setEnabled(false);
                            } else {
                                mBtnRead.setEnabled(true);
                            }
                            //修改按钮状态
                            setBtnCarState();
                            setBtnFavorState();
                        } else {
                            showTagCancelAndDetermineDialog(R.string.books_request_details_fail, R.string.cancel, R.string.retry, mTagForRequestDetailsFail);
                            Log.v("FH", "获取图书详情失败");
                        }
                    } else if (o instanceof RequirePayOrderRep) {
                        RequirePayOrderRep rep = (RequirePayOrderRep) o;
                        if (rep.getCode() == 200) {
                            RequirePayOrderRep.OrderObj orderObj = rep.getData().get(0);
                            orderObj.setBookList(new ArrayList<BookInfo>() {
                                {
                                    add(mBookInfo);
                                }
                            });
                            Intent intent = new Intent(ShopBookDetailsActivity.this, ConfirmOrderActivity.class);
                            intent.putExtra(ShopGloble.ORDER, orderObj);
                            startActivity(intent);
                            finish();
                        } else {
                            showCenterDetermineDialog(R.string.books_request_order_fail);
                        }
                    } else if (o instanceof PromoteBookRep) {
                        PromoteBookRep rep = (PromoteBookRep) o;
                        if (rep.getCode() == 200) {
                            mBooks.clear();
                            mBooks.addAll(rep.getData());
                            mPromoteBookAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.showToast(getApplicationContext(), "");
                            showCenterDetermineDialog(R.string.books_request_recommended_fail);
                        }
                    }
                }
            }
        }));
        super.handleEvent();
    }

    private void requestPromoteBook() {
        if (NetUtils.isNetConnected()) {
            PromoteBookRequest request = new PromoteBookRequest(mBookInfo.getBookId());
            ProtocolManager.promoteBookProtocol(request, ProtocolId.PROTOCOL_ID_PROMOTE_BOOK
                    , new PromoteBookCallBack(this, request));
        } else {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagNoNet);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActivityHide = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActivityHide = true;
    }

    private void requestOrder() {
        if (NetUtils.isNetConnected()) {
            RequirePayOrderRequest request = new RequirePayOrderRequest();
            request.setOrderOwner(SpUtil.getAccountId());
            request.getData().add(new RequirePayOrderRequest.BookIdObj(mBookInfo.getBookId()));
            ProtocolManager.requirePayOrderProtocol(request, ProtocolId.PROTOCOL_ID_REQUIRE_PAY_ORDER
                    , new RequireOrderCallBack(this, ProtocolId.PROTOCOL_ID_REQUIRE_PAY_ORDER, request));
        } else {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagNoNet);
        }
    }

    /***
     * 添加购物车
     */
    private void addBooksToCar(List<BookInfo> bookInfoList) {
        if (NetUtils.isNetConnected()) {
            AppendBookCartRequest request = new AppendBookCartRequest();
            request.setUserId(SpUtil.getAccountId());
            for (BookInfo bookInfo : bookInfoList) {
                request.getData().add(new AppendBookCartRequest.BookIdObj(bookInfo.getBookId()));
            }
            AppendBookCartCallBack call = new AppendBookCartCallBack(this, ProtocolId.PROTOCOL_ID_APPEND_BOOK_CART, request);
            ProtocolManager.appendBookCartProtocol(request, ProtocolId.PROTOCOL_ID_APPEND_BOOK_CART, call);
        } else {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagNoNet);
        }
    }

    /***
     * 添加收藏
     */
    private void addBooksToFavor(List<BookInfo> bookInfoList) {
        if (NetUtils.isNetConnected()) {
            AppendBookFavorRequest request = new AppendBookFavorRequest();
            request.setUserId(SpUtil.getAccountId());
            for (BookInfo bookInfo : bookInfoList) {
                request.getData().add(new AppendBookFavorRequest.BookIdObj(bookInfo.getBookId()));
            }
            AppendBookFavorCallBack call = new AppendBookFavorCallBack(this, ProtocolId.PROTOCOL_ID_APPEND_BOOK_FAVOR, request);
            ProtocolManager.appendBookFavorProtocol(request, ProtocolId.PROTOCOL_ID_APPEND_BOOK_FAVOR, call);
        } else {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagNoNet);
        }
    }

    @Override
    public void onCancelListener() {
        DownloadManager.cancel();
        mDialog.dismiss();
    }

    @Override
    public void onConfirmListener() {
        mDialog.getBtnConfirm().setVisibility(View.GONE);

        if (mBookInfo.isBookInShelf()) {
            //下载文件
            List<DownInfo> mFiles = new ArrayList<>();
            DownInfo info = new DownInfo(mBookInfo.getBookDownload(), FileUtils.getTextBookFilesDir(), mBookInfo.getBookId() + ".pdf", true, false, mBookInfo.getBookId());
            info.setBookName(mBookInfo.getBookTitle());
            mFiles.add(info);
            downBook(mFiles);
        } else {
            try {
                int kb = 1024;
                long fileDirSizeMB = (FileUtils.getDirLength(new File(FileUtils.getProbationBookFilesDir())) / kb / kb);
                //目录大小超过200MB以后删除该目录
                if (fileDirSizeMB >= ShopGloble.PROBATION_FILES_DIR_MAX_SIZE) {
                    //删除文件
                    FileUtils.delFileOrFolder(FileUtils.getProbationBookFilesDir());
                    //重新创建目录
                    FileUtils.createDirs(FileUtils.getProbationBookFilesDir());
                }
                //下载文件
                List<DownInfo> mFiles = new ArrayList<>();
                DownInfo info = new DownInfo(mBookInfo.getBookPreview(), FileUtils.getProbationBookFilesDir(), ShopGloble.probationToken + mBookInfo.getBookId() + ".pdf", true, false, mBookInfo.getBookId());
                info.setBookName(mBookInfo.getBookTitle());
                mFiles.add(info);
                downBook(mFiles);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 文件下载，下载位置 ，FileUtils.getTextBookFilesDir()
     */
    private void downBook(List<DownInfo> mFiles) {
        DownloadManager.downloadFile(mFiles, new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {
                LogUtils.i("  onDownloadError     what ........" + what);
                DownloadManager.cancel();
                mDialog.setTitle(UIUtils.getString(R.string.down_book_error));
                mDialog.getBtnConfirm().setVisibility(View.VISIBLE);
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                LogUtils.i("  onStart     what ........" + what);
            }

            @Override
            public void onProgress(int what, int progress, long fileCount) {
                mDialog.setTitle(String.format(getString(R.string.down_book_loading), progress + "%"));
            }

            @Override
            public void onFinish(int what, String filePath) {
                if (DownloadManager.isFinish()) {
                    mDialog.dismiss();
                    // 自动进入
                    if (mBookInfo.isBookInShelf()) {
                        jumpToControlFragmentActivity();
                    } else {
                        jumpProbationActivity();
                    }
                }
            }

            @Override
            public void onCancel(int what) {
            }
        });
    }

    @Override
    public void onUiCancelListener() {
        super.onUiCancelListener();
        dissMissUiPromptDialog();
        // 获取图书详情 ，无网络
        if (getUiPromptDialog().getTag() == mTagForRequestDetailsNoNet || getUiPromptDialog().getTag() == mTagForRequestDetailsFail) {
            this.finish();
        }
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        dissMissUiPromptDialog();
        // 获取图书详情 ，无网络
        if (getUiPromptDialog().getTag() == mTagForRequestDetailsNoNet || getUiPromptDialog().getTag() == mTagNoNet) {
            jumpToNet();
            //打开PDF
        } else if (getUiPromptDialog().getTag() == mTagBookReader) {
            jumpToControlFragmentActivity();
            //获取图书详情失败
        } else if (getUiPromptDialog().getTag() == mTagForRequestDetailsFail) {
            refreshData();
        }
    }


    private void jumpToControlFragmentActivity() {
        String filePath = FileUtils.getTextBookFilesDir() + mBookInfo.getBookId() + ".pdf";
        if (FileUtils.exists(filePath)) {
            Bundle extras = new Bundle();
            //课本进入
            extras.putString(FileContonst.JUMP_FRAGMENT, FileContonst.JUMP_TEXT_BOOK);
            //笔记创建者
            extras.putInt(FileContonst.NOTE_CREATOR, -1);
            //笔记id
            extras.putInt(FileContonst.NOTE_ID, mBookInfo.getBookFitNoteId());
            //图书id
            extras.putInt(FileContonst.BOOK_ID, mBookInfo.getBookId());
            //分类码
            extras.putInt(FileContonst.CATEGORY_ID, mBookInfo.getBookCategory());
            //笔记类型
            extras.putInt(FileContonst.NOTE_Style, mBookInfo.getNoteStyle());
            extras.putInt(FileContonst.NOTE_SUBJECT_ID, mBookInfo.getBookFitSubjectId());
            extras.putString(FileContonst.NOTE_SUBJECT_NAME, mBookInfo.getBookFitSubjectName());
            //作业ID
            extras.putInt(FileContonst.HOME_WROK_ID, mBookInfo.getBookFitHomeworkId());
            extras.putString(FileContonst.NOTE_TITLE, mBookInfo.getBookFitNoteTitle());
            loadIntentWithExtras(ControlFragmentActivity.class, extras);
        } else {
//            downBookDialog();
            //跳转到书包
            loadIntentWithSpecificFlag(MainActivity.class , Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            finish();
        }
    }

    @Override
    public void onUiCenterDetermineListener() {
        super.onUiCenterDetermineListener();
        dissMissUiPromptDialog();
    }

    private void jumpToNet() {
        Intent intent = new Intent("android.intent.action.WIFI_ENABLE");
        startActivity(intent);
    }

    private void setBtnCarState() {
        if (mBookInfo.isBookInCart()) {
            mBtnAddCar.setText(R.string.books_already_add_car);
            mBtnAddCar.setTextColor(UIUtils.getColor(R.color.directory_text));
        }
    }

    private void setBtnFavorState() {
        mBtnAddFavor.setText(R.string.books_already_add_collection);
        mBtnAddFavor.setTextColor(UIUtils.getColor(R.color.directory_text));
    }
}