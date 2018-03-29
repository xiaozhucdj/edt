package com.yougy.shop.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.reader.ReaderContract;
import com.onyx.reader.ReaderPresenter;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.AppendBookCartCallBack;
import com.yougy.common.protocol.callback.AppendBookFavorCallBack;
import com.yougy.common.protocol.callback.QueryBookCartCallBack;
import com.yougy.common.protocol.callback.QueryOrderListCallBack;
import com.yougy.common.protocol.callback.QueryShopBookDetailCallBack;
import com.yougy.common.protocol.callback.RequireOrderCallBack;
import com.yougy.common.protocol.request.AppendBookCartRequest;
import com.yougy.common.protocol.request.AppendBookFavorRequest;
import com.yougy.common.protocol.request.RequirePayOrderRequest;
import com.yougy.common.protocol.response.AppendBookCartRep;
import com.yougy.common.protocol.response.AppendBookFavorRep;
import com.yougy.common.protocol.response.QueryBookCartRep;
import com.yougy.common.protocol.response.QueryBookOrderListRep;
import com.yougy.common.protocol.response.QueryShopBookDetailRep;
import com.yougy.common.protocol.response.RequirePayOrderRep;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.bean.BriefOrder;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.view.dialog.HintDialog;
import com.yougy.view.showView.TextThumbSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by FH on 2017/2/14.
 * 试读PDF ，需要把试读的pdf 下载到本地阅读
 */

public class ProbationReadBookActivity extends ShopBaseActivity implements ReaderContract.ReaderView {

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
    @BindView(R.id.btn_buy)
    Button mBtnBuy;
    @BindView(R.id.img_pageBack)
    ImageView mImgPageBack;
    @BindView(R.id.img_pageNext)
    ImageView mImgPageNext;
    @BindView(R.id.seekbar_page)
    TextThumbSeekBar mSeekbarPage;
    @BindView(R.id.cart_count_tv)
    TextView cartCountTV;

    private BookInfo mBookInfo;
    private Subscription nextScription;
    private Subscription backScription;
    private String mProbationUrl;
    private ImageView mOnyxImgView;
    private int mPageCounts;
    private int mPageSliderRes;
    private View mRootView;
    private int mCurrentMarksPage = 0;
    private int mTagForNoNet = 1;
    private int mTagForRequestDetailsFail = 2;
    private int mTagForQueryNoNet = 3;

    /////////////////////////////////Files///////////////////////////


    @Override
    protected void setContentView() {
        mRootView = UIUtils.inflate(R.layout.activity_shop_probation_read_book);
        setContentView(mRootView);
    }

    @Override
    protected void init() {
        mBookInfo = getIntent().getParcelableExtra(ShopGloble.BOOK_INFO);
        mImgbtnFavor.setImageResource(mBookInfo.isBookInFavor() ? R.drawable.icon_shoucang_yitianjia : R.drawable.icon_shoucang);
    }

    @Override
    protected void initLayout() {
        //初始化布局
        initViewData();
    }


    @Override
    protected void loadData() {
        nextScription = RxView.clicks(mImgPageNext).throttleFirst(50, TimeUnit.MILLISECONDS).subscribe(getNextSubscriber());
        backScription = RxView.clicks(mImgPageBack).throttleFirst(50, TimeUnit.MILLISECONDS).subscribe(getBackSubscriber());
        initPDF();
    }

    private void initPDF() {
//     mProbationUrl = FileUtils.getProbationBookFilesDir() + ShopGloble.probationToken + mBookInfo.getBookId() + ".pdf";

        mProbationUrl = FileUtils.getBookFileName(mBookInfo.getBookId(), FileUtils.bookProbation);
        LogUtils.i("mProbationUrl ......" + mProbationUrl);
        mOnyxImgView = new ImageView(this);
        mOnyxImgView.setLayoutParams(new LinearLayout.LayoutParams(UIUtils.getScreenWidth(), UIUtils.getScreenHeight()));
        mLlPdfFather.addView(mOnyxImgView, 0);
        getReaderPresenter().openDocument(mProbationUrl, "");
    }

