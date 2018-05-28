package com.yougy.shop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.PageableRecyclerView;
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

    @Override
    public void init() {
        setNeedRecieveEventAfterOnStop(true);
    }

    @Override
    protected void initLayout() {

    }

    public View layout() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_shop_order_list, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        binding.mainRecyclerview.setMaxItemNumInOnePage(6);
        binding.mainRecyclerview.setLayoutManager(new LinearLayoutManager(OrderListActivity.this
                , LinearLayoutManager.VERTICAL , false));
        binding.mainRecyclerview.setAdapter(new PageableRecyclerView.Adapter<MyHolder>() {
            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                MyHolder holder = new MyHolder(DataBindingUtil.inflate(LayoutInflater.from(OrderListActivity.this)
                        , R.layout.item_order_list , null , false));
                holder.binding.cancleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder(holder.orderSummary.orderId);
                    }
                });
                holder.binding.payBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), OrderDetailActivity.class);
                        intent.putExtra("orderId", holder.getData().orderId);
                        startActivity(intent);
                    }
                });
                holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
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
        return binding.getRoot();
    }

    @Override
    protected void handleEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof String && o.equals("refreshOrderList")) {
                    refreshData();
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
        refreshData();
    }

    @Override
    public void loadData() {}

    @Override
    protected void refreshView() {

    }

    private void refreshData() {
        NetWorkManager.queryMyOrderList(String.valueOf(SpUtils.getUserId()))
                .subscribe(new Action1<List<OrderSummary>>() {
                    @Override
                    public void call(List<OrderSummary> orderSummaryList) {
                        orderList.clear();
                        if (orderSummaryList.size() == 0){
                            binding.noResultLayout.setVisibility(VISIBLE);
                        }
                        else {
                            binding.noResultLayout.setVisibility(GONE);
                            orderList.addAll(orderSummaryList);
                            binding.mainRecyclerview.setCurrentPage(1);
                            binding.mainRecyclerview.notifyDataSetChanged();
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
                                refreshData();
                            }
                        }, "重试").show();
                    }
                });
    }

    private void setOnClickListenersInItem(OrderListItem item) {
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
                                refreshData();
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
        refreshData();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        ItemOrderListBinding binding;
        OrderSummary orderSummary;
        public MyHolder(ItemOrderListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void setData(OrderSummary data){
            orderSummary = data;
            binding.orderNumTv.setText("订单编号 : " + orderSummary.orderId);
            binding.orderPriceTv.setText("订单金额 : ￥" + orderSummary.orderAmount);
            binding.orderTimeTv.setText("下单时间 : " + orderSummary.orderCreateTime);
            if (orderSummary.orderStatus.equals("待支付")){
                binding.orderStatusTv.setVisibility(GONE);
                binding.cancleBtn.setVisibility(VISIBLE);
                binding.payBtn.setVisibility(VISIBLE);
            }
            else {
                binding.orderStatusTv.setText(orderSummary.orderStatus);
                binding.orderStatusTv.setVisibility(VISIBLE);
                binding.cancleBtn.setVisibility(GONE);
                binding.payBtn.setVisibility(GONE);
            }
        }
        public OrderSummary getData(){
            return orderSummary;
        }
    }
}
