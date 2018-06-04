package com.yougy.shop.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yougy.common.bean.Result;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.protocol.request.PromotionReq;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.ResultUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.rx_subscriber.ShopSubscriber;
import com.yougy.shop.adapter.PromotionAdapter;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.bean.PromotionResult;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.PromotionBinding;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jiangliang on 2018-3-8.
 */

public class ShopPromotionActivity extends ShopBaseActivity{

    private PromotionBinding binding;
    private PromotionAdapter mPromotionAdapter;
    private static final int COUNT_PER_PAGE = 5;
    public static final String COUPON_ID = "couponId";

    private List<Button> btns = new ArrayList<>();
    private List<BookInfo> mPageInfos = new ArrayList<>();
    private int couponId;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_promotion_layout);
        binding.setActivity(this);
    }

    @Override
    public void init() {
        Intent intent = getIntent();
        couponId = intent.getIntExtra(COUPON_ID,1);
    }

    @Override
    protected void initLayout() {
        CustomLinearLayoutManager layoutManager = new CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setScrollHorizontalEnabled(false);
        binding.promotionRecycler.setLayoutManager(layoutManager);
    }

    @Override
    public void loadData() {
        PromotionReq req = new PromotionReq();
        req.setCouponId(couponId);
       Observable.create((Observable.OnSubscribe<PromotionResult>) subscriber -> {
            Response response = NewProtocolManager.queryPromotionActivityInfo(req);
            if (response.isSuccessful()){
                try {
                    String resultJson = response.body().string();
                    LogUtils.e(tag,"result json : " + resultJson);
                    Result<List<PromotionResult>> result = ResultUtils.fromJsonArray(resultJson, PromotionResult.class);
                    List<PromotionResult> promotions = result.getData();
                    subscriber.onNext(promotions.get(0));
                    subscriber.onCompleted();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io())
       .observeOn(AndroidSchedulers.mainThread())
       .subscribe(new ShopSubscriber<PromotionResult>(this) {
           @Override
           public void require() {
               loadData();
           }

           @Override
           public void onNext(PromotionResult promotionResult) {
                updateUI(promotionResult);
           }
       });


    }

    @Override
    protected void refreshView() {

    }

    private void updateUI(PromotionResult result){
        binding.promotionName.setText(result.getCouponName());
        binding.promotionContent.setText(result.getCouponContentExplain());
        String time = getString(R.string.activity_time,result.getCouponStartTime(),result.getCouponEndTime());
        binding.activityTime.setText(time);

        List<BookInfo> infos = result.getCouponBook();
        generateBtn(infos);
        if (mPageInfos.size() > 0) {
            mPageInfos.clear();
        }
        int end = infos.size() > COUNT_PER_PAGE ? COUNT_PER_PAGE : infos.size();
        mPageInfos.addAll(infos.subList(0, end));
        mPromotionAdapter = new PromotionAdapter(mPageInfos);
        binding.promotionRecycler.setAdapter(mPromotionAdapter);
        binding.promotionRecycler.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.promotionRecycler) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                itemClick(mPromotionAdapter.getItemBook(vh.getAdapterPosition()));
            }
        });
    }

    private void itemClick(BookInfo bookInfo) {
        Intent intent = new Intent(this, ShopBookDetailsActivity.class);
        intent.putExtra(ShopGloble.BOOK_ID, Integer.parseInt(bookInfo.getBookId() + ""));
        startActivity(intent);
    }

    private void generateBtn(final List<BookInfo> mBookInfos) {
        if (binding.pageNumberLayout.getChildCount() != 0) {
            binding.pageNumberLayout.removeAllViews();
        }
        int count = mBookInfos.size() % COUNT_PER_PAGE == 0 ? mBookInfos.size() / COUNT_PER_PAGE : mBookInfos.size() / COUNT_PER_PAGE + 1;
        for (int index = 1; index <= count; index++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = UIUtils.px2dip(25);
            View pageLayout = View.inflate(this, R.layout.page_item, null);
            final Button pageBtn = (Button) pageLayout.findViewById(R.id.page_btn);
            if (index == 1) {
                pageBtn.setSelected(true);
            }
            pageBtn.setText(Integer.toString(index));
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
                mPromotionAdapter.notifyDataSetChanged();
            });
            binding.pageNumberLayout.addView(pageLayout, params);
        }
    }

    public void onBack(View view){
        onBackPressed();
    }
}
