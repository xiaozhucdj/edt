package com.yougy.shop.activity;

import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.QueryBookFavorCallback;
import com.yougy.common.protocol.callback.RemoveBookFavorCallBack;
import com.yougy.common.protocol.request.RemoveBookFavorRequest;
import com.yougy.common.protocol.response.QueryBookFavorRep;
import com.yougy.common.protocol.response.RemoveBookFavorProtocol;
import com.yougy.common.utils.SpUtil;
import com.yougy.shop.bean.Favor;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.view.NewShopBookItem;
import com.yougy.view.dialog.ConfirmDialog;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by FH on 2017/2/13.
 */

public class ShopFavoriteActivity extends ShopAutoLayoutBaseActivity implements View.OnClickListener , NewShopBookItem.OnItemActionListener {
    //本地数据缓存
    ArrayList<Favor> favorList = new ArrayList<Favor>();
    //已选条目
    ArrayList<Favor> checkedFavorList = new ArrayList<Favor>();
    //每次最多展示的页数
    final int ONCE_SHOW_PAGE_NUM = 5;
    //每一页的条数
    final int ITEM_NUM = 4;

    //当前展示的第一页页号(从0开始)
    int currentShowFirstPageIndex = 0;
    //当前选定的的页码序号(从0开始)
    int currentSelectedPageIndex= 0;


    @BindView(R.id.shop_favorite_back_btn)
    ImageView backBtn;//后退按钮

    @BindView(R.id.shop_favorite_delete_btn)
    ImageView deleteBtn;//全部删除按钮

    @BindView(R.id.shop_favorite_select_all_checkbox)
    ImageButton selectAllCheckbox;//全选按钮

    @BindView(R.id.shop_favorite_page_btn_container)
    LinearLayout pageBtnContainer;//页码按钮容器

    @BindView(R.id.shop_favorite_item_container)
    LinearLayout itemContainer;//条目容器

    @BindView(R.id.shop_favorite_empty_layout)
    LinearLayout emptyLayout; //没有收藏的提示层

    //条目item的引用
    ArrayList<NewShopBookItem> bookItems = new ArrayList<NewShopBookItem>();

    //是否需要刷新items
    boolean needRefreshItems = false;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_shop_favorite);
    }

    @Override
    protected void init() {
        //初始化假数据
        ProtocolManager.initSimulateData();
    }

    @Override
    protected void initLayout() {
        //初始化条目item,添加至容器,并且保存一份引用
        for (int i = 0; i < ITEM_NUM; i++) {
            NewShopBookItem item = new NewShopBookItem(this);
            item.setOnItemActionListener(this);
            item.setBtnText(getResources().getString(R.string.delete_favorite));
            itemContainer.addView(item);
            bookItems.add(item);
        }
    }

    @Override
    protected void handleEvent() {
        tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                Log.v("FH" , "-----------------" + o);
                if (o instanceof QueryBookFavorRep) {
                    //获取收藏列表的回调在这
                    QueryBookFavorRep protocol = (QueryBookFavorRep) o;
                    favorList.clear();
                    if (protocol.getData() != null) {
                        favorList.addAll(protocol.getData());
                    }
                    //如果已选中的某些项在新的数据中不存在,则删除它们
                    for (int i = 0 ; i < checkedFavorList.size() ; ) {
                        Favor favor = checkedFavorList.get(i);
                        if (findFavorByID(favorList, favor.getBookId()) == null){
                            checkedFavorList.remove(favor);
                            continue;
                        }
                        i++;
                    }
                    //如果之前选中的页号在新的数据中已经不存在了,则把选中的页号确定为最后一页.
                    if (favorList.size() <= currentSelectedPageIndex * ITEM_NUM){
                        currentSelectedPageIndex = (favorList.size() - 1) / ITEM_NUM;
                        if (currentSelectedPageIndex < 0){
                            currentSelectedPageIndex = 0;
                        }
                    }
                    //此处得到新的显示第一页的页号,如(11/5*5 = 10)
                    currentShowFirstPageIndex = currentSelectedPageIndex / ONCE_SHOW_PAGE_NUM * ONCE_SHOW_PAGE_NUM;
                    //请求刷新items
                    needRefreshItems = true;
                    refreshViewSafe();
                    if (favorList.size() == 0){
                        deleteBtn.setVisibility(View.INVISIBLE);
                    }
                    else {
                        deleteBtn.setVisibility(View.VISIBLE);
                    }
                }
                else if (o instanceof RemoveBookFavorProtocol){
                    //删除收藏的回调在这
                    RemoveBookFavorProtocol protocal = (RemoveBookFavorProtocol) o;
                    if (protocal.getCode() == 200) {
                        Log.v("FH" , "---------------------删除成功");
                        ProtocolManager.queryBookFavorProtocol(SpUtil.getAccountId()
                                , ProtocolId.PROTOCOL_ID_QUERY_BOOK_FAVOR
                                , new QueryBookFavorCallback(ShopFavoriteActivity.this , ProtocolId.PROTOCOL_ID_QUERY_BOOK_FAVOR));
                    }
                    else {
                        Log.v("FH" , "---------------------删除失败");
                    }
                }
            }
        });
        super.handleEvent();
    }

    @Override
    protected void loadData() {
        ProtocolManager.queryBookFavorProtocol(SpUtil.getAccountId()
                , ProtocolId.PROTOCOL_ID_QUERY_BOOK_FAVOR
                , new QueryBookFavorCallback(ShopFavoriteActivity.this , ProtocolId.PROTOCOL_ID_QUERY_BOOK_FAVOR));
    }

    @Override
    protected void refreshView() {
        pageBtnContainer.removeAllViews();
        if (favorList.size() == 0) {
            //隐藏所有之前的item,并且显示没有收藏的提示
            fillItems(0 , -1);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            //本次显示的最大的页码(从1开始)
            int showPageNumEnd;
            //本次能展示的最后一本书的序号(从0开始)
            int lastShowBookIndex;
            //是否显示<<向前按钮
            boolean showForward = (currentShowFirstPageIndex == 0 ? false : true);
            //是否显示>>向后按钮
            boolean showNext;
            if (favorList.size() <= (currentShowFirstPageIndex + ONCE_SHOW_PAGE_NUM) * ITEM_NUM){
                lastShowBookIndex = favorList.size() - 1;
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
                Favor favor = favorList.get(i);
                bookItems.get(j).setData(favor);
                boolean checked = (findFavorByID(checkedFavorList, favor.getBookId()) == null) ? false : true;
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
                bookItems.get(j).setData(null);
            }
        }
        //设置全选按钮的选中状态
        selectAllCheckbox.setSelected(allCheck);
    }

    /**
     * 操作BookInfo列表的方法,在列表中查找拥有指定bookID的BookInfo
     * @param favorList 要查找的BookInfo列表
     * @param bookID 指定的bookID
     * @return 如果找到,则返回该BookInfo,如果有多个,返回第一个,如果没有,返回null.
     */
    public Favor findFavorByID(ArrayList<Favor> favorList, int bookID) {
        for (Favor favor : favorList) {
            if (favor.getBookId() == bookID) {
                return favor;
            }
        }
        return null;
    }
    /**
     * 操作BookInfo列表的方法,在指定的BookInfo列表中删除拥有给定的bookInfo列表中bookInfo的项
     * @param fromFavorList 在其中删除的BookInfo列表
     * @param toRemoveFavorList 指定的bookInfo列表
     * @return 返回删除后的BookInfo列表
     */
    public void removeBooksByID(ArrayList<Favor> fromFavorList, ArrayList<Favor> toRemoveFavorList) {
        for (Favor toRemoveFavor : toRemoveFavorList) {
            Favor favor = findFavorByID(fromFavorList, toRemoveFavor.getBookId());
            if (favor != null) {
                fromFavorList.remove(favor);
            }
        }
    }
    @OnClick({R.id.shop_favorite_back_btn, R.id.shop_favorite_delete_btn, R.id.shop_favorite_select_all_checkbox})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shop_favorite_back_btn:
                finish();
                break;
            case R.id.shop_favorite_delete_btn:
                if (checkedFavorList.size() > 0 ){
                    new ConfirmDialog(this, "确实要删除这" + checkedFavorList.size() +"本书吗?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RemoveBookFavorRequest request = new RemoveBookFavorRequest();
                            request.setUserId(SpUtil.getAccountId());
                            for (Favor favor : checkedFavorList) {
                                request.getData().add(new RemoveBookFavorRequest.BookIdObj(favor.getBookId()));
                            }
                            ProtocolManager.bookFavorRemoveProtocol(request
                                    , ProtocolId.PROTOCOL_ID_REMOVE_BOOK_FAVOR
                                    , new RemoveBookFavorCallBack(ShopFavoriteActivity.this , ProtocolId.PROTOCOL_ID_REMOVE_BOOK_FAVOR , request));
                            dialog.dismiss();
                        }
                    }).show();
                }
                break;
            case R.id.shop_favorite_select_all_checkbox:
                boolean setCheck = !selectAllCheckbox.isSelected();
                for (int i = 0 ; i < bookItems.size() && i < favorList.size(); i++){
                    NewShopBookItem everyItem = bookItems.get(i);
                    everyItem.setChecked(setCheck , true);
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
    private boolean isAllChecked(){
        boolean allChecked = true;
        for (int i = 0 ; i < bookItems.size() && i < favorList.size() ; i++){
            NewShopBookItem item = bookItems.get(i);
            if (!item.isChecked()) {
                allChecked = false;
                break;
            }
        }
        return allChecked;
    }

    /**
     * 切换到制定的页
     * @param pageIndex 制定的页序号
     */
    private void toPage(int pageIndex) {
        if ((pageIndex + 1) * bookItems.size() < favorList.size()) {
            fillItems(pageIndex * bookItems.size(), (pageIndex + 1) * bookItems.size() - 1);
        } else {
            fillItems(pageIndex * bookItems.size(), favorList.size() - 1);
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
        pageBtn.setLayoutParams(params);
        pageBtn.setOnClickListener(this);
        AutoUtils.auto(pageBtn);
        pageBtnContainer.addView(pageBtn);
    }

    /**
     * item被点击的回调
     * @param position 被点击的item的数据在本地缓存中的位置.
     */
    @Override
    public void onItemClick(int position) {
        loadIntentWithExtra(ShopBookDetailsActivity.class , ShopGloble.BOOK_ID , favorList.get(position).getBookId());
    }

    /**
     * item中的勾选框被选中或取消时的回调
     * @param position 被点击的item的数据在本地缓存中的位置.
     * @param checked true时被选中,false时被取消
     */
    @Override
    public void onCheckedChanged(int position, boolean checked) {
        Favor favor = favorList.get(position);
        if (checked) {
            checkedFavorList.add(favor);
        } else {
            ArrayList<Favor> toRemoveList = new ArrayList<Favor>();
            toRemoveList.add(favor);
            removeBooksByID(checkedFavorList, toRemoveList);
        }
        selectAllCheckbox.setSelected(checked && isAllChecked());
    }

    /**
     * item中的按钮被点击时的回调
     * @param position 被点击的item的数据在本地缓存中的位置.
     */
    @Override
    public void onBtnClick(final int position) {
        new ConfirmDialog(this, "确实要删除这本书吗?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RemoveBookFavorRequest request = new RemoveBookFavorRequest();
                request.setUserId(SpUtil.getAccountId());
                request.getData().add(new RemoveBookFavorRequest.BookIdObj(favorList.get(position).getBookId()));
                ProtocolManager.bookFavorRemoveProtocol(request
                        , ProtocolId.PROTOCOL_ID_REMOVE_BOOK_FAVOR
                        , new RemoveBookFavorCallBack(ShopFavoriteActivity.this , ProtocolId.PROTOCOL_ID_REMOVE_BOOK_FAVOR , request));
                dialog.dismiss();
            }
        }).show();
    }
}
