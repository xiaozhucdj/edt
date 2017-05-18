package com.yougy.shop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.QueryBookCartCallBack;
import com.yougy.common.protocol.callback.RemoveBookCartCallBack;
import com.yougy.common.protocol.request.RemoveBookCartRequest;
import com.yougy.common.protocol.response.QueryBookCartProtocol;
import com.yougy.common.protocol.response.RemoveBookCartProtocol;
import com.yougy.common.utils.SpUtil;
import com.yougy.home.bean.DataBookBean;
import com.yougy.init.bean.BookInfo;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.view.NewShopBookItem;
import com.yougy.view.dialog.ConfirmDialog;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by FH on 2017/2/13.
 */

public class NewShopCartActivity extends ShopAutoLayoutBaseActivity implements View.OnClickListener, NewShopBookItem.OnItemActionListener {
    //本地数据缓存
    ArrayList<BookInfo> bookInfoList = new ArrayList<BookInfo>();
    //已选条目
    ArrayList<BookInfo> checkedBookList = new ArrayList<BookInfo>();
    //每次最多展示的页数
    final int ONCE_SHOW_PAGE_NUM = 5;
    //每一页的条数
    final int ITEM_NUM = 4;

    //当前展示的第一页页号(从0开始)
    int currentShowFirstPageIndex = 0;
    //当前选定的的页码序号(从0开始)
    int currentSelectedPageIndex= 0;

    @BindView(R.id.shop_cart_back_btn)
    ImageView backBtn;//后退按钮

    @BindView(R.id.shop_cart_delete_btn)
    ImageView deleteBtn;//批量删除按钮

    @BindView(R.id.shop_cart_select_all_checkbox)
    ImageButton selectAllCheckbox;//全选

    @BindView(R.id.shop_cart_page_btn_container)
    LinearLayout pageBtnContainer;//页码按钮容器

    @BindView(R.id.shop_cart_item_container)
    LinearLayout itemContainer;//条目容器

    @BindView(R.id.shop_cart_empty_hint_layout)
    LinearLayout emptyHintLayout;//购物车没有东西的提示层

    @BindView(R.id.shop_cart_checkout_btn)
    Button checkoutBtn;//结算按钮

    @BindView(R.id.shop_cart_sum_textview)
    TextView sumTextview;//金额共计文本

    //条目item的引用
    ArrayList<NewShopBookItem> bookItems = new ArrayList<NewShopBookItem>();

    //是否需要刷新items
    boolean needRefreshItems = false;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_new_shop_cart);
    }

    @Override
    protected void init() {
        //初始化假数据
        ProtocolManager.initSimulateData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ProtocolManager.fake_queryBookCartProtocol(SpUtil.getAccountId()
                , ProtocolId.PROTOCOL_ID_QUERY_BOOK_CART
                , new QueryBookCartCallBack(NewShopCartActivity.this , ProtocolId.PROTOCOL_ID_QUERY_BOOK_CART));
    }

    @Override
    protected void initLayout() {
        //初始化条目item,添加至容器,并且保存一份引用
        for (int i = 0; i < ITEM_NUM; i++) {
            NewShopBookItem item = new NewShopBookItem(this);
            item.setOnItemActionListener(this);
            item.setBtnText("    " + getResources().getString(R.string.delete) + "    ");
            itemContainer.addView(item);
            bookItems.add(item);
        }
    }

    @Override
    protected void handleEvent() {
        tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof QueryBookCartProtocol) {
                    //获取购物车列表的回调在这
                    QueryBookCartProtocol protocol = (QueryBookCartProtocol) o;
                    if (protocol.getData() != null && protocol.getData().get(0) != null
                            && protocol.getData().get(0).getBookList() != null
                            ) {
                        bookInfoList.clear();
                        bookInfoList.addAll(protocol.getData().get(0).getBookList());
                    }
                    //如果已选中的某些项在新的数据中不存在,则删除它们
                    for (int i = 0 ; i < checkedBookList.size() ; ) {
                        BookInfo checkedInfo = checkedBookList.get(i);
                        if (findBookByID(bookInfoList , checkedInfo.getBookId()) == null){
                            checkedBookList.remove(checkedInfo);
                            continue;
                        }
                        i++;
                    }
                    //如果之前选中的页号在新的数据中已经不存在了,则把选中的页号确定为最后一页.
                    if (bookInfoList.size() <= currentSelectedPageIndex * ITEM_NUM){
                        currentSelectedPageIndex = (bookInfoList.size() - 1) / ITEM_NUM;
                        if (currentSelectedPageIndex < 0){
                            currentSelectedPageIndex = 0;
                        }
                    }
                    //此处得到新的显示第一页的页号,如(11/5*5 = 10)
                    currentShowFirstPageIndex = currentSelectedPageIndex / ONCE_SHOW_PAGE_NUM * ONCE_SHOW_PAGE_NUM;
                    //请求刷新items和下方合计栏.
                    needRefreshItems = true;
                    refreshViewSafe();
                }
            }
        });
        tapEventEmitter.observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof RemoveBookCartProtocol){
                    //删除购物车的回调在这
                    RemoveBookCartProtocol protocal = (RemoveBookCartProtocol) o;
                    if (protocal.getCode() == 200) {
                        ProtocolManager.fake_queryBookCartProtocol(SpUtil.getAccountId()
                                , ProtocolId.PROTOCOL_ID_QUERY_BOOK_CART
                                , new QueryBookCartCallBack(NewShopCartActivity.this , ProtocolId.PROTOCOL_ID_QUERY_BOOK_CART));
                    }
                }
            }
        });
        super.handleEvent();
    }

    @Override
    protected void loadData() {
        ProtocolManager.fake_queryBookCartProtocol(SpUtil.getAccountId()
                , ProtocolId.PROTOCOL_ID_QUERY_BOOK_CART
                , new QueryBookCartCallBack(NewShopCartActivity.this , ProtocolId.PROTOCOL_ID_QUERY_BOOK_CART));
    }


    /**
     * 操作BookInfo列表的方法,在列表中查找拥有指定bookID的BookInfo
     * @param bookInfoList 要查找的BookInfo列表
     * @param bookID 指定的bookID
     * @return 如果找到,则返回该BookInfo,如果有多个,返回第一个,如果没有,返回null.
     */
    public BookInfo findBookByID(ArrayList<BookInfo> bookInfoList, int bookID) {
        for (BookInfo bookInfo : bookInfoList) {
            if (bookInfo.getBookId() == bookID) {
                return bookInfo;
            }
        }
        return null;
    }
    /**
     * 操作BookInfo列表的方法,在指定的BookInfo列表中删除拥有给定的bookInfo列表中bookInfo的项
     * @param fromBookInfoList 在其中删除的BookInfo列表
     * @param toRemoveBookInfoList 指定的bookInfo列表
     * @return 返回删除后的BookInfo列表
     */
    public void removeBooksByID(ArrayList<BookInfo> fromBookInfoList, ArrayList<BookInfo> toRemoveBookInfoList) {
        for (BookInfo toRemoveBookInfo : toRemoveBookInfoList) {
            BookInfo bookInfo = findBookByID(fromBookInfoList, toRemoveBookInfo.getBookId());
            if (bookInfo != null) {
                fromBookInfoList.remove(bookInfo);
            }
        }
    }

    @Override
    protected void refreshView() {
        pageBtnContainer.removeAllViews();
        if (bookInfoList.size() == 0) {
            //隐藏所有之前的item,并且显示没有商品的提示
            fillItems(0, -1);
            emptyHintLayout.setVisibility(View.VISIBLE);
        } else {
            emptyHintLayout.setVisibility(View.GONE);
            //本次显示的最大的页码(从1开始)
            int showPageNumEnd;
            //本次能展示的最后一本书的序号(从0开始)
            int lastShowBookIndex;
            //是否显示<<向前按钮
            boolean showForward = (currentShowFirstPageIndex == 0 ? false : true);
            //是否显示>>向后按钮
            boolean showNext;
            if (bookInfoList.size() <= (currentShowFirstPageIndex + ONCE_SHOW_PAGE_NUM) * ITEM_NUM){
                lastShowBookIndex = bookInfoList.size() - 1;
                showNext = false;
            }
            else {
                lastShowBookIndex = (currentShowFirstPageIndex + ONCE_SHOW_PAGE_NUM) * ITEM_NUM - 1;
                showNext = true;
            }
            showPageNumEnd = lastShowBookIndex / ITEM_NUM + 1;
            addBtns(currentShowFirstPageIndex + 1, showPageNumEnd , showForward, showNext);
            //如果需要刷新,则刷新items和下方合计按钮文字
            if (needRefreshItems){
                toPage(currentSelectedPageIndex);
                sumTextview.setText("合计 : ￥" + getCheckedBookPriceSum());
                checkoutBtn.setText("结算(" + checkedBookList.size() + ")");
                needRefreshItems = false;
            }
            //如果当前显示的页码按钮在页码容器中,则高亮显示
            TextView selectedBtn = (TextView) pageBtnContainer.findViewWithTag((currentSelectedPageIndex + 1));
            if (selectedBtn != null) {
                selectedBtn.setSelected(true);
            }
        }
    }

    /**
     * 根据当前显示的bookInfo信息填充items
     * @param startIndex 要显示的多个bookInfo在本地缓存中的开始位置
     * @param endIndex 要显示的多个bookInfo在本地缓存中的结束位置
     */
    private void fillItems(int startIndex, int endIndex) {
        boolean allCheck = true;
        for (int i = startIndex, j = 0; j < bookItems.size(); i++, j++) {
            if (i <= endIndex) {
                BookInfo bookInfo = bookInfoList.get(i);
                bookItems.get(j).setBookInfo(bookInfo);
                boolean checked = (findBookByID(checkedBookList, bookInfo.getBookId()) == null) ? false : true;
                if (!checked) {
                    allCheck = false;
                }
                bookItems.get(j).setChecked(checked, false);
                bookItems.get(j).setPosition(i);
                //隐藏最后一个Item的下分割线
                if (i == endIndex) {
                    bookItems.get(j).setIsLast(true);
                } else {
                    bookItems.get(j).setIsLast(false);
                }
            } else {
                bookItems.get(j).setBookInfo(null);
            }
        }
        //设置全选按钮的选中状态
        selectAllCheckbox.setSelected(allCheck);
    }

    @OnClick({R.id.shop_cart_back_btn, R.id.shop_cart_delete_btn,
            R.id.shop_cart_select_all_checkbox, R.id.shop_cart_checkout_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shop_cart_back_btn:
                finish();
                break;
            case R.id.shop_cart_delete_btn:
                if (checkedBookList.size() > 0){
                    new ConfirmDialog(this, "确实要删除这" + checkedBookList.size() +  "本书吗?" , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RemoveBookCartRequest request = new RemoveBookCartRequest();
                            request.setUserId(SpUtil.getAccountId());
                            request.setCount(1);
                            ArrayList<DataBookBean> dataList = new ArrayList<DataBookBean>();
                            DataBookBean bean = new DataBookBean();
                            bean.setCount(checkedBookList.size());
                            bean.setBookList(checkedBookList);
                            dataList.add(bean);
                            request.setData(dataList);
                            ProtocolManager.fake_removeBookCartProtocol(request
                                    , ProtocolId.PROTOCOL_ID_REMOVE_BOOK_CART
                                    , new RemoveBookCartCallBack(NewShopCartActivity.this , ProtocolId.PROTOCOL_ID_REMOVE_BOOK_CART , request));
                            dialog.dismiss();
                        }
                    }).show();
                }
                break;
            case R.id.shop_cart_select_all_checkbox:
                boolean setCheck = !selectAllCheckbox.isSelected();
                for (NewShopBookItem everyItem : bookItems) {
                    everyItem.setChecked(setCheck, true);
                }
                break;
            case R.id.shop_cart_checkout_btn:
                if (checkedBookList.size() == 0){
                    showToastSafe(R.string.nothing_to_checkout , Toast.LENGTH_SHORT);
                }
                else {
                    Bundle extra = new Bundle();
                    extra.putParcelableArrayList("orderBookList" , checkedBookList);
                    loadIntentWithExtras(ConfirmOrderActivity.class , extra);
                }
                break;
            case R.id.shop_page_page_btn:
                int i = (int) view.getTag();
                switch (i) {
                    case -1://向前
                        currentShowFirstPageIndex = currentShowFirstPageIndex - ONCE_SHOW_PAGE_NUM;
                        refreshViewSafe();
                        break;
                    case -2://向后
                        currentShowFirstPageIndex = currentShowFirstPageIndex + ONCE_SHOW_PAGE_NUM;
                        refreshViewSafe();
                        break;
                    default:
                        TextView btn = (TextView) pageBtnContainer.findViewWithTag(currentSelectedPageIndex + 1);
                        if (btn != null) {
                            btn.setSelected(false);
                        }
                        btn = (TextView) pageBtnContainer.findViewWithTag(i);
                        if (btn != null){
                            btn.setSelected(true);
                        }
                        currentSelectedPageIndex = i - 1;
                        toPage(i - 1);
                        break;

                }
                break;
        }
    }

    /**
     * 判断是否本页中所有item都被选中
     * @return 如果所有item都被选中,返回true,只要有任意一个没有被选中,返回false.
     */
    private boolean isAllChecked() {
        boolean allChecked = true;
        for (NewShopBookItem item : bookItems) {
            if (!item.isChecked()) {
                allChecked = false;
                break;
            }
        }
        return allChecked;
    }

    /**
     * 切换到制定的页(只更新items)
     * @param pageIndex 制定的页序号
     */
    private void toPage(int pageIndex) {
        if ((pageIndex + 1) * bookItems.size() < bookInfoList.size()) {
            fillItems(pageIndex * bookItems.size(), (pageIndex + 1) * bookItems.size() - 1);
        } else {
            fillItems(pageIndex * bookItems.size(), bookInfoList.size() - 1);
        }
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
        pageBtnContainer.addView(pageBtn, params);
    }

    /**
     * 计算已选的bookInfo的总价格
     * @return 总价格
     */
    private float getCheckedBookPriceSum(){
        float sum = 0;
        for (BookInfo bookInfo : checkedBookList) {
            sum = sum + bookInfo.getBookSalePrice();
        }
        return sum;
    }

    /**
     * item被点击的回调
     * @param position 被点击的item的数据在本地缓存中的位置.
     */
    @Override
    public void onItemClick(int position) {
        Bundle extra = new Bundle();
        extra.putParcelable(ShopGloble.JUMP_BOOK_KEY, bookInfoList.get(position));
        loadIntentWithExtras(NewBookItemDetailsActivity.class, extra);
    }


    /**
     * item中的勾选框被选中或取消时的回调
     * @param position 被点击的item的数据在本地缓存中的位置.
     * @param checked true时被选中,false时被取消
     */

    @Override
    public void onCheckedChanged(int position, boolean checked) {
        BookInfo bookInfo = bookInfoList.get(position);
        if (checked) {
            checkedBookList.add(bookInfo);
        } else {
            ArrayList<BookInfo> toRemoveList = new ArrayList<BookInfo>();
            toRemoveList.add(bookInfo);
            removeBooksByID(checkedBookList, toRemoveList);
        }
        selectAllCheckbox.setSelected(checked && isAllChecked());
        sumTextview.setText("合计 : ￥" + getCheckedBookPriceSum());
        checkoutBtn.setText("结算(" + checkedBookList.size() + ")");
    }

    /**
     * item中的按钮被点击时的回调
     * @param position 被点击的item的数据在本地缓存中的位置.
     */
    @Override
    public void onBtnClick(final int position) {
        new ConfirmDialog(this, "确定要删除这本书吗?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RemoveBookCartRequest request = new RemoveBookCartRequest();
                request.setUserId(SpUtil.getAccountId());
                request.setCount(1);
                ArrayList<DataBookBean> dataList = new ArrayList<DataBookBean>();
                DataBookBean bean = new DataBookBean();
                bean.setCount(1);
                bean.setBookList(new ArrayList<BookInfo>(bookInfoList.subList(position , position + 1)));
                dataList.add(bean);
                request.setData(dataList);
                ProtocolManager.fake_removeBookCartProtocol(request
                        , ProtocolId.PROTOCOL_ID_REMOVE_BOOK_CART
                        , new RemoveBookCartCallBack(NewShopCartActivity.this , ProtocolId.PROTOCOL_ID_REMOVE_BOOK_CART , request));
                dialog.dismiss();
            }
        }).show();
    }
}
