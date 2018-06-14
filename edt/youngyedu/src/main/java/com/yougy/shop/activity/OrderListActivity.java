package com.yougy.shop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frank.etude.pageBtnBar.PageBtnBarAdapter;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.shop.bean.OrderSummary;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityShopOrderListBinding;
import com.yougy.ui.activity.databinding.ItemOrderListBinding;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.HintDialog;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by FH on 2017/6/29.
 * 我的订单列表
 */

public class OrderListActivity extends ShopBaseActivity {
    ActivityShopOrderListBinding binding;
    ArrayList<OrderSummary> orderList = new ArrayList<OrderSummary>();
    int totalCount = 0;
    final int ITEM_NUM_PER_PAGE = 3;
    ArrayList<String> selectedOrderIdList = new ArrayList<String>();

    @Override
    public void init() {
        setNeedRecieveEventAfterOnStop(true);
    }

    @Override
    protected void initLayout() {
        binding.mainRecyclerview.setLayoutManager(new LinearLayoutManager(OrderListActivity.this
                , LinearLayoutManager.VERTICAL , false));
        binding.mainRecyclerview.setAdapter(new RecyclerView.Adapter<MyHolder>() {
            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                MyHolder holder = new MyHolder(DataBindingUtil.inflate(LayoutInflater.from(OrderListActivity.this)
                        , R.layout.item_order_list , binding.mainRecyclerview , false));
                holder.itemBinding.checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.isSelected()){
                            selectedOrderIdList.remove(holder.getData().orderId);
                        }
                        else {
                            selectedOrderIdList.add(holder.getData().orderId);
                        }
                        v.setSelected(!v.isSelected());
                        binding.selectAllCheckbox.setSelected(isAllSelected());
                    }
                });
                holder.itemBinding.cancleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder(holder.orderSummary.orderId);
                    }
                });
                holder.itemBinding.payBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), OrderDetailActivity.class);
                        intent.putExtra("orderId", holder.getData().orderId);
                        startActivity(intent);
                    }
                });
                holder.itemBinding.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                holder.itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), OrderDetailActivity.class);
                        intent.putExtra("orderId", holder.getData().orderId);
                        intent.putExtra("showSuccessDialog" , false);
                        startActivity(intent);
                    }
                });
                return holder;
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                OrderSummary orderSummary = orderList.get(position);
                holder.setData(orderSummary);
            }

            @Override
            public int getItemCount() {
                return orderList.size();
            }
        });
        binding.pageBtnBar.setPageBarAdapter(new PageBtnBarAdapter(this) {
            @Override
            public int getPageBtnCount() {
                return (totalCount + ITEM_NUM_PER_PAGE - 1)/ITEM_NUM_PER_PAGE;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn) {
                refreshData(btnIndex);
            }
        });
        binding.selectAllCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()){
                    for (OrderSummary orderSummary : orderList){
                        selectedOrderIdList.remove(orderSummary.orderId);
                    }
                }
                else {
                    for (OrderSummary orderSummary :
                            orderList) {
                        if (!selectedOrderIdList.contains(orderSummary.orderId)){
                            selectedOrderIdList.add(orderSummary.orderId);
                        }
                    }
                }
                binding.mainRecyclerview.getAdapter().notifyDataSetChanged();
                v.setSelected(!v.isSelected());
            }
        });
        binding.deleteSelectedOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.deleteSelectedOrderBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    public View layout() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_shop_order_list, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    protected void handleEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof String && o.equals("refreshOrderList")) {
                    refreshData(binding.pageBtnBar.getCurrentSelectPageIndex());
                }
            }
        }));
        super.handleEvent();
    }

    @Override
    protected void setContentView() {
        setContentView(layout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData(binding.pageBtnBar.getCurrentSelectPageIndex());
    }

    @Override
    public void loadData() {

    }

    @Override
    protected void refreshView() {

    }

    private void refreshData(int pageIndex) {
        NetWorkManager.queryMyOrderList(String.valueOf(SpUtils.getUserId()) , ITEM_NUM_PER_PAGE , pageIndex + 1)
                .subscribe(new Action1<Pair<Integer, List<OrderSummary>>>() {
                    @Override
                    public void call(Pair<Integer, List<OrderSummary>> result) {
                        orderList.clear();
                        totalCount = result.first;
                        if (totalCount == 0) {
                            binding.noResultLayout.setVisibility(VISIBLE);
                        } else {
                            binding.noResultLayout.setVisibility(GONE);
                            orderList.addAll(result.second);
                            binding.mainRecyclerview.getAdapter().notifyDataSetChanged();
                            binding.pageBtnBar.refreshPageBar();
                            binding.selectAllCheckbox.setSelected(isAllSelected());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.e("FH", "获取我的订单列表失败 : " + throwable.getMessage());
                        throwable.printStackTrace();
                        new ConfirmDialog(OrderListActivity.this, "获取订单列表失败,是否重试?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                refreshData(binding.pageBtnBar.getCurrentSelectPageIndex());
                            }
                        }, "重试").show();
                    }
                });
    }

    public void cancelOrder(String orderId) {
        new ConfirmDialog(getThisActivity(), "确定要取消订单" + orderId + "吗?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NetWorkManager.cancelOrder(orderId, SpUtils.getUserId())
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                new HintDialog(getThisActivity(), "取消订单成功").show();
                                LogUtils.e("FH", "取消订单成功");
                                refreshData(binding.pageBtnBar.getCurrentSelectPageIndex());
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                LogUtils.e("FH", "取消失败 " + throwable.getMessage());
                                throwable.printStackTrace();
                                new ConfirmDialog(OrderListActivity.this, "取消订单失败,是否重试?", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        cancelOrder(orderId);
                                    }
                                }, "重试").show();
                            }
                        });
                dialog.dismiss();
            }
        }).show();
    }

    public void toShop(View view) {
        loadIntent(BookShopActivityDB.class);
        finish();
    }

    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        refreshData(binding.pageBtnBar.getCurrentSelectPageIndex());
    }

    private boolean isAllSelected(){
        if (orderList.size() == 0){
            return false;
        }
        for (OrderSummary orderSummary : orderList) {
            if (!selectedOrderIdList.contains(orderSummary.orderId)){
                return false;
            }
        }
        return true;
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        ItemOrderListBinding itemBinding;
        OrderSummary orderSummary;
        public MyHolder(ItemOrderListBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
        public void setData(OrderSummary data){
            orderSummary = data;
            itemBinding.orderNumTv.setText("订单编号 : " + orderSummary.orderId);
//            itemBinding.order.setText("订单金额 : ￥" + orderSummary.orderAmount);
            itemBinding.orderTimeTv.setText("下单时间 : " + orderSummary.orderCreateTime);
            itemBinding.orderStatusTv.setText(orderSummary.orderStatus);
            if (orderSummary.orderStatus.equals("待支付")){
                itemBinding.cancleBtn.setVisibility(VISIBLE);
                itemBinding.payBtn.setVisibility(VISIBLE);
            }
            else {
                itemBinding.cancleBtn.setVisibility(GONE);
                itemBinding.payBtn.setVisibility(GONE);
            }
            itemBinding.checkbox.setSelected(selectedOrderIdList.contains(data.orderId));
        }
        public OrderSummary getData(){
            return orderSummary;
        }
    }
}
