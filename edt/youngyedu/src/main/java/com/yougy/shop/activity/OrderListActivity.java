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

import com.bumptech.glide.Glide;
import com.frank.etude.pageBtnBar.PageBtnBarAdapter;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.message.ListUtil;
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
    ArrayList<OrderSummary> selectedOrderSummaryList = new ArrayList<OrderSummary>();

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
                        ListUtil.conditionalRemove(selectedOrderSummaryList, new ListUtil.ConditionJudger<OrderSummary>() {
                            @Override
                            public boolean isMatchCondition(OrderSummary nodeInList) {
                                return nodeInList.getOrderId().equals(holder.getData().getOrderId());
                            }
                        });
                        if (!v.isSelected()){
                            selectedOrderSummaryList.add(holder.getData());
                        }
                        v.setSelected(!v.isSelected());
                        binding.selectAllCheckbox.setSelected(isAllSelected());
                    }
                });
                holder.itemBinding.cancleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder(holder.orderSummary.getOrderId());
                    }
                });
                holder.itemBinding.payBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), OrderDetailActivity.class);
                        intent.putExtra("orderId", holder.getData().getOrderId());
                        startActivity(intent);
                    }
                });
                holder.itemBinding.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ConfirmDialog(OrderListActivity.this, "您确定要删除这本书吗?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                NetWorkManager.removeOrder(holder.orderSummary.getOrderId() , String.valueOf(SpUtils.getUserId()))
                                        .compose(bindToLifecycle()).subscribe(new Action1<Object>() {
                                    @Override
                                    public void call(Object o) {
                                        ToastUtil.showCustomToast(getApplicationContext() , "删除成功");
                                        selectedOrderSummaryList.remove(holder.orderSummary.getOrderId());
                                        refreshData(binding.pageBtnBar.getCurrentSelectPageIndex());
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        throwable.printStackTrace();
                                        ToastUtil.showCustomToast(getApplicationContext() , "删除失败");
                                    }
                                });
                            }
                        } , "确定").show();
                    }
                });
                holder.itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), OrderDetailActivity.class);
                        intent.putExtra("orderId", holder.getData().getOrderId());
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
                for (OrderSummary orderSummary : orderList){
                    ListUtil.conditionalRemove(selectedOrderSummaryList, new ListUtil.ConditionJudger<OrderSummary>() {
                        @Override
                        public boolean isMatchCondition(OrderSummary nodeInList) {
                            return orderSummary.getOrderId().equals(nodeInList.getOrderId());
                        }
                    });
                }
                if (!v.isSelected()){
                    for (OrderSummary orderSummary : orderList) {
                        selectedOrderSummaryList.add(orderSummary);
                    }
                }
                binding.mainRecyclerview.getAdapter().notifyDataSetChanged();
                v.setSelected(!v.isSelected());
            }
        });
        binding.deleteSelectedOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderIdStr = "[";
                for (OrderSummary orderSummary : selectedOrderSummaryList) {
                    if (!orderSummary.getOrderStatus().equals("交易成功")) {
                        orderIdStr = orderIdStr + "\"" + orderSummary.getOrderId() + "\",";
                    }
                }
                if (orderIdStr.equals("[")){
                    ToastUtil.showCustomToast(getApplicationContext() , "没有可以删除的订单");
                }
                else {
                    orderIdStr = orderIdStr.substring(0, orderIdStr.length() - 1) + "]";
                    String finalOrderIdStr = orderIdStr;
                    new ConfirmDialog(OrderListActivity.this, "您确定要删除这些订单吗?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            NetWorkManager.removeOrder(finalOrderIdStr, String.valueOf(SpUtils.getUserId()))
                                    .compose(bindToLifecycle()).subscribe(new Action1<Object>() {
                                @Override
                                public void call(Object o) {
                                    ToastUtil.showCustomToast(getApplicationContext(), "删除成功");
                                    selectedOrderSummaryList.clear();
                                    refreshData(binding.pageBtnBar.getCurrentSelectPageIndex());
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    throwable.printStackTrace();
                                    ToastUtil.showCustomToast(getApplicationContext(), "删除失败");
                                }
                            });
                        }
                    } , "确定").show();
                }
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
            if (!ListUtil.conditionalContains(selectedOrderSummaryList, new ListUtil.ConditionJudger<OrderSummary>() {
                @Override
                public boolean isMatchCondition(OrderSummary nodeInList) {
                    return nodeInList.getOrderId().equals(orderSummary.getOrderId());
                }
            })){
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
            itemBinding.orderNumTv.setText("订单编号 : " + orderSummary.getOrderId());
            itemBinding.orderTimeTv.setText("下单时间 : " + orderSummary.getOrderCreateTime());
            itemBinding.orderStatusTv.setText(orderSummary.getOrderStatus());
            if (orderSummary.getOrderStatus().equals("交易成功")){
                itemBinding.deleteBtn.setVisibility(GONE);
            }
            else {
                itemBinding.deleteBtn.setVisibility(VISIBLE);
            }
            if (orderSummary.getOrderStatus().equals("待支付")){
                itemBinding.cancleBtn.setVisibility(VISIBLE);
                itemBinding.payBtn.setVisibility(VISIBLE);
            }
            else {
                itemBinding.cancleBtn.setVisibility(GONE);
                itemBinding.payBtn.setVisibility(GONE);
            }
            itemBinding.checkbox.setSelected(ListUtil.conditionalContains(selectedOrderSummaryList, new ListUtil.ConditionJudger<OrderSummary>() {
                @Override
                public boolean isMatchCondition(OrderSummary nodeInList) {
                    return nodeInList.getOrderId().equals(orderSummary.getOrderId());
                }
            }));
            int totalBookCount = 0;
            for (OrderSummary.OrderInfoBean orderInfoBean : orderSummary.getOrderInfo()) {
                totalBookCount = totalBookCount + orderInfoBean.getBookCount();
            }
            itemBinding.bookCountTv.setText("共" + totalBookCount + "本");
            itemBinding.bookNameTv.setText(orderSummary.getOrderInfo().get(0).getBookInfo().getBookTitle());
            itemBinding.orderTotalPriceTv.setText("总额 : " + orderSummary.getOrderAmount() + "元");
            itemBinding.orderFinalPriceTv.setText("应付 : " + (orderSummary.getOrderAmount() - orderSummary.getOrderDeduction()) + "元");
            Glide.with(OrderListActivity.this)
                    .load(orderSummary.getOrderInfo().get(0).getBookInfo().getBookCoverS())
                    .into(itemBinding.bookCoverImv);
        }
        public OrderSummary getData(){
            return orderSummary;
        }
    }
}
