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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yougy.common.bean.Result;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.ResultUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.shop.adapter.BookAdapter;
import com.yougy.shop.adapter.BookShopAdapter;
import com.yougy.shop.adapter.StageAdapter;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.bean.CategoryInfo;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.ShopBinding;
import com.yougy.view.decoration.GridSpacingItemDecoration;
import com.yougy.view.decoration.SpaceItemDecoration;
import com.yougy.view.dialog.LoadingProgressDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindString;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;


/**
 * Created by jiangliang on 2017/3/2.
 */
public class BookShopActivityDB extends ShopBaseActivity implements BookShopAdapter.OnMoreClickListener {

    @BindString(R.string.all_version)
    String mAllVersion;
    @BindString(R.string.school_version)
    String mSchoolVersion;
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

    private StageAdapter mStageAdapter;
    private StageAdapter mExtraClassifyAdapter;
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

    private List<Button> btns = new ArrayList<>();
    private List<BookInfo> mPageInfos = new ArrayList<>();
    private SparseArray<List<CategoryInfo>> gradeSparseArray = new SparseArray<>();
    private SparseArray<List<CategoryInfo>> subjectSparseArray = new SparseArray<>();

    @Override
    protected void loadData() {
        mHistoryRecords = SpUtil.getHistoryRecord();
        Observable.zip(getCategoryInfo(), getHomeInfo(), new Func2<List<CategoryInfo>, List<BookInfo>, List<BookInfo>>() {

            @Override
            public List<BookInfo> call(List<CategoryInfo> categoryInfos, List<BookInfo> bookInfos) {
                handleCategoryInfo(categoryInfos);
                return bookInfos;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<BookInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<BookInfo> bookInfos) {
                        handleHomeInfo(bookInfos);
                    }
                });
    }


    @Override
    protected void refreshView() {

    }

    private void handleCategoryInfo(List<CategoryInfo> categories) {
        for (int i = 0; i < categories.size(); i++) {
            CategoryInfo level1 = categories.get(i);
            mClassifies.add(level1.getCategoryDisplay());
            mClassifyIds.put(i, level1.getCategoryId());
            gradeSparseArray.put(level1.getCategoryId(), level1.getChilds());
            for (int j = 0; j < level1.getChilds().size(); j++) {
                CategoryInfo level2 = level1.getChilds().get(j);
                subjectSparseArray.put(level2.getCategoryId(), level2.getChilds());
            }
        }
    }

