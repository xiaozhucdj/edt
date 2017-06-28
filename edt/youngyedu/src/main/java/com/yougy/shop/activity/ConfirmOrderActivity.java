package com.yougy.shop.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.CancelBookOrderCallBack;
import com.yougy.common.protocol.callback.IsOrderPaySuccessCallBack;
import com.yougy.common.protocol.callback.QueryQRStrCallBack;
import com.yougy.common.protocol.request.QueryQRStrRequest;
import com.yougy.common.protocol.response.IsOrderPaySuccessRep;
import com.yougy.common.protocol.response.QueryQRStrRep;
import com.yougy.common.protocol.response.RequirePayOrderRep;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.init.bean.BookInfo;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.view.dialog.QRCodeDialog;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by FH on 2017/2/15.
 */

public class ConfirmOrderActivity extends ShopAutoLayoutBaseActivity implements View.OnClickListener {
    final int PAY_WECHAT = 1;
    final int PAY_ALIPAY = 2;
    final int BOOK_INFO_MAX_LINES = 2;
    final int BOOK_INFO_MAX_ROW = 5;

    final int MAX_ONCE_SHOW_PAGE_NUM = 5;

    int currentSelectPageIndex = -1;
    int currentShowFirstPageNum = 1;
    OrderBookAdapter mAdapter;

    ArrayList<BookInfo> orderBookInfoList = new ArrayList<BookInfo>();
    @BindView(R.id.confirm_order_back_btn)
    ImageView backBtn;
    @BindView(R.id.confirm_order_order_code_tv)
    TextView orderCodeTv;
    @BindView(R.id.confirm_order_order_price_tv)
    TextView orderPriceTv;
    @BindView(R.id.confirm_order_recyclerview)
    RecyclerView orderRecyclerview;
    @BindView(R.id.confirm_order_page_btn_container)
    LinearLayout pageBtnContainer;
    @BindView(R.id.confirm_order_order_info_tv)
    TextView orderInfoTv;
    @BindView(R.id.confirm_order_wechat_pay_btn)
    LinearLayout wechatPayBtn;
    @BindView(R.id.confirm_order_alipay_btn)
    LinearLayout alipayBtn;

    RequirePayOrderRep.OrderObj order;
    String qrStr;
    QRCodeDialog qrCodeDialog;
    private int mTagForNoNet = 1;
    private int mTagForCancelOrder = 2;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_confirm_order);
    }

    @Override
    protected void init() {
        order = getIntent().getParcelableExtra(ShopGloble.ORDER);
        if (order == null) {
            ToastUtil.showToast(getApplicationContext(), "order信息错误");
        }
        orderBookInfoList.addAll(order.getBookList());
    }

    @Override
    protected void handleEvent() {
        tapEventEmitter.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o instanceof QueryQRStrRep) {
                            QueryQRStrRep rep = (QueryQRStrRep) o;
                            String qrcode;
                            if (rep.getCode() == 200
                                    && rep.getData() != null
                                    && rep.getData().size() != 0
                                    && !TextUtils.isEmpty(rep.getData().get(0).getQrcode())) {
                                qrcode = rep.getData().get(0).getQrcode();
                                qrCodeDialog = new QRCodeDialog(ConfirmOrderActivity.this
//                                , protocol.getQrStr()
                                        , qrcode
                                        , "请使用手机打开支付宝扫一扫"
                                        , new QRCodeDialog.OnBtnClickListener() {

                                    @Override
                                    public void onBtnClick(QRCodeDialog.FUNCTION function) {
                                        switch (function) {
                                            case CANCLE:
                                                qrCodeDialog.dismiss();
                                                break;
                                            case HAS_FINISH_PAY:
                                                qrCodeDialog.showHint("正在查询订单状态...");
                                                if (!NetUtils.isNetConnected()) {
                                                    showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
                                                    return;
                                                }
                                                ProtocolManager.isOrderPaySuccessProtocol(order.getOrderId()
                                                        , SpUtil.getAccountId()
                                                        , ProtocolId.PROTOCOL_ID_IS_ORDER_PAY_SUCCESS
                                                        , new IsOrderPaySuccessCallBack(ConfirmOrderActivity.this
                                                                , order.getOrderId()
                                                                , SpUtil.getAccountId()
                                                                , ProtocolId.PROTOCOL_ID_IS_ORDER_PAY_SUCCESS));
                                                break;
                                            case RETRY:
                                                qrCodeDialog.dismiss();
                                                queryQRStr(PAY_ALIPAY);
                                                break;
                                            case OK:
                                                qrCodeDialog.dismiss();
                                                loadIntent(ShopCartActivity.class);
                                                break;
                                        }
                                    }
                                });
                                qrCodeDialog.show();
                            } else {
                                showToastSafe("请求支付二维码失败,请稍后重试...", Toast.LENGTH_SHORT);
                            }
                        } else if (o instanceof IsOrderPaySuccessRep) {
                            IsOrderPaySuccessRep rep = (IsOrderPaySuccessRep) o;
                            if (rep.getCode() == 200) {
                                Intent intent = new Intent(ConfirmOrderActivity.this, PaySuccessActivity.class);
                                intent.putExtra(ShopGloble.ORDER, order);
                                startActivity(intent);
                                //通知主界面刷新
                                BaseEvent baseEvent = new BaseEvent(EventBusConstant.need_refresh, null);
                                EventBus.getDefault().post(baseEvent);
                                qrCodeDialog.dismiss();
                                finish();
                            } else {
                                qrCodeDialog.showHintAndRetry("支付未成功", "重试");
                            }
                        }
