package com.yougy.shop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.QueryOrderListCallBack;
import com.yougy.common.protocol.callback.RequireOrderCallBack;
import com.yougy.common.protocol.request.RequirePayOrderRequest;
import com.yougy.common.protocol.response.QueryBookOrderListRep;
import com.yougy.common.protocol.response.RequirePayOrderRep;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.shop.bean.BookIdObj;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.bean.BriefOrder;
import com.yougy.shop.bean.CartItem;
import com.yougy.shop.bean.RemoveRequestObj;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityShopCartBinding;
import com.yougy.ui.activity.databinding.ShopCartFavoriteListBookItemBinding;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.HintDialog;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import rx.functions.Action1;

/**
 * Created by FH on 2017/2/13.
 */

public class ShopCartActivity extends ShopAutoLayoutBaseActivity {
    ActivityShopCartBinding binding;
    //本地数据缓存
    ArrayList<CartItem> cartItemList = new ArrayList<CartItem>();
    //已选条目
    ArrayList<CartItem> checkedCartItemList = new ArrayList<CartItem>();

    private int mTagForNoNet = 1;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(ShopCartActivity.this)
                , R.layout.activity_shop_cart , null , false);
        setContentView(binding.getRoot());
    }

    @Override
    protected void init() {
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadData();
    }

    @Override
    protected void initLayout() {
        binding.mainRecyclerview.setMaxItemNumInOnePage(4);
        binding.mainRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext() , LinearLayoutManager.VERTICAL , false));
        binding.mainRecyclerview.setAdapter(new PageableRecyclerView.Adapter<MyHolder>() {
            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyHolder(DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext())
                        , R.layout.shop_cart_favorite_list_book_item, parent , false));
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                CartItem cartItem = cartItemList.get(position);
                holder.setData(cartItem);
            }

            @Override
            public int getItemCount() {
                return cartItemList.size();
            }
        });
    }

    @Override
    protected void handleEvent() {
        tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof RequirePayOrderRep) {
                    //生成订单的回调
                    RequirePayOrderRep rep = (RequirePayOrderRep) o;
                    if (rep.getCode() == 200) {
                        BriefOrder orderObj = rep.getData().get(0);
                        orderObj.setOrderStatus("待支付");
                        orderObj.setOrderTime(DateUtils.getCalendarAndTimeString());
                        Log.v("FH" , "orderPrice : " + orderObj.getOrderPrice());
                        if(orderObj.getOrderPrice() == 0d){
                            YougyApplicationManager.getRxBus(ShopCartActivity.this).send("refreshOrderList");
                            Intent intent = new Intent(ShopCartActivity.this, PaySuccessActivity.class);
                            intent.putExtra(ShopGloble.ORDER, orderObj);
                            startActivity(intent);
                            //通知主界面刷新
                            BaseEvent baseEvent = new BaseEvent(EventBusConstant.need_refresh, null);
                            EventBus.getDefault().post(baseEvent);
                        }
                        else {
                            orderObj.setBookList(new ArrayList<BookInfo>() {
                                {
                                    for (CartItem cartItem : checkedCartItemList) {
                                        BookInfo bookInfo = new BookInfo();
                                        bookInfo.setBookSalePrice(cartItem.getBookSalePrice());
                                        bookInfo.setBookCoverL(cartItem.getBookCoverS());
                                        bookInfo.setBookTitle(cartItem.getBookTitle());
                                        add(bookInfo);
                                    }
                                }
                            });
                            Intent intent = new Intent(ShopCartActivity.this, ConfirmOrderActivity.class);
                            intent.putExtra(ShopGloble.ORDER, orderObj);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        showCenterDetermineDialog(R.string.get_order_fail);
                    }
                }
                else if (o instanceof QueryBookOrderListRep){
                    if (((QueryBookOrderListRep) o).getCode() == ProtocolId.RET_SUCCESS){
                        Log.v("FH", "查询已支付待支付订单成功 : 未支付订单个数 : " + ((QueryBookOrderListRep) o).getData().size());
                        if (((QueryBookOrderListRep) o).getData().size() > 0) {
                            new HintDialog(ShopCartActivity.this, "您还有未完成的订单,请支付或取消后再生成新的订单").show();
                            return;
                        }
                        RequirePayOrderRequest request = new RequirePayOrderRequest();
                        request.setOrderOwner(SpUtil.getAccountId());
                        for (CartItem cartItem : checkedCartItemList) {
                            request.getData().add(new RequirePayOrderRequest.BookIdObj(cartItem.getBookId()));
                        }
                        ProtocolManager.requirePayOrderProtocol(request, ProtocolId.PROTOCOL_ID_REQUIRE_PAY_ORDER
                                , new RequireOrderCallBack(ShopCartActivity.this, ProtocolId.PROTOCOL_ID_REQUIRE_PAY_ORDER, request));
                    }
                    else {
                        new HintDialog(ShopCartActivity.this, "查询已支付待支付订单失败 : " + ((QueryBookOrderListRep) o).getMsg()).show();
                        Log.v("FH", "查询已支付待支付订单失败 : " + ((QueryBookOrderListRep) o).getMsg());
                    }
                }
            }
        });
        super.handleEvent();
    }

    @Override
    protected void loadData() {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
            return;
        }
        NetWorkManager.queryCart(SpUtil.getAccountId() + "")
                .subscribe(new Action1<List<CartItem>>() {
            @Override
            public void call(List<CartItem> cartItems) {
                cartItemList.clear();
                cartItemList.addAll(cartItems);
                binding.mainRecyclerview.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void refreshView() {
        if (cartItemList.size() == 0) {
            binding.emptyHintLayout.setVisibility(View.VISIBLE);
        } else {
            binding.emptyHintLayout.setVisibility(View.GONE);
        }
    }

    public void onBack(View view){
        onBackPressed();
    }
    public void onDeleteClick(View view){
        if (checkedCartItemList.size() > 0) {
            if (!NetUtils.isNetConnected()) {
                showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
                return;
            }
            new ConfirmDialog(ShopCartActivity.this, "确定要从购物车中删除选中的这" + checkedCartItemList.size() + "本书吗?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NetWorkManager.removeCart(new RemoveRequestObj(SpUtil.getUserId() , new ArrayList<BookIdObj>(){
                        {
                            for (CartItem cartItem : checkedCartItemList) {
                                add(new BookIdObj(cartItem.getBookId()));
                            }
                        }
                    })).subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                            cartItemList.removeAll(checkedCartItemList);
                            checkedCartItemList.clear();
                            binding.mainRecyclerview.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
                }
            }).show();
        }
    }
    public void onCheckout(View view){
        if (checkedCartItemList.size() == 0) {
            showCenterDetermineDialog(R.string.nothing_to_checkout);
        } else {
            requestOrder();
        }
    }
    public void onSelectAllClick(View view){
        boolean setCheck = !binding.selectAllCheckbox.isSelected();
        int currentPageIndex = binding.mainRecyclerview.getCurrentSelectPage() - 1;
        if (currentPageIndex == -1){
            return;
        }
        int firstIndex = currentPageIndex* binding.mainRecyclerview.getMaxItemNumInOnePage();
        int lastIndex = firstIndex + binding.mainRecyclerview.getMaxItemNumInOnePage() - 1;
        for (int i = firstIndex ; i <= lastIndex && i < cartItemList.size() ; i++){
            CartItem cartItem = cartItemList.get(i);
            if (setCheck){
                if (!checkedCartItemList.contains(cartItem)){
                    checkedCartItemList.add(cartItem);
                }
            }
            else {
                checkedCartItemList.remove(cartItem);
            }
        }
        binding.mainRecyclerview.notifyDataSetChanged();
        binding.checkoutBtn.setText("结算(" + checkedCartItemList.size() + ")");
        binding.selectAllCheckbox.setSelected(setCheck);
    }
    private void requestOrder() {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
            return;
        }
        for (CartItem cartItem : checkedCartItemList) {
            if (cartItem.getBookStatus().contains("下架")){
                new HintDialog(ShopCartActivity.this , "您勾选的商品中包含已下架商品,无法为您下单,请删除已下架商品后再试").show();
                return;
            }
        }
        ProtocolManager.queryBookOrderProtocol(String.valueOf(SpUtil.getAccountId())
                , "[\"已支付\",\"待支付\"]"
                , ProtocolId.PROTOCOL_ID_QUERY_BOOK_ORDER
                , new QueryOrderListCallBack(ShopCartActivity.this , ProtocolId.PROTOCOL_ID_QUERY_BOOK_ORDER));
    }

    /**
     * 判断是否本页中所有item都被选中
     *
     * @return 如果所有item都被选中, 返回true, 只要有任意一个没有被选中, 返回false.
     */
    private boolean isAllChecked() {
        int currentPageIndex = binding.mainRecyclerview.getCurrentSelectPage() - 1;
        if (currentPageIndex == -1){
            return false;
        }
        int firstIndex = currentPageIndex* binding.mainRecyclerview.getMaxItemNumInOnePage();
        int lastIndex = firstIndex + binding.mainRecyclerview.getMaxItemNumInOnePage() - 1;
        for (int i = firstIndex ; i <= lastIndex && i < cartItemList.size() ; i++){
            CartItem cartItem = cartItemList.get(i);
            if (!checkedCartItemList.contains(cartItem)){
                return false;
            }
        }
        return true;
    }

    /**
     * 计算已选的bookInfo的总价格
     *
     * @return 总价格
     */
    private double getCheckedBookPriceSum() {
        double sum = 0;
        for (CartItem cartItem : checkedCartItemList) {
            sum = sum + cartItem.getBookSalePrice();
        }
        return sum;
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        if (mUiPromptDialog.getTag() == mTagForNoNet) {
            jumpTonet();
        }
    }

    private class MyHolder extends RecyclerView.ViewHolder{
        ShopCartFavoriteListBookItemBinding itemBinding;
        CartItem cartItem;
        public MyHolder(ShopCartFavoriteListBookItemBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }

        public void setData(CartItem item){
            cartItem = item;
            if (!"删除".equals(itemBinding.shopBookItemBtn.getText())){
                itemBinding.shopBookItemBtn.setText("删除");
                itemBinding.shopBookItemBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ConfirmDialog(ShopCartActivity.this, "确定要从购物车中删除" + cartItem.getBookTitle() + "?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NetWorkManager.removeCart(new RemoveRequestObj(SpUtil.getUserId() , new ArrayList<BookIdObj>(){
                                    {
                                        add(new BookIdObj(cartItem.getBookId()));
                                    }
                                })).subscribe(new Action1<Object>() {
                                    @Override
                                    public void call(Object o) {
                                        cartItemList.remove(cartItem);
                                        checkedCartItemList.remove(cartItem);
                                        binding.mainRecyclerview.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        throwable.printStackTrace();
                                    }
                                });
                            }
                        }).show();
                    }
                });
                itemBinding.centerLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadIntentWithExtra(ShopBookDetailsActivity.class, ShopGloble.BOOK_ID, cartItem.getBookId());
                    }
                });
                itemBinding.shopBookItemCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemBinding.shopBookItemCheckbox.isSelected()){
                            checkedCartItemList.remove(cartItem);
                            binding.selectAllCheckbox.setSelected(false);
                        }
                        else {
                            checkedCartItemList.add(cartItem);
                            if (isAllChecked()){
                                binding.selectAllCheckbox.setSelected(true);
                            }
                        }
                        itemBinding.shopBookItemCheckbox.setSelected(!itemBinding.shopBookItemCheckbox.isSelected());
                        binding.checkoutBtn.setText("结算(" + checkedCartItemList.size() + ")");
                    }
                });
            }
            itemBinding.shopBookItemBookNameTv.setText(cartItem.getBookTitle());
            if (cartItem.getBookStatus().contains("下架")){
                itemBinding.shopBookItemBookNameTv.setText(itemBinding.shopBookItemBookNameTv.getText() + "(下架)");
            }
            itemBinding.shopBookItemBookAuthorTv.setText("作者:" + cartItem.getBookAuthor());
            itemBinding.shopBookItemBookPriceTv.setText("价格:￥" + cartItem.getBookSalePrice());

            itemBinding.shopBookItemCheckbox.setSelected(checkedCartItemList.contains(cartItem));

            refreshImg(itemBinding.shopBookItemBookImg , cartItem.getBookCoverS());
        }
    }

    private void refreshImg(ImageView view, String url) {
        int w = view.getMeasuredWidth();
        int h = view.getMeasuredHeight();
        if (w == 0 || h == 0) {
            //测量控件大小
            view.measure(View.MeasureSpec.makeMeasureSpec(0 , View.MeasureSpec.UNSPECIFIED),View.MeasureSpec.makeMeasureSpec(0 , View.MeasureSpec.UNSPECIFIED));
            w  = view.getMeasuredWidth() ;
            h  = view.getMeasuredHeight() ;
            if (w == 0){
                w = view.getLayoutParams() == null ? 0 : view.getLayoutParams().width;
            }
            if (h == 0){
                h = view.getLayoutParams() == null ? 0 : view.getLayoutParams().height;
            }
        }
        ImageLoaderManager.getInstance().loadImageContext(ShopCartActivity.this,
                url,
                R.drawable.img_book_cover,
                R.drawable.img_book_cover,
                w,
                h,
                view);
    }

}
