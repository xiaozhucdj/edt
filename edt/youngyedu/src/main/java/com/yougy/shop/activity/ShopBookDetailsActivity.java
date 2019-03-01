package com.yougy.shop.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.download.DownloadListener;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.DownloadManager;
import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.nohttp.DownInfo;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.activity.MainActivity;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.message.SizeUtil;
import com.yougy.shop.AllowOrderRequestObj;
import com.yougy.shop.CreateOrderRequestObj;
import com.yougy.shop.bean.BookIdObj;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.bean.CartItem;
import com.yougy.shop.bean.OrderIdObj;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityShopBookDetailsBinding;
import com.yougy.ui.activity.databinding.ItemShopBookDetailPromotionListBinding;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.DividerGridItemDecoration;
import com.yougy.view.dialog.BookDetailsDialog;
import com.yougy.view.dialog.DownBookDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import rx.functions.Action1;

import static com.yougy.shop.activity.ShopPromotionActivity.COUPON_ID;

/**
 * Created by FH on 2017/6/9.
 * 图书详情页
 */

public class ShopBookDetailsActivity extends ShopBaseActivity implements DownBookDialog.DownBookListener {
    boolean showAllPromotion = false;
    ActivityShopBookDetailsBinding binding;
    DownBookDialog mProReadDialog;

    ////////////////////////global Files//////////////////////////////////////////////////
    int bookId;
    /**
     * 图书
     */
    private BookInfo mBookInfo;
    /***
     * 设置 推荐的书具体数目
     */
    private final int PROMOTE_BOOK_COUT = 5;
    /**
     * 图书推荐适配器
     */
    private ShopBookAdapter recommendBookAdapter;
    /**
     * 图书推荐数据集
     */
    private List<BookInfo> mBooks = new ArrayList<BookInfo>();


