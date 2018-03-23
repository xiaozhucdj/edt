package com.yougy.shop.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.shop.bean.BookIdObj;
import com.yougy.shop.bean.Favor;
import com.yougy.shop.bean.RemoveRequestObj;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.view.ShopBookItem2;
import com.yougy.view.dialog.HintDialog;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by FH on 2017/2/13.
 */

public class ShopFavoriteActivity extends ShopBaseActivity implements View.OnClickListener , ShopBookItem2.OnItemActionListener {
    //本地数据缓存
    ArrayList<Favor> favorList = new ArrayList<Favor>();
    //已选条目
    ArrayList<Favor> checkedBookInfoList = new ArrayList<Favor>();
    //每次最多展示的页数
    final int ONCE_SHOW_PAGE_NUM = 5;
    //每一页的条数
    final int ITEM_NUM = 4;

    //当前展示的第一页页号(从0开始)
    int currentShowFirstPageIndex = 0;
    //当前选定的的页码序号(从0开始)
    int currentSelectedPageIndex = 0;


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
    ArrayList<ShopBookItem2> bookItems = new ArrayList<ShopBookItem2>();

    //是否需要刷新items
    boolean needRefreshItems = false;
    private int mTagForNoNet = 1;
    private int mTagForGetFaverFail = 2;
    private int mTagForCancelAllBook =3;
    private int mRemoverPosition;
    private int mTagForCancelBook =4;

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
        return UIUtils.inflate(R.layout.activity_shop_favorite);
    }

    protected void afterBinding() {
        //初始化条目item,添加至容器,并且保存一份引用
        for (int i = 0; i < ITEM_NUM; i++) {
            ShopBookItem2 item = new ShopBookItem2(this);
            item.setOnItemActionListener(this);
            item.setBtnText("取消收藏");
            itemContainer.addView(item);
            bookItems.add(item);
        }
    }

    @Override
    public void loadData() {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
            return;
        }
        NetWorkManager.queryFavor(SpUtils.getUserId())
                .subscribe(favors -> {
                    favorList.clear();
                    if (favors != null) {
                        favorList.addAll(favors);
                    }
                    //如果已选中的某些项在新的数据中不存在,则删除它们
                    for (int i = 0; i < checkedBookInfoList.size(); ) {
                        Favor checkedInfo = checkedBookInfoList.get(i);
                        if (findFavorByBookID(favorList, checkedInfo.getBookId()) == null) {
                            checkedBookInfoList.remove(checkedInfo);
                            continue;
                        }
                        i++;
                    }
                    //如果之前选中的页号在新的数据中已经不存在了,则把选中的页号确定为最后一页.
                    if (favorList.size() <= currentSelectedPageIndex * ITEM_NUM) {
                        currentSelectedPageIndex = (favorList.size() - 1) / ITEM_NUM;
                        if (currentSelectedPageIndex < 0) {
                            currentSelectedPageIndex = 0;
                        }
                    }
                    //此处得到新的显示第一页的页号,如(11/5*5 = 10)
                    currentShowFirstPageIndex = currentSelectedPageIndex / ONCE_SHOW_PAGE_NUM * ONCE_SHOW_PAGE_NUM;
                    //请求刷新items
                    needRefreshItems = true;
                    refreshView();
                    if (favorList.size() == 0) {
                        deleteBtn.setVisibility(View.INVISIBLE);
                    } else {
                        deleteBtn.setVisibility(View.VISIBLE);
                    }
                }, throwable -> {
                    Log.v("FH", "请求收藏夹失败");
                    showTagCancelAndDetermineDialog(R.string.load_error_tost, R.string.cancel, R.string.retry, mTagForGetFaverFail);
                    throwable.printStackTrace();
                });
    }

    protected void refreshView() {
        pageBtnContainer.removeAllViews();
        if (favorList.size() == 0) {
            //隐藏所有之前的item,并且显示没有收藏的提示
            fillItems(0, -1);
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
            if (favorList.size() <= (currentShowFirstPageIndex + ONCE_SHOW_PAGE_NUM) * ITEM_NUM) {
                lastShowBookIndex = favorList.size() - 1;
                showNext = false;
            } else {
                lastShowBookIndex = (currentShowFirstPageIndex + ONCE_SHOW_PAGE_NUM) * ITEM_NUM - 1;
                showNext = true;
            }
            showPageNumEnd = lastShowBookIndex / ITEM_NUM + 1;
            addBtns(currentShowFirstPageIndex + 1, showPageNumEnd, showForward, showNext);

            if (needRefreshItems) {
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
     * 根据当前显示的favor信息填充items
     *
     * @param startIndex 要显示的多个favor在本地缓存中的开始位置
     * @param endIndex   要显示的多个favor在本地缓存中的结束位置
     */
    private void fillItems(int startIndex, int endIndex) {
        boolean allCheck = true;
        for (int i = startIndex, j = 0; j < bookItems.size(); i++, j++) {
            if (i <= endIndex) {
                Favor favor = favorList.get(i);
                bookItems.get(j).setData(favor);
                boolean checked = (findFavorByBookID(checkedBookInfoList, favor.getBookId()) == null) ? false : true;
                if (!checked) {
                    allCheck = false;
                }
                bookItems.get(j).setChecked(checked, false);
                bookItems.get(j).setPosition(i);
            } else {
                bookItems.get(j).setData(null);
            }
        }
        //设置全选按钮的选中状态
        selectAllCheckbox.setSelected(allCheck);
    }

    /**
     * 操作BookInfo列表的方法,在列表中查找拥有指定bookID的BookInfo
     *
     * @param favorList 要查找的BookInfo列表
     * @param bookID    指定的bookID
     * @return 如果找到, 则返回该BookInfo, 如果有多个, 返回第一个, 如果没有, 返回null.
     */
    public Favor findFavorByBookID(ArrayList<Favor> favorList, int bookID) {
        for (Favor favor : favorList) {
            if (favor.getBookId() == bookID) {
                return favor;
            }
        }
        return null;
    }

    /**
     * 操作BookInfo列表的方法,在指定的BookInfo列表中删除拥有给定的bookInfo列表中bookInfo的项
     * @param fromFavorList     在其中删除的BookInfo列表
     * @param toRemoveFavorList 指定的bookInfo列表
     * @return 返回删除后的BookInfo列表
     */
    public void removeFavors(ArrayList<Favor> fromFavorList, ArrayList<Favor> toRemoveFavorList) {
        for (Favor toRemoveFavor : toRemoveFavorList) {
            Favor favor = findFavorByBookID(fromFavorList, toRemoveFavor.getBookId());
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
                if (checkedBookInfoList.size() > 0) {
                    String formatC = getResources().getString(R.string.cancle_collection_all_book);
                    String resultC = String.format(formatC, checkedBookInfoList.size()+"") ;
                    showTagCancelAndDetermineDialog(resultC, mTagForCancelAllBook);
                }
                break;
            case R.id.shop_favorite_select_all_checkbox:
                boolean setCheck = !selectAllCheckbox.isSelected();
                for (int i = 0; i < bookItems.size() && i < favorList.size(); i++) {
                    ShopBookItem2 everyItem = bookItems.get(i);
                    everyItem.setChecked(setCheck, true);
                }
                break;
            case R.id.shop_page_page_btn:
                int i = (int) view.getTag();
                switch (i) {
                    case -1://向前
                        currentShowFirstPageIndex = currentShowFirstPageIndex - ONCE_SHOW_PAGE_NUM;
                        refreshView();
                        break;
                    case -2://向后
                        currentShowFirstPageIndex = currentShowFirstPageIndex + ONCE_SHOW_PAGE_NUM;
                        refreshView();
                        break;
                    default:
                        TextView btn = (TextView) pageBtnContainer.findViewWithTag(currentSelectedPageIndex + 1);
                        if (btn != null) {
                            btn.setSelected(false);
                        }
                        btn = (TextView) pageBtnContainer.findViewWithTag(i);
                        if (btn != null) {
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
     * @return 如果所有item都被选中, 返回true, 只要有任意一个没有被选中, 返回false.
     */
    private boolean isAllChecked() {
        boolean allChecked = true;
        for (int i = 0; i < bookItems.size() && i < favorList.size(); i++) {
            ShopBookItem2 item = bookItems.get(i);
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
     * @param lastBtnNum  要添加的最后一个按钮的序号
     * @param hasForward  是否添加向前按钮
     * @param hasNext     是否添加向后按钮
     */
    private void addBtns(int firstBtnNum, int lastBtnNum, boolean hasForward, boolean hasNext) {
        if (hasForward) addBtn(-1);
        for (int index = firstBtnNum; index <= lastBtnNum; index++) {
            addBtn(index);
        }
        if (hasNext) addBtn(-2);
    }

    /**
     * 添加一个按钮,添加的按钮tag会是它文字的int,向前的tag为-1,向后的tag为-2.
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
        Intent intent = new Intent(this, ShopBookDetailsActivity.class);
        intent.putExtra(ShopGloble.BOOK_ID, favorList.get(position).getBookId());
        startActivity(intent);
    }

    /**
     * item中的勾选框被选中或取消时的回调
     * @param position 被点击的item的数据在本地缓存中的位置.
     * @param checked  true时被选中,false时被取消
     */
    @Override
    public void onCheckedChanged(int position, boolean checked) {
        if (position < favorList.size()) {
            Favor favor = favorList.get(position);
            if (checked) {
                checkedBookInfoList.add(favor);
            } else {
                ArrayList<Favor> toRemoveList = new ArrayList<Favor>();
                toRemoveList.add(favor);
                removeFavors(checkedBookInfoList, toRemoveList);
            }
            selectAllCheckbox.setSelected(checked && isAllChecked());
        }
    }

    /**
     * item中的按钮被点击时的回调
     * @param position 被点击的item的数据在本地缓存中的位置.
     */
    @Override
    public void onBtnClick(final int position) {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
            return;
        }

        mRemoverPosition  = position;
        String formatC = getResources().getString(R.string.cancle_collection_book);
        String resultC = String.format(formatC, favorList.get(mRemoverPosition).getBookTitle()) ;
        showTagCancelAndDetermineDialog(resultC, mTagForCancelBook);
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        if (mUiPromptDialog.getTag() == mTagForNoNet) {
            jumpTonet();
        } else if (mUiPromptDialog.getTag() == mTagForGetFaverFail) {
            loadData();
        } else if (mUiPromptDialog.getTag() ==mTagForCancelAllBook) {
            List<BookIdObj> bookIdObjList = new ArrayList<BookIdObj>();
            for (Favor favor : checkedBookInfoList) {
                bookIdObjList.add(new BookIdObj(favor.getBookId()));
            }
            removeFavorAll(bookIdObjList);
        } else if (mUiPromptDialog.getTag() ==mTagForCancelBook) {
            removeFavor();
        }
    }

    @Override
    public void onUiCancelListener() {
        super.onUiCancelListener();
        if (mUiPromptDialog.getTag() == mTagForGetFaverFail) {
            this.finish();
        }
    }

    private void removeFavorAll(List<BookIdObj> bookIdObjList) {

        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForNoNet);
            return;
        }

        NetWorkManager.removeFavor(new RemoveRequestObj(SpUtils.getUserId(), bookIdObjList))
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        loadData();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showCenterDetermineDialog(R.string.cancle_collection_fail);
                        Log.v("FH", "删除失败");
                        throwable.printStackTrace();
                    }
                });
    }

    private void removeFavor(){
        NetWorkManager.removeFavor(SpUtils.getUserId(), favorList.get(mRemoverPosition).getBookId())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        new HintDialog(getThisActivity() , "删除成功").show();
                        loadData();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.v("FH", "删除失败");
                        showCenterDetermineDialog(R.string.cancle_collection_fail);
                        throwable.printStackTrace();
                    }
                });
    }
}
