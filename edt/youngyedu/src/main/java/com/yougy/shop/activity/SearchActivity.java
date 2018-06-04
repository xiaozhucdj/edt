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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frank.etude.pageBtnBar.PageBtnBarAdapter;
import com.yougy.anwser.BaseResult;
import com.yougy.common.new_network.BookStoreQueryBookInfoReq;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.shop.adapter.RecyclerAdapter;
import com.yougy.shop.adapter.SearchResultAdapter;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.bean.CategoryInfo;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.SearchBinding;
import com.yougy.view.CustomLinearLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindString;
import rx.functions.Action1;


/**
 * Created by jiangliang on 2017/3/2.
 */

public class SearchActivity extends ShopBaseActivity {
    @BindString(R.string.all_grade)
    String mAllGrade;
    @BindArray(R.array.stages)
    String[] mStages;
    @BindArray(R.array.subjects)
    String[] mSubjects;
    @BindArray(R.array.versions)
    String[] mVersions;


    private SearchBinding binding;
    private List<String> mStageList;
    private RecyclerAdapter mStageAdapter;
    private String mSubject;
    private String mVersion;
    private List<BookInfo> mBookInfos = new ArrayList<BookInfo>();

    private SearchResultAdapter mAdapter;
    private int totalCount = 0;

    private static final int COUNT_PER_PAGE = 5;

    private List<String> mHistoryRecords;

    private String bookTitle;
    private int categoryId;
    private int bookVersion = -1;
    private int bookCategory = -1;
    private int bookCategoryMatch = -1;

    //预筛选,点击了筛选项,但是还没有点确定.
    private int preChoosedBookVersion = -1;
    private int preChoosedBookCategory = -1;
    private int preChoosedBookCategoryMatch = -1;