    //////////////////////////////////////tag//////////////////
    /**
     * 去获 图书详情,无网络
     */
    private int mTagForRequestDetailsNoNet = 1;
    /**
     * 打开PDF
     */
    private int mTagBookReader = 2;
    /**
     * 没有网络
     */
    private int mTagNoNet = 3;
    /**
     * 获取图书详情失败
     */
    private int mTagForRequestDetailsFail = 4;
//////////////////////////////////////tag////////////////


    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_shop_book_details, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initLayout() {
        //初始化推荐列表
        initRecycler();
        initCouponListview();
        if (SpUtils.getStudent().getSchoolLevel() > 0) {
            binding.cartCountTv.setVisibility(View.GONE);
            binding.toCartBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void refreshView() {

    }

    /**
     * 初始化数据
     */
    @Override
    public void init() {
        bookId = getIntent().getIntExtra(ShopGloble.BOOK_ID, -1);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        init();
    }


    @Override
    public void loadData() {
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public void toCart(View view) {
        //查看购物车
        loadIntent(ShopCartActivity.class);
    }

    public void buyBook(View view) {
        if (mBookInfo.isBookInShelf()) {
            showReaderForPackage(false);
            return;
        }
        else {
            if (!NetUtils.isNetConnected()) {
                showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagNoNet);
                return;
            }
            //新建订单
            List<BookIdObj> bookIdList = new ArrayList<BookIdObj>() {
                {
                    add(new BookIdObj(mBookInfo.getBookId()));
                }
            };
            if (SpUtils.getStudent().getSchoolLevel() > 0){
                NetWorkManager.createOrder(new CreateOrderRequestObj(SpUtils.getUserId(), bookIdList , SpUtils.getStudent().getSchoolLevel()))
                        .subscribe(new Action1<List<OrderIdObj>>() {
                            @Override
                            public void call(List<OrderIdObj> orderIdObjList) {
                                showReaderForPackage(false);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                UIUtils.showToastSafe("添加图书失败,请稍后再试.");
                                LogUtils.e("FH", "添加图书失败,请稍后再试.");
                                throwable.printStackTrace();
                            }
                        });
            }
            else {
                NetWorkManager.allowOrder(new AllowOrderRequestObj(SpUtils.getUserId(), bookIdList))
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                LogUtils.v("订单查重成功,未查到重复订单");
                                NetWorkManager.createOrder(new CreateOrderRequestObj(SpUtils.getUserId(), bookIdList , SpUtils.getStudent().getSchoolLevel()))
                                        .subscribe(new Action1<List<OrderIdObj>>() {
                                            @Override
                                            public void call(List<OrderIdObj> orderIdObjList) {
                                                OrderIdObj orderIdObj = orderIdObjList.get(0);
                                                Intent intent = new Intent(ShopBookDetailsActivity.this.getApplicationContext(), OrderDetailActivity.class);
                                                intent.putExtra("orderId", orderIdObj.getOrderId());
                                                startActivity(intent);
                                                finish();
                                                SpUtils.newOrderCountPlusOne();
                                            }
                                        }, new Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable throwable) {
                                                UIUtils.showToastSafe(R.string.books_request_order_fail);
                                                LogUtils.e("FH", "下单失败");
                                                throwable.printStackTrace();
                                            }
                                        });
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                ToastUtil.showCustomToast(getApplicationContext(), "下单失败!\r\n无法购买之前已经下过单的图书");
                                throwable.printStackTrace();
                            }
                        });
            }
        }
    }

    public void addFavor(View view) {
        if (mBookInfo.isBookInFavor()) {
            UIUtils.showToastSafe(R.string.books_already_add_collection);
        } else {
            if (NetUtils.isNetConnected()) {
                NetWorkManager.appendFavor(SpUtils.getUserId(), mBookInfo.getBookId())
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                LogUtils.e("FH", "添加到收藏夹成功");
                                UIUtils.showToastSafe(R.string.books_add_collection_success);
                                mBookInfo.setBookInFavor(true);
                                ShopBookDetailsActivity.this.setBtnFavorState();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                LogUtils.e("FH", "添加到收藏夹失败");
                                UIUtils.showToastSafe(R.string.books_add_collection_fail);
                                throwable.printStackTrace();
                            }
                        });
            } else {
                showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagNoNet);
            }
        }
    }

    /**
     * 查看更多
     */
    public void lookMorePromotion(int couponId) {
        Intent intent = new Intent(this, ShopPromotionActivity.class);
        intent.putExtra(COUPON_ID, couponId);
        startActivity(intent);
    }


    public void addCart(View view) {
        if (mBookInfo.isBookInShelf()) {
            showReaderForPackage(true);
        }
        else {
            if (SpUtils.getStudent().getSchoolLevel() > 0){
                //新建订单
                List<BookIdObj> bookIdList = new ArrayList<BookIdObj>() {
                    {
                        add(new BookIdObj(mBookInfo.getBookId()));
                    }
                };
                NetWorkManager.createOrder(new CreateOrderRequestObj(SpUtils.getUserId(), bookIdList , SpUtils.getStudent().getSchoolLevel()))
                        .subscribe(new Action1<List<OrderIdObj>>() {
                            @Override
                            public void call(List<OrderIdObj> orderIdObjList) {
                                refreshData();
                                BaseEvent baseEvent = new BaseEvent(EventBusConstant.need_refresh, null);
                                EventBus.getDefault().post(baseEvent);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                UIUtils.showToastSafe("添加图书失败,请稍后再试.");
                                LogUtils.e("FH", "添加图书失败,请稍后再试.");
                                throwable.printStackTrace();
                            }
                        });
            }
            else {
                if (mBookInfo.isBookInCart()) {
                    UIUtils.showToastSafe(R.string.books_already_add_car_2);
                } else {
                    // 加入购物车
                    if (NetUtils.isNetConnected()) {
                        NetWorkManager.appendCart(SpUtils.getUserId(), mBookInfo.getBookId())
                                .subscribe(new Action1<Object>() {
                                    @Override
                                    public void call(Object o) {
                                        UIUtils.showToastSafe(R.string.books_add_car_success);
                                        mBookInfo.setBookInCart(true);
                                        setBtnCarState();
                                        refreshCartCount();
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        UIUtils.showToastSafe(R.string.books_add_car_fail);
                                        LogUtils.e("FH", "加入购物车失败");
                                        throwable.printStackTrace();
                                    }
                                });
                    } else {
                        showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagNoNet);
                    }
                }
            }
        }
    }

    public void tryRead(View view) {
        if (mBookInfo == null){
            return;
        }
        if (mBookInfo.isBookInShelf()) {
            showReaderForPackage(true);
            return;
        }
        //跳转在线试读
        if (SpUtils.getStudent().getSchoolLevel() > 0) {
            if (!StringUtils.isEmpty(FileUtils.getBookFileName(mBookInfo.getBookId(), FileUtils.bookDir))) {
                jumpToControlFragmentActivity();
            } else {
                LogUtils.i("试读文件不存在");
                downBookTask(mBookInfo.getBookId());
            }
        } else {
            if (!StringUtils.isEmpty(FileUtils.getBookFileName(mBookInfo.getBookId(), FileUtils.bookProbation))) {
                jumpProbationActivity();
            } else {
                LogUtils.i("试读文件不存在");
                downBookDialog();
            }
        }
    }

    private void jumpProbationActivity() {
        Intent intent = new Intent(this, ProbationReadBookActivity.class);
        intent.putExtra(ShopGloble.JUMP_BOOK_KEY, mBookInfo);
        startActivity(intent);
    }

    private void downBookDialog() {
        if (NetUtils.isNetConnected()) {
            if (mProReadDialog == null) {
                mProReadDialog = new DownBookDialog(this);
                mProReadDialog.setListener(this);
            }
            mProReadDialog.show();
            mProReadDialog.getBtnConfirm().setVisibility(View.VISIBLE);
            mProReadDialog.setTitle(UIUtils.getString(R.string.down_book_defult));
            mProReadDialog.runConfirmClick();
        } else {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagNoNet);
        }
    }

    /***
     * 下载图片
     * @param view
     * @param url
     */
    private void refreshImg(ImageView view, String url) {
        ImageLoaderManager.getInstance().loadImageActivity(this,
                url,
                R.drawable.img_book_cover,
                R.drawable.img_book_cover,
                FileContonst.withS,
                FileContonst.heightS,
                view);
    }

    private void initCouponListview() {
        binding.allPromotionListview.setDivider(new ColorDrawable(Color.WHITE));
        binding.allPromotionListview.setDividerHeight(10);

        binding.allPromotionListview.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                if (mBookInfo == null) {
                    return 0;
                }
                List<BookInfo.BookCouponBean> couponList = mBookInfo.getBookCoupon();
                int couponCount = 0;
                if (couponList != null) {
                    for (BookInfo.BookCouponBean bookCouponBean : couponList) {
                        couponCount = couponCount + bookCouponBean.getCouponContent().size();
                    }
                }
                if (couponCount == 0) {
                    return couponCount;
                } else if (showAllPromotion) {
                    return couponCount;
                } else {
                    return 1;
                }
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ItemShopBookDetailPromotionListBinding itemBinding;
                if (convertView == null) {
                    itemBinding = DataBindingUtil.inflate(LayoutInflater.from(ShopBookDetailsActivity.this)
                            , R.layout.item_shop_book_detail_promotion_list, null, false);
                    convertView = itemBinding.getRoot();
                    convertView.setTag(itemBinding);
                    itemBinding.showAllPromotionBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAllPromotion = true;
                            notifyDataSetChanged();
                        }
                    });
                    itemBinding.lookMorePromotionBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lookMorePromotion(itemBinding.getCouponId());
                        }
                    });
                } else {
                    itemBinding = (ItemShopBookDetailPromotionListBinding) convertView.getTag();
                }
                int couponCount = 0;
                int couponId = -1;
                BookInfo.BookCouponBean.CouponContentBean targetCouponContentBean = null;
                for (BookInfo.BookCouponBean bookCouponBean : mBookInfo.getBookCoupon()) {
                    for (BookInfo.BookCouponBean.CouponContentBean couponContentBean : bookCouponBean.getCouponContent()) {
                        if (couponCount == position) {
                            targetCouponContentBean = couponContentBean;
                            couponId = bookCouponBean.getCouponId();
                        }
                        couponCount++;
                    }
                }
                itemBinding.setCouponId(couponId);
                if (!TextUtils.isEmpty(targetCouponContentBean.getOver())) {
                    itemBinding.promotionName.setText("满减");
                    itemBinding.promotionContent.setText("限时满减  满" + targetCouponContentBean.getOver()
                            + "元减" + targetCouponContentBean.getCut() + "元");
                } else if (!TextUtils.isEmpty(targetCouponContentBean.getFree())) {
                    itemBinding.promotionName.setText("限免");
                    itemBinding.promotionContent.setText("限时免费");
                } else if (!TextUtils.isEmpty(targetCouponContentBean.getOff())) {
                    itemBinding.promotionName.setText("折扣");
                    String tempText = "限时折扣    ";
                    if (targetCouponContentBean.getOff().length() == 2 && targetCouponContentBean.getOff().charAt(1) == '0') {
                        tempText = tempText + targetCouponContentBean.getOff().charAt(0) + "折";
                    } else {
                        tempText = tempText + targetCouponContentBean.getOff() + "折";
                    }
                    itemBinding.promotionContent.setText(tempText);
                } else {
                    itemBinding.promotionName.setText("未知");
                    itemBinding.promotionContent.setText("未知活动");
                }
                if (!showAllPromotion) {
                    if (couponCount > 1) {
                        itemBinding.showAllPromotionBtn.setVisibility(View.VISIBLE);
                        itemBinding.lookMorePromotionBtn.setVisibility(View.GONE);
                    } else {
                        itemBinding.showAllPromotionBtn.setVisibility(View.GONE);
                        itemBinding.lookMorePromotionBtn.setVisibility(View.VISIBLE);
                    }
                } else {
                    itemBinding.showAllPromotionBtn.setVisibility(View.GONE);
                    itemBinding.lookMorePromotionBtn.setVisibility(View.VISIBLE);
                }
                return convertView;
            }
        });
    }

    /***
     * 初始化 推荐列表
     */
    private void initRecycler() {
        binding.bookRecommandRecyclerView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
        CustomGridLayoutManager layout = new CustomGridLayoutManager(this, PROMOTE_BOOK_COUT);
        layout.setScrollEnabled(true);
        binding.bookRecommandRecyclerView.setLayoutManager(layout);
        recommendBookAdapter = new ShopBookAdapter(this, mBooks);
        binding.bookRecommandRecyclerView.setAdapter(recommendBookAdapter);
        binding.bookRecommandRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.bookRecommandRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                ShopBookAdapter.BookInfoViewHolder holder = (ShopBookAdapter.BookInfoViewHolder) vh;
                int position = holder.getAdapterPosition();
                itemClick(position);
            }
        });
        recommendBookAdapter.notifyDataSetChanged();
    }

    /**
     * 点击holder  item
     *
     * @param position
     */
    private void itemClick(int position) {
        if (mBooks.size() > 0) {
            Intent intent = new Intent(this, ShopBookDetailsActivity.class);
            intent.putExtra(ShopGloble.BOOK_ID, mBooks.get(position).getBookId());
            startActivity(intent);
        }
    }

    @Override
    public void onCancelListener() {
        DownloadManager.cancel();
        mProReadDialog.dismiss();
    }

    @Override
    public void onConfirmListener() {
        mProReadDialog.getBtnConfirm().setEnabled(false);
        mProReadDialog.setTitle("正在开始下载...");
        if (mBookInfo.isBookInShelf()) {
            //下载文件
//            List<DownInfo> mFiles = new ArrayList<>();
//            DownInfo info = new DownInfo(mBookInfo.getBookDownload(), FileUtils.getTextBookFilesDir(), mBookInfo.getBookId() + ".pdf", true, false, mBookInfo.getBookId());
//            info.setBookName(mBookInfo.getBookTitle());
//            mFiles.add(info);
//            downBook(mFiles);
        } else {
            try {
                int kb = 1024;
                long fileDirSizeMB = (FileUtils.getDirLength(new File(FileUtils.getProbationBookFilesDir())) / kb / kb);
                //目录大小超过200MB以后删除该目录
                if (fileDirSizeMB >= ShopGloble.PROBATION_FILES_DIR_MAX_SIZE) {
                    //删除文件
                    FileUtils.delFileOrFolder(FileUtils.getProbationBookFilesDir());
                    //重新创建目录
                    FileUtils.createDirs(FileUtils.getProbationBookFilesDir());
                }

                //下载文件
                List<DownInfo> mFiles = new ArrayList<>();
                DownInfo info = new DownInfo(mBookInfo.getBookPreview(), FileUtils.getProbationBookFilesDir(), mBookInfo.getBookId() + "", true, false, mBookInfo.getBookId());
                info.setBookName(mBookInfo.getBookTitle());
                mFiles.add(info);
                downBook(mFiles);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 文件下载，下载位置 ，FileUtils.getTextBookFilesDir()
     */
    private void downBook(List<DownInfo> mFiles) {
        DownloadManager.downloadFile(mFiles, new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {
                LogUtils.i("  onDownloadError     what ........" + what);
                DownloadManager.cancel();
                mProReadDialog.setTitle(UIUtils.getString(R.string.down_book_error));
                mProReadDialog.getBtnConfirm().setVisibility(View.VISIBLE);
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                LogUtils.i("  onStart     what ........" + what);
            }

            @Override
            public void onProgress(int what, int progress, long fileCount) {
                mProReadDialog.setTitle(String.format(getString(R.string.down_book_loading), progress + "%"));
            }

            @Override
            public void onFinish(int what, String filePath) {
                if (DownloadManager.isFinish()) {
                    mProReadDialog.dismiss();
                    // 自动进入
                    if (mBookInfo.isBookInShelf()) {
                        jumpToControlFragmentActivity();
                    } else {
                        jumpProbationActivity();
                    }
                }
            }

            @Override
            public void onCancel(int what) {
                mProReadDialog.dismiss();
                mProReadDialog = null;
            }
        });
    }

    private void refreshData() {
        if (NetUtils.isNetConnected()) {
            NetWorkManager.queryShopBook(SpUtils.getUserId(), bookId, null, null, null, null).subscribe(new Action1<List<BookInfo>>() {
                @Override
                public void call(List<BookInfo> bookInfos) {
                    mBookInfo = bookInfos.get(0);
                    if (mBooks == null || mBooks.size() == 0) {
                        getProteBook();
                    }
                    //图片
                    refreshImg(binding.bookCoverImv, mBookInfo.getBookCoverS());
                    //标题
                    binding.titleTv.setText("图书详情");
                    //图书名称
                    binding.bookNameTv.setText(mBookInfo.getBookTitle());
                    //出版社
                    binding.bookPublisherTv.setText(getString(R.string.publisher_text, mBookInfo.getBookPublisherName()));
                    //作者
                    binding.bookAuthorTv.setText(getString(R.string.author_text, mBookInfo.getBookAuthor()));
                    //出版时间
                    if (TextUtils.isEmpty(mBookInfo.getBookPublishTime())) {
                        binding.bookPublishTimeTv.setText(getString(R.string.publish_time, "暂无"));
                    } else {
                        binding.bookPublishTimeTv.setText(getString(R.string.publish_time, mBookInfo.getBookPublishTime()));
                    }
                    //文件大小
                    binding.bookDownloadSizeTv.setText(getString(R.string.file_size_text, SizeUtil.convertSizeLong2String(mBookInfo.getBookDownloadSize())));
                    //价格
                    if (SpUtils.getStudent().getSchoolLevel() > 0 || mBookInfo.isBookInShelf()) {
                        binding.tryReadBtn.setVisibility(View.GONE);
                        binding.buyBtn.setText(R.string.read_promptly);
                    } else {
                        binding.bookOriginPriceTv.setText(getString(R.string.list_price, mBookInfo.getBookSalePrice() + ""));
                        binding.bookSalePriceTv.setText(getString(R.string.sale_price, mBookInfo.getBookSpotPrice() + ""));
                        //购买按钮价格
                        binding.buyBtn.setText("￥" + mBookInfo.getBookSpotPrice() + "购买");
                        //在线试读是否可点
                        if (TextUtils.isEmpty(mBookInfo.getBookPreview())) {
                            binding.tryReadBtn.setEnabled(false);
                            binding.tryReadBtn.setText("无试读");
                        } else {
                            binding.tryReadBtn.setEnabled(true);
                            binding.tryReadBtn.setText(R.string.read_online);
                        }
                    }
                    //图书详情
                    if (TextUtils.isEmpty(mBookInfo.getBookSummary())) {
                        binding.bookDetailTv.setText("");
                    } else {
                        binding.bookDetailTv.setText(Html.fromHtml(mBookInfo.getBookSummary()));
                    }

                    //修改按钮状态
                    setBtnCarState();
                    setBtnFavorState();
                    ((BaseAdapter) binding.allPromotionListview.getAdapter()).notifyDataSetChanged();
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    ToastUtil.showCustomToast(getApplicationContext(), "获取图书详情失败!");
                    throwable.printStackTrace();
                }
            });
        } else {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagForRequestDetailsNoNet);
        }
    }


    private void getProteBook() {
        if (mBooks.size() == 0) {
            NetWorkManager.promoteBook(SpUtils.getUserId(), bookId)
                    .subscribe(shopBookInfos -> {
                        mBooks.clear();
                        mBooks.addAll(shopBookInfos);
                        recommendBookAdapter.notifyDataSetChanged();
                    }, throwable -> {
                        LogUtils.e("FH", "获取推荐图书信息失败");
                        throwable.printStackTrace();
                        UIUtils.showToastSafe(R.string.books_request_recommended_fail);
                    });
        }
    }

    private void setBtnCarState() {
        if (mBookInfo.isBookInShelf()){
            binding.addCarBtn.setText("已在书架");
            binding.addCarBtn.setBackgroundResource(R.drawable.btn_selector_rectangle_white_green_gray);
            binding.addCarBtn.setClickable(true);
        }
        else {
            if (SpUtils.getStudent().getSchoolLevel() > 0) {
                binding.addCarBtn.setText("加入书架");
                binding.addCarBtn.setBackgroundResource(R.drawable.btn_selector_rectangle_white_green_gray);
                binding.addCarBtn.setClickable(true);
            }
            else {
                if (mBookInfo.isBookInCart()){
                    binding.addCarBtn.setText("已在购物车");
                    binding.addCarBtn.setBackgroundResource(R.drawable.shape_rectangle_black_border_gray_fill);
                    binding.addCarBtn.setClickable(false);
                }
                else {
                    binding.addCarBtn.setText("加入购物车");
                    binding.addCarBtn.setBackgroundResource(R.drawable.btn_selector_rectangle_white_green_gray);
                    binding.addCarBtn.setClickable(true);
                }
            }
        }
    }

    private void setBtnFavorState() {
        if (mBookInfo.isBookInFavor()) {
            binding.addFavorBtn.setText(R.string.books_already_add_collection);
            binding.addFavorBtn.setBackgroundResource(R.drawable.shape_rectangle_black_border_gray_fill);
            binding.addFavorBtn.setClickable(false);
        } else {
            binding.addFavorBtn.setText("加入收藏夹");
            binding.addFavorBtn.setBackgroundResource(R.drawable.btn_selector_rectangle_white_green_gray);
            binding.addFavorBtn.setClickable(true);
        }
    }

    private BookDetailsDialog mBookDetailsDialog;

    private void resolveAfterAdd2BookCase() {
        if (!StringUtils.isEmpty(FileUtils.getBookFileName(mBookInfo.getBookId(), FileUtils.bookDir))) {
            jumpToControlFragmentActivity();

        } else {
            if (NetUtils.isNetConnected()) {
                downBookTask(mBookInfo.getBookId());
            } else {
                showCancelAndDetermineDialog(R.string.jump_to_net);
            }
        }
    }

    private void showReaderForPackage(boolean showConfirm) {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagNoNet);
            return;
        }
        NetWorkManager.addBookToBookcase(mBookInfo.getBookId(), SpUtils.getUserId()).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
