package com.yougy.shop.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yougy.common.bean.Result;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.ResultUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.shop.adapter.SearchResultAdapter1;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.callback.QueryBookCallBack;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.SearchBinding;
import com.yougy.view.decoration.SpaceItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindString;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

//import com.yougy.init.bean.BookInfo;

/**
 * Created by jiangliang on 2017/3/2.
 */

public class SearchActivityDb extends ShopBaseActivity {
    @BindString(R.string.search)
    String mSearchText;
    @BindString(R.string.text_filtrate)
    String mFiltrateText;
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
    private ArrayAdapter<String> mStageAdapter;
    private String mSubject;
    private String mVersion;
    private boolean mSearchFlag;
    private List<BookInfo> mBookInfos;
    private List<BookInfo> mPageInfos = new ArrayList<>();

    private SearchResultAdapter1 mAdapter;

    private List<Button> btns = new ArrayList<>();

    private static final int COUNT_PER_PAGE = 5;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.search_result_layout_db);
        binding.setActivity(this);
    }

    @Override
    protected void handleEvent() {
        handleQueryBookEvent();
        super.handleEvent();
    }

    private void handleQueryBookEvent() {
//        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
//            @Override
//            public void call(Object o) {
//                if (o instanceof QueryBookInfo) {
//                    QueryBookInfo info = (QueryBookInfo) o;
//                    if (info.getBookList() == null || info.getBookList().size() == 0) {
//                        binding.noResult.setVisibility(View.VISIBLE);
////                        binding.searchTv.setText(mSearchText);
//                        mSearchFlag = true;
//                    } else {
//                        mSearchFlag = false;
////                        binding.searchTv.setText(mFiltrateText);
//                        mBookInfos = info.getBookList();
//                        if (mBookInfos != null) {
//                            if (mBookInfos.size() >= COUNT_PER_PAGE) {
//                                mPageInfos.addAll(mBookInfos.subList(0, COUNT_PER_PAGE));
//                            } else {
//                                mPageInfos.addAll(mBookInfos);
//                            }
//                        }
//                        generateBtn();
//                        mAdapter = new SearchResultAdapter1(mPageInfos);
//                        binding.resultRecycler.setAdapter(mAdapter);
//                        binding.resultRecycler.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.resultRecycler) {
//                            @Override
//                            public void onItemClick(RecyclerView.ViewHolder vh) {
//                                itemClick(vh.getAdapterPosition());
//                            }
//                        });
//                    }
//                }
//            }
//        }));
    }

    private void itemClick(int position) {
        BookInfo info = mPageInfos.get(position);
        LogUtils.e(tag, "onItemClick......" + info.getBookTitle());
        Bundle extras = new Bundle();
        extras.putParcelable(ShopGloble.JUMP_BOOK_KEY, info);
        loadIntentWithExtras(NewBookItemDetailsActivity.class, extras);
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        bookName = intent.getStringExtra("search_key");
        categoryId = intent.getIntExtra("categoryId",-1);
        mStageList = new ArrayList<>();
        mStageList.add(mAllGrade);
        mStageList.addAll(Arrays.asList(mStages));
    }

    private void generateBtn() {
        int count = mBookInfos.size() % COUNT_PER_PAGE == 0 ? mBookInfos.size() / COUNT_PER_PAGE : mBookInfos.size() / COUNT_PER_PAGE + 1;
        for (int index = 1; index <= count; index++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 20;
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
                    mAdapter.notifyDataSetChanged();
                }
            });
            binding.pageNumberLayout.addView(pageBtn, params);
        }
    }

    @Override
    protected void initLayout() {
        binding.searchKey.setText(bookName);
        binding.resultRecycler.addItemDecoration(new SpaceItemDecoration(10));
        binding.resultRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mStageAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, mStageList);
        mStageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.stageSpinner.setAdapter(mStageAdapter);
        binding.subjectWrap.setHorizontalMargin(40);
        binding.subjectWrap.setVerticalMargin(20);
        binding.versionWrap.setHorizontalMargin(40);
        binding.versionWrap.setVerticalMargin(20);
        for (final String version : mVersions) {
            final TextView tv = (TextView) View.inflate(this, R.layout.text_view, null);
            tv.setText(version);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetVersionTv();
                    mVersion = version;
                    tv.setSelected(true);
                }
            });
            binding.versionWrap.addView(tv);
        }

        for (final String subject : mSubjects) {
            final TextView tv = (TextView) View.inflate(this, R.layout.text_view, null);
            tv.setText(subject);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetSubjectTv();
                    binding.versionLayout.setVisibility(View.VISIBLE);
                    mSubject = subject;
                    tv.setSelected(true);
                }
            });
            binding.subjectWrap.addView(tv);
        }
    }

    private void resetSubjectTv() {
        int count = binding.subjectWrap.getChildCount();
        for (int i = 0; i < count; i++) {
            binding.subjectWrap.getChildAt(i).setSelected(false);
        }
    }

    private void resetVersionTv() {
        int count = binding.versionWrap.getChildCount();
        for (int i = 0; i < count; i++) {
            binding.versionWrap.getChildAt(i).setSelected(false);
        }
    }

    private String bookName;
    private int categoryId;

    @Override
    protected void loadData() {
        Observable.create(new Observable.OnSubscribe<List<BookInfo>>() {
            @Override
            public void call(Subscriber<? super List<BookInfo>> subscriber) {
                Response response = ProtocolManager.queryBookProtocol(SpUtil.getUserId(), bookName, categoryId, 0, 0);
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
                        LogUtils.e(tag, "bookinfos' size : " + bookInfos.size());
                        refreshResultView(bookInfos);
                    }
                });

    }

    private void refreshResultView(List<BookInfo> bookInfos) {
        if (bookInfos == null || bookInfos.size() == 0) {
            binding.noResult.setVisibility(View.VISIBLE);
            mSearchFlag = true;
        } else {
            mSearchFlag = false;
            mBookInfos = bookInfos;
            if (mBookInfos.size() >= COUNT_PER_PAGE) {
                mPageInfos.addAll(mBookInfos.subList(0, COUNT_PER_PAGE));
            } else {
                mPageInfos.addAll(mBookInfos);
            }
            generateBtn();
            mAdapter = new SearchResultAdapter1(mPageInfos);
            binding.resultRecycler.setAdapter(mAdapter);
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

    public void search(View view) {
        if (mSearchFlag) {
            bookName = binding.searchKey.getText().toString();
            if (!TextUtils.isEmpty(bookName)) {
                QueryBookCallBack callBack = new QueryBookCallBack(this);
                callBack.setBookName(bookName);
                ProtocolManager.queryBookProtocol(SpUtil.getUserId(), bookName, -1, 0, 0, ProtocolId.PROTOCOL_ID_QUERY_BOOK, callBack);
            }
        } else {
            //进行筛选
            showFiltrateLayout();
        }
    }

    public void reset(View view) {
        resetVersionTv();
        resetSubjectTv();
        resetFiltrate();
    }

    public void confirm(View view) {
        resetVersionTv();
        resetSubjectTv();
        hideFiltrateLayout();
        //TODO:集合数据中，根据条件进行筛选
        resetFiltrate();
    }

    public void clickTextBookTv(View view) {
        binding.textBookTv.setSelected(true);
        binding.guideBookTv.setSelected(false);
        binding.extraBookTv.setSelected(false);
        showSubjectLayout();
    }

    public void clickGuideBookTv(View view) {
        binding.textBookTv.setSelected(false);
        binding.guideBookTv.setSelected(true);
        binding.extraBookTv.setSelected(false);
        showSubjectLayout();
    }

    public void clickExtraBookTv(View view) {
        binding.textBookTv.setSelected(false);
        binding.guideBookTv.setSelected(false);
        binding.extraBookTv.setSelected(true);
        showSubjectLayout();
    }

    private void resetFiltrate() {
        binding.stageSpinner.setSelection(0);
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
     * 隐藏科目布局
     */
    private void hideSubjectLayout() {
        binding.subjectLayout.setVisibility(View.GONE);
    }

    /**
     * 显示版本布局
     */
    private void showVersionLayout() {
        binding.versionLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏版本布局
     */
    private void hideVersionLayout() {
        binding.versionLayout.setVisibility(View.GONE);
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
}