//                            else if (o instanceof CancelBookOrderRep) {
//                            CancelBookOrderRep rep = (CancelBookOrderRep) o;
//                            if (rep.getCode() == 200) {
//                                loadIntentWithSpecificFlag(BookShopActivityDB.class, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            } else {
//                                showTagCancelAndDetermineDialog(R.string.cancel_order_fail, mTagForCancelOrder);
//                            }
//                        }
                    }
                });
        super.handleEvent();
    }

    @Override
    protected void initLayout() {
        orderRecyclerview.setLayoutManager(new GridLayoutManager(this, BOOK_INFO_MAX_ROW) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        orderRecyclerview.setAdapter(mAdapter = new OrderBookAdapter());
    }

    @Override
    protected void loadData() {
        refreshView();
    }

    @Override
    protected void refreshView() {
        pageBtnContainer.removeAllViews();
        //把页码转换成从0开始的序号
        int currentshowFirstPageIndex = currentShowFirstPageNum - 1;
        //每页最多能显示的条目数
        int maxItemNumsPerPage = BOOK_INFO_MAX_LINES * BOOK_INFO_MAX_ROW;

        boolean showFoward, showNext;
        //是否显示<<按钮
        if (currentshowFirstPageIndex == 0) {
            showFoward = false;
        } else {
            showFoward = true;
        }
        //计算本次显示的页码,和是否要显示>>按钮
        int lastShowItemIndex;
        if ((currentshowFirstPageIndex + MAX_ONCE_SHOW_PAGE_NUM) * maxItemNumsPerPage < orderBookInfoList.size()) {
            lastShowItemIndex = (currentshowFirstPageIndex + MAX_ONCE_SHOW_PAGE_NUM) * maxItemNumsPerPage - 1;
            showNext = true;
        } else {
            lastShowItemIndex = orderBookInfoList.size() - 1;
            showNext = false;
        }
        addBtns(currentShowFirstPageNum, lastShowItemIndex / maxItemNumsPerPage + 1, showFoward, showNext);
        //如果初始化时没有任何页面被显示,则显示第0页
        if (currentSelectPageIndex == -1) {
            currentSelectPageIndex = 0;
            toPage(0);
        }
        //如果当前显示的页码按钮在页码容器中,则高亮显示
        TextView selectedBtn = (TextView) pageBtnContainer.findViewWithTag((currentSelectPageIndex + 1));
        if (selectedBtn != null) {
            selectedBtn.setSelected(true);
        }
        //刷新订单编号,订单金额UI
        orderCodeTv.setText("订单编号 : " + order.getOrderId());
        orderPriceTv.setText("订单金额 :　￥" + order.getOrderPrice());
        //刷新下方本地订单总价格和订单书本数
        orderInfoTv.setText("共" + orderBookInfoList.size() + "本书 , 总计 : " + getCheckedBookPriceSum() + "元");
    }

    /**
     * 向服务器请求支付二维码串
     *
     * @param i 请求的支付渠道, 0代表支付宝,1代表微信支付
     */
    private void queryQRStr(int i) {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
            return;
        }
        if (i == PAY_ALIPAY) {
            QueryQRStrRequest request = new QueryQRStrRequest();
            request.setOrderId(order.getOrderId());
            request.setOrderOwner(SpUtil.getAccountId());
            request.setOrderPrice(order.getOrderPrice());
            request.setPayMethod(PAY_ALIPAY);
            QueryQRStrCallBack callBack = new QueryQRStrCallBack(this, ProtocolId.PROTOCOL_ID_QUERY_QR_CODE, request);
            ProtocolManager.qureyQRStrProtocol(request, ProtocolId.PROTOCOL_ID_QUERY_QR_CODE, callBack);
        } else if (i == 1) {

        }
    }

    @OnClick({R.id.confirm_order_back_btn, R.id.confirm_order_wechat_pay_btn, R.id.confirm_order_alipay_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_order_back_btn:
                onBackPressed();
                break;
            case R.id.confirm_order_wechat_pay_btn:
                showToastSafe("微信支付即将开通,敬请期待", Toast.LENGTH_SHORT);
                break;
            case R.id.confirm_order_alipay_btn:
                queryQRStr(PAY_ALIPAY);
                break;
            case R.id.shop_page_page_btn:
                int i = (int) view.getTag();
                switch (i) {
                    case -1://向前
                        currentShowFirstPageNum = currentShowFirstPageNum - MAX_ONCE_SHOW_PAGE_NUM;
                        refreshViewSafe();
                        break;
                    case -2://向后
                        currentShowFirstPageNum = currentShowFirstPageNum + MAX_ONCE_SHOW_PAGE_NUM;
                        //如果本地缓存中已有数据,则直接切换显示页面,如果没有数据则先从接口获取再显示
                        refreshViewSafe();
                        break;
                    default:
                        toPage(i - 1);
                        break;
                }
                break;
        }
    }

    private void toPage(int pageIndex) {
        TextView btn = (TextView) pageBtnContainer.findViewWithTag(currentSelectPageIndex + 1);
        if (btn != null) {
            btn.setSelected(false);
        }
        btn = (TextView) pageBtnContainer.findViewWithTag(pageIndex + 1);
        btn.setSelected(true);
        currentSelectPageIndex = pageIndex;

        mAdapter.mdata.clear();
        int firstItemIndex = pageIndex * BOOK_INFO_MAX_LINES * BOOK_INFO_MAX_ROW;
        int lastItemIndex;
        if ((pageIndex + 1) * BOOK_INFO_MAX_LINES * BOOK_INFO_MAX_ROW > orderBookInfoList.size()) {
            lastItemIndex = orderBookInfoList.size() - 1;
        } else {
            lastItemIndex = (pageIndex + 1) * BOOK_INFO_MAX_LINES * BOOK_INFO_MAX_ROW - 1;
        }
        mAdapter.mdata.addAll(orderBookInfoList.subList(firstItemIndex, lastItemIndex + 1));
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 计算bookInfo的总价格
     *
     * @return 总价格
     */
    private double getCheckedBookPriceSum() {
        double sum = 0;
        for (BookInfo bookInfo : orderBookInfoList) {
            sum = sum + bookInfo.getBookSalePrice();
        }
        return sum;
    }

    /**
     * 添加多个按钮
     *
     * @param firstBtnNum 要添加的第一个按钮的序号
     * @param lastBtnNum  要添加的最后一个按钮的序号
     * @param hasForward  是否添加向前按钮
     * @param hasNext     是否添加向后按钮
     */
    private void addBtns(int firstBtnNum, int lastBtnNum, boolean hasForward, boolean hasNext) {
        if (hasForward) addBtn(-1);
        for (int index = firstBtnNum; index <= lastBtnNum; index++) {
            addBtn(index);
        }
        if (hasNext) addBtn(-2);
    }

    /**
     * 添加一个按钮,添加的按钮tag会是它文字的int,向前的tag为-1,向后的tag为-2.
     *
     * @param index -1会显示<<<表示向前,-2会显示>>表示向后.
     */
    private void addBtn(int index) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 20;
        TextView pageBtn = (TextView) View.inflate(this, R.layout.shop_page_item, null);
        pageBtn.setTag(index);
        switch (index) {
            case -1:
                pageBtn.setText("<<");
                break;
            case -2:
                pageBtn.setText(">>");
                break;
            default:
                pageBtn.setText(index + "");
                break;
        }
        pageBtn.setOnClickListener(this);
        pageBtn.setLayoutParams(params);
        AutoUtils.auto(pageBtn);
        pageBtnContainer.addView(pageBtn);
    }

    private void refreshImg(ImageView view, String url) {
        int w = view.getMeasuredWidth();
        int h = view.getMeasuredHeight();

        if (w == 0 || h == 0) {
            //测量控件大小
            int result[] = UIUtils.getViewWidthAndHeight(view);
            w = result[0];
            h = result[1];
        }
        if (w == 0 || h == 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(view.getLayoutParams().width, View.MeasureSpec.EXACTLY)
                    , View.MeasureSpec.makeMeasureSpec(view.getLayoutParams().height, View.MeasureSpec.EXACTLY));
            w = view.getMeasuredWidth();
            h = view.getMeasuredHeight();
        }
        ImageLoaderManager.getInstance().loadImageContext(this,
                url,
                R.drawable.img_book_cover,
                view);
    }

    public class OrderBookAdapter extends RecyclerView.Adapter<OrderBookAdapter.BookInfoViewHolder> {
        ArrayList<BookInfo> mdata = new ArrayList<BookInfo>();

        @Override
        public BookInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            BookInfoViewHolder holder = new BookInfoViewHolder(
                    LayoutInflater.from(ConfirmOrderActivity.this)
                            .inflate(R.layout.order_book_info_item, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(BookInfoViewHolder holder, int position) {
            holder.bookNameTv.setText(mdata.get(position).getBookTitle());
            holder.bookPriceTv.setText("￥" + mdata.get(position).getBookSalePrice());
            refreshImg(holder.bookImg, mdata.get(position).getBookCover());
        }

        @Override
        public int getItemCount() {
            return mdata.size();
        }

        public class BookInfoViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.order_book_item_img)
            ImageView bookImg;
            @BindView(R.id.order_book_item_book_name_tv)
            TextView bookNameTv;
            @BindView(R.id.order_book_item_book_price_tv)
            TextView bookPriceTv;

            public BookInfoViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        cancelOrder();
//    }

    private void cancelOrder() {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
            return;
        }
        ProtocolManager.cancelPayOrderProtocol(order.getOrderId(), SpUtil.getAccountId(), ProtocolId.PROTOCOL_ID_CANCEL_PAY_ORDER
                , new CancelBookOrderCallBack(this, ProtocolId.PROTOCOL_ID_CANCEL_PAY_ORDER, order.getOrderId(), SpUtil.getAccountId()));

    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        if (mUiPromptDialog.getTag() == mTagForNoNet) {
            jumpTonet();
        } else if (mUiPromptDialog.getTag() == mTagForCancelOrder) {
            cancelOrder();
        }
    }
}
