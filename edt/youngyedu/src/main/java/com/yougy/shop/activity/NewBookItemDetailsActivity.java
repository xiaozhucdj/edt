package com.yougy.shop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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
import com.yougy.common.protocol.request.AppendBookCartRequest;
import com.yougy.common.protocol.request.AppendBookFavorRequest;
import com.yougy.common.protocol.request.PromoteBookRequest;
import com.yougy.common.protocol.response.AppendBookCartProtocol;
import com.yougy.common.protocol.response.AppendBookFavorProtocol;
import com.yougy.common.protocol.response.PromoteBookProtocol;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.BookAdapter;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.home.bean.DataBookBean;
import com.yougy.shop.adapter.PromoteBookAdapter;
import com.yougy.shop.bean.BookInfo;
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
 * Created by Administrator on 2017/2/8.
 * 新_图书详情页
 */

public class NewBookItemDetailsActivity extends ShopBaseActivity implements DownBookDialog.DownBookListener {
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
        if (null != getIntent().getExtras()) {
            mBookInfo = getIntent().getParcelableExtra(ShopGloble.JUMP_BOOK_KEY);
            LogUtils.e(tag,mBookInfo.getBookTitle());
        }
    }

    /**
     * 初始化 布局 默认值
     */
    @Override
    protected void initLayout() {
        //图片
        refreshImg(mImgBookIcon, mBookInfo.getBookCover());
        //标题
        mTvTitle.setText(mBookInfo.getBookTitle());
        //出版社
        mTvBookPublisher.setText(mBookInfo.getBookPublisher()+"");
        //出版时间
        mTvBookPublishTime.setText(mBookInfo.getBookPublishTime());
        //作者
        mTvBookAuthor.setText(mBookInfo.getBookAuthor());
        //TODO:文件大小
//        mTvBookDownloadSize.setText(mBookInfo.getBookDownloadSize());
        //价格
        mTvBookSalePrice.setText(mBookInfo.getBookSalePrice() + "");
        //判断是否加入收藏
        //mBtnAddFavor.setSelected(mBookInfo.isBookInFavor());
        //判断是否加入购物车
        //mBtnAddCar.setSelected(mBookInfo.isBookInCart());
        //购买按钮价格
        mBtnBuy.setText(mBookInfo.getBookSalePrice() + "");
        //图书详情
        mTvBookDetails.setText(mBookInfo.getBookSummary());
        //初始化推荐列表
        initRecycler();
    }

    /***
     * 加载网络数据 或者其他
     */
    @Override
    protected void loadData() {
        //获取图书 推荐
        PromoteBookRequest request = new PromoteBookRequest();
        PromoteBookCallBack callBack = new PromoteBookCallBack(this, ProtocolId.PROTOCOL_ID_PROMOTE_BOOK, request);
        ProtocolManager.promoteBookProtocol(request, ProtocolId.PROTOCOL_ID_PROMOTE_BOOK, callBack);
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
                loadIntent(NewShopCartActivity.class);
                break;
            case R.id.btn_buy:
                //跳转 购买页面
                Bundle extra = new Bundle();
                ArrayList<BookInfo> infos = new ArrayList<>() ;
                infos.add(mBookInfo) ;
                extra.putParcelableArrayList(ShopGloble.JUMP_ORDER_CONFIRM_BOOK_LIST_KEY , infos);
                loadIntentWithExtras(ConfirmOrderActivity.class , extra);
                this.finish();
                break;
            case R.id.btn_addCar:
                // 加入购车
                // 图书未购买+图书为付费+图书未被加入购物车三个条件满足，才可有加入购物车操作
                //TODO:根据 购买日期判断是否需要加入购物车
//                if (StringUtils.isEmpty(mBookInfo.getBookDownload()) && mBookInfo.getBookSalePrice() != 0 && !mBookInfo.isBookInCart()) {
//                    requestAddCarProtocol();
//                }
                break;
            case R.id.btn_addFavor:
                //TODO:加入收藏夹
//                if (!mBookInfo.isBookInFavor())
//                    requestAddFavorProtocol();
                break;
            case R.id.btn_read:
                //跳转在线试读
                String probationUrl = FileUtils.getProbationBookFilesDir() + ShopGloble.probationToken + mBookInfo.getBookId() + ".pdf";
                if (FileUtils.exists(probationUrl)) {
                    Bundle extras = new Bundle();
                    extras.putParcelable(ShopGloble.JUMP_BOOK_KEY, mBookInfo);
                    Intent intent = new Intent(BaseActivity.getCurrentActivity(), NewProbationRedBookActivity.class);
                    intent.putExtras(extras);
                    BaseActivity.getCurrentActivity().startActivity(intent);
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


        ImageLoaderManager.getInstance().loadImageActivity(NewBookItemDetailsActivity.this,
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
                BookAdapter.HolerFragmentBook bookHolder = (BookAdapter.HolerFragmentBook) vh;
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
            Bundle extras = new Bundle();
            extras.putParcelable(ShopGloble.JUMP_BOOK_KEY, mBooks.get(position));
            Intent intent = new Intent(this, NewBookItemDetailsActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
        }
    }

    @Override
    protected void handleEvent() {
        handlePromoteBookEvent();
        super.handleEvent();
    }

    /***
     * 获取推荐图书的协议回调 ，在Recycler 显示
     */
    private void handlePromoteBookEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (!mActivityHide) {
                    if (o instanceof PromoteBookProtocol) {
                        PromoteBookProtocol protocol = (PromoteBookProtocol) o;
                        if (protocol.getData() != null && protocol.getData().get(0) != null && protocol.getData().get(0).getBookList() != null)
                            mBooks.clear();
                        //TODO:add book to books
//                        mBooks.addAll(protocol.getData().get(0).getBookList());
                        mPromoteBookAdapter.notifyDataSetChanged();
                    } else if (o instanceof AppendBookCartProtocol) {
                        //更新 购车按钮状态
//                        mBookInfo.setBookInCart(true);
                    } else if (o instanceof AppendBookFavorProtocol) {
                        //更新 收藏按钮状态
//                        mBookInfo.setBookInFavor(true);
                    }
                }
            }
        }));
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

    /***
     * 添加购物车
     */
    private void requestAddCarProtocol() {

        AppendBookCartRequest request = new AppendBookCartRequest();

        request.setUserId(SpUtil.getAccountId());
        request.setCount(1);

        List<DataBookBean> list = new ArrayList<>();

        DataBookBean bean = new DataBookBean();
        List<BookInfo> bookInfoList = new ArrayList<>();
        bookInfoList.add(mBookInfo);
//        bean.setBookList(bookInfoList);
        bean.setCount(bookInfoList.size());

        request.setData(list);
        AppendBookCartCallBack call = new AppendBookCartCallBack(this, ProtocolId.PROTOCOL_ID_APPEND_BOOK_CART,request);
        ProtocolManager.appendBookCartProtocol(request, ProtocolId.PROTOCOL_ID_APPEND_BOOK_CART, call);
    }

    /***
     * 添加收藏
     */
    private void requestAddFavorProtocol() {

        AppendBookFavorRequest request = new AppendBookFavorRequest();

        request.setUserId(SpUtil.getAccountId());
        request.setCount(1);

        List<DataBookBean> list = new ArrayList<>();

        DataBookBean bean = new DataBookBean();
        List<BookInfo> bookInfoList = new ArrayList<>();
        bookInfoList.add(mBookInfo);
//        bean.setBookList(bookInfoList);
        bean.setCount(bookInfoList.size());

        request.setData(list);
        AppendBookFavorCallBack call = new AppendBookFavorCallBack(this, ProtocolId.PROTOCOL_ID_APPEND_BOOK_FAVOR, request);
        ProtocolManager.bookFavorAppendProtocol(request, ProtocolId.PROTOCOL_ID_APPEND_BOOK_FAVOR, call);
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
//            DownInfo info = new DownInfo(mBookInfo.getBookPreview(), FileUtils.getProbationBookFilesDir(), ShopGloble.probationToken + mBookInfo.getBookId() + ".pdf", true, false, mBookInfo.getBookId());
//            info.setBookName(mBookInfo.getBookTitle());
//            mFiles.add(info);
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
                mDialog.setTitle(UIUtils.getString(R.string.down_book_defult));
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