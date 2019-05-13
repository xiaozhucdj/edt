package com.yougy.shop.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.reader.ReaderContract;
import com.onyx.reader.ReaderPresenter;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.media.BookVoiceBean;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.RefreshUtil;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.shop.AllowOrderRequestObj;
import com.yougy.shop.CreateOrderRequestObj;
import com.yougy.shop.bean.BookIdObj;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.bean.CartItem;
import com.yougy.shop.bean.OrderIdObj;
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

    @BindView(R.id.ll_title)
    RelativeLayout ll_title;
    @BindView(R.id.buttom_bar)
    RelativeLayout buttom_bar;
    @BindView(R.id.img_page_back)
    ImageView img_page_back;
    @BindView(R.id.img_page_next)
    ImageView img_page_next;
    @BindView(R.id.img_btn_hide)
    Button img_btn_hide;


    private BookInfo mBookInfo;

    /////////////////////////////////Files///////////////////////////
    private String mProbationUrl;
    private ImageView mOnyxImgView;
    /**
     * pdf总页码
     */
    private int mPageCounts;
    private int mPageSliderRes;
    private int mCurrentMarksPage = 0;
    protected static final int DURATION = 1;
    private Subscription backScription;
    private Subscription nextScription;
    private View mRootView;
    private int mTagForNoNet = 1;
    private int mTagForRequestDetailsFail = 2;
    private int mTagForQueryNoNet = 3;
    private Subscription nextScription2;
    private Subscription backScription2;

    @Override
    public void init() {
        mBookInfo = getIntent().getParcelableExtra(ShopGloble.JUMP_BOOK_KEY);
        mImgbtnFavor.setImageResource(mBookInfo.isBookInFavor() ? R.drawable.icon_shoucang_yitianjia : R.drawable.icon_shoucang);
    }

    @Override
    protected void setContentView() {
        mRootView = UIUtils.inflate(R.layout.activity_shop_probation_read_book);
        setContentView(mRootView);
    }

    @Override
    protected void refreshView() {

    }

    @Override
    protected void initLayout() {
        initViewData();
        initPDF();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshData();
    }

    @Override
    public void loadData() {
    }

    public void refreshData() {

        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForQueryNoNet);
            return;
        }


        NetWorkManager.queryShopBook(SpUtils.getUserId(), mBookInfo.getBookId(), null, null, null, null)
                .subscribe(new Action1<List<BookInfo>>() {
                    @Override
                    public void call(List<BookInfo> shopBookInfos) {
                        if (shopBookInfos != null && shopBookInfos.size() > 0) {
                            mBookInfo = shopBookInfos.get(0);
                            mBtnBuy.setText("￥" + mBookInfo.getBookSpotPrice() + "购买");
                            getCartCount();
                        } else {
                            showTagCancelAndDetermineDialog(R.string.books_request_details_fail, R.string.cancel, R.string.retry, mTagForRequestDetailsFail);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.e("FH", "获取详情失败");
                        showTagCancelAndDetermineDialog(R.string.books_request_details_fail, R.string.cancel, R.string.retry, mTagForRequestDetailsFail);
                        throwable.printStackTrace();
                    }
                });
    }


    private void getCartCount() {
        NetWorkManager.queryCart(SpUtils.getUserId() + "")
                .subscribe(new Action1<List<CartItem>>() {
                    @Override
                    public void call(List<CartItem> cartItems) {
                        if (cartItems == null) {
                            cartCountTV.setText("0");
                        } else {
                            if (cartItems.size() > 99) {
                                cartCountTV.setText("…");
                            } else {
                                cartCountTV.setText("" + cartItems.size());
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.e("FH", "获取购物车失败");
                        throwable.printStackTrace();
                    }
                });
    }

    @OnClick({R.id.imgbtn_back, R.id.imgbtn_jumpCar, R.id.imgbtn_addCar, R.id.imgbtn_favor, R.id.btn_buy, R.id.img_btn_hide})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgbtn_back:
//                onBackPressed()
                this.finish();
                break;
            case R.id.imgbtn_jumpCar:
                loadIntent(ShopCartActivity.class);
//                onBackPressed();
                this.finish();
                break;
            case R.id.imgbtn_addCar:
                if (mBookInfo.isBookInCart()) {
                    showCenterDetermineDialog(R.string.books_already_add_car_2);
                    return;
                }
                if (mBookInfo.isBookInShelf()) {
                    showCenterDetermineDialog(R.string.books_already_buy);
                    return;
                }
                // 加入购物车
                if (!NetUtils.isNetConnected()) {
                    showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
                    return;
                }
                NetWorkManager.appendCart(SpUtils.getUserId(), mBookInfo.getBookId())
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                showCenterDetermineDialog(R.string.books_add_car_success);
                                mBookInfo.setBookInCart(true);
                                cartCountTV.setText((Integer.parseInt(cartCountTV.getText().toString()) + 1) + "");
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                showCenterDetermineDialog(R.string.books_add_car_fail);
                                LogUtils.e("FH", "加入购物车失败");
                                throwable.printStackTrace();
                            }
                        });
                break;
            case R.id.imgbtn_favor:
                if (mBookInfo.isBookInFavor()) {
                    showCenterDetermineDialog(R.string.books_already_add_collection);
                    return;
                }
                if (!NetUtils.isNetConnected()) {
                    showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
                    return;
                }
                NetWorkManager.appendFavor(SpUtils.getUserId(), mBookInfo.getBookId())
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                LogUtils.e("FH", "添加到收藏夹成功");
                                showCenterDetermineDialog(R.string.books_add_collection_success);
                                mBookInfo.setBookInFavor(true);
                                mImgbtnFavor.setImageResource(R.drawable.icon_shoucang_yitianjia);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                LogUtils.e("FH", "添加到收藏夹失败");
                                showCenterDetermineDialog(R.string.books_add_collection_fail);
                                throwable.printStackTrace();
                            }
                        });
                break;
            case R.id.btn_buy:
                if (mBookInfo.isBookInShelf()) {
                    showCenterDetermineDialog(R.string.books_already_buy);
                    return;
                }
                if (!NetUtils.isNetConnected()) {
                    showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
                    return;
                }
                //新建订单
                List<BookIdObj> bookIdList = new ArrayList<BookIdObj>() {
                    {
                        add(new BookIdObj(mBookInfo.getBookId()));
                    }
                };
                NetWorkManager.allowOrder(new AllowOrderRequestObj(SpUtils.getUserId(), bookIdList))
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                LogUtils.v("订单查重成功,未查到重复订单");
                                NetWorkManager.createOrder(new CreateOrderRequestObj(SpUtils.getUserId(), bookIdList , SpUtils.getStudent().getSchoolLevel()))
                                        .subscribe(new Action1<List<OrderIdObj>>() {
                                            @Override
                                            public void call(List<OrderIdObj> orderIdObjs) {
                                                OrderIdObj orderIdObj = orderIdObjs.get(0);
                                                Intent intent = new Intent(getApplicationContext(), OrderDetailActivity.class);
                                                intent.putExtra("orderId", orderIdObj.getOrderId());
                                                startActivity(intent);
                                                SpUtils.newOrderCountPlusOne();
                                            }
                                        }, new Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable throwable) {
                                                showCenterDetermineDialog(R.string.get_order_fail);
                                                LogUtils.e("FH", "下单失败");
                                                throwable.printStackTrace();
                                            }
                                        });
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                ToastUtil.showCustomToast(getApplicationContext(), "下单失败!\r\n无法购买之前已经下过单的图书");
                                throwable.printStackTrace();
                            }
                        });
                break;

            case R.id.img_btn_hide:
                onBackListener();
                break;
        }
    }

    /***
     * 初始化PDF
     */
    private void initPDF() {
//        mProbationUrl = FileUtils.getProbationBookFilesDir() + ShopGloble.probationToken + mBookInfo.getBookId() + ".pdf";
        mProbationUrl = FileUtils.getBookFileName(mBookInfo.getBookId(), FileUtils.bookProbation);
        LogUtils.i("mProbationUrl ......" + mProbationUrl);
        mOnyxImgView = new ImageView(this);
        mOnyxImgView.setLayoutParams(new LinearLayout.LayoutParams(UIUtils.getScreenWidth(), UIUtils.getScreenHeight()));
        mLlPdfFather.addView(mOnyxImgView, 0);
        getReaderPresenter().openDocument(mProbationUrl, "");
    }

    /***
     * 初始化布局参数
     */
    private void initViewData() {

        nextScription = RxView.clicks(mImgPageNext).throttleFirst(50, TimeUnit.MILLISECONDS).subscribe(getNextSubscriber());
        backScription = RxView.clicks(mImgPageBack).throttleFirst(50, TimeUnit.MILLISECONDS).subscribe(getBackSubscriber());

        nextScription2 = RxView.clicks(img_page_next).throttleFirst(50, TimeUnit.MILLISECONDS).subscribe(getNextSubscriber());
        backScription2 = RxView.clicks(img_page_back).throttleFirst(50, TimeUnit.MILLISECONDS).subscribe(getBackSubscriber());
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backScription != null) {
            backScription.unsubscribe();
        }
        if (nextScription != null) {
            nextScription.unsubscribe();
        }

        if (backScription2 != null) {
            backScription2.unsubscribe();
        }
        if (nextScription2 != null) {
            nextScription2.unsubscribe();
        }
        getReaderPresenter().close();
    }

    //////////////////////////////////////////解析PDF////////////////////////////////////////////////////
    private ReaderPresenter mReaderPresenter;

    private ReaderContract.ReaderPresenter getReaderPresenter() {
        if (mReaderPresenter == null) {
            mReaderPresenter = new ReaderPresenter(this);
            mReaderPresenter.setmIsSetGama(false);
        }
        return mReaderPresenter;
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void updatePage(int page, Bitmap bitmap, BookVoiceBean bean) {
        RefreshUtil.invalidate(mRootView);
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
        initSeekBar();
        requestPageTask(mCurrentMarksPage);
    }

    @Override
    public void updateDirectory(ReaderDocumentTableOfContent content) {
        //更新图书目录

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
        getReaderPresenter().gotoPage(position);
    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        if (getUiPromptDialog().getTag() == mTagForRequestDetailsFail) {
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

    public void onBackListener() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) img_btn_hide.getLayoutParams();
        if (ll_title.getVisibility() == View.VISIBLE) {
            ll_title.setVisibility(View.INVISIBLE);
            buttom_bar.setVisibility(View.INVISIBLE);
            img_page_next.setVisibility(View.VISIBLE);
            img_page_back.setVisibility(View.VISIBLE);
            img_btn_hide.setText("显示菜单栏");
        }else{
            ll_title.setVisibility(View.VISIBLE);
            buttom_bar.setVisibility(View.VISIBLE);
            img_page_next.setVisibility(View.GONE);
            img_page_back.setVisibility(View.GONE);
            img_btn_hide.setText("隐藏菜单栏");
        }
        img_btn_hide.setLayoutParams(params);
    }

}