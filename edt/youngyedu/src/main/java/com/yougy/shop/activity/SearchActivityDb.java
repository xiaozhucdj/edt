package com.yougy.shop.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yougy.common.bean.Result;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.protocol.request.NewBookStoreBookReq;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.ResultUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.rx_subscriber.ShopSubscriber;
import com.yougy.shop.adapter.RecyclerAdapter;
import com.yougy.shop.adapter.SearchResultAdapter1;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.bean.CategoryInfo;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.SearchBinding;
import com.yougy.view.CustomLinearLayoutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by jiangliang on 2017/3/2.
 */

public class SearchActivityDb extends ShopBaseActivity {
    @BindString(R.string.all_grade)
    String mAllGrade;

    private SearchBinding binding;
    private RecyclerAdapter mStageAdapter;
    private List<BookInfo> mBookInfos;
    private List<BookInfo> mPageInfos = new ArrayList<>();

    private SearchResultAdapter1 mAdapter;

    private List<Button> btns = new ArrayList<>();
    private List<String> mHistoryRecords;

    private static final int COUNT_PER_PAGE = 5;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.search_result_layout_db);
        binding.setActivity(this);
    }

    private void itemClick(int position) {
        BookInfo info = mPageInfos.get(position);
        LogUtils.e(tag, "onItemClick......" + info.getBookTitle());
        loadIntentWithExtra(ShopBookDetailsActivity.class, ShopGloble.BOOK_ID, Integer.parseInt(info.getBookId() + ""));
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        bookTitle = intent.getStringExtra("search_key");
        categoryId = intent.getIntExtra("categoryId", -1);
        mHistoryRecords = SpUtils.getHistoryRecord();
    }

    private void generateBtn() {
        int count = mBookInfos.size() % COUNT_PER_PAGE == 0 ? mBookInfos.size() / COUNT_PER_PAGE : mBookInfos.size() / COUNT_PER_PAGE + 1;
        binding.pageNumberLayout.removeAllViews();
        for (int index = 1; index <= count; index++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 20;
            View pageLayout = View.inflate(this, R.layout.page_item, null);
            final Button pageBtn = (Button) pageLayout.findViewById(R.id.page_btn);
            if (index == 1) {
                pageBtn.setSelected(true);
            }
            pageBtn.setText(index + "");
            btns.add(pageBtn);
            final int page = index - 1;
            pageBtn.setOnClickListener(v -> {
                for (Button btn : btns) {
                    btn.setSelected(false);
                }
                pageBtn.setSelected(true);
                mPageInfos.clear();
                int start = page * COUNT_PER_PAGE;
                int end = (page + 1) * COUNT_PER_PAGE;
                if (end > mBookInfos.size()) {
                    end = mBookInfos.size();
                }
                mPageInfos.addAll(mBookInfos.subList(start, end));
                mAdapter.notifyDataSetChanged();
            });
            binding.pageNumberLayout.addView(pageLayout, params);
        }
    }

    private int bookVersion = -1;
    private int bookCategory = -1;
    private int bookCategoryMatch = -1;

    @Override
    protected void initLayout() {
        binding.searchKey.setText(bookTitle);
        binding.resultRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        binding.subjectWrap.setHorizontalMargin(40);
        binding.subjectWrap.setVerticalMargin(25);
        binding.versionWrap.setHorizontalMargin(40);
        binding.versionWrap.setVerticalMargin(25);
        CustomLinearLayoutManager stageManager = new CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        stageManager.setScrollEnabled(false);
        binding.stageRecycler.setLayoutManager(stageManager);
        binding.stageRecycler.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.stageRecycler) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                hideStageRecyclerLayout(null);
                generateSubjectLayout(mStageAdapter.getItem(vh.getAdapterPosition()));
            }
        });
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.searchOrCancelTv.setText(R.string.search);
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.searchOrCancelTv.setText(s.length() == 0 ? R.string.cancel : R.string.search);
            }
        });
        binding.searchEt.setOnEditorActionListener((v, actionId, event) -> {
            hideSearchLayout();
            bookTitle = binding.searchEt.getText().toString();
            if (!TextUtils.isEmpty(bookTitle)) {
                search();
            }
            return false;
        });
    }

    private void generateSubjectLayout(CategoryInfo info) {
        bookCategoryMatch = info.getCategoryId();
        List<CategoryInfo> childs = info.getCategoryList();
        if (binding.subjectWrap.getChildCount() != 0) {
            binding.subjectWrap.removeAllViews();
        }
        if (null == childs || childs.size() == 0) {
            binding.subjectLayout.setVisibility(View.GONE);
            return;
        }else{
            showSubjectLayout();
        }
        for (final CategoryInfo item : info.getCategoryList()) {
            View layout = View.inflate(this, R.layout.text_view, null);
            final TextView tv = (TextView) layout.findViewById(R.id.text_tv);
            String display = item.getCategoryDisplay();
            if (display.length() > 4) {
                display = display.substring(0, 4);
            }
            tv.setText(display);
            tv.setOnClickListener(v -> {
                resetSubjectTv();
                generateVersionLayout(item);
                binding.versionLayout.setVisibility(View.VISIBLE);
                bookCategory = item.getCategoryId();
                tv.setSelected(true);
            });
            binding.subjectWrap.addView(layout);
        }
    }

    private void generateVersionLayout(CategoryInfo info) {
        if (binding.versionWrap.getChildCount() != 0) {
            binding.versionWrap.removeAllViews();
        }
        for (final CategoryInfo item : info.getCategoryList()) {
            View layout = View.inflate(this, R.layout.text_view, null);
            final TextView tv = (TextView) layout.findViewById(R.id.text_tv);
            tv.setText(item.getCategoryDisplay());
            tv.setOnClickListener(v -> {
                resetVersionTv();
                tv.setSelected(true);
                bookVersion = item.getCategoryId();
            });
            binding.versionWrap.addView(layout);
        }
    }

    private void resetSubjectTv() {
        int count = binding.subjectWrap.getChildCount();
        for (int i = 0; i < count; i++) {
            FrameLayout layout = (FrameLayout) binding.subjectWrap.getChildAt(i);
            layout.getChildAt(0).setSelected(false);
        }
    }

    private void resetVersionTv() {
        int count = binding.versionWrap.getChildCount();
        for (int i = 0; i < count; i++) {
            FrameLayout layout = (FrameLayout) binding.versionWrap.getChildAt(i);
            layout.getChildAt(0).setSelected(false);
        }
    }

    private String bookTitle;
    private int categoryId;

    @Override
    protected void loadData() {
        final NewBookStoreBookReq req = new NewBookStoreBookReq();
        req.setBookTitleMatch(bookTitle);
        queryBook(req);

    }

    private void queryBook(final NewBookStoreBookReq req) {

        if (!NetUtils.isNetConnected()) {
            showCancelAndDetermineDialog(R.string.jump_to_net);
            return;
        }

        Observable<List<BookInfo>> observable = Observable.create(new Observable.OnSubscribe<List<BookInfo>>() {
            @Override
            public void call(Subscriber<? super List<BookInfo>> subscriber) {
                Response response = NewProtocolManager.queryBook(req);
                bookCategory = -1;
                bookVersion = -1;
                bookCategoryMatch = -1;
                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        Result<List<BookInfo>> result = ResultUtils.fromJsonArray(json, BookInfo.class);
                        List<BookInfo> infos = result.getData();
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(infos);
                            subscriber.onCompleted();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        ShopSubscriber<List<BookInfo>> subscriber = new ShopSubscriber<List<BookInfo>>(this) {

            @Override
            public void onNext(List<BookInfo> bookInfos) {
                LogUtils.e(tag, "bookinfos' size : " + bookInfos.size());
                refreshResultView(bookInfos);
            }

            @Override
            public void require() {
                queryBook(req);
            }
        };

        observable.subscribe(subscriber);
    }

    private void refreshResultView(List<BookInfo> bookInfos) {
        if (bookInfos == null || bookInfos.size() == 0) {
            binding.noResult.setVisibility(View.VISIBLE);
            binding.pageNumberLayout.removeAllViews();
            binding.resultRecycler.setVisibility(View.GONE);
        } else {
            mBookInfos = bookInfos;
            mPageInfos.clear();
            if (mBookInfos.size() >= COUNT_PER_PAGE) {
                mPageInfos.addAll(mBookInfos.subList(0, COUNT_PER_PAGE));
            } else {
                mPageInfos.addAll(mBookInfos);
            }
            generateBtn();
            binding.noResult.setVisibility(View.GONE);
            mAdapter = new SearchResultAdapter1(mPageInfos);
            binding.resultRecycler.setAdapter(mAdapter);
            binding.resultRecycler.setVisibility(View.VISIBLE);
            binding.resultRecycler.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.resultRecycler) {
                @Override
                public void onItemClick(RecyclerView.ViewHolder vh) {
                    itemClick(vh.getAdapterPosition());
                }
            });
        }
    }

    @Override
    protected void refreshView() {

    }

    public void back(View view) {
        finish();
    }

    private void search() {
        if (!mHistoryRecords.contains(bookTitle)) {
            if (mHistoryRecords.contains(UIUtils.getContext().getResources().getString(R.string.no_history_record))) {
                mHistoryRecords.clear();
            }
            mHistoryRecords.add(bookTitle);
            SpUtils.putHistoryRecord(mHistoryRecords);
        }
        if (!TextUtils.isEmpty(bookTitle)) {
            binding.searchKey.setText(bookTitle);
            NewBookStoreBookReq req = new NewBookStoreBookReq();
            req.setBookTitleMatch(bookTitle);
            queryBook(req);
        }
    }

    public void search(View view) {
        generateHistoryRecord();
        binding.searchLayout.setVisibility(View.VISIBLE);
        binding.searchOrCancelTv.setText(R.string.cancel);
        binding.searchEt.setText("");
        binding.searchEt.setFocusable(true);
        binding.searchEt.setFocusableInTouchMode(true);
        binding.searchEt.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(binding.searchEt, 0);
    }

    public void clickSearchOrCancelTv(View view) {
        hideSearchLayout();
        bookTitle = binding.searchEt.getText().toString();
        if (!TextUtils.isEmpty(bookTitle)) {
            search();
        }
    }
    /**
     * 生成历史搜索记录
     */
    private void generateHistoryRecord() {
        binding.historyRecordLayout.removeAllViews();
        for (final String record : mHistoryRecords) {
            TextView recordTv = new TextView(this);
            recordTv.setText(record);
            recordTv.setIncludeFontPadding(false);
            recordTv.setTextSize(UIUtils.px2dip(24));
            recordTv.setTextColor(getResources().getColor(R.color.text_color_black));
            recordTv.setOnClickListener(v -> {
                bookTitle = record;
                hideSearchLayout();
                search();
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = UIUtils.px2dip(20);
            binding.historyRecordLayout.addView(recordTv, params);
        }
    }
    private void hideSearchLayout() {
        InputMethodManager inputManager1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager1.hideSoftInputFromWindow(binding.searchEt.getWindowToken(), 0);
        binding.searchLayout.setVisibility(View.GONE);
        binding.searchEt.clearFocus();
    }
    public void clearHistoryRecord(View view) {
        SpUtils.clearHistoryRecord();
        mHistoryRecords.clear();
        generateHistoryRecord();
    }
    public void filtrate(View view) {
        showFiltrateLayout();
    }

    public void reset(View view) {
        resetVersionTv();
        resetSubjectTv();
        resetFiltrate();
        hideGradeLayout();
    }

    public void confirm(View view) {
        LogUtils.e(tag, "book title : " + bookTitle + ", bookVersion : " + bookVersion + ", bookCategory : " + bookCategory + ",bookCategoryMatch : " + bookCategoryMatch);
        resetVersionTv();
        resetSubjectTv();
        hideFiltrateLayout();
        //TODO:集合数据中，根据条件进行筛选
        NewBookStoreBookReq req = new NewBookStoreBookReq();
        req.setBookTitleMatch(bookTitle);
        req.setBookVersion(bookVersion);
        req.setBookCategory(bookCategory);
        req.setBookCategoryMatch(bookCategoryMatch);
        queryBook(req);
        resetFiltrate();
    }

    private int currentItem = -1;

    public void clickTextBookTv(View view) {
        currentItem = 0;
        binding.textBookTv.setSelected(true);
        binding.guideBookTv.setSelected(false);
        binding.extraBookTv.setSelected(false);
        showGradeLayout();
    }

    public void clickGuideBookTv(View view) {
        currentItem = 1;
        binding.textBookTv.setSelected(false);
        binding.guideBookTv.setSelected(true);
        binding.extraBookTv.setSelected(false);
        showGradeLayout();
    }

    public void clickExtraBookTv(View view) {
        currentItem = 2;
        binding.textBookTv.setSelected(false);
        binding.guideBookTv.setSelected(false);
        binding.extraBookTv.setSelected(true);
        showGradeLayout();
    }

    public void cancel(View view) {
        reset(view);
        hideFiltrateLayout();
    }

    private void resetFiltrate() {
        binding.stageButton.setText(mStageAdapter.getItem(0).getCategoryDisplay());
        binding.subjectLayout.setVisibility(View.GONE);
        binding.versionLayout.setVisibility(View.GONE);
        binding.textBookTv.setSelected(false);
        binding.guideBookTv.setSelected(false);
        binding.extraBookTv.setSelected(false);
    }

    /**
     * 显示科目布局
     */
    private void showSubjectLayout() {
        binding.subjectLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 显示筛选布局
     */
    private void showFiltrateLayout() {
        binding.filtrateLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏筛选布局
     */
    private void hideFiltrateLayout() {
        binding.filtrateLayout.setVisibility(View.GONE);
    }

    private void showGradeLayout() {
        List<CategoryInfo> infos = BookShopActivityDB.grades.get(currentItem).getCategoryList();
        mStageAdapter = new RecyclerAdapter(infos);
        binding.stageRecycler.setAdapter(mStageAdapter);
        binding.stageButton.setText(mStageAdapter.getItem(0).getCategoryDisplay());
        binding.gradeLayout.setVisibility(View.VISIBLE);
        binding.subjectLayout.setVisibility(View.GONE);
    }
    public void stageClick(View view) {
        LogUtils.e(tag, "stage click............." + binding.stageRecyclerLayout.getVisibility()+"");
        binding.stageRecyclerLayout.setVisibility(binding.stageRecyclerLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    public void hideStageRecyclerLayout(View view) {
        if (binding.stageRecyclerLayout.getVisibility() == View.VISIBLE) {
            binding.stageRecyclerLayout.setVisibility(View.GONE);
        }
    }
    private void hideGradeLayout() {
        binding.gradeLayout.setVisibility(View.GONE);
    }
}
