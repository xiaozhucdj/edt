package com.yougy.shop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.shop.QueryQRStrObj;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.bean.Coupon;
import com.yougy.shop.bean.OrderDetailBean;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityOrderDetailBinding;
import com.yougy.ui.activity.databinding.ItemOrderDetailBinding;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.PaySuccessDialog;
import com.yougy.view.dialog.QRCodeDialog;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import rx.functions.Action1;

/**
 * Created by FH on 2018/3/20.
 * 订单详情界面
 */

public class OrderDetailActivity extends ShopBaseActivity {
    ActivityOrderDetailBinding binding;
    String orderId;
    double orderPrice;
    public static final int PAY_WECHAT = 1;
    public static final int PAY_ALIPAY = 2;
    QRCodeDialog qrCodeDialog;
    private int mTagForNoNet = 1;
    private int mTagForZxingfail = 2;
    private boolean showSuccessDialog = true;

    ArrayList<Pair<CouponInfo, ArrayList<OrderDetailBean.OrderInfo>>> dataList
            = new ArrayList<Pair<CouponInfo, ArrayList<OrderDetailBean.OrderInfo>>>();

    @Override
    protected void setContentView() {
        setContentView(layout());
    }

    @Override
    public void init() {
        orderId = getIntent().getStringExtra("orderId");
        showSuccessDialog = getIntent().getBooleanExtra("showSuccessDialog" , true);
    }

    @Override
    protected void initLayout() {

    }