    private void itemClick(int position) {
        Intent intent = new Intent(this, ShopBookDetailsActivity.class);
        intent.putExtra(ShopGloble.BOOK_ID, mBookInfos.get(position).getBookId());
        startActivity(intent);

    }

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.search_result_layout);
        binding.setActivity(this);
        setContentView(binding.getRoot());
    }

    @Override
    public void init() {
        Intent intent = getIntent();
        bookTitle = intent.getStringExtra("search_key");
        categoryId = intent.getIntExtra("categoryId", -1);
        mStageList = new ArrayList<>();
        mStageList.add(mAllGrade);
        mStageList.addAll(Arrays.asList(mStages));
        mHistoryRecords = SpUtils.getHistoryRecord();
        initLayout();
    }

    protected void initLayout() {
        binding.searchKey.setText(bookTitle);
        binding.resultRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        binding.subjectWrap.setHorizontalMargin(40);
        binding.subjectWrap.setVerticalMargin(25);
        binding.versionWrap.setHorizontalMargin(40);
        binding.versionWrap.setVerticalMargin(25);

        CustomLinearLayoutManager stageManager = new CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        stageManager.setScrollHorizontalEnabled(false);
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
        binding.pageBtnBar.setPageBarAdapter(new PageBtnBarAdapter(getThisActivity()) {
            @Override
            public int getPageBtnCount() {
                return (totalCount + COUNT_PER_PAGE - 1) / COUNT_PER_PAGE;
            }

            @Override
            public void onPageBtnClick(View view, int i, String s) {
                hideFiltrateLayout();
                queryBookBaseOnFiltration(i+1);
            }
        });
        mAdapter = new SearchResultAdapter(mBookInfos);
        binding.resultRecycler.setAdapter(mAdapter);
        binding.resultRecycler.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.resultRecycler) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                itemClick(vh.getAdapterPosition());
            }
        });
    }

    private void generateSubjectLayout(CategoryInfo info) {
        LogUtils.e(tag, "subject is : " + mSubjects.length);
        preChoosedBookCategoryMatch = info.getCategoryId();
        binding.stageButton.setText(info.getCategoryDisplay());
        List<CategoryInfo> childs = info.getCategoryList();
        LogUtils.e(tag, "childs : " + childs);
        if (binding.subjectWrap.getChildCount() != 0) {
            binding.subjectWrap.removeAllViews();
        }
        if (null == childs || childs.size() == 0) {
            binding.subjectLayout.setVisibility(View.GONE);
            return;
        } else {
            showSubjectLayout();
        }
        for (final CategoryInfo item : childs) {

            View layout = View.inflate(this, R.layout.text_view, null);
            final TextView tv = (TextView) layout.findViewById(R.id.text_tv);
            String display = item.getCategoryDisplay();
            if (display.length() > 4) {
                display = display.substring(0, 4);
            }
            tv.setText(display);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchActivity.this.resetSubjectTv();
                    SearchActivity.this.generateVersionLayout(item);
                    binding.versionLayout.setVisibility(View.VISIBLE);
                    mSubject = item.getCategoryDisplay();
                    preChoosedBookCategory = item.getCategoryId();
                    tv.setSelected(true);
                }
            });
            binding.subjectWrap.addView(layout);
        }

    }

    private void generateVersionLayout(CategoryInfo info) {
        LogUtils.e(tag, "version is : " + mVersions.length);
        List<CategoryInfo> childs = info.getCategoryList();
        if (binding.versionWrap.getChildCount() != 0) {
            binding.versionWrap.removeAllViews();
        }
        if (null == childs || childs.size() == 0) {
            binding.versionLayout.setVisibility(View.GONE);
            return;
        } else {
            showVersionLayout();
        }
        for (final CategoryInfo item : childs) {
            View layout = View.inflate(this, R.layout.text_view, null);
            final TextView tv = (TextView) layout.findViewById(R.id.text_tv);
            String display = item.getCategoryDisplay();
            if (display.length() > 4) {
                display = display.substring(0, 4);
            }
            tv.setText(display);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchActivity.this.resetVersionTv();
                    mVersion = item.getCategoryDisplay();
                    tv.setSelected(true);
                    preChoosedBookVersion = item.getCategoryId();
                }
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

    @Override
    public void loadData() {
        queryBookBaseOnFiltration(1);
    }

    @Override
    protected void refreshView() {

    }


    private void refreshResultView(List<BookInfo> bookInfos) {
        if (bookInfos == null || bookInfos.size() == 0) {
            binding.noResult.setVisibility(View.VISIBLE);
            binding.resultRecycler.setVisibility(View.GONE);
        } else {
            mBookInfos.clear();
            mBookInfos.addAll(bookInfos);
            binding.resultRecycler.setVisibility(View.VISIBLE);
            binding.noResult.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
            binding.pageBtnBar.refreshPageBar();
        }
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
            reset(binding.resetTv);
            bookVersion = -1;
            bookCategory = -1;
            bookCategoryMatch = -1;
            preChoosedBookVersion = -1;
            preChoosedBookCategory = -1;
            preChoosedBookCategoryMatch = -1;
            binding.pageBtnBar.setCurrentSelectPageIndex(-1);
            queryBookBaseOnFiltration(1);
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
        preChoosedBookVersion = -1;
        preChoosedBookCategory = -1;
        preChoosedBookCategoryMatch = -1;
        resetVersionTv();
        resetSubjectTv();
        resetFiltration();
        hideGradeLayout();
    }

    public void confirm(View view) {
        LogUtils.e(tag, "subject : " + mSubject + ", version : " + mVersion);
        LogUtils.e(tag, "book title : " + bookTitle + ", bookVersion : " + bookVersion + ", bookCategory : " + bookCategory + ",bookCategoryMatch : " + bookCategoryMatch);
        hideFiltrateLayout();
        //TODO:集合数据中，根据条件进行筛选
        bookVersion = preChoosedBookVersion;
        bookCategory = preChoosedBookCategory;
        bookCategoryMatch = preChoosedBookCategoryMatch;
        binding.pageBtnBar.setCurrentSelectPageIndex(-1);
        queryBookBaseOnFiltration(1);
    }

    private void queryBookBaseOnFiltration(int pageNo) {
        BookStoreQueryBookInfoReq req = new BookStoreQueryBookInfoReq();
        req.setBookTitleMatch(bookTitle);
        req.setBookCategoryMatch(bookCategoryMatch);
        req.setBookCategory(bookCategory);
        req.setBookVersion(bookVersion);
        req.setPs(COUNT_PER_PAGE);
        req.setPn(pageNo);
        NetWorkManager.queryBookInfo(req).subscribe(new Action1<BaseResult<List<BookInfo>>>() {
            @Override
            public void call(BaseResult<List<BookInfo>> result) {
                LogUtils.e(tag, "bookInfos' size : " + result.getData().size());
                totalCount = result.getCount();
                SearchActivity.this.refreshResultView(result.getData());
            }
        });
    }

    public void cancel(View view) {
        hideFiltrateLayout();
    }

    private int currentItem = -1;

    public void clickTextBookTv(View view) {
        resetSubjectTv();
        binding.subjectLayout.setVisibility(View.GONE);
        resetVersionTv();
        binding.versionLayout.setVisibility(View.GONE);

        currentItem = 0;
        binding.textBookTv.setSelected(true);
        binding.guideBookTv.setSelected(false);
        binding.extraBookTv.setSelected(false);
        showGradeLayout();
    }

    public void clickGuideBookTv(View view) {
        resetSubjectTv();
        binding.subjectLayout.setVisibility(View.GONE);
        resetVersionTv();
        binding.versionLayout.setVisibility(View.GONE);

        currentItem = 1;
        binding.textBookTv.setSelected(false);
        binding.guideBookTv.setSelected(true);
        binding.extraBookTv.setSelected(false);
        showGradeLayout();
    }

    public void clickExtraBookTv(View view) {
        resetSubjectTv();
        binding.subjectLayout.setVisibility(View.GONE);
        resetVersionTv();
        binding.versionLayout.setVisibility(View.GONE);

        currentItem = 2;
        binding.textBookTv.setSelected(false);
        binding.guideBookTv.setSelected(false);
        binding.extraBookTv.setSelected(true);
        showGradeLayout();
    }

    private void resetFiltration() {
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

    private void showVersionLayout(){
        binding.versionLayout.setVisibility(View.VISIBLE);
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
        generateSubjectLayout(mStageAdapter.getItem(0));

        binding.gradeLayout.setVisibility(View.VISIBLE);
        binding.nameText.setText(getString(currentItem == 2 ? R.string.book_classify : R.string.grade_text));
    }

    public void stageClick(View view) {
        LogUtils.e(tag, "stage click............." + binding.stageRecyclerLayout.getVisibility() + "");
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

