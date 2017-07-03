package com.yougy.shop.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.yougy.shop.bean.BriefOrder;
import com.yougy.ui.activity.databinding.ItemOrderListBinding;

/**
 * Created by FH on 2017/6/29.
 */
public class OrderListItem extends RelativeLayout {
    private ItemOrderListBinding binding;
    private BriefOrder briefOrder;
    public OrderListItem(Context context) {
        this(context, null);
    }
    public OrderListItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public OrderListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ItemOrderListBinding getBinding() {
        return binding;
    }

    public OrderListItem setBinding(ItemOrderListBinding binding) {
        this.binding = binding;
        return this;
    }
    public BriefOrder getBriefOrder() {
        return briefOrder;
    }

    public OrderListItem setBriefOrder(BriefOrder briefOrder) {
        if (briefOrder == null){
            setVisibility(GONE);
        }
        else {
            setVisibility(VISIBLE);
            this.briefOrder = briefOrder;
            binding.orderNumTv.setText("订单编号 : " + briefOrder.getOrderId());
            binding.orderPriceTv.setText("订单金额 : ￥" + briefOrder.getOrderPrice());
            binding.orderTimeTv.setText("下单时间 : " + briefOrder.getOrderTime());
            if (briefOrder.getOrderStatus().equals("待支付")){
                binding.orderStatusTv.setVisibility(GONE);
                binding.finishBtn.setVisibility(GONE);
                binding.cancleBtn.setVisibility(VISIBLE);
                binding.payBtn.setVisibility(VISIBLE);
            }
            else if (briefOrder.getOrderStatus().equals("已支付")){
                binding.orderStatusTv.setText(briefOrder.getOrderStatus());
                binding.orderStatusTv.setVisibility(VISIBLE);
                binding.finishBtn.setVisibility(VISIBLE);
                binding.cancleBtn.setVisibility(GONE);
                binding.payBtn.setVisibility(GONE);
            }
            else {
                binding.orderStatusTv.setText(briefOrder.getOrderStatus());
                binding.orderStatusTv.setVisibility(VISIBLE);
                binding.finishBtn.setVisibility(GONE);
                binding.cancleBtn.setVisibility(GONE);
                binding.payBtn.setVisibility(GONE);
            }
        }
        return this;
    }
}
