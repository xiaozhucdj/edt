package com.yougy.shop.activity;


import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.pdf.View.MuPDFPageView;
import com.artifex.mupdfdemo.pdf.bean.OutlineActivityData;
import com.jakewharton.rxbinding.view.RxView;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.AppendBookCartCallBack;
import com.yougy.common.protocol.callback.AppendBookFavorCallBack;
import com.yougy.common.protocol.callback.QueryBookCartCallBack;
import com.yougy.common.protocol.request.AppendBookCartRequest;
import com.yougy.common.protocol.request.AppendBookFavorRequest;
import com.yougy.common.protocol.response.AppendBookCartProtocol;
import com.yougy.common.protocol.response.AppendBookFavorProtocol;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.bean.DataBookBean;
import com.yougy.home.imple.PageListener;
import com.yougy.init.bean.BookInfo;
import com.yougy.rx_subscriber.BaseSubscriber;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.view.showView.TextThumbSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

import static com.yougy.ui.activity.R.id.btn_buy;

/**
 * Created by Administrator on 2017/2/14.
 * 试读PDF ，需要把试读的pdf 下载到本地阅读
 */

public class NewProbationRedBookActivity extends ShopBaseActivity {

    @BindView(R.id.ll_pdfFather)
    LinearLayout mLlPdfFather;
    @BindView(R.id.imgbtn_back)
    ImageButton mImgbtnBack;
    @BindView(R.id.imgbtn_jumpCar)
    ImageButton mImgbtnJumpCar;
    @BindView(R.id.imgbtn_addCar)
    ImageButton mImgbtnAddCar;
    @BindView(R.id.imgbtn_favor)
    ImageButton mImgbtnFavor;
    @BindView(btn_buy)
    Button mBtnBuy;
    @BindView(R.id.img_pageBack)
    ImageView mImgPageBack;
    @BindView(R.id.img_pageNext)
    ImageView mImgPageNext;
    @BindView(R.id.seekbar_page)
    TextThumbSeekBar mSeekbarPage;

    private BookInfo mBookInfo;

    /////////////////////////////////Files///////////////////////////
    private String mProbationUrl;
    /***
     * 解析pdf
     */
    private MuPDFCore mCore;
    /***
     * pdf总页数
     */
    private int mPdfCounts;
    /***
     * 显示PDF的view
     */
    private MuPDFPageView mPdfView;
    private PageListenerImple mPageListenerImple;

