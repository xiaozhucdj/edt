package com.yougy.shop.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frank.etude.pageBtnBar.PageBtnBarAdapter;
import com.yougy.anwser.BaseResult;
import com.yougy.common.bean.Result;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.new_network.BookStoreQueryBookInfoReq;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.protocol.request.NewBookStoreCategoryReq;
import com.yougy.common.protocol.request.NewBookStoreHomeReq;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.ResultUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.rx_subscriber.ShopSubscriber;
import com.yougy.shop.adapter.BookAdapter;
import com.yougy.shop.adapter.BookShopAdapter;
import com.yougy.shop.adapter.RecyclerAdapter;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.bean.CategoryInfo;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.ShopBinding;
import com.yougy.view.CustomLinearLayoutManager;
import com.yougy.view.decoration.GridSpacingItemDecoration;
import com.yougy.view.decoration.SpaceItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static rx.Observable.create;


/**
 * Created by jiangliang on 2017/3/2.
 */
public class BookShopActivityDB extends ShopBaseActivity implements BookShopAdapter.OnMoreClickListener {

//    @BindString(R.string.all_version)
//    String mAllVersion;
//    @BindString(R.string.school_version)
//    String mSchoolVersion;
    @BindArray(R.array.classifies)
    String[] mExtraClassifies;
    @BindArray(R.array.stages)
    String[] mStages;
    @BindArray(R.array.subjects)
    String[] mSubjects;
    @BindArray(R.array.versions)
    String[] mVersions;

    private String mStage = "";
    private String mVersion = "";
    private String mSubject = "";
    private String mComposite = "";

    private int stagePosition;

    //    private StageAdapter mExtraClassifyAdapter;
    //    private StageAdapter mStageAdapter;
    private RecyclerAdapter mStageAdapter;
    private RecyclerAdapter mExtraClassifyAdapter;

    private List<String> mClassifies = new ArrayList<>();
    private SparseIntArray mClassifyIds = new SparseIntArray();
    private int mClassifyId = -1;
    private List<List<BookInfo>> mRecommandInfos = new ArrayList<>();

    private List<BookInfo> textbooks;
    private List<BookInfo> guidbooks;
    private List<BookInfo> extrabooks;

    private BookAdapter mBookAdapter;
    private BookShopAdapter adapter;
    private List<String> mHistoryRecords;
    private String mSearchKey;
    private ShopBinding binding;

    private static final int COUNT_PER_PAGE = 15;
    private static final int SPAN_COUNT = 5;

    private SparseArray<List<CategoryInfo>> gradeSparseArray = new SparseArray<>();
    private SparseArray<List<CategoryInfo>> subjectSparseArray = new SparseArray<>();
    private SparseArray<List<CategoryInfo>> versionSparseArray = new SparseArray<>();

    private int totalCount = 0;

    private List<BookInfo> bookInfos = new ArrayList<BookInfo>();