    public View layout() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(OrderDetailActivity.this)
                , R.layout.activity_order_detail, null, false);
        binding.mainRecyclerview.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this
                , LinearLayoutManager.VERTICAL, false));
        binding.mainRecyclerview.setMaxItemNumInOnePage(3);
        binding.mainRecyclerview.setAdapter(new PageableRecyclerView.Adapter<MyHolder>() {
            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyHolder(DataBindingUtil.inflate(
                        LayoutInflater.from(OrderDetailActivity.this)
                        , R.layout.item_order_detail, binding.mainRecyclerview.getRealRcyView(), false));
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                int tempI = position;
                for (Pair<CouponInfo, ArrayList<OrderDetailBean.OrderInfo>> pair : dataList) {
                    ArrayList<OrderDetailBean.OrderInfo> orderInfoList = pair.second;
                    int itemCountInThisList = (orderInfoList.size() + 1) / 2;
                    if (tempI < itemCountInThisList) {
                        OrderDetailBean.OrderInfo orderInfo1 = orderInfoList.get(2 * tempI);
                        OrderDetailBean.OrderInfo orderInfo2 = null;
                        if (2 * tempI + 1 < orderInfoList.size()) {
                            orderInfo2 = orderInfoList.get(2 * tempI + 1);
                        }
                        holder.setData(pair.first, orderInfo1, orderInfo2, (tempI == 0));
                        break;
                    } else {
                        tempI = tempI - itemCountInThisList;
                    }
                }
            }

            @Override
            public int getItemCount() {
                int itemCount = 0;
                for (Pair<CouponInfo, ArrayList<OrderDetailBean.OrderInfo>> pair : dataList) {
                    itemCount = itemCount + ((pair.second.size() + 1) / 2);
                }
                return itemCount;
            }
        });
        return binding.getRoot();
    }

    private OrderDetailBean mTopOrderDetail;
    private PaySuccessDialog mPaySuccessDialog;

    @Override
    public void loadData() {
        NetWorkManager.queryOrderTree(orderId)
                .subscribe(new Action1<List<OrderDetailBean>>() {
                    @Override
                    public void call(List<OrderDetailBean> orderDetailBeenList) {
                        mTopOrderDetail = orderDetailBeenList.get(0);
                        dataList.clear();
                        binding.orderIdTv.setText("订单编号 : " + mTopOrderDetail.orderId);
                        binding.orderCreateTimeTv.setText("下单时间 : " + mTopOrderDetail.orderCreateTime);
                        String orderStatus = mTopOrderDetail.orderStatus;
                        if (orderStatus.equals("待支付")) {
                            binding.cancleBtn.setVisibility(View.VISIBLE);
                            binding.payBtn.setVisibility(View.VISIBLE);
                            binding.orderStatusTv.setVisibility(View.GONE);
                        } else {
                            binding.cancleBtn.setVisibility(View.GONE);
                            binding.payBtn.setVisibility(View.GONE);
                            binding.orderStatusTv.setVisibility(View.VISIBLE);
                            binding.orderStatusTv.setText(orderStatus);
                        }
                        parseData(mTopOrderDetail);
                        binding.mainRecyclerview.setCurrentPage(1);
                        binding.mainRecyclerview.notifyDataSetChanged();
                        binding.bookNumTv.setText(mTopOrderDetail.orderInfo.size() + "件商品");
                        binding.orderTotalPriceTv.setText("总计 : ￥" + (mTopOrderDetail.orderAmount + mTopOrderDetail.orderDeduction));
                        binding.orderOffPriceTv.setText("优惠 : ￥" + mTopOrderDetail.orderDeduction);
                        binding.orderFinalPriceTv.setText("订单金额 : ￥" + mTopOrderDetail.orderAmount);
                        orderPrice = mTopOrderDetail.orderAmount;


                        if (mTopOrderDetail.orderInfo.size() == 1 && orderStatus.equals("交易成功") && showSuccessDialog) {
                            showPaySuccessDialog();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtil.showCustomToast(getApplicationContext(), "获取订单信息失败!");
                        throwable.printStackTrace();
                        OrderDetailActivity.this.finish();
                    }
                });
    }

    @Override
    protected void refreshView() {

    }

    private void parseData(OrderDetailBean topOrderDetailBean) {
        if (topOrderDetailBean.orderChild.size() == 0) {
            Coupon coupon = null;
            if (topOrderDetailBean.orderCoupon != null && topOrderDetailBean.orderCoupon.size() != 0) {
                coupon = topOrderDetailBean.orderCoupon.get(0);
            }
            if (coupon != null && (coupon.getCouponTypeCode().equals("BO03") || coupon.getCouponTypeCode().equals("BO04"))) {
                CouponInfo couponInfo = new CouponInfo(Double.valueOf(coupon.getCouponContent().get(0).getOver())
                        , Double.valueOf(coupon.getCouponContent().get(0).getCut())
                        , topOrderDetailBean.orderAmount
                        , topOrderDetailBean.orderDeduction);
                putAllToDataList(couponInfo, topOrderDetailBean);
            } else {
                putAllToDataList(null, topOrderDetailBean);
            }
        } else {
            for (OrderDetailBean subOrderDetailBean : topOrderDetailBean.orderChild) {
                Coupon coupon = null;
                if (subOrderDetailBean.orderCoupon != null && subOrderDetailBean.orderCoupon.size() != 0) {
                    coupon = subOrderDetailBean.orderCoupon.get(0);
                }
                if (coupon != null && (coupon.getCouponTypeCode().equals("BO03") || coupon.getCouponTypeCode().equals("BO04"))) {
                    CouponInfo couponInfo = new CouponInfo(Double.valueOf(coupon.getCouponContent().get(0).getOver())
                            , Double.valueOf(coupon.getCouponContent().get(0).getCut())
                            , subOrderDetailBean.orderAmount
                            , subOrderDetailBean.orderDeduction);
                    putAllToDataList(couponInfo, subOrderDetailBean);
                } else {
                    putAllToDataList(null, subOrderDetailBean);
                }
            }
        }
    }

    private void putAllToDataList(CouponInfo couponInfo, OrderDetailBean topOrderDetailBean) {
        Pair<CouponInfo, ArrayList<OrderDetailBean.OrderInfo>> targetPair = null;
        for (Pair<CouponInfo, ArrayList<OrderDetailBean.OrderInfo>> tempPair : dataList) {
            if (tempPair.first == couponInfo) {
                targetPair = tempPair;
            }
        }
        if (targetPair == null) {
            targetPair = new Pair<CouponInfo, ArrayList<OrderDetailBean.OrderInfo>>(couponInfo, new ArrayList<OrderDetailBean.OrderInfo>());
            dataList.add(targetPair);
        }

        recursivePut(topOrderDetailBean, targetPair.second);
    }

    private void recursivePut(OrderDetailBean topOrderDetailBean, ArrayList<OrderDetailBean.OrderInfo> list) {
        if (topOrderDetailBean.orderChild == null || topOrderDetailBean.orderChild.size() == 0) {
            list.addAll(topOrderDetailBean.orderInfo);
        } else {
            for (OrderDetailBean everyOrderDetailBean : topOrderDetailBean.orderChild) {
                recursivePut(everyOrderDetailBean, list);
            }
        }
    }

    private void refreshImg(ImageView view, String url) {
        int w = view.getMeasuredWidth();
        int h = view.getMeasuredHeight();
        if (w == 0 || h == 0) {
            //测量控件大小
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            w = view.getMeasuredWidth();
            h = view.getMeasuredHeight();
            if (w == 0) {
                w = view.getLayoutParams() == null ? 0 : view.getLayoutParams().width;
            }
            if (h == 0) {
                h = view.getLayoutParams() == null ? 0 : view.getLayoutParams().height;
            }
        }
        ImageLoaderManager.getInstance().loadImageContext(OrderDetailActivity.this,
                url,
                R.drawable.img_book_cover,
                R.drawable.img_book_cover,
                w,
                h,
                view);
    }

    /**
     * 向服务器请求支付二维码串
     *
     * @param i 请求的支付渠道
     */
    private void queryQRStr(int i) {
        if (i == PAY_ALIPAY) {
            NetWorkManager.checkOrder(orderId, SpUtils.getUserId()
                    , orderPrice, PAY_WECHAT)
                    .subscribe(new Action1<List<QueryQRStrObj>>() {
                        @Override
                        public void call(List<QueryQRStrObj> queryQRStrObjs) {
                            if (queryQRStrObjs != null
                                    && queryQRStrObjs.size() != 0
                                    && !TextUtils.isEmpty(queryQRStrObjs.get(0).getQrcode())) {
                                String qrStr = queryQRStrObjs.get(0).getQrcode();
                                qrCodeDialog = new QRCodeDialog(OrderDetailActivity.this
                                        , qrStr
                                        , "请使用手机打开支付宝扫一扫"
                                        , new QRCodeDialog.OnBtnClickListener() {
                                    @Override
                                    public void onBtnClick(QRCodeDialog.FUNCTION function) {
                                        switch (function) {
                                            case CANCLE:
                                                qrCodeDialog.dismiss();
                                                break;
                                            case HAS_FINISH_PAY:
                                                if (!NetUtils.isNetConnected()) {
                                                    showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
                                                    return;
                                                }
                                                qrCodeDialog.showHint("正在查询订单状态...");
                                                isPaySuccess();
                                                break;
                                            case RETRY:
                                                qrCodeDialog.dismiss();
                                                if (!NetUtils.isNetConnected()) {
                                                    showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
                                                    return;
                                                }
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
                                showTagCancelAndDetermineDialog(R.string.get_zxing_fail, mTagForZxingfail);
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            LogUtils.e("FH", "请求二维码失败");
                            showTagCancelAndDetermineDialog(R.string.get_zxing_fail, mTagForZxingfail);
                            throwable.printStackTrace();
                        }
                    });
        } else if (i == PAY_WECHAT) {

        }
    }

    private void isPaySuccess() {
        NetWorkManager.isOrderPaySuccess(orderId, SpUtils.getUserId())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        qrCodeDialog.dismiss();
                        if (mTopOrderDetail.orderInfo.size() == 1){
                            showPaySuccessDialog();
                        }else{
                            Intent intent = new Intent(OrderDetailActivity.this, PaySuccessActivity.class);
                            intent.putExtra("price", orderPrice);
                            startActivity(intent);
                        }



                        BaseEvent baseEvent = new BaseEvent(EventBusConstant.need_refresh, null);
                        EventBus.getDefault().post(baseEvent);
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.e("FH", "获取订单状态失败");
                        throwable.printStackTrace();
                        qrCodeDialog.showHintAndRetry("支付未成功", "重试");
                    }
                });
    }

    public void pay(View view) {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
            return;
        }
        queryQRStr(PAY_ALIPAY);
    }

    public void cancleOrder(View view) {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
            return;
        }
        new ConfirmDialog(getThisActivity(), "确定要取消此订单吗?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NetWorkManager.cancelOrder(orderId, SpUtils.getUserId())
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                LogUtils.e("FH", "订单" + orderId + "取消成功");
                                finish();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                new ConfirmDialog(getThisActivity(), "订单取消失败,是否重试?", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        cancleOrder(null);
                                    }
                                }).show();
                                LogUtils.e("FH", "订单" + orderId + "取消失败" + throwable.getMessage());
                                throwable.printStackTrace();
                            }
                        });
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        if (mUiPromptDialog.getTag() == mTagForNoNet) {
            jumpTonet();
        } else if (mUiPromptDialog.getTag() == mTagForZxingfail) {
            queryQRStr(PAY_ALIPAY);
        }
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ItemOrderDetailBinding binding;
        OrderDetailBean.OrderInfo orderInfo1, orderInfo2;
        CouponInfo couponInfo;

        public MyHolder(ItemOrderDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(CouponInfo couponInfo
                , OrderDetailBean.OrderInfo orderInfo1, OrderDetailBean.OrderInfo orderInfo2
                , boolean showCoupon) {
            this.orderInfo1 = orderInfo1;
            this.orderInfo2 = orderInfo2;
            this.couponInfo = couponInfo;
            refreshImg(binding.bookCoverImv1, orderInfo1.bookInfo.get(0).getBookCoverS());
            binding.bookNameTv1.setText(orderInfo1.bookInfo.get(0).getBookTitle());
            binding.bookAuthorTv1.setText("作者 : " + orderInfo1.bookInfo.get(0).getBookAuthor());
            if (orderInfo1.bookFinalPrice == orderInfo1.bookSalePrice) {
                binding.bookPriceTv1.setText("价格 : ￥" + orderInfo1.bookFinalPrice);
            } else {
                binding.bookPriceTv1.setText("原价 : ￥" + orderInfo1.bookSalePrice
                        + "\n\n现价 : " + orderInfo1.bookFinalPrice);
            }
            if (orderInfo2 != null) {
                binding.bookNameTv2.setVisibility(View.VISIBLE);
                binding.bookAuthorTv2.setVisibility(View.VISIBLE);
                binding.bookCoverImv2.setVisibility(View.VISIBLE);
                binding.bookPriceTv2.setVisibility(View.VISIBLE);
                refreshImg(binding.bookCoverImv2, orderInfo2.bookInfo.get(0).getBookCoverS());
                binding.bookNameTv2.setText(orderInfo2.bookInfo.get(0).getBookTitle());
                binding.bookAuthorTv2.setText("作者 : " + orderInfo2.bookInfo.get(0).getBookAuthor());
                if (orderInfo2.bookFinalPrice == orderInfo2.bookSalePrice) {
                    binding.bookPriceTv2.setText("价格 : ￥" + orderInfo2.bookFinalPrice);
                } else {
                    binding.bookPriceTv2.setText("原价 : ￥" + orderInfo2.bookSalePrice
                            + "\n\n现价 : " + orderInfo2.bookFinalPrice);
                }
            } else {
                binding.bookNameTv2.setVisibility(View.GONE);
                binding.bookAuthorTv2.setVisibility(View.GONE);
                binding.bookCoverImv2.setVisibility(View.GONE);
                binding.bookPriceTv2.setVisibility(View.GONE);
            }
            if (showCoupon) {
                binding.promotionLayout.setVisibility(View.VISIBLE);
                if (couponInfo != null) {
                    binding.noPromotionTv.setVisibility(View.GONE);
                    binding.promotionContentTv.setVisibility(View.VISIBLE);
                    binding.promotionNameTv.setVisibility(View.VISIBLE);
                    binding.promotionPriceSumTv.setVisibility(View.VISIBLE);
                    binding.promotionContentTv.setText("满" + couponInfo.over + "元减" + couponInfo.cut + "元");
                    binding.promotionPriceSumTv.setText("共计 : ￥" + couponInfo.orderFinalPrice + "元，已优惠￥" +
                            couponInfo.orderDeduction + "元");
                } else {
                    binding.noPromotionTv.setVisibility(View.VISIBLE);
                    binding.promotionContentTv.setVisibility(View.GONE);
                    binding.promotionNameTv.setVisibility(View.GONE);
                    binding.promotionPriceSumTv.setVisibility(View.GONE);
                }
            } else {
                binding.promotionLayout.setVisibility(View.GONE);
            }
        }
    }

    private class CouponInfo {
        double over, cut, orderFinalPrice, orderDeduction;

        public CouponInfo(double over, double cut, double orderFinalPrice, double orderDeduction) {
            this.over = over;
            this.cut = cut;
            this.orderFinalPrice = orderFinalPrice;
            this.orderDeduction = orderDeduction;
        }
    }


    private void jumpToControlFragmentActivity(BookInfo info) {
        mPaySuccessDialog.dismiss();
//        String filePath = FileUtils.getTextBookFilesDir() + mBookInfo.getBookId() + ".pdf";
        if (!StringUtils.isEmpty(FileUtils.getBookFileName(info.getBookId(), FileUtils.bookDir))) {

            Bundle extras = new Bundle();
            //课本进入
            extras.putString(FileContonst.JUMP_FRAGMENT, FileContonst.JUMP_TEXT_BOOK);
            //笔记创建者
            extras.putInt(FileContonst.NOTE_CREATOR, -1);
            //分类码
            extras.putInt(FileContonst.CATEGORY_ID, info.getBookCategory());
            //笔记类型
            extras.putInt(FileContonst.NOTE_Style, info.getNoteStyle());
            extras.putInt(FileContonst.NOTE_SUBJECT_ID, info.getBookFitSubjectId());
            extras.putString(FileContonst.NOTE_SUBJECT_NAME, info.getBookFitSubjectName());
            //作业ID
            extras.putInt(FileContonst.HOME_WROK_ID, info.getBookFitHomeworkId());
            //笔记id
            extras.putInt(FileContonst.NOTE_ID, info.getBookFitNoteId());
            //图书id
            extras.putInt(FileContonst.BOOK_ID, info.getBookId());
            extras.putString(FileContonst.NOTE_TITLE, info.getBookFitNoteTitle());
            loadIntentWithExtras(ControlFragmentActivity.class, extras);
            this.finish();
        }
    }


    private void showPaySuccessDialog() {
        BookInfo bookInfo;
        CouponInfo couponInfo;
        OrderDetailBean.OrderInfo orderInfo;
        try {
            orderInfo = dataList.get(0).second.get(0);
            bookInfo = dataList.get(0).second.get(0).bookInfo.get(0);
            couponInfo = dataList.get(0).first;
        }
        catch (Exception e){
            e.printStackTrace();
            ToastUtil.showCustomToast(getApplication() , "支付成功提示信息弹出失败");
            return;
        }
        //跳转到图书
        if (mPaySuccessDialog == null){
            mPaySuccessDialog = new PaySuccessDialog(OrderDetailActivity.this);
            mPaySuccessDialog.setBookDetailsListener(new PaySuccessDialog.PaySuccessListener() {
                @Override
                public void onCancelListener() {
                    mPaySuccessDialog.dismiss();
                    OrderDetailActivity.this.finish();
                }

                @Override
                public void onConfirmListener() {
                    mPaySuccessDialog.dismiss();
                    if (!StringUtils.isEmpty(FileUtils.getBookFileName(bookInfo.getBookId(), FileUtils.bookDir))) {
                        jumpToControlFragmentActivity(bookInfo);
                    } else {
                        if (NetUtils.isNetConnected()) {
                            downBookTask(bookInfo.getBookId());
                        } else {
                            showCancelAndDetermineDialog(R.string.jump_to_net);
                        }
                    }
                }
            });
        }
        mPaySuccessDialog.show();
        mPaySuccessDialog.setOrderNumber("订单编号 : " + mTopOrderDetail.orderId);
        mPaySuccessDialog.setOrderTime("下单时间 : " + mTopOrderDetail.orderCreateTime);
        //TODO: 满减 ，图书详情
        if (orderInfo.bookFinalPrice == 0){
            mPaySuccessDialog.setSalesDetail("限免");
            mPaySuccessDialog.setMarketPrice("销售价 : ￥" + orderInfo.bookFinalPrice);
        }
        else if (couponInfo != null){
            mPaySuccessDialog.setSalesDetail("满减");
            mPaySuccessDialog.setMarketPrice("销售价 : ￥" + couponInfo.orderFinalPrice);
        }
        else if (orderInfo.bookFinalPrice != orderInfo.bookSalePrice){
            mPaySuccessDialog.setSalesDetail("折扣");
            mPaySuccessDialog.setMarketPrice("销售价 : ￥" + orderInfo.bookFinalPrice);
        }
        else {
            mPaySuccessDialog.setSalesDetail("不参加任何满减");
            mPaySuccessDialog.setMarketPrice("销售价 : ￥" + orderInfo.bookFinalPrice);
        }
        mPaySuccessDialog.setSmallIcon(bookInfo.getBookCoverS() , this);
        mPaySuccessDialog.setBookName(bookInfo.getBookTitle());
        mPaySuccessDialog.setBookAuther(bookInfo.getBookAuthor());
        mPaySuccessDialog.setFactoryPrice("定价 : ￥" + orderInfo.bookSalePrice);
    }

    @Override
    protected void onDownBookFinish() {
        super.onDownBookFinish();
        mPaySuccessDialog.dismiss();
        jumpToControlFragmentActivity(null);
    }
}