    //////////////////////////////////////////解析PDF////////////////////////////////////////////////////
    private ReaderPresenter mReaderPresenter;

    private ReaderContract.ReaderPresenter getReaderPresenter() {
        if (mReaderPresenter == null) {
            mReaderPresenter = new ReaderPresenter(this);
        }
        return mReaderPresenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backScription != null) {
            backScription.unsubscribe();
        }
        if (nextScription != null) {
            nextScription.unsubscribe();
        }
        getReaderPresenter().close();
    }

    private Action1<? super Void> getBackSubscriber() {
        return new Action1<Void>() {

            @Override
            public void call(Void aVoid) {
                requestPageTask(mCurrentMarksPage - 1);
            }
        };
    }

    private Action1<? super Void> getNextSubscriber() {
        return new Action1<Void>() {

            @Override
            public void call(Void aVoid) {
                requestPageTask(mCurrentMarksPage + 1);
            }
        };
    }


    protected void refreshData() {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForQueryNoNet);
            return;
        }

        ProtocolManager.queryShopBookDetailByIdProtocol(SpUtils.getAccountId(), mBookInfo.getBookId()
                , ProtocolId.PROTOCOL_ID_QUERY_SHOP_BOOK_DETAIL, new QueryShopBookDetailCallBack(this, mBookInfo.getBookId()));

        ProtocolManager.queryBookCartProtocol(SpUtils.getAccountId(), ProtocolId.PROTOCOL_ID_QUERY_BOOK_CART
                , new QueryBookCartCallBack(this, ProtocolId.PROTOCOL_ID_QUERY_BOOK_CART));
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshData();
    }

    @Override
    protected void refreshView() {
    }

    @OnClick({R.id.imgbtn_back, R.id.imgbtn_jumpCar, R.id.imgbtn_addCar, R.id.imgbtn_favor, R.id.btn_buy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgbtn_back:
                this.finish();
                break;
            case R.id.imgbtn_jumpCar:
                loadIntent(ShopCartActivity.class);
                this.finish();
                break;
            case R.id.imgbtn_addCar:
                if (mBookInfo.isBookInCart()) {
                    showCenterDetermineDialog(R.string.books_already_add_car);
                } else if (mBookInfo.isBookInShelf()) {
                    showCenterDetermineDialog(R.string.books_already_buy);
                } else {
                    addBooksToCar(new ArrayList<BookInfo>() {{
                        add(mBookInfo);
                    }});
                }
                break;
            case R.id.imgbtn_favor:
                if (mBookInfo.isBookInFavor()) {
                    showCenterDetermineDialog(R.string.books_already_add_collection);
                } else {
                    addBooksToFavor(new ArrayList<BookInfo>() {{
                        add(mBookInfo);
                    }});
                }
                break;
            case R.id.btn_buy:
                if (mBookInfo.isBookInShelf()) {
                    showCenterDetermineDialog(R.string.books_already_buy);
                } else {
                    requestOrder();
                }
                break;
        }
    }


    private void requestOrder() {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
            return;
        }
        ProtocolManager.queryBookOrderProtocol(String.valueOf(SpUtils.getAccountId())
                , "[\"已支付\",\"待支付\"]"
                , ProtocolId.PROTOCOL_ID_QUERY_BOOK_ORDER
                , new QueryOrderListCallBack(ProbationReadBookActivity.this, ProtocolId.PROTOCOL_ID_QUERY_BOOK_ORDER));
    }

    /***
     * 初始化布局参数
     */
    private void initViewData() {
    }

    @Override
    protected void handleEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof AppendBookCartRep) {
                    AppendBookCartRep rep = (AppendBookCartRep) o;
                    if (rep.getCode() == 200) {
                        showCenterDetermineDialog(R.string.books_add_car_success);

                        mBookInfo.setBookInCart(true);
                        cartCountTV.setText((Integer.parseInt(cartCountTV.getText().toString()) + 1) + "");
                    } else {
                        showCenterDetermineDialog(R.string.books_add_car_fail);
                    }
                } else if (o instanceof AppendBookFavorRep) {
                    AppendBookFavorRep rep = (AppendBookFavorRep) o;
                    if (rep.getCode() == 200) {
                        showCenterDetermineDialog(R.string.books_add_collection_success);
                        mBookInfo.setBookInFavor(true);
                    } else {
                        showCenterDetermineDialog(R.string.books_add_collection_fail);
                    }
                } else if (o instanceof QueryShopBookDetailRep) {
                    QueryShopBookDetailRep rep = (QueryShopBookDetailRep) o;
                    if (rep.getData() != null && rep.getData().size() > 0) {
                        mBookInfo = rep.getData().get(0);
                        mBtnBuy.setText("￥" + mBookInfo.getBookSalePrice() + "购买");
                    } else {
                        showTagCancelAndDetermineDialog(R.string.books_request_details_fail, R.string.cancel, R.string.retry, mTagForRequestDetailsFail);
                        Log.v("FH", "获取图书详情失败");
                    }
                } else if (o instanceof QueryBookCartRep) {
                    QueryBookCartRep rep = (QueryBookCartRep) o;
                    if (rep.getCode() != 200) {
                    } else if (rep.getData() != null && rep.getData().size() != 0) {
                        cartCountTV.setText("" + rep.getData().size());
                    } else {
                        cartCountTV.setText("0");
                    }
                } else if (o instanceof RequirePayOrderRep) {
                    RequirePayOrderRep rep = (RequirePayOrderRep) o;
                    if (rep.getCode() == 200) {
                        BriefOrder orderObj = rep.getData().get(0);
                        Intent intent = new Intent(ProbationReadBookActivity.this, OrderDetailActivity.class);
                        intent.putExtra("orderId", orderObj.getOrderId());
                        startActivity(intent);
                        finish();
                    } else {
                        showCenterDetermineDialog(R.string.get_order_fail);
                    }
                } else if (o instanceof QueryBookOrderListRep) {
                    if (((QueryBookOrderListRep) o).getCode() == ProtocolId.RET_SUCCESS) {
                        Log.v("FH", "查询已支付待支付订单成功 : 未支付订单个数 : " + ((QueryBookOrderListRep) o).getData().size());
                        if (((QueryBookOrderListRep) o).getData().size() > 0) {
                            new HintDialog(ProbationReadBookActivity.this, "您还有未完成的订单,请支付或取消后再生成新的订单").show();
                            return;
                        }
                        RequirePayOrderRequest request = new RequirePayOrderRequest();
                        request.setOrderOwner(SpUtils.getAccountId());
                        request.getData().add(new RequirePayOrderRequest.BookIdObj(mBookInfo.getBookId()));
                        ProtocolManager.requirePayOrderProtocol(request, ProtocolId.PROTOCOL_ID_REQUIRE_PAY_ORDER
                                , new RequireOrderCallBack(ProbationReadBookActivity.this, ProtocolId.PROTOCOL_ID_REQUIRE_PAY_ORDER, request));
                    } else {
                        new HintDialog(ProbationReadBookActivity.this, "查询已支付待支付订单失败 : " + ((QueryBookOrderListRep) o).getMsg()).show();
                        Log.v("FH", "查询已支付待支付订单失败 : " + ((QueryBookOrderListRep) o).getMsg());
                    }
                }
            }
        }));
        super.handleEvent();
    }

    /***
     * 添加购物车
     */
    private void addBooksToCar(List<BookInfo> bookInfoList) {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
            return;
        }

        AppendBookCartRequest request = new AppendBookCartRequest();
        request.setUserId(SpUtils.getAccountId());
        for (BookInfo bookInfo :
                bookInfoList) {
            request.getData().add(new AppendBookCartRequest.BookIdObj(bookInfo.getBookId()));
        }
        AppendBookCartCallBack call = new AppendBookCartCallBack(this, ProtocolId.PROTOCOL_ID_APPEND_BOOK_CART, request);
        ProtocolManager.appendBookCartProtocol(request, ProtocolId.PROTOCOL_ID_APPEND_BOOK_CART, call);
    }

    /***
     * 添加收藏
     */
    private void addBooksToFavor(List<BookInfo> bookInfoList) {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
            return;
        }
        AppendBookFavorRequest request = new AppendBookFavorRequest();
        request.setUserId(SpUtils.getAccountId());
        for (BookInfo bookInfo :
                bookInfoList) {
            request.getData().add(new AppendBookFavorRequest.BookIdObj(bookInfo.getBookId()));
        }
        AppendBookFavorCallBack call = new AppendBookFavorCallBack(this, ProtocolId.PROTOCOL_ID_APPEND_BOOK_FAVOR, request);
        ProtocolManager.appendBookFavorProtocol(request, ProtocolId.PROTOCOL_ID_APPEND_BOOK_FAVOR, call);
        mImgbtnFavor.setImageResource(R.drawable.icon_shoucang_yitianjia);
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void updatePage(int page, Bitmap bitmap) {
        EpdController.invalidate(mRootView, UpdateMode.GC);
        mOnyxImgView.setImageBitmap(bitmap);
        mImgPageBack.setEnabled(true);
        mImgPageNext.setEnabled(true);
        mSeekbarPage.setClickable(true);
        mSeekbarPage.setProgress(mCurrentMarksPage * mPageSliderRes);
    }

    @Override
    public View getContentView() {
        return mOnyxImgView;
    }

    @Override
    public void showThrowable(Throwable throwable) {

    }

    @Override
    public void openDocumentFinsh() {

        mPageCounts = mReaderPresenter.getPages();
        LogUtils.i("mPageCounts ..." + mPageCounts);
        initSeekBar();
        requestPageTask(mCurrentMarksPage);
    }

    private void initSeekBar() {
        LogUtils.i("mPageCounts ==" + mPageCounts);
        mSeekbarPage.setPdfCounts(mPageCounts);
        mSeekbarPage.setVisibility(View.VISIBLE);
        int max = Math.max(mPageCounts - 1, 1);
        mPageSliderRes = ((10 + max - 1) / max) * 2;
        mSeekbarPage.setPageSliderRes(mPageSliderRes);
        mSeekbarPage.setMax((mPageCounts - 1) * mPageSliderRes);

        LogUtils.i("max ==" + (mPageCounts - 1) * mPageSliderRes);
        LogUtils.i("mPageSliderRes==" + mPageSliderRes);
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
    public void updateDirectory(ReaderDocumentTableOfContent content) {

    }

    private void requestPageTask(final int position) {
        LogUtils.i("requestPageTask");
        mImgPageBack.setEnabled(false);
        mImgPageNext.setEnabled(false);
        mSeekbarPage.setClickable(false);
        judgeFlipPage(position);
    }


    private void judgeFlipPage(int position) {
        if (position >= mPageCounts) {
            UIUtils.showToastSafe("当前是最后一页", Toast.LENGTH_SHORT);
            mImgPageNext.setEnabled(true);
            mImgPageBack.setEnabled(true);
            mSeekbarPage.setClickable(true);
            return;
        }
        if (position < 0) {
            UIUtils.showToastSafe("当前是第一页", Toast.LENGTH_SHORT);
            mImgPageNext.setEnabled(true);
            mImgPageBack.setEnabled(true);
            mSeekbarPage.setClickable(true);
            return;
        }
        mCurrentMarksPage = position;
        LogUtils.i("position ..." + position);
        getReaderPresenter().gotoPage(position);
    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        if (mUiPromptDialog.getTag() == mTagForNoNet) {
            jumpTonet();
        } else if (getUiPromptDialog().getTag() == mTagForRequestDetailsFail) {
            refreshData();
        }
    }

    @Override
    public void onUiCancelListener() {
        super.onUiCancelListener();
        if (getUiPromptDialog().getTag() == mTagForRequestDetailsFail || getUiPromptDialog().getTag() == mTagForQueryNoNet) {
            this.finish();
        }
    }
}