//                UIUtils.showToastSafe("添加图书成功");
                BaseEvent baseEvent = new BaseEvent(EventBusConstant.need_refresh, null);
                EventBus.getDefault().post(baseEvent);
                if (!showConfirm) {
                    resolveAfterAdd2BookCase();
                }
                else {
                    if (mBookDetailsDialog == null) {
                        mBookDetailsDialog = new BookDetailsDialog(ShopBookDetailsActivity.this);
                        mBookDetailsDialog.setBookDetailsListener(new BookDetailsDialog.BookDetailsListener() {
                            @Override
                            public void onCancelListener() {
                                mBookDetailsDialog.dismiss();
                                ShopBookDetailsActivity.this.finish();
                            }

                            @Override
                            public void onConfirmListener() {
                                mBookDetailsDialog.dismiss();
                                resolveAfterAdd2BookCase();
                            }
                        });
                    }
                    mBookDetailsDialog.show();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                UIUtils.showToastSafe("添加图书失败,请稍候再试");
            }
        });
    }

    private void jumpToControlFragmentActivity() {
//        String filePath = FileUtils.getTextBookFilesDir() + mBookInfo.getBookId() + ".pdf";
        if (!StringUtils.isEmpty(FileUtils.getBookFileName(mBookInfo.getBookId(), FileUtils.bookDir))) {

            Bundle extras = new Bundle();
            //课本进入
            extras.putString(FileContonst.JUMP_FRAGMENT, FileContonst.JUMP_TEXT_BOOK);
            //笔记创建者
            extras.putInt(FileContonst.NOTE_CREATOR, -1);
            //分类码
            extras.putInt(FileContonst.CATEGORY_ID, mBookInfo.getBookCategory());
            //笔记类型
            extras.putInt(FileContonst.NOTE_Style, mBookInfo.getNoteStyle());
            extras.putInt(FileContonst.NOTE_SUBJECT_ID, mBookInfo.getBookFitSubjectId());
            extras.putString(FileContonst.NOTE_SUBJECT_NAME, mBookInfo.getBookFitSubjectName());
            //作业ID
            extras.putInt(FileContonst.HOME_WROK_ID, mBookInfo.getBookFitHomeworkId());
            //笔记id
            extras.putInt(FileContonst.NOTE_ID, mBookInfo.getBookFitNoteId());
            //图书id
            extras.putInt(FileContonst.BOOK_ID, mBookInfo.getBookId());
            extras.putString(FileContonst.NOTE_TITLE, mBookInfo.getBookFitNoteTitle());
            loadIntentWithExtras(ControlFragmentActivity.class, extras);
            this.finish();
        } else {
//        downBookDialog();
            //跳转到书包
            loadIntentWithSpecificFlag(MainActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            finish();
        }
    }

    @Override
    public void onUiCenterDetermineListener() {
        super.onUiCenterDetermineListener();
    }

    @Override
    public void onUiCancelListener() {
        super.onUiCancelListener();
        // 获取图书详情 ，无网络
        if (getUiPromptDialog().getTag() == mTagForRequestDetailsNoNet || getUiPromptDialog().getTag() == mTagForRequestDetailsFail) {
            this.finish();
        }
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        // 获取图书详情 ，无网络
        if (getUiPromptDialog().getTag() == mTagBookReader) {
            jumpToControlFragmentActivity();
            //获取图书详情失败
        } else if (getUiPromptDialog().getTag() == mTagForRequestDetailsFail) {
            refreshData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
        refreshCartCount();
    }

    private void refreshCartCount() {
        NetWorkManager.queryCart(SpUtils.getUserId() + "")
                .subscribe(new Action1<List<CartItem>>() {
                    @Override
                    public void call(List<CartItem> cartItems) {
                        if (cartItems == null) {
                            binding.cartCountTv.setText("0");
                        } else {
                            if (cartItems.size() > 99) {
                                binding.cartCountTv.setText("…");
                            } else {
                                binding.cartCountTv.setText("" + cartItems.size());
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.e("FH", "获取购物车失败");
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    protected void onDownBookFinish() {
        super.onDownBookFinish();
        jumpToControlFragmentActivity();
    }

}