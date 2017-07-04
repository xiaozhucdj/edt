package com.yougy.shop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.CancelBookOrderCallBack;
import com.yougy.common.protocol.callback.QueryOrderListCallBack;
import com.yougy.common.protocol.response.CancelBookOrderRep;
import com.yougy.common.protocol.response.IsOrderPaySuccessRep;
import com.yougy.common.protocol.response.QueryBookOrderListRep;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.init.bean.BookInfo;
import com.yougy.shop.bean.BriefOrder;
import com.yougy.shop.bean.OrderInfo;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityShopOrderListBinding;
import com.yougy.ui.activity.databinding.ItemOrderListBinding;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.HintDialog;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2017/6/29.
 */

public class OrderListActivity extends ShopBaseActivity {
    ActivityShopOrderListBinding binding;
    ArrayList<BriefOrder> orderList = new ArrayList<BriefOrder>();

    //每次最多展示的页数
    final int ONCE_SHOW_PAGE_NUM = 5;
    //每一页的条数
    final int ITEM_NUM = 6;

    //当前展示的第一页页号(从0开始)
    int currentShowFirstPageIndex = 0;
    //当前选定的的页码序号(从0开始)
    int currentSelectedPageIndex= 0;

    //是否需要刷新items
    boolean needRefreshItems = false;


    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_shop_order_list , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    public void init() {
        setNeedRecieveEventAfterOnStop(true);
    }

    @Override
    protected void initLayout() {}

    @Override
    protected void handleEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                 if (o instanceof String && o.equals("refreshOrderList")){
                     loadOrder();
                 }
                 else if (o instanceof QueryBookOrderListRep){
                    if (((QueryBookOrderListRep) o).getCode() == ProtocolId.RET_SUCCESS){
                        orderList.clear();
                        List<OrderInfo> orderInfos = ((QueryBookOrderListRep) o).getData();
                        if (orderInfos != null){
                            for (final OrderInfo newOrderInfo: orderInfos) {
                                BriefOrder oldOrder = findOrderById(newOrderInfo.getOrderId());
                                if (oldOrder == null){
                                    oldOrder = new BriefOrder(){{
                                        setOrderId(newOrderInfo.getOrderId());
                                        setOrderPrice(newOrderInfo.getOrderPrice());
                                        setOrderTime(newOrderInfo.getOrderCreateTime());
                                        setOrderStatus(newOrderInfo.getOrderStatus());
                                        setBookList(new ArrayList<BookInfo>(){{
                                            add(new BookInfo(){{
                                                setBookTitle(newOrderInfo.getBookTitle());
                                                setBookCover(newOrderInfo.getBookCover());
                                                Log.v("FH" , "newOrderInfo.getBookPrice " + newOrderInfo.getBookPrice());
                                                setBookSalePrice(newOrderInfo.getBookPrice());
                                            }});
                                        }});
                                    }};
                                    orderList.add(oldOrder);
                                }
                                else {
                                    oldOrder.getBookList().add(new BookInfo(){{
                                        setBookTitle(newOrderInfo.getBookTitle());
                                        setBookCover(newOrderInfo.getBookCover());
                                        setBookSalePrice(newOrderInfo.getBookPrice());
                                        Log.v("FH" , "222newOrderInfo.getBookPrice " + newOrderInfo.getBookPrice());
                                    }});
                                }
                            }
                        }
                        //如果之前选中的页号在新的数据中已经不存在了,则把选中的页号确定为最后一页.
                        if (orderList.size() <= currentSelectedPageIndex * ITEM_NUM){
                            currentSelectedPageIndex = (orderList.size() - 1) / ITEM_NUM;
                            if (currentSelectedPageIndex < 0){
                                currentSelectedPageIndex = 0;
                            }
                        }
                        //此处得到新的显示第一页的页号,如(11/5*5 = 10)
                        currentShowFirstPageIndex = currentSelectedPageIndex / ONCE_SHOW_PAGE_NUM * ONCE_SHOW_PAGE_NUM;
                        //请求刷新items
                        needRefreshItems = true;
                        refreshUI();
                    }
                    else {
                        Log.v("FH" , "获取我的订单列表失败 : " + ((QueryBookOrderListRep) o).getMsg());
                        new ConfirmDialog(OrderListActivity.this, "获取订单列表失败,是否重试?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                loadOrder();
                            }
                        } , "重试").show();
                    }
                 }
                 else if (o instanceof CancelBookOrderRep){
                     if (((CancelBookOrderRep) o).getCode() == ProtocolId.RET_SUCCESS){
                         Log.v("FH" , "取消订单成功");
                         new HintDialog(getThisActivity() , "取消订单成功").show();
                         loadOrder();
                     }
                     else {
                         new HintDialog(getThisActivity() , "取消失败" + ((CancelBookOrderRep) o).getMsg()).show();
                         Log.v("FH" , "取消失败 " + ((CancelBookOrderRep) o).getMsg());
                     }
                 }
                 else if (o instanceof IsOrderPaySuccessRep){
                    if (((IsOrderPaySuccessRep) o).getCode() == ProtocolId.RET_SUCCESS){
                        new HintDialog(getThisActivity() , "已支付订单已完成").show();
                        Log.v("FH" , "已支付订单已完成");
                        loadOrder();
                    }
                    else {
                        new HintDialog(getThisActivity() , "完成已支付订单失败" + ((IsOrderPaySuccessRep) o).getMsg()).show();
                        Log.v("FH" , "完成已支付订单失败" + ((IsOrderPaySuccessRep) o).getMsg());
                    }
                 }
            }
        }));
        super.handleEvent();
    }

    @Override
    public void loadData() {
        for (int i = 0 ; i < ITEM_NUM ; i++){
            ItemOrderListBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.item_order_list , null , false);
            ((OrderListItem)itemBinding.getRoot()).setBinding(itemBinding);
            setOnClickListenersInItem(((OrderListItem)itemBinding.getRoot()));
            binding.orderItemContainer.addView(itemBinding.getRoot());
        }
        loadOrder();
    }

    @Override
    protected void refreshView() {}

    private void loadOrder(){
        ProtocolManager.queryBookOrderProtocol(String.valueOf(SpUtil.getAccountId())
                , ProtocolId.PROTOCOL_ID_QUERY_BOOK_ORDER
                , new QueryOrderListCallBack(this , ProtocolId.PROTOCOL_ID_QUERY_BOOK_ORDER));
    }

    private BriefOrder findOrderById(String orderId){
        if (TextUtils.isEmpty(orderId)){
            return null;
        }
        for (BriefOrder briefOrder : orderList) {
            if (briefOrder.getOrderId().equals(orderId)){
                return briefOrder;
            }
        }
        return null;
    }

    private void setOnClickListenersInItem(final OrderListItem item){
        item.getBinding().cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProtocolManager.cancelPayOrderProtocol(item.getBriefOrder().getOrderId()
                        , SpUtil.getAccountId()
                        , ProtocolId.PROTOCOL_ID_CANCEL_PAY_ORDER
                        , new CancelBookOrderCallBack(OrderListActivity.this, ProtocolId.PROTOCOL_ID_CANCEL_PAY_ORDER , item.getBriefOrder().getOrderId() , SpUtil.getAccountId()));
            }
        });
        item.getBinding().payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConfirmOrderActivity.class);
                intent.putExtra(ShopGloble.ORDER, item.getBriefOrder());
                startActivity(intent);
            }
        });
    }
    public void refreshUI(){
        binding.pageBtnContainer.removeAllViews();
        if (orderList.size() == 0) {
            //隐藏所有之前的item,并且显示没有收藏的提示
            fillItems(0 , -1);
            binding.noResultLayout.setVisibility(View.VISIBLE);
        } else {
            binding.noResultLayout.setVisibility(View.GONE);
            //本次显示的最大的页码(从1开始)
            int showPageNumEnd;
            //本次能展示的最后一本书的序号(从0开始)
            int lastShowBookIndex;
            //是否显示<<向前按钮
            boolean showForward = (currentShowFirstPageIndex == 0 ? false : true);
            //是否显示>>向后按钮
            boolean showNext;
            if (orderList.size() <= (currentShowFirstPageIndex + ONCE_SHOW_PAGE_NUM) * ITEM_NUM){
                lastShowBookIndex = orderList.size() - 1;
                showNext = false;
            }
            else {
                lastShowBookIndex = (currentShowFirstPageIndex + ONCE_SHOW_PAGE_NUM) * ITEM_NUM - 1;
                showNext = true;
            }
            showPageNumEnd = lastShowBookIndex / ITEM_NUM + 1;
            addBtns(currentShowFirstPageIndex + 1 , showPageNumEnd , showForward, showNext);

            if (needRefreshItems){
                toPage(currentSelectedPageIndex);
                needRefreshItems = false;
            }
            //如果当前显示的页码按钮在页码容器中,则高亮显示
            TextView selectedBtn = (TextView) binding.pageBtnContainer.findViewWithTag((currentSelectedPageIndex + 1));
            if (selectedBtn != null) {
                selectedBtn.setSelected(true);
            }
        }
    }

    /**
     * 切换到制定的页
     * @param pageIndex 制定的页序号
     */
    private void toPage(int pageIndex) {
        if ((pageIndex + 1) * ITEM_NUM < orderList.size()) {
            fillItems(pageIndex * ITEM_NUM, (pageIndex + 1) * ITEM_NUM - 1);
        } else {
            fillItems(pageIndex * ITEM_NUM, orderList.size() - 1);
        }
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
        pageBtn.setLayoutParams(params);
        pageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int) v.getTag();
                switch (i) {
                    case -1://向前
                        currentShowFirstPageIndex = currentShowFirstPageIndex - ONCE_SHOW_PAGE_NUM;
                        refreshUI();
                        break;
                    case -2://向后
                        currentShowFirstPageIndex = currentShowFirstPageIndex + ONCE_SHOW_PAGE_NUM;
                        refreshUI();
                        break;
                    default:
                        TextView btn = (TextView) binding.pageBtnContainer.findViewWithTag(currentSelectedPageIndex + 1);
                        if (btn != null) {
                            btn.setSelected(false);
                        }
                        btn = (TextView) binding.pageBtnContainer.findViewWithTag(i);
                        if (btn != null){
                            btn.setSelected(true);
                        }
                        currentSelectedPageIndex = i - 1;
                        toPage(i - 1);
                        break;

                }
            }
        });
        AutoUtils.auto(pageBtn);
        binding.pageBtnContainer.addView(pageBtn);
    }

    /**
     * 添加多个按钮
     * @param firstBtnNum 要添加的第一个按钮的序号
     * @param lastBtnNum 要添加的最后一个按钮的序号
     * @param hasForward 是否添加向前按钮
     * @param hasNext 是否添加向后按钮
     */
    private void addBtns(int firstBtnNum, int lastBtnNum, boolean hasForward, boolean hasNext) {
        if (hasForward) addBtn(-1);
        for (int index = firstBtnNum; index <= lastBtnNum ; index++) {
            addBtn(index);
        }
        if (hasNext) addBtn(-2);
    }


    /**
     * 根据当前显示的favor信息填充items
     * @param startIndex 要显示的多个favor在本地缓存中的开始位置
     * @param endIndex 要显示的多个favor在本地缓存中的结束位置
     */
    private void fillItems(int startIndex, int endIndex) {
        for (int i = startIndex, j = 0; j < ITEM_NUM; i++, j++) {
            if (i <= endIndex) {
                BriefOrder briefOrder = orderList.get(i);
                ((OrderListItem)binding.orderItemContainer.getChildAt(j)).setBriefOrder(briefOrder);
            } else {
                ((OrderListItem)binding.orderItemContainer.getChildAt(j)).setBriefOrder(null);
            }
        }
    }

    public void toShop(View view){
        loadIntent(BookShopActivityDB.class);
        finish();
    }

    public void onBack(View view){
        onBackPressed();
    }

}