    /***
     * 当前显示页数
     */
    private int mCurrentPage;
    private int mPageSliderRes;
    protected static final int DURATION = 1;
    private Subscription backScription;
    private Subscription nextScription;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_shop_probation_read_book);
    }

    @Override
    protected void init() {
        if (null != getIntent().getExtras()) {
            mBookInfo = getIntent().getExtras().getParcelable(ShopGloble.JUMP_BOOK_KEY);
        }
    }

    @Override
    protected void initLayout() {
        //初始化布局
        initViewData();
        //初始化PDF
        initPDF();
        //初始化sekkbar
        initSeekbar();
        backScription = RxView.clicks(mImgPageBack).throttleFirst(DURATION, TimeUnit.SECONDS).subscribe(getBackSubscriber());
        nextScription = RxView.clicks(mImgPageNext).throttleFirst(DURATION, TimeUnit.SECONDS).subscribe(getNextSubscriber());
    }

    /**
     * 初始化seekar
     */
    private void initSeekbar() {
        int smax = Math.max(mCore.countPages() - 1, 1);
        mPageSliderRes = ((10 + smax - 1) / smax) * 2;
        mSeekbarPage.setPageSliderRes(mPageSliderRes);
        mSeekbarPage.setMax((mPdfCounts - 1) * mPageSliderRes);

        mSeekbarPage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {

                requestPageTask((seekBar.getProgress() + mPageSliderRes / 2) / mPageSliderRes);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });

    }


    @Override
    protected void loadData() {
        // 获取购物车列表 只为了读取购物车个数 显示在UI上
//        requestCars();
        //加载PDF


        mBtnBuy.setText("￥"+mBookInfo.getBookSalePrice()+"购买");
        requestPageTask(mCurrentPage);
    }

    @Override
    protected void refreshView() {

    }



    @OnClick({R.id.imgbtn_back, R.id.imgbtn_jumpCar, R.id.imgbtn_addCar, R.id.imgbtn_favor, btn_buy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgbtn_back:
                this.finish();
                break;
            case R.id.imgbtn_jumpCar:
                //TODO:进入购物车页面
                break;
            case R.id.imgbtn_addCar:
                //加入购车
                // 图书未购买+图书为付费+图书未被加入购物车三个条件满足，才可有加入购物车操作
                if (StringUtils.isEmpty(mBookInfo.getBookDownload()) && mBookInfo.getBookSalePrice() != 0 && !mBookInfo.isBookInCart()) {
                    requestAddCarProtocol();
                }
                break;
            case R.id.imgbtn_favor:
                //加入收藏夹
                if (!mBookInfo.isBookInFavor())
                    requestAddFavorProtocol();
                break;
            case btn_buy:
                //TODO:进购买页面
                Bundle extra = new Bundle();
                ArrayList<BookInfo> infos = new ArrayList<>() ;
                infos.add(mBookInfo) ;
                extra.putParcelableArrayList(ShopGloble.JUMP_ORDER_CONFIRM_BOOK_LIST_KEY , infos);
                loadIntentWithExtras(ConfirmOrderActivity.class , extra);
                this.finish();
                break;
        }
    }

    /***
     * 初始化PDF
     */
    private void initPDF() {
        mProbationUrl = FileUtils.getProbationBookFilesDir() + ShopGloble.probationToken + mBookInfo.getBookId() + ".pdf";
        mCore = openFile(mProbationUrl);
        if (mCore != null) {
            mPdfCounts = mCore.countPages();
            Point point = new Point(UIUtils.getScreenWidth(), UIUtils.getScreenHeight());
            mPdfView = new MuPDFPageView(this, mCore, point);
            mPageListenerImple = new PageListenerImple();
            mPdfView.setPageListener(mPageListenerImple);
            mLlPdfFather.addView(mPdfView, 0);
        }
    }

    /***
     * 初始化布局参数
     */
    private void initViewData() {

    }

    /***
     * 获取购物车
     */
    private void requestCars() {
        QueryBookCartCallBack callBack = new QueryBookCartCallBack(this, ProtocolId.PROTOCOL_ID_QUERY_BOOK_CART);
        ProtocolManager.queryBookCartProtocol(SpUtil.getAccountId(), ProtocolId.PROTOCOL_ID_QUERY_BOOK_CART, callBack);
    }

    @Override
    protected void handleEvent() {
        responseHandleEvent();
        super.handleEvent();
    }

    /**
     * 协议回调
     */

    private void responseHandleEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
              /*  if (o instanceof QueryBookCartRep) {
                    QueryBookCartRep protocol = (QueryBookCartRep) o;
                    int carsCount = protocol.getCount();
                    //TODO:UI 提示购物车数量
                }else*/ if (o instanceof AppendBookCartProtocol) {
                    //更新 购车按钮状态
                    mBookInfo.setBookInCart(true);
                } else if (o instanceof AppendBookFavorProtocol) {
                    //更新 收藏按钮状态
                    mBookInfo.setBookInFavor(true);
                }
            }
        }));
    }

    //////////////////////////////////////////解析PDF////////////////////////////////////////////////////

    /**
     * 解析PDF
     *
     * @param path ：文件路径
     * @return
     */
    private MuPDFCore openFile(String path) {
        MuPDFCore core = null;
        try {
            // 解析PDF 核心类
            core = new MuPDFCore(this, path);
            //删除 PDF 目录 ，需要回复数据
            OutlineActivityData.set(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return core;
    }

    /***
     * 解析PDF页数后的回调函数
     */
    private class PageListenerImple implements PageListener {

        @Override
        public void onPangeFinishListener() {
            initSeekbarAndTextNumber();
            mImgPageBack.setEnabled(true);
            mImgPageNext.setEnabled(true);
            mSeekbarPage.setClickable(true);
        }
    }

    /**
     * 加载当前某页PDF
     */
    private void requestPageTask(final int position) {
        LogUtils.i("requestPageTask");
        mImgPageBack.setEnabled(false);
        mImgPageNext.setEnabled(false);
        mSeekbarPage.setClickable(false);

        if (position >= mPdfCounts) {
            UIUtils.showToastSafe("当前是最后一页", Toast.LENGTH_SHORT);
            mImgPageBack.setEnabled(true);
            mImgPageNext.setEnabled(true);
            mSeekbarPage.setClickable(true);
            return;
        }
        if (position < 0) {
            UIUtils.showToastSafe("当前是第一页", Toast.LENGTH_SHORT);
            mImgPageBack.setEnabled(true);
            mImgPageNext.setEnabled(true);
            mSeekbarPage.setClickable(true);
            return;
        }
        mCurrentPage = position;
        mPdfView.setPage(position, mCore);
    }

    /**
     * 设置seekbar显示内容
     */
    private void initSeekbarAndTextNumber() {
        if (mCore == null)
            return;
        // 获取 当前显示的 PDF 角标
        final int index = mCurrentPage;
        mSeekbarPage.setProgress(index * mPageSliderRes);
    }

    @NonNull
    private BaseSubscriber<Void> getNextSubscriber() {
        return new BaseSubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                requestPageTask(mPdfView.getPage() + 1);
            }
        };
    }

    @NonNull
    private BaseSubscriber<Void> getBackSubscriber() {
        return new BaseSubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                requestPageTask(mPdfView.getPage() - 1);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (backScription != null) {
            backScription.unsubscribe();
        }
        if (nextScription != null) {
            nextScription.unsubscribe();
        }
    }


    /***
     * 添加购物车
     */
    private void requestAddCarProtocol() {

        AppendBookCartRequest request = new AppendBookCartRequest();

        request.setUserId(SpUtil.getAccountId());
        request.setCount(1);

        List<DataBookBean> list = new ArrayList<>() ;

        DataBookBean bean = new DataBookBean();
        List<BookInfo> bookInfoList = new ArrayList<>() ;
        bookInfoList.add(mBookInfo) ;
        bean.setBookList(bookInfoList);
        bean.setCount(bookInfoList.size());

        request.setData(list);

        AppendBookCartCallBack call = new AppendBookCartCallBack(this, ProtocolId.PROTOCOL_ID_APPEND_BOOK_CART, request);

        ProtocolManager.appendBookCartProtocol(request, ProtocolId.PROTOCOL_ID_APPEND_BOOK_CART, call);
    }

    /***
     * 添加收藏
     */
    private void requestAddFavorProtocol() {


        AppendBookFavorRequest request = new AppendBookFavorRequest();

        request.setUserId(SpUtil.getAccountId());
        request.setCount(1);

        List<DataBookBean> list = new ArrayList<>() ;

        DataBookBean bean = new DataBookBean();
        List<BookInfo> bookInfoList = new ArrayList<>() ;
        bookInfoList.add(mBookInfo) ;
        bean.setBookList(bookInfoList);
        bean.setCount(bookInfoList.size());

        request.setData(list);
        AppendBookFavorCallBack call = new AppendBookFavorCallBack(this, ProtocolId.PROTOCOL_ID_APPEND_BOOK_FAVOR, request);
        ProtocolManager.bookFavorAppendProtocol(request, ProtocolId.PROTOCOL_ID_APPEND_BOOK_FAVOR, call);
    }
}