    @Override
    protected void loadData() {
        Observable<List<BookInfo>> observable = Observable.zip(getCategoryInfo(), getHomeInfo(), (categoryInfos, bookInfos) -> {
            long start = System.currentTimeMillis();
            handleCategoryInfo(categoryInfos);
            long end = System.currentTimeMillis();
            LogUtils.e(tag, "call........................." + (end - start));
            return bookInfos;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        ShopSubscriber<List<BookInfo>> subscriber = new ShopSubscriber<List<BookInfo>>(this) {

            @Override
            public void onNext(List<BookInfo> bookInfos) {
                LogUtils.e(tag, "book infos' size : " + bookInfos.size());
                handleHomeInfo(bookInfos);
                onCompleted();
            }

            @Override
            public void require() {
                loadData();
            }
        };
        observable.subscribe(subscriber);
    }


    @Override
    protected void refreshView() {

    }

    public static List<CategoryInfo> grades = new ArrayList<>();

    private void handleCategoryInfo(List<CategoryInfo> categories) {
        for (int i = 0; i < categories.size(); i++) {
            CategoryInfo level1 = categories.get(i);
            mClassifies.add(level1.getCategoryDisplay());
            mClassifyIds.put(i, level1.getCategoryId());
            List<CategoryInfo> childs1 = level1.getCategoryList();
//            LogUtils.e(tag,"childs1 : " + childs1);
            gradeSparseArray.put(level1.getCategoryId(), childs1);
            grades.add(level1);
            if (null != childs1 && childs1.size() > 0) {
                for (int j = 0; j < childs1.size(); j++) {
                    CategoryInfo level2 = level1.getCategoryList().get(j);
                    List<CategoryInfo> childs2 = level2.getCategoryList();
                    subjectSparseArray.put(level2.getCategoryId(), childs2);
//                    LogUtils.e(tag, "childs2 : " + childs2);
                    if (null != childs2 && childs2.size() > 0) {
                        for (int n = 0; n < childs2.size(); n++) {
                            CategoryInfo level3 = childs2.get(n);
                            versionSparseArray.put(level3.getCategoryId(), level3.getCategoryList());
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取分类数据
     */
    private Observable<List<CategoryInfo>> getCategoryInfo() {
        return create(subscriber -> {
            try {
                long start = System.currentTimeMillis();
                Response response = NewProtocolManager.queryBookCategory(new NewBookStoreCategoryReq());
                long end = System.currentTimeMillis();
                LogUtils.e(tag,"getCategoryInfo takes time : " + (end - start));
                if (response.isSuccessful()) {
                    start = System.currentTimeMillis();
                    String resultJson = response.body().string();
                    end = System.currentTimeMillis();
                    LogUtils.e(tag, "category info size : " + resultJson.length() + ",take time : " + (end - start));
                    start = System.currentTimeMillis();
                    Result<List<CategoryInfo>> result = ResultUtils.fromJsonArray(resultJson, CategoryInfo.class);

//                    List<CategoryInfo> categories = json2list(resultJson);
                    end = System.currentTimeMillis();
                    LogUtils.e(tag,"getCategoryInfo jiexi shuju takes time : " + (end - start));
                    List<CategoryInfo> categories = result.getData();
                    LogUtils.e(tag, "categories' size : " + categories.size());

                    subscriber.onNext(categories);
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e(tag, "IOException : " + e.getMessage());
            }
        });
    }

    private void handleHomeInfo(List<BookInfo> bookInfos) {
        for (int i = 0; i < mClassifies.size(); i++) {
            String category = mClassifies.get(i);
            List<BookInfo> infos = new ArrayList<>();
            for (BookInfo info : bookInfos) {
                if (category.equals(info.getBookCategoryFamilyName())) {
                    infos.add(info);
                }
            }
            if ("教材".equals(category)) {
                textbooks = infos;
            } else if ("教辅".equals(category)) {
                guidbooks = infos;
                LogUtils.e(tag, "教辅：" + infos);
            } else {
                extrabooks = infos;
            }
            mRecommandInfos.add(infos);
        }
        adapter = new BookShopAdapter(mClassifies, mRecommandInfos);
        adapter.setOnMoreClickListener(BookShopActivityDB.this);
        binding.allClassifyRecycler.setAdapter(adapter);
    }

    /**
     * 获取首页数据
     */
    private Observable<List<BookInfo>> getHomeInfo() {
        return create(subscriber -> {
            long start = System.currentTimeMillis();
            Response response = NewProtocolManager.queryBookShopHome(new NewBookStoreHomeReq());
            long end = System.currentTimeMillis();
            LogUtils.e(tag,"getHomeInfo takes time : " + (end - start));
            if (response.isSuccessful()) {
                try {
                    String resultJson = response.body().string();
                    LogUtils.e(tag, "home info : " + resultJson);
                    start = System.currentTimeMillis();
                    Result<List<BookInfo>> result = ResultUtils.fromJsonArray(resultJson, BookInfo.class);
                    LogUtils.e(tag, "bookinfos : " + result.getData().size());
                    end = System.currentTimeMillis();
                    LogUtils.e(tag,"getHomeInfo jiexi shuju takes time : " + (end - start));
                    subscriber.onNext(result.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 根据类别获取图书信息
     */
    private void getSingleBookInfo(int pageNo) {
        BookStoreQueryBookInfoReq req = new BookStoreQueryBookInfoReq();
        req.setBookCategoryMatch(mClassifyId);
        LogUtils.e(tag, "get single book info classify id : " + mClassifyId);
        req.setPs(COUNT_PER_PAGE);
        req.setPn(pageNo);
        req.setUserId(SpUtils.getUserId());
        NetWorkManager.queryBookInfo(req).subscribe(new Action1<BaseResult<List<BookInfo>>>() {
            @Override
            public void call(BaseResult<List<BookInfo>> result) {
                totalCount = result.getCount();
                binding.pageBtnBar.refreshPageBar();
                bookInfos.clear();
                bookInfos.addAll(result.getData());
                BookShopActivityDB.this.refreshSingleClassifyRecycler(result.getData());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(tag, "error is : " + throwable.getMessage());
            }
        });
    }


    @Override
    protected void initLayout() {
//        mVersion = mSchoolVersion;
        binding.tvAll.setSelected(true);
        binding.allClassifyRecycler.addItemDecoration(new SpaceItemDecoration(UIUtils.px2dip(15)));
        binding.allClassifyRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.singleClassifyRecycler.addItemDecoration(new GridSpacingItemDecoration(SPAN_COUNT, UIUtils.px2dip(32), false));
        binding.singleClassifyRecycler.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        binding.correspondSchool.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showAllVersion = !isChecked;
            hideRecycler();
            if (isChecked) {
                hideFiltrateTv();
                hideFiltrateLayout();
//                mVersion = mSchoolVersion;
            } else {
                showFiltrateTv();
//                mVersion = mAllVersion;
            }
            setCompositeText();
            refreshSingleClassifyRecycler(bookInfos);
        });
        binding.subjectWrap.setHorizontalMargin(22);
        binding.subjectWrap.setVerticalMargin(20);
        binding.versionWrap.setHorizontalMargin(22);
        binding.versionWrap.setVerticalMargin(20);
        binding.searchEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                hideRecycler();
                LogUtils.e(tag, "search et onFocusChange...........");
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
        binding.searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                hideSearchLayout();
                mSearchKey = binding.searchEt.getText().toString();
                if (!TextUtils.isEmpty(mSearchKey)) {
                    search();
                }
                return false;
            }
        });

        CustomLinearLayoutManager classifyManager = new CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        classifyManager.setScrollHorizontalEnabled(false);
        binding.classifyRecycler.setLayoutManager(classifyManager);
        binding.classifyRecycler.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.classifyRecycler) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                hideRecycler();
                classifyItemClick(vh.getAdapterPosition());
            }
        });
        CustomLinearLayoutManager stageManager = new CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        stageManager.setScrollHorizontalEnabled(false);
        binding.stageRecycler.setLayoutManager(stageManager);
        binding.stageRecycler.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.stageRecycler) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                hideRecycler();
                stageItemClick(vh.getAdapterPosition());
            }
        });
        binding.pageBtnBar.setPageBarAdapter(new PageBtnBarAdapter(getThisActivity()) {
            @Override
            public int getPageBtnCount() {
                return (totalCount + COUNT_PER_PAGE - 1)/ COUNT_PER_PAGE;
            }

            @Override
            public void onPageBtnClick(View view, int i, String s) {
                hideRecycler();
                getSingleBookInfo(i + 1);
            }
        });
        mBookAdapter = new BookAdapter(bookInfos, this);
        binding.singleClassifyRecycler.setAdapter(mBookAdapter);
        binding.singleClassifyRecycler.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.singleClassifyRecycler) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                itemClick(mBookAdapter.getItemBook(vh.getAdapterPosition()));
            }
        });
    }

    public void stageClick(View view) {
        LogUtils.e(tag, "stage click.............");
        hideFiltrateLayout();
        binding.stageRecyclerLayout.setVisibility(binding.stageRecyclerLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    public void classifyClick(View view) {
        LogUtils.e(tag, "classify click......");
        binding.classifyRecyclerLayout.setVisibility(binding.classifyRecyclerLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    /**
     * 生成历史搜索记录
     */
    private void generateHistoryRecord() {
        mHistoryRecords = SpUtils.getHistoryRecord();
        binding.historyRecordLayout.removeAllViews();
        for (final String record : mHistoryRecords) {
            TextView recordTv = new TextView(this);
            recordTv.setText(record);
            recordTv.setIncludeFontPadding(false);
            recordTv.setTextSize(UIUtils.px2dip(24));
            recordTv.setTextColor(getResources().getColor(R.color.text_color_black));
            recordTv.setOnClickListener(v -> {
                mSearchKey = record;
                hideSearchLayout();
                search();
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = UIUtils.px2dip(20);
            binding.historyRecordLayout.addView(recordTv, params);
        }
    }

    @Override
    protected void init() {
    }

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.book_shop_layout_auto);
        binding.setActivity(this);
    }

    public void clickTvAll(View view) {
        hideRecycler();
        mClassifyId = -1;
        mClassifyPosition = -1;
        hideSpinnerLayout();
        binding.tvAll.setSelected(true);
        binding.guidebook.setSelected(false);
        binding.extrabook.setSelected(false);
        binding.textbook.setSelected(false);
        hideAll();
        binding.singleClassifyLayout.setVisibility(View.GONE);
        binding.allClassifyRecycler.setVisibility(View.VISIBLE);
    }

    public void clickTextBook(View view) {
        hideRecycler();
        textbookSelected();
    }

    public void clickGuideBook(View view) {
        hideRecycler();
        guidebookSelected();
    }

    public void clickExtraBook(View view) {
        hideRecycler();
        guidebookSelected();
        extrabookSelected();
    }

    public void clickShoolBag(View view) {
        hideRecycler();
        finish();
    }

    public void clickCartGo(View view) {
        hideRecycler();
        loadIntent(ShopCartActivity.class);
    }

    public void clickFavorite(View view) {
        hideRecycler();
        loadIntent(ShopFavoriteActivity.class);
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

    public void filtrate(View view) {
        //TODO:显示筛选条件布局
        if (binding.filtrateLayout.getVisibility() == View.VISIBLE) {
            binding.filtrateLayout.setVisibility(View.GONE);
            binding.versionLayout.setVisibility(View.GONE);
        } else {
            binding.filtrateLayout.setVisibility(View.VISIBLE);
        }
        for (int j = 0; j < binding.subjectWrap.getChildCount(); j++) {
            View v = binding.subjectWrap.getChildAt(j);
            if (v.isSelected()) {
                v.setSelected(false);
            }
        }
    }

    public void clickSearchOrCancelTv(View view) {
        hideSearchLayout();
        mSearchKey = binding.searchEt.getText().toString();
        if (!TextUtils.isEmpty(mSearchKey)) {
            search();
        }
    }

    public void clearHistoryRecord(View view) {
        SpUtils.clearHistoryRecord();
        mHistoryRecords.clear();
        generateHistoryRecord();
    }

    private void hideSearchLayout() {
        InputMethodManager inputManager1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager1.hideSoftInputFromWindow(binding.searchEt.getWindowToken(), 0);
        binding.searchLayout.setVisibility(View.GONE);
        binding.searchEt.clearFocus();
    }

    private void search() {
        if (!mHistoryRecords.contains(mSearchKey)) {
            if (mHistoryRecords.contains(UIUtils.getContext().getResources().getString(R.string.no_history_record))) {
                mHistoryRecords.clear();
            }
            mHistoryRecords.add(mSearchKey);
            SpUtils.putHistoryRecord(mHistoryRecords);
        }
        Bundle extras = new Bundle();
        extras.putString("search_key", mSearchKey);
        extras.putInt("categoryId", mClassifyId);
        loadIntentWithExtras(SearchActivity.class, extras);
    }

    private int mClassifyPosition = -1;

    private void extrabookSelected() {
        if (mClassifyPosition == 2) {
            return;
        }
        mClassifyPosition = 2;
        hideFiltrateLayout();
        binding.tvAll.setSelected(false);
        binding.textbook.setSelected(false);
        binding.guidebook.setSelected(false);
        binding.extrabook.setSelected(true);
        mExtraClassifyAdapter = new RecyclerAdapter(gradeSparseArray.get(mClassifyIds.get(mClassifyPosition)));
        binding.classifyRecycler.setAdapter(mExtraClassifyAdapter);
        hideFiltrateTv();
        hideCorrespondSchoolCb();
        showClassifySpinner();
        refreshSearchResultView();
        resetComposite();
//        if (extrabooks == null) {
        binding.pageBtnBar.setCurrentSelectPageIndex(-1);
        getSingleBookInfo(1);
//        } else {
//            refreshSingleClassifyRecycler(extrabooks);
//        }
    }

    private void stageItemClick(int position) {
        if (binding.filtrateLayout.getVisibility() == View.VISIBLE) {
            generateFiltrateLayout();
        }
        stagePosition = position;
        CategoryInfo info = mStageAdapter.getItem(position);
        mStage = info.getCategoryDisplay();
        mClassifyId = info.getCategoryId();
        gradeDisplay = mStage;
        LogUtils.e("Spinner", "stage is : " + mStage);
        binding.stageButton.setText(info.getCategoryDisplay());
        setCompositeText();
        binding.pageBtnBar.setCurrentSelectPageIndex(-1);
        getSingleBookInfo(1);
        binding.stageRecyclerLayout.setVisibility(View.GONE);
    }

    private void classifyItemClick(int position) {
        CategoryInfo info = mExtraClassifyAdapter.getItem(position);
        mSubject = info.getCategoryDisplay();
        mClassifyId = info.getCategoryId();
        extraDisplay = mSubject;
        extraId = mClassifyId;
        LogUtils.e("Spinner", "subject is : " + mSubject);
        binding.classifyButton.setText(info.getCategoryDisplay());
        binding.compositeInfo.setText(mSubject);
        mVersion = "";
        binding.pageBtnBar.setCurrentSelectPageIndex(-1);
        getSingleBookInfo(1);
        binding.classifyRecyclerLayout.setVisibility(View.GONE);
    }

    private String gradeDisplay = "";

    private void guidebookSelected() {
        if (mClassifyPosition == 1) {
            return;
        }
        mClassifyPosition = 1;
        confirmClassifyId();
        gradeDisplay = mStage;
        LogUtils.e(tag, "stage's size : " + gradeSparseArray.get(mClassifyIds.get(mClassifyPosition)).size());
        mStageAdapter = new RecyclerAdapter(gradeSparseArray.get(mClassifyIds.get(mClassifyPosition)));
        binding.stageRecycler.setAdapter(mStageAdapter);

        showStageLayout();
        binding.tvAll.setSelected(false);
        binding.textbook.setSelected(false);
        binding.extrabook.setSelected(false);
        binding.guidebook.setSelected(true);
        hideClassifySpinner();
        hideFiltrateLayout();
//        if (binding.correspondSchool.isChecked()) {
//            hideFiltrateTv();
//        } else {
            showFiltrateTv();
//        }
        showCorrespondSchoolCb();
        refreshSearchResultView();
        resetComposite();
        setCompositeText();
//        if (guidbooks == null) {
        binding.pageBtnBar.setCurrentSelectPageIndex(-1);
        getSingleBookInfo(1);
//        } else {
//            refreshSingleClassifyRecycler(guidbooks);
//        }
    }


    private void textbookSelected() {
        if (mClassifyPosition == 0) {
            return;
        }
        mClassifyPosition = 0;
        confirmClassifyId();
        gradeDisplay = mStage;
        mStageAdapter = new RecyclerAdapter(gradeSparseArray.get(mClassifyIds.get(mClassifyPosition)));
        binding.stageRecycler.setAdapter(mStageAdapter);
        showStageLayout();
        hideFiltrateLayout();
        binding.tvAll.setSelected(false);
        binding.guidebook.setSelected(false);
        binding.extrabook.setSelected(false);
        binding.textbook.setSelected(true);
        hideClassifySpinner();
//        if (binding.correspondSchool.isChecked()) {
//            hideFiltrateTv();
//        } else {
            showFiltrateTv();
//        }
        showCorrespondSchoolCb();
        refreshSearchResultView();
        resetComposite();
        setCompositeText();
//        if (textbooks == null) {
        binding.pageBtnBar.setCurrentSelectPageIndex(-1);
        getSingleBookInfo(1);
//        } else {
//            refreshSingleClassifyRecycler(textbooks);
//        }
    }

    private boolean showAllVersion;

    private void refreshSingleClassifyRecycler(List<BookInfo> infos) {
        LogUtils.e(tag, "refreshSingleClassifyRecycler..............");
        mBookAdapter.notifyDataSetChanged();
        binding.emptyTv.setVisibility(infos.size() == 0 ? View.VISIBLE : View.GONE);
    }

    private void itemClick(BookInfo bookInfo) {
        loadIntentWithExtra(ShopBookDetailsActivity.class, ShopGloble.BOOK_ID, Integer.parseInt(bookInfo.getBookId() + ""));
    }

    private void resetComposite() {
        mSubject = "";
//        mVersion = binding.correspondSchool.isChecked() ? mSchoolVersion : mAllVersion;
//        if (binding.classifyButton.getVisibility() == View.VISIBLE) {
            mVersion = "";
//        }
    }

    private void setCompositeText() {
        LogUtils.e(tag, "subject : " + mSubject + ",version : " + mVersion);
        mComposite = mStage + (TextUtils.isEmpty(mSubject) ? "" : ">") + mSubject + (TextUtils.isEmpty(mVersion) ? "" : ">") + mVersion;
        binding.compositeInfo.setText(mComposite);
    }

    private void refreshSearchResultView() {
        if (binding.allClassifyRecycler.getVisibility() == View.VISIBLE) {
            binding.allClassifyRecycler.setVisibility(View.GONE);
        }
        if (binding.singleClassifyLayout.getVisibility() == View.GONE) {
            binding.singleClassifyLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideRecycler() {
        if (binding.stageRecyclerLayout.getVisibility() == View.VISIBLE) {
            binding.stageRecyclerLayout.setVisibility(View.GONE);
        }
        if (binding.classifyRecyclerLayout.getVisibility() == View.VISIBLE) {
            binding.classifyRecyclerLayout.setVisibility(View.GONE);
        }
    }

    private void hideAll() {
        hideClassifySpinner();
        hideCorrespondSchoolCb();
        hideFiltrateTv();
    }

    /**
     * 显示Spinner布局
     */
    private void showStageLayout() {
        if (binding.spinnerLayout.getVisibility() == View.GONE) {
            binding.spinnerLayout.setVisibility(View.VISIBLE);
        }

        binding.stageButton.setText(mStage);
        setCompositeText();
        if (binding.stageButton.getVisibility() == View.GONE) {
            binding.stageButton.setVisibility(View.VISIBLE);
        }
    }

    private void confirmClassifyId() {
        String grade = SpUtils.getStudent().getGradeDisplay();
        if (!TextUtils.isEmpty(gradeDisplay)) {
            grade = gradeDisplay;
        }
        if (TextUtils.isEmpty(grade)) {
            mStage = gradeSparseArray.get(mClassifyIds.get(mClassifyPosition)).get(0).getCategoryDisplay();
            mClassifyId = gradeSparseArray.get(mClassifyIds.get(mClassifyPosition)).get(0).getCategoryId();
        } else {
            mStage = grade;
            for (int position = 0; position < gradeSparseArray.get(mClassifyIds.get(mClassifyPosition)).size(); position++) {
//            for (CategoryInfo info : gradeSparseArray.get(mClassifyIds.get(mClassifyPosition))) {
                CategoryInfo info = gradeSparseArray.get(mClassifyIds.get(mClassifyPosition)).get(position);
                if (mStage.equals(info.getCategoryDisplay())) {
                    mClassifyId = info.getCategoryId();
                    stagePosition = position;
                    LogUtils.e(tag, "show spinner layout classify id : " + mClassifyId + ", stage : " + mStage);
                    break;
                }
            }
        }
    }

    public void hideStageRecyclerLayout(View view) {
        if (binding.stageRecyclerLayout.getVisibility() == View.VISIBLE) {
            binding.stageRecyclerLayout.setVisibility(View.GONE);
        }
    }

    public void hideClassifyRecyclerLayout(View view) {
        if (binding.classifyRecyclerLayout.getVisibility() == View.VISIBLE) {
            binding.classifyRecyclerLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏Spinner布局
     */
    private void hideSpinnerLayout() {
        if (binding.spinnerLayout.getVisibility() == View.VISIBLE) {
            binding.spinnerLayout.setVisibility(View.GONE);
        }
    }


    /**
     * 隐藏课外书分类下拉列表
     */
    private void hideClassifySpinner() {
        if (binding.classifyButton.getVisibility() == View.VISIBLE) {
            binding.classifyButton.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏只显示学校版本选项
     */
    private void hideCorrespondSchoolCb() {
//        if (binding.correspondSchool.getVisibility() == View.VISIBLE) {
//            binding.correspondSchool.setVisibility(View.GONE);
//        }
        if (binding.filtrate.getVisibility() == View.VISIBLE){
            binding.filtrate.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏筛选按钮
     */
    private void hideFiltrateTv() {
        if (binding.filtrate.getVisibility() == View.VISIBLE) {
            binding.filtrate.setVisibility(View.GONE);
        }
        if (binding.versionLayout.getVisibility() == View.VISIBLE) {
            binding.versionLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏筛选布局
     */
    public void hideFiltrateLayout(View view){
        hideFiltrateLayout();
    }
    private void hideFiltrateLayout() {
        for (int i = 0; i < binding.subjectWrap.getChildCount(); i++) {
            View child = binding.subjectWrap.getChildAt(i);
            if (child.isSelected()) {
                child.setSelected(false);
            }
        }

        if (binding.filtrateLayout.getVisibility() == View.VISIBLE) {
            binding.filtrateLayout.setVisibility(View.GONE);
        }
        if (binding.versionLayout.getVisibility() == View.VISIBLE) {
            binding.versionLayout.setVisibility(View.GONE);
        }

    }

    /**
     * 显示筛选按钮
     */
    private void showFiltrateTv() {
        if (binding.filtrate.getVisibility() == View.GONE) {
            binding.filtrate.setVisibility(View.VISIBLE);
        }
        generateFiltrateLayout();
    }

    private void generateFiltrateLayout() {
        List<CategoryInfo> gradeInfos = gradeSparseArray.get(mClassifyIds.get(mClassifyPosition));
        List<CategoryInfo> subjectInfos = subjectSparseArray.get(gradeInfos.get(stagePosition).getCategoryId());
        generateSubjectLayout(subjectInfos);
    }

    private int bookCategory;

    private void generateSubjectLayout(List<CategoryInfo> subjectInfos) {
        binding.subjectWrap.removeAllViews();
        for (int i = 0; i < subjectInfos.size(); i++) {
            final CategoryInfo info = subjectInfos.get(i);
            View layout = View.inflate(this, R.layout.text_view, null);
            final TextView subjectTv = (TextView) layout.findViewById(R.id.text_tv);
            String display = info.getCategoryDisplay();
            if (display.length() > 4) {
                display = display.substring(0, 4);
            }
            subjectTv.setText(display);
            subjectTv.setOnClickListener(v -> {
                for (int i1 = 0; i1 < binding.subjectWrap.getChildCount(); i1++) {
                    View child = binding.subjectWrap.getChildAt(i1);
                    TextView tv = (TextView) child.findViewById(R.id.text_tv);
                    if (tv.isSelected()) {
                        tv.setSelected(false);
                    }
                }
                subjectTv.setSelected(true);
                bookCategory = info.getCategoryId();
                mSubject = info.getCategoryDisplay();
                generateVersionLayout(info);
            });
            binding.subjectWrap.addView(layout);
        }
    }

    private void generateVersionLayout(CategoryInfo info) {
        binding.versionWrap.removeAllViews();
        List<CategoryInfo> versionInfos = versionSparseArray.get(info.getCategoryId());
        if (versionInfos == null || versionInfos.size() == 0) {
            ToastUtil.showCustomToast(this, "没有对应版本。。。");
            return;
        }
        LogUtils.e(tag, "version's size : " + versionInfos.size());
        for (int j = 0; j < versionInfos.size(); j++) {
            final CategoryInfo versionInfo = versionInfos.get(j);
            View layout = View.inflate(this, R.layout.text_view, null);
            final TextView versionTv = (TextView) layout.findViewById(R.id.text_tv);
            String display = versionInfo.getCategoryDisplay();
            if (display.length() > 4) {
                display = display.substring(0, 4);
            }
            versionTv.setText(display);
            versionTv.setOnClickListener(v -> {
                for (int i = 0; i < binding.versionWrap.getChildCount(); i++) {
                    View child = binding.versionWrap.getChildAt(i);
                    TextView tv = (TextView) child.findViewById(R.id.text_tv);
                    if (tv.isSelected()) {
                        tv.setSelected(false);
                    }
                }
                LogUtils.e(tag, "category id : " + bookCategory + ",version id : " + versionInfo.getCategoryId());
                mVersion = versionInfo.getCategoryDisplay();
                versionTv.setSelected(true);
                int bookVersion = versionInfo.getCategoryId();
                //TODO:图书查询接口
                BookStoreQueryBookInfoReq req = new BookStoreQueryBookInfoReq();
                req.setBookCategoryMatch(bookCategory);
                req.setBookVersion(bookVersion);
                queryBookByVersion(req);
            });
            binding.versionWrap.addView(layout);
        }
        binding.versionLayout.setVisibility(View.VISIBLE);
    }

    private void queryBookByVersion(final BookStoreQueryBookInfoReq req) {
        NetWorkManager.queryBookInfo(req).subscribe(new Action1<BaseResult<List<BookInfo>>>() {
            @Override
            public void call(BaseResult<List<BookInfo>> result) {
                BookShopActivityDB.this.setCompositeText();
                BookShopActivityDB.this.hideFiltrateLayout();
                BookShopActivityDB.this.refreshSingleClassifyRecycler(result.getData());
            }
        });
    }


    /**
     * 显示只显示学校版本
     */
    private void showCorrespondSchoolCb() {
//        if (binding.correspondSchool.getVisibility() == View.GONE) {
//            binding.correspondSchool.setVisibility(View.VISIBLE);
//        }
        if(binding.filtrate.getVisibility() == View.GONE){
            binding.filtrate.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示课外书分类下拉列表
     */
    private int extraId = -1;
    private String extraDisplay = "";

    private void showClassifySpinner() {
        if (binding.spinnerLayout.getVisibility() == View.GONE) {
            binding.spinnerLayout.setVisibility(View.VISIBLE);
        }
        if (binding.stageButton.getVisibility() == View.VISIBLE) {
            binding.stageButton.setVisibility(View.GONE);
        }
        if (binding.classifyButton.getVisibility() == View.GONE) {
            binding.classifyButton.setVisibility(View.VISIBLE);
            String classify = gradeSparseArray.get(mClassifyIds.get(mClassifyPosition)).get(0).getCategoryDisplay();
            mClassifyId = gradeSparseArray.get(mClassifyIds.get(mClassifyPosition)).get(0).getCategoryId();
            if (extraId != -1) {
                mClassifyId = extraId;
            } else {
                extraId = mClassifyId;
            }

            if (!TextUtils.isEmpty(extraDisplay)) {
                classify = extraDisplay;
            } else {
                extraDisplay = classify;
            }
            binding.classifyButton.setText(classify);
            binding.compositeInfo.setText(classify);
        }
    }

    @Override
    public void onMoreClick(int position) {
        switch (position) {
            case 0:
                textbookSelected();
                break;
            case 1:
                guidebookSelected();
                break;
            case 2:
                extrabookSelected();
                break;
        }
    }

    public void clickEmpty(View view) {
        hideFiltrateLayout();
    }

    private void refreshCartCount() {
        NetWorkManager.queryCart(String.valueOf(SpUtils.getUserId()))
                .subscribe(cartItems -> {
                    if (cartItems == null) {
                        binding.cartCountTv.setText("0");
                    } else {
                        if (cartItems.size() > 99) {
                            binding.cartCountTv.setText("…");
                        } else {
                            binding.cartCountTv.setText("" + cartItems.size());
                        }
                    }
                }, throwable -> {
                    LogUtils.e("FH", "获取购物车失败");
                    throwable.printStackTrace();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCartCount();
    }
}