    /**
     * 获取分类数据
     */
    private Observable<List<CategoryInfo>> getCategoryInfo() {
        return Observable.create(new Observable.OnSubscribe<List<CategoryInfo>>() {
            @Override
            public void call(Subscriber<? super List<CategoryInfo>> subscriber) {
                try {
                    Response response = ProtocolManager.queryBookCategoryProtocol(SpUtil.getUserId(), -1);
                    if (response.isSuccessful()) {
                        String resultJson = response.body().string();
                        Result<List<CategoryInfo>> result = ResultUtils.fromJsonArray(resultJson, CategoryInfo.class);
                        List<CategoryInfo> categories = result.getData();
                        subscriber.onNext(categories);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleHomeInfo(List<BookInfo> bookInfos) {
        for (int i = 0; i < mClassifies.size(); i++) {
            String category = mClassifies.get(i);
            List<BookInfo> infos = new ArrayList<>();
            for (BookInfo info : bookInfos) {
                if (category.equals(info.getBookCategoryFamily())) {
                    infos.add(info);
                }
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
    private Observable getHomeInfo() {
        return Observable.create(new Observable.OnSubscribe<List<BookInfo>>() {
            @Override
            public void call(Subscriber<? super List<BookInfo>> subscriber) {
                Response response = ProtocolManager.requireBookMainProtocol(SpUtil.getUserId(), "", -1, -1, -1, -1);
                if (response.isSuccessful()) {
                    try {
                        String resultJson = response.body().string();
                        Result<List<BookInfo>> result = ResultUtils.fromJsonArray(resultJson, BookInfo.class);
                        subscriber.onNext(result.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 根据类别获取图书信息
     */
    private void getSingleBookInfo() {
        Observable.create(new Observable.OnSubscribe<List<BookInfo>>() {
            @Override
            public void call(Subscriber<? super List<BookInfo>> subscriber) {
                Response response = ProtocolManager.queryBookProtocol(SpUtil.getUserId(), "", mClassifyId, -1, -1);
                if (response.isSuccessful()) {
                    try {
                        String resultJson = response.body().string();
                        Result<List<BookInfo>> result = ResultUtils.fromJsonArray(resultJson, BookInfo.class);
                        List<BookInfo> bookInfos = result.getData();
                        subscriber.onNext(bookInfos);
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<BookInfo>>() {
                    LoadingProgressDialog loadingDialog = new LoadingProgressDialog(BookShopActivityDB.this);

                    @Override
                    public void onStart() {
                        super.onStart();
                        if (!loadingDialog.isShowing()) {
                            loadingDialog.show();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onNext(List<com.yougy.shop.bean.BookInfo> bookInfos) {
//                        switch (mClassifyPosition) {
//                            case 0:
//                                textbooks = bookInfos;
//                                break;
//                            case 1:
//                                guidbooks = bookInfos;
//                                break;
//                            case 2:
//                                extrabooks = bookInfos;
//                                break;
//                        }
                        refreshSingleClassifyRecycler(bookInfos);
                    }
                });
    }


    @Override
    protected void initLayout() {
        mVersion = mSchoolVersion;
        binding.tvAll.setSelected(true);
        binding.allClassifyRecycler.addItemDecoration(new SpaceItemDecoration(UIUtils.px2dip(15)));
        binding.allClassifyRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.singleClassifyRecycler.addItemDecoration(new GridSpacingItemDecoration(SPAN_COUNT, UIUtils.px2dip(32), false));
        binding.singleClassifyRecycler.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        binding.spinnerStage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (binding.filtrateLayout.getVisibility() == View.VISIBLE) {
                    generateFiltrateLayout();
                }
                stagePosition = position;
                mStage = mStageAdapter.getItem(position).getCategoryDisplay();
                mClassifyId = mStageAdapter.getItem(position).getCategoryId();
                LogUtils.e("Spinner", "stage is : " + mStage);
                setCompositeText();
                //TODO:确定该扩展分类的层级后发送请求
//                ProtocolManager.queryBookProtocol(SpUtil.getUserId(), mStage, 1, -1, -1, ProtocolId.PROTOCOL_ID_QUERY_BOOK, new QueryBookCallBack(BookShopActivityDB.this));
                getSingleBookInfo();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.spinnerClassify.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSubject = mExtraClassifyAdapter.getItem(position).getCategoryDisplay();
                mClassifyId = mExtraClassifyAdapter.getItem(position).getCategoryId();
                LogUtils.e("Spinner", "subject is : " + mSubject);
                mVersion = "";
                setCompositeText();
                getSingleBookInfo();
                //TODO:确定该扩展分类的层级后发送请求
//                ProtocolManager.queryBookProtocol(SpUtil.getUserId(), mSubject, 1, -1, -1, ProtocolId.PROTOCOL_ID_QUERY_BOOK, new QueryBookCallBack(BookShopActivityDB.this));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.correspondSchool.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hideFiltrateTv();
                    hideFiltrateLayout();
                    mVersion = mSchoolVersion;
                } else {
                    showFiltrateTv();
                    mVersion = mAllVersion;
                }
                setCompositeText();
            }
        });
        binding.subjectWrap.setHorizontalMargin(50);
        binding.subjectWrap.setVerticalMargin(10);
        binding.versionWrap.setHorizontalMargin(50);
        binding.versionWrap.setVerticalMargin(10);
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
    }

    private void generateBtn(final List<BookInfo> mBookInfos) {
        if (binding.pageNumberLayout.getChildCount() != 0) {
            binding.pageNumberLayout.removeAllViews();
        }
        int count = mBookInfos.size() % COUNT_PER_PAGE == 0 ? mBookInfos.size() / COUNT_PER_PAGE : mBookInfos.size() / COUNT_PER_PAGE + 1;
        for (int index = 1; index <= count; index++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = UIUtils.px2dip(20);
            View pageLayout = View.inflate(this, R.layout.page_item, null);
            final Button pageBtn = (Button) pageLayout.findViewById(R.id.page_btn);
            if (index == 1) {
                pageBtn.setSelected(true);
            }
            pageBtn.setText(Integer.toString(index));
            btns.add(pageBtn);
            final int page = index - 1;
            pageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                    mBookAdapter.notifyDataSetChanged();
                }
            });
            binding.pageNumberLayout.addView(pageBtn, params);
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
            recordTv.setTextSize(UIUtils.px2dip(20));
            recordTv.setTextColor(getResources().getColor(R.color.text_color_black));
            recordTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSearchKey = record;
                    hideSearchLayout();
                    search();
                }
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
        mClassifyId = -1;
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
        textbookSelected();
    }

    public void clickGuideBook(View view) {
        guidebookSelected();
    }

    public void clickExtraBook(View view) {
        extrabookSelected();
    }

    public void clickShoolBag(View view) {
        finish();
    }

    public void clickCartGo(View view) {
        loadIntent(NewShopCartActivity.class);
    }

    public void clickFavorite(View view) {
        loadIntent(NewShopFavoriteActivity.class);
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
    }

    public void clickSearchOrCancelTv(View view) {
        hideSearchLayout();
        mSearchKey = binding.searchEt.getText().toString();
        if (!TextUtils.isEmpty(mSearchKey)) {
            search();
        }
    }

    public void clearHistoryRecord(View view) {
        SpUtil.clearHistoryRecord();
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
            SpUtil.putHistoryRecord(mHistoryRecords);
        }
        Bundle extras = new Bundle();
        extras.putString("search_key", mSearchKey);
        extras.putInt("categoryId",mClassifyId);
        loadIntentWithExtras(SearchActivityDb.class, extras);
    }

    private int mClassifyPosition;

    private void extrabookSelected() {
        mClassifyPosition = 2;
        mExtraClassifyAdapter = new StageAdapter(gradeSparseArray.get(mClassifyIds.get(2)));
        binding.spinnerClassify.setAdapter(mExtraClassifyAdapter);
        showSpinnerLayout();
        hideFiltrateLayout();
        binding.tvAll.setSelected(false);
        binding.textbook.setSelected(false);
        binding.guidebook.setSelected(false);
        binding.extrabook.setSelected(true);
        hideFiltrateTv();
        hideCorrespondSchoolCb();
        showClassifySpinner();
        refreshSearchResultView();
        resetComposite();
        setCompositeText();
//        if (extrabooks == null) {
//            getSingleBookInfo();
//        } else {
//            refreshSingleClassifyRecycler(extrabooks);
//        }
    }

    private void guidebookSelected() {
        mClassifyPosition = 1;
        mStageAdapter = new StageAdapter(gradeSparseArray.get(mClassifyIds.get(1)));
        binding.spinnerStage.setAdapter(mStageAdapter);
        showSpinnerLayout();
        binding.tvAll.setSelected(false);
        binding.textbook.setSelected(false);
        binding.extrabook.setSelected(false);
        binding.guidebook.setSelected(true);
        hideClassifySpinner();
        hideFiltrateLayout();
        if (binding.correspondSchool.isChecked()) {
            hideFiltrateTv();
        } else {
            showFiltrateTv();
        }
        showCorrespondSchoolCb();
        refreshSearchResultView();
        resetComposite();
        setCompositeText();
//        if (guidbooks == null) {
//            getSingleBookInfo();
//        } else {
//            refreshSingleClassifyRecycler(guidbooks);
//        }
    }

    private void textbookSelected() {
        mClassifyPosition = 0;
//        mStageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mStages);
        mStageAdapter = new StageAdapter(gradeSparseArray.get(mClassifyIds.get(0)));
        binding.spinnerStage.setAdapter(mStageAdapter);
        showSpinnerLayout();
        hideFiltrateLayout();
        binding.tvAll.setSelected(false);
        binding.guidebook.setSelected(false);
        binding.extrabook.setSelected(false);
        binding.textbook.setSelected(true);
        hideClassifySpinner();
        if (binding.correspondSchool.isChecked()) {
            hideFiltrateTv();
        } else {
            showFiltrateTv();
        }
        showCorrespondSchoolCb();
        refreshSearchResultView();
        resetComposite();
        setCompositeText();
//        if (textbooks == null) {
//            getSingleBookInfo();
//        } else {
//            refreshSingleClassifyRecycler(textbooks);
//        }
    }

    private void refreshSingleClassifyRecycler(List<BookInfo> infos) {
        generateBtn(infos);
        if (mPageInfos.size() > 0) {
            mPageInfos.clear();
        }
        int end = infos.size() > COUNT_PER_PAGE ? COUNT_PER_PAGE : infos.size();
        mPageInfos.addAll(infos.subList(0, end));
        mBookAdapter = new BookAdapter(mPageInfos);
        binding.singleClassifyRecycler.setAdapter(mBookAdapter);
        binding.singleClassifyRecycler.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.singleClassifyRecycler) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                itemClick(mBookAdapter.getItemBook(vh.getAdapterPosition()));
            }
        });
    }

    private void itemClick(BookInfo bookInfo) {
        Bundle extras = new Bundle();
        extras.putParcelable("book_info", bookInfo);
        loadIntentWithExtras(NewBookItemDetailsActivity.class, extras);
    }

    private void resetComposite() {
        mSubject = "";
        mVersion = binding.correspondSchool.isChecked() ? mSchoolVersion : mAllVersion;
        if (binding.spinnerClassify.getVisibility() == View.VISIBLE) {
            mVersion = "";
        }
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

    private void hideAll() {
        hideClassifySpinner();
        hideCorrespondSchoolCb();
        hideFiltrateTv();
    }

    /**
     * 显示Spinner布局
     */
    private void showSpinnerLayout() {
        if (binding.spinnerLayout.getVisibility() == View.GONE) {
            binding.spinnerLayout.setVisibility(View.VISIBLE);
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
        if (binding.spinnerClassify.getVisibility() == View.VISIBLE) {
            binding.spinnerClassify.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏只显示学校版本选项
     */
    private void hideCorrespondSchoolCb() {
        if (binding.correspondSchool.getVisibility() == View.VISIBLE) {
            binding.correspondSchool.setVisibility(View.GONE);
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
    private void hideFiltrateLayout() {
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
            generateFiltrateLayout();
            binding.filtrate.setVisibility(View.VISIBLE);
        }
    }

    private void generateFiltrateLayout() {
        List<CategoryInfo> gradeInfos = gradeSparseArray.get(mClassifyIds.get(mClassifyPosition));
        List<CategoryInfo> subjectInfos = subjectSparseArray.get(gradeInfos.get(stagePosition).getCategoryId());
        binding.subjectWrap.removeAllViews();
        for (int i = 0; i < subjectInfos.size(); i++) {
            CategoryInfo info = subjectInfos.get(i);
            final TextView subjectTv = (TextView) View.inflate(this, R.layout.text_view, null);
            subjectTv.setText(info.getCategoryDisplay());
            subjectTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < binding.subjectWrap.getChildCount(); i++) {
                        binding.subjectWrap.getChildAt(i).setSelected(false);
                    }
                    subjectTv.setSelected(true);
                    binding.versionWrap.removeAllViews();
                }
            });
            binding.subjectWrap.addView(subjectTv);
        }
    }

    /**
     * 显示只显示学校版本
     */
    private void showCorrespondSchoolCb() {
        if (binding.correspondSchool.getVisibility() == View.GONE) {
            binding.correspondSchool.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示课外书分类下拉列表
     */
    private void showClassifySpinner() {
        if (binding.spinnerClassify.getVisibility() == View.GONE) {
            binding.spinnerClassify.setVisibility(View.VISIBLE);
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
}
