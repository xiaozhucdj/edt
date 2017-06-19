package com.yougy.shop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.download.DownloadListener;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.manager.DownloadManager;
import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.nohttp.DownInfo;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.AppendBookCartCallBack;
import com.yougy.common.protocol.callback.AppendBookFavorCallBack;
import com.yougy.common.protocol.callback.PromoteBookCallBack;
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
import com.yougy.home.activity.MainActivity;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.init.bean.BookInfo;
import com.yougy.shop.adapter.PromoteBookAdapter;
import com.yougy.common.protocol.callback.QueryShopBookDetailCallBack;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.DividerGridItemDecoration;
import com.yougy.view.dialog.ConfirmDialog;
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
        bookId = getIntent().getIntExtra(ShopGloble.BOOK_ID , -1);
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
    protected void loadData() {}

    private void refreshData (){
        ProtocolManager.queryShopBookDetailByIdProtocol(SpUtil.getAccountId() , bookId
                , ProtocolId.PROTOCOL_ID_QUERY_SHOP_BOOK_DETAIL , new QueryShopBookDetailCallBack(this , bookId));
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
                if (mBookInfo.isBookInShelf()){
                    ToastUtil.showToast(getApplicationContext() , "这本书已经购买过");
                }
                else {
                    requestOrder();
                }
                break;
            case R.id.btn_addCar:
                if (mBookInfo.isBookInCart() || mBookInfo.isBookInShelf()){
                    ToastUtil.showToast(getApplicationContext() , "本书已在购物车里或已经购买过!");
                }
                else {
                    addBooksToCar(new ArrayList<BookInfo>(){{add(mBookInfo);}});
                }
                break;
            case R.id.btn_addFavor:
                if (mBookInfo.isBookInFavor()){
                    ToastUtil.showToast(getApplicationContext() , "本书已在收藏里");
                }
                else {
                    addBooksToFavor(new ArrayList<BookInfo>(){{add(mBookInfo);}});
                }
                break;
            case R.id.btn_read:
                if (mBookInfo.isBookInShelf()){
                    new ConfirmDialog(this, "您好,您已经购买过该图书", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadIntentWithSpecificFlag(MainActivity.class , Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            finish();
                        }
                    }).setConfirmBtnText("在书包查看").setCancleBtnText("取消").show();
                    return;
                }
                //跳转在线试读
                String probationUrl = FileUtils.getProbationBookFilesDir() + ShopGloble.probationToken + mBookInfo.getBookId() + ".pdf";
                if (FileUtils.exists(probationUrl)) {
                    Intent intent = new Intent(BaseActivity.getCurrentActivity(), ProbationReadBookActivity.class);
                    intent.putExtra(ShopGloble.BOOK_INFO , mBookInfo);
                    startActivity(intent);
                } else {
                    LogUtils.i("试读文件不存在");
                    if (NetUtils.isNetConnected()) {
                        if (mDialog == null) {
                            mDialog = new DownBookDialog(this);
                            mDialog.setListener(this);
                        }
                        mDialog.show();
                        mDialog.getBtnConfirm().setVisibility(View.VISIBLE);
                        mDialog.setTitle(UIUtils.getString(R.string.down_book_defult));
                    } else {
                        UIUtils.showToastSafe(R.string.net_not_connection, Toast.LENGTH_SHORT);
                    }
                }
                break;
        }
    }

    /***
     * 下载图片
     *
     * @param view
     * @param url
     */
    private void refreshImg(ImageView view, String url) {
        int w = view.getMeasuredWidth();
        int h = view.getMeasuredHeight();

        if (w == 0 || h == 0) {
            //测量控件大小
            int result[] = UIUtils.getViewWidthAndHeight(view);
            w = result[0];
            h = result[1];
        }


        ImageLoaderManager.getInstance().loadImageActivity(ShopBookDetailsActivity.this,
                url,
                R.drawable.img_book_cover,
                R.drawable.img_book_cover,
                w,
                h,
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
            loadIntentWithExtra(ShopBookDetailsActivity.class , ShopGloble.BOOK_ID , mBooks.get(position).getBookId());
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
                        if (rep.getCode() == 200){
                            ToastUtil.showToast(getApplicationContext() , "添加到购物车成功");
                            mBookInfo.setBookInCart(true);
                        }
                        else {
                            ToastUtil.showToast(getApplicationContext() , "添加到购物车失败");
                        }
                    } else if (o instanceof AppendBookFavorRep) {
                        AppendBookFavorRep rep = (AppendBookFavorRep) o;
                        if (rep.getCode() == 200){
                            ToastUtil.showToast(getApplicationContext() , "添加到收藏成功");
                            mBookInfo.setBookInFavor(true);
                        }
                        else {
                            ToastUtil.showToast(getApplicationContext() , "添加到收藏失败");
                        }
                    } else if (o instanceof QueryShopBookDetailRep){
                        QueryShopBookDetailRep rep = (QueryShopBookDetailRep) o;
                        if (rep.getData() != null){
                            mBookInfo = rep.getData().get(0);
                            if (mBooks == null || mBooks.size() ==0){
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
                            //TODO:文件大小
                            mTvBookDownloadSize.setText("文件大小 : " + "TODO");
                            //价格
                            mTvBookSalePrice.setText("￥" + mBookInfo.getBookSalePrice());
                            //购买按钮价格
                            mBtnBuy.setText("￥" + mBookInfo.getBookSalePrice() + "购买");
                            //图书详情
                            mTvBookDetails.setText(mBookInfo.getBookSummary());
                            //在线试读是否可点
                            if (TextUtils.isEmpty(mBookInfo.getBookPreview())){
                                mBtnRead.setEnabled(false);
                            }
                            else {
                                mBtnRead.setEnabled(true);
                            }
//                        //获取图书 推荐
//                        PromoteBookRequest request = new PromoteBookRequest();
//                        PromoteBookCallBack callBack = new PromoteBookCallBack(ShopBookDetailsActivity.this, ProtocolId.PROTOCOL_ID_PROMOTE_BOOK, request);
//                        ProtocolManager.promoteBookProtocol(request, ProtocolId.PROTOCOL_ID_PROMOTE_BOOK, callBack);
                        }
                        else {
                            ToastUtil.showToast(getApplicationContext() , "获取图书详情失败");
                            Log.v("FH" , "获取图书详情失败");
                        }
                    }
                    else if (o instanceof RequirePayOrderRep){
                        RequirePayOrderRep rep = (RequirePayOrderRep) o;
                        if (rep.getCode() == 200){
                            RequirePayOrderRep.OrderObj orderObj = rep.getData().get(0);
                            orderObj.setBookList(new ArrayList<BookInfo>(){
                                {
                                    add(mBookInfo);
                                }
                            });
                            Intent intent = new Intent(ShopBookDetailsActivity.this , ConfirmOrderActivity.class);
                            intent.putExtra(ShopGloble.ORDER , orderObj);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            ToastUtil.showToast(getApplicationContext() , "下单失败");
                        }
                    }
                    else if (o instanceof PromoteBookRep){
                        PromoteBookRep rep = (PromoteBookRep) o;
                        if (rep.getCode() == 200){
                            mBooks.clear();
                            mBooks.addAll(rep.getData());
                            mPromoteBookAdapter.notifyDataSetChanged();
                        }
                        else {
                            ToastUtil.showToast(getApplicationContext() , "获取推荐图书失败");
                        }
                    }
                }
            }
        }));
        super.handleEvent();
    }

    private void requestPromoteBook(){
        PromoteBookRequest request = new PromoteBookRequest(mBookInfo.getBookId());
        ProtocolManager.promoteBookProtocol(request , ProtocolId.PROTOCOL_ID_PROMOTE_BOOK
                , new PromoteBookCallBack(this , request));
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

    private void requestOrder (){
        RequirePayOrderRequest request = new RequirePayOrderRequest();
        request.setOrderOwner(SpUtil.getAccountId());
        request.getData().add(new RequirePayOrderRequest.BookIdObj(mBookInfo.getBookId()));
        ProtocolManager.requirePayOrderProtocol(request , ProtocolId.PROTOCOL_ID_REQUIRE_PAY_ORDER
                , new RequireOrderCallBack(this , ProtocolId.PROTOCOL_ID_REQUIRE_PAY_ORDER , request));
    }

    /***
     * 添加购物车
     */
    private void addBooksToCar(List<BookInfo> bookInfoList) {
        AppendBookCartRequest request = new AppendBookCartRequest();
        request.setUserId(SpUtil.getAccountId());
        for (BookInfo bookInfo :
                bookInfoList) {
            request.getData().add(new AppendBookCartRequest.BookIdObj(bookInfo.getBookId()));
        }
        AppendBookCartCallBack call = new AppendBookCartCallBack(this, ProtocolId.PROTOCOL_ID_APPEND_BOOK_CART,request);
        ProtocolManager.appendBookCartProtocol(request, ProtocolId.PROTOCOL_ID_APPEND_BOOK_CART, call);
    }
    /***
     * 添加收藏
     */
    private void addBooksToFavor(List<BookInfo> bookInfoList) {
        AppendBookFavorRequest request = new AppendBookFavorRequest();
        request.setUserId(SpUtil.getAccountId());
        for (BookInfo bookInfo :
                bookInfoList) {
            request.getData().add(new AppendBookFavorRequest.BookIdObj(bookInfo.getBookId()));
        }
        AppendBookFavorCallBack call = new AppendBookFavorCallBack(this, ProtocolId.PROTOCOL_ID_APPEND_BOOK_FAVOR, request);
        ProtocolManager.appendBookFavorProtocol(request, ProtocolId.PROTOCOL_ID_APPEND_BOOK_FAVOR, call);
    }

    @Override
    public void onCancelListener() {
        DownloadManager.cancel();
        mDialog.dismiss();
    }

    @Override
    public void onConfirmListener() {
        mDialog.getBtnConfirm().setVisibility(View.GONE);
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
                }
            }

            @Override
            public void onCancel(int what) {

            }
        });
    }
}