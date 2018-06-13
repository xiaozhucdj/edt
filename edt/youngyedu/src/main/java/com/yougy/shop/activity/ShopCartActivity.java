package com.yougy.shop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.shop.CreateOrderRequestObj;
import com.yougy.shop.bean.BookIdObj;
import com.yougy.shop.bean.CartItem;
import com.yougy.shop.bean.Coupon;
import com.yougy.shop.bean.OrderIdObj;
import com.yougy.shop.bean.OrderInfo;
import com.yougy.shop.bean.RemoveRequestObj;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityShopCartBinding;
import com.yougy.ui.activity.databinding.ItemShopCartFavoriteListBinding;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.HintDialog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import rx.functions.Action1;

import static com.yougy.shop.activity.ShopPromotionActivity.COUPON_ID;

/**
 * Created by FH on 2017/2/13.
 */

public class ShopCartActivity extends ShopBaseActivity {
    ActivityShopCartBinding binding;
    //本地数据缓存
    ArrayList<CartItem> cartItemList = new ArrayList<CartItem>();
    HashMap<Integer , ArrayList<CartItem>> sortMap = new HashMap<Integer , ArrayList<CartItem>>();

    //已选条目
    ArrayList<CartItem> checkedCartItemList = new ArrayList<CartItem>();
    private int mTagForNoNet = 1;

    @Override
    protected void setContentView() {
        setContentView(layout());
    }

    @Override
    public void init() {
    }

    @Override
    protected void initLayout() {
        afterBinding();
    }

    public View layout() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(ShopCartActivity.this)
                , R.layout.activity_shop_cart , null , false);
        return binding.getRoot();
    }

    protected void afterBinding() {
        binding.mainRecyclerview.setMaxItemNumInOnePage(4);
        binding.mainRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext() , LinearLayoutManager.VERTICAL , false));
        binding.mainRecyclerview.setAdapter(new PageableRecyclerView.Adapter<MyHolder>() {
            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyHolder(DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext())
                        , R.layout.item_shop_cart_favorite_list, parent , false));
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                CartItem cartItem;
                int finalposition = position;
                Iterator<Integer> iterator = sortMap.keySet().iterator();
                int key = iterator.next();
                ArrayList<CartItem> sortCartItemList = sortMap.get(key);
                while(true){
                    if (finalposition < sortCartItemList.size()){
                        cartItem = sortCartItemList.get(finalposition);
                        if (finalposition != 0){
                            sortCartItemList = null;
                        }
                        break;
                    }
                    else {
                        finalposition = finalposition - sortCartItemList.size();
                        key = iterator.next();
                        sortCartItemList = sortMap.get(key);
                    }
                }
                holder.setData(cartItem , sortCartItemList);
            }

            @Override
            public int getItemCount() {
                return cartItemList.size();
            }

            @Override
            public void onSelectPageChanged(int changedPageIndex) {
                refreshBottomBar();
            }
        });
    }

    @Override
    public void loadData() {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
            return;
        }
        NetWorkManager.queryCart(String.valueOf(SpUtils.getUserId()))
                .subscribe(new Action1<List<CartItem>>() {
                    @Override
                    public void call(List<CartItem> cartItems) {
                        cartItemList.clear();
                        cartItemList.addAll(cartItems);
                        sortCartItem();
                        refreshView();
                    }
                });
    }

    protected void refreshView() {
        if (cartItemList.size() == 0) {
            binding.emptyHintLayout.setVisibility(View.VISIBLE);
        } else {
            binding.emptyHintLayout.setVisibility(View.GONE);
            binding.mainRecyclerview.notifyDataSetChanged();
            refreshBottomBar();
        }
    }

    /**
     * 判断是否本页中所有item都被选中
     *
     * @return 如果所有item都被选中, 返回true, 只要有任意一个没有被选中, 返回false.
     */
    private boolean isAllSelected() {
        int currentPageIndex = binding.mainRecyclerview.getCurrentSelectPageIndex();
        if (currentPageIndex == -1){
            return false;
        }
        int firstIndex = currentPageIndex* binding.mainRecyclerview.getMaxItemNumInOnePage();
        int lastIndex = firstIndex + binding.mainRecyclerview.getMaxItemNumInOnePage() - 1;
        for (int i = firstIndex ; i <= lastIndex && i < cartItemList.size() ; i++){
            CartItem cartItem;
            int finalposition = i;
            Iterator<Integer> iterator = sortMap.keySet().iterator();
            int key = iterator.next();
            ArrayList<CartItem> sortCartItemList = sortMap.get(key);
            while(true){
                if (finalposition < sortCartItemList.size()){
                    cartItem = sortCartItemList.get(finalposition);
                    break;
                }
                else {
                    finalposition = finalposition - sortCartItemList.size();
                    key = iterator.next();
                    sortCartItemList = sortMap.get(key);
                }
            }

            if (!checkedCartItemList.contains(cartItem)){
                return false;
            }
        }
        return true;
    }

    private void requestOrder() {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
            return;
        }
        for (CartItem cartItem : checkedCartItemList) {
            if (cartItem.bookStatus.contains("下架")) {
                new HintDialog(getThisActivity(), "您勾选的商品中包含已下架商品,无法为您下单,请删除已下架商品后再试").show();
                return;
            }
        }
        //新建订单
        NetWorkManager.createOrder(new CreateOrderRequestObj(SpUtils.getUserId(), new ArrayList<BookIdObj>() {
            {
                for (CartItem cartItem : checkedCartItemList) {
                    add(new BookIdObj(cartItem.bookId));
                }
            }
        })).subscribe(new Action1<List<OrderIdObj>>() {
                    @Override
                    public void call(List<OrderIdObj> orders) {
                        OrderIdObj orderIdObj = orders.get(0);
                        Intent intent = new Intent(getApplicationContext(), OrderDetailActivity.class);
                        intent.putExtra("orderId", orderIdObj.getOrderId());
                        startActivity(intent);
                        finish();
                        SpUtils.newOrderCountPlusOne();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showCenterDetermineDialog(R.string.get_order_fail);
                        LogUtils.e("FH", "生成订单失败");
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        if (mUiPromptDialog.getTag() == mTagForNoNet) {
            jumpTonet();
        }
    }
    public void sortCartItem(){
        sortMap.clear();
        ArrayList<CartItem> noCouponList = new ArrayList<CartItem>();
        sortMap.put(-1 , noCouponList);
        for1:for (CartItem cartItem : cartItemList){
            Coupon bookCouponBean = findManjianCoupon(cartItem);
            if (bookCouponBean != null){
                ArrayList<CartItem> sortedCartItemList = sortMap.get(bookCouponBean.getCouponId());
                if (sortedCartItemList == null){
                    sortedCartItemList = new ArrayList<CartItem>();
                    sortMap.put(bookCouponBean.getCouponId() , sortedCartItemList);
                }
                sortedCartItemList.add(cartItem);
            }
            else {
                noCouponList.add(cartItem);
            }
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
                    NetWorkManager.removeCart(new RemoveRequestObj(SpUtils.getUserId() , new ArrayList<BookIdObj>(){
                        {
                            for (CartItem cartItem : checkedCartItemList) {
                                add(new BookIdObj(cartItem.bookId));
                            }
                        }
                    })).subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                            cartItemList.removeAll(checkedCartItemList);
                            checkedCartItemList.clear();
                            sortCartItem();
                            refreshView();
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
        int currentPageIndex = binding.mainRecyclerview.getCurrentSelectPageIndex();
        if (currentPageIndex == -1){
            return;
        }
        int firstIndex = currentPageIndex* binding.mainRecyclerview.getMaxItemNumInOnePage();
        int lastIndex = firstIndex + binding.mainRecyclerview.getMaxItemNumInOnePage() - 1;
        for (int i = firstIndex ; i <= lastIndex && i < cartItemList.size() ; i++){
            CartItem cartItem;
            int finalposition = i;
            Iterator<Integer> iterator = sortMap.keySet().iterator();
            int key = iterator.next();
            ArrayList<CartItem> sortCartItemList = sortMap.get(key);
            while(true){
                if (finalposition < sortCartItemList.size()){
                    cartItem = sortCartItemList.get(finalposition);
                    break;
                }
                else {
                    finalposition = finalposition - sortCartItemList.size();
                    key = iterator.next();
                    sortCartItemList = sortMap.get(key);
                }
            }

            if (setCheck){
                if (!checkedCartItemList.contains(cartItem)){
                    checkedCartItemList.add(cartItem);
                }
            }
            else {
                checkedCartItemList.remove(cartItem);
            }
        }
        refreshView();
    }
    private void refreshImg(ImageView view, String url) {
/*        int w = view.getMeasuredWidth();
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
        }*/
        ImageLoaderManager.getInstance().loadImageContext(ShopCartActivity.this,
                url,
                R.drawable.img_book_cover,
                R.drawable.img_book_cover,
                FileContonst.withS ,
                FileContonst.heightS,
                view);
    }

    private class MyHolder extends RecyclerView.ViewHolder{
        ItemShopCartFavoriteListBinding itemBinding;
        CartItem cartItem;
        public MyHolder(ItemShopCartFavoriteListBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }

        public void setData(CartItem item , ArrayList<CartItem> sortCartItemList){
            cartItem = item;
            if (!"删除".equals(itemBinding.shopBookItemBtn.getText())){
                itemBinding.shopBookItemBtn.setText("删除");
                itemBinding.shopBookItemBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ConfirmDialog(ShopCartActivity.this, "确定要从购物车中删除" + cartItem.bookTitle + "?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NetWorkManager.removeCart(new RemoveRequestObj(SpUtils.getUserId() , new ArrayList<BookIdObj>(){
                                    {
                                        add(new BookIdObj(cartItem.bookId));
                                    }
                                })).subscribe(new Action1<Object>() {
                                    @Override
                                    public void call(Object o) {
                                        cartItemList.remove(cartItem);
                                        checkedCartItemList.remove(cartItem);
                                        sortCartItem();
                                        refreshView();
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
                        Intent intent = new Intent(getApplicationContext() , ShopBookDetailsActivity.class);
                        intent.putExtra(ShopGloble.BOOK_ID , cartItem.bookId);
                        startActivity(intent);
                    }
                });
                itemBinding.shopBookItemCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemBinding.shopBookItemCheckbox.isSelected()){
                            checkedCartItemList.remove(cartItem);
                        }
                        else {
                            checkedCartItemList.add(cartItem);
                        }
                        refreshView();
                    }
                });
                itemBinding.coudanBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext() , ShopPromotionActivity.class);
                        intent.putExtra(COUPON_ID , findManjianCoupon(cartItem).getCouponId());
                        startActivity(intent);
                    }
                });
            }
            itemBinding.shopBookItemBookNameTv.setText(cartItem.bookTitle);
            if (cartItem.bookStatus.contains("下架")){
                itemBinding.shopBookItemBookNameTv.setText(itemBinding.shopBookItemBookNameTv.getText() + "(下架)");
            }
            itemBinding.shopBookItemBookAuthorTv.setText("作者:" + cartItem.bookAuthor);
            itemBinding.shopBookItemBookPriceTv.setText("价格:￥" + cartItem.bookSalePrice);

            itemBinding.shopBookItemCheckbox.setSelected(checkedCartItemList.contains(cartItem));

            refreshImg(itemBinding.shopBookItemBookImg , cartItem.bookCoverS);

            boolean hasManjian = false;
            for (Coupon bookCouponBean : cartItem.bookCoupon){
                switch (bookCouponBean.getCouponTypeCode()){
                    case "BO01":
                        String text = "价格:￥" + cartItem.getBookSpotPrice()
                                + "        ￥" + cartItem.bookSalePrice + "\n\n限时折扣";
                        StringBuilder sb = new StringBuilder(text);
                        SpannableString ss = new SpannableString(sb);
                        ss.setSpan(new StrikethroughSpan() , text.lastIndexOf('￥') , text.indexOf('\n') , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        itemBinding.shopBookItemBookPriceTv.setText(ss);
                        break;
                    case "BO02":
                        itemBinding.shopBookItemBookPriceTv.setText("价格:￥0\n\n限时免费");
                        break;
                    case "BO03":
                    case "BO04":
                        hasManjian = true;
                        itemBinding.promotionNameTv.setVisibility(View.VISIBLE);
                        itemBinding.promotionContentTv.setVisibility(View.VISIBLE);
                        itemBinding.noPromotionTv.setVisibility(View.GONE);
                        itemBinding.promotionNameTv.setText("满减");
                        Coupon.CouponContentBean couponContentBean = bookCouponBean.getCouponContent().get(0);
                        itemBinding.promotionContentTv.setText("满" + couponContentBean.getOver()
                                + "元减" + couponContentBean.getCut() +"元");
                        break;
                }
            }
            if (!hasManjian){
                itemBinding.promotionNameTv.setVisibility(View.GONE);
                itemBinding.promotionContentTv.setVisibility(View.GONE);
                itemBinding.noPromotionTv.setVisibility(View.VISIBLE);
            }
            if (sortCartItemList != null){
                itemBinding.promotionLayout.setVisibility(View.VISIBLE);
                double[] result = calculatePromotionPrice(sortCartItemList);
                if (result[1] == -1){
                    itemBinding.coudanBtn.setVisibility(View.GONE);
                    itemBinding.promotionPriceSumTv.setVisibility(View.GONE);
                }
                else {
                    itemBinding.promotionPriceSumTv.setVisibility(View.VISIBLE);
                    if (result[1] == 0){
                        itemBinding.coudanBtn.setVisibility(View.VISIBLE);
                        itemBinding.promotionPriceSumTv.setText("共计:￥" + result[0] + "元");
                    }
                    else {
                        itemBinding.coudanBtn.setVisibility(View.GONE);
                        itemBinding.promotionPriceSumTv.setText("共计:￥"
                                + result[2] + "元,已优惠￥" +
                                result[1] + "元");
                    }
                }
            }
            else {
                itemBinding.promotionLayout.setVisibility(View.GONE);
            }
        }
    }
    /**
     *
     * @param sortedCartItemList
     * @return 返回值是double[3]数组,double[0]表示这个cartItemList中总价
     * ,double[1]表示这个cartItemList中总可以减去的优惠钱数(double[1]可以为-1,代表没有满减活动,而为0代表有满减活动但是没有符合能够满减的金额)
     * ,double[2]表示这个cartItemList中优惠过的总价
     */
    private double[] calculatePromotionPrice(ArrayList<CartItem> sortedCartItemList){
        double[] result = new double[3];
        double totalPrice = 0;
        for (CartItem cartItem : sortedCartItemList) {
            if (checkedCartItemList.contains(cartItem)){
                totalPrice = totalPrice + cartItem.getBookSpotPrice();
            }
        }
        result[0] = totalPrice;
        CartItem firstCartItem;
        if (sortedCartItemList.size() != 0){
            firstCartItem= sortedCartItemList.get(0);
            Coupon bookCouponBean = findManjianCoupon(firstCartItem);
            if (bookCouponBean != null){
                double overPrice = Double.valueOf(bookCouponBean.getCouponContent().get(0).getOver());
                if (totalPrice >= overPrice){
                    result[1] = Double.valueOf(bookCouponBean.getCouponContent().get(0).getCut());
                    result[2] = result[0] - result[1];
                }
                else {
                    result[1] = 0;
                    result[2] = totalPrice;
                }
            }
            else {
                result[1] = -1;
                result[2] = totalPrice;
            }
        }
        else {
            result[1] = -1;
            result[2] = totalPrice;
        }
        result[0] = formatDouble(result[0] , 2);
        result[1] = formatDouble(result[1] , 2);
        result[2] = formatDouble(result[2] , 2);
        return result;
    }

    public void refreshBottomBar(){
        binding.selectAllCheckbox.setSelected(isAllSelected());
        binding.checkoutBtn.setText("结算(" + checkedCartItemList.size() + ")");
        Iterator<Integer> iterator = sortMap.keySet().iterator();
        int key;
        double totalPrice = 0 , totalCut = 0 , finalTotalPrice = 0;
        ArrayList<CartItem> sortCartItemList;
        while(iterator.hasNext()){
            key = iterator.next();
            sortCartItemList = sortMap.get(key);
            double[] result = calculatePromotionPrice(sortCartItemList);
            totalPrice = totalPrice + result[0];
            if (result[1] > 0){
                totalCut = totalCut + result[1];
            }
            finalTotalPrice = finalTotalPrice + result[2];
        }
        finalTotalPrice = formatDouble(finalTotalPrice , 2);
        totalPrice = formatDouble(totalPrice , 2);
        totalCut = formatDouble(totalCut , 2);

        binding.totalPriceTv.setText("总计:￥" + finalTotalPrice);
        binding.discountTv.setText("总额:￥" + totalPrice + "    已享满减:￥" + totalCut);
    }

    public double formatDouble(double origin , int scale){
        return new BigDecimal(origin).setScale(scale , RoundingMode.HALF_UP).doubleValue();
    }

    public Coupon findManjianCoupon(CartItem cartItem){
        Coupon bookCouponBean = null;
        if (cartItem.bookCoupon != null){
            for (Coupon tempBookCouponBean : cartItem.bookCoupon) {
                if (tempBookCouponBean.getCouponTypeCode().equals("BO03")|| tempBookCouponBean.getCouponTypeCode().equals("BO04")){
                    bookCouponBean = tempBookCouponBean;
                    break;
                }
            }
        }
        return bookCouponBean;
    }
}
