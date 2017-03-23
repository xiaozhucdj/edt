package com.yougy.home.fragment.mainFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.fragment.BFragment;
import com.yougy.ui.activity.R;


/**
 * Created by Administrator on 2016/7/12.
 * 全部作业 fragment_homework
 */
public class AllHomeworkFragment extends BFragment  {

//    /**
//     * 适配器 数据
//     */
//    private List<BookInfo> mBooks = new ArrayList<>();
//    /**
//     * 服务器返回数据
//     */
//    private List<BookInfo> mCountBooks = new ArrayList<>();
    private ViewGroup mRootView;
//    private RecyclerView mRecyclerView;
//    private HomeworkAdapter mHomeworkAdapter;
//    private boolean mIsFist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_book, null);
//        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_View);
//        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
//        CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(), 4);
//        layout.setScrollEnabled(true);
//        mRecyclerView.setLayoutManager(layout);
//
//        mHomeworkAdapter = new HomeworkAdapter(mBooks);
//        mHomeworkAdapter.setListener(this);
//        mRecyclerView.setAdapter(mHomeworkAdapter);
//        mHomeworkAdapter.notifyDataSetChanged();
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mIsFist = true;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {

        super.onHiddenChanged(hidden);
//        if (mIsFist && !hidden && mCountBooks.size() == 0) {
//            loadData();
//        }
//        if (!hidden) {
//            LogUtils.i("当前--全部作业");
//            setRefreshListener();
//        }
    }



//    private void setRefreshListener() {
//     SearchImple imple =  new SearchImple() ;
//        ((MainActivity) getActivity()).setRefreshListener(imple);
//    }

//    class  SearchImple implements RefreshBooksListener {
//        @Override
//        public void onRefreshClickListener() {
//            loadData();
//        }
//    }

//    private void loadData() {
//        ProtocolManager.bookShelfProtocol(Integer.parseInt(SpUtil.getAccountId()), 0, 10000, "", ProtocolId.PROTOCOL_ID_BOOK_SHELF, new AllHomeworkCallBack(getActivity()));
//    }

//        @Override
//        public void onClickItemHomeworkListener(BookInfo bookInfo) {
////        loadIntent(HomeworkActivity.class);
//    }

//    private class AllHomeworkCallBack extends BaseCallBack<BookShelfProtocol> {
//
//        public AllHomeworkCallBack(Context context) {
//            super(context);
//        }
//
//        @Override
//        public BookShelfProtocol parseNetworkResponse(Response response, int id) throws Exception {
//            String str = response.body().string();
//            LogUtils.i("response json ...." + str);
//            return GsonUtil.fromJson(str,BookShelfProtocol.class);
//        }
//
//        @Override
//        public void onResponse(BookShelfProtocol response, int id) {
//
//            if (response.getCode() == ProtocolId.RET_SUCCESS) {
//                if (response.getBookList() != null && response.getBookList().size() > 0) {
//                    // 总数据赋值
//                    mCountBooks.clear();
//                    mCountBooks.addAll(response.getBookList());
//                    refreshAdapterData();
//                } else {
//                    //数据为空
//                }
//            } else {
//                //协议失败
//            }
//        }

//        @Override
//        public void onClick() {
//            super.onClick();
//            ProtocolManager.bookShelfProtocol(Integer.parseInt(SpUtil.getAccountId()), 0, 10000, "", ProtocolId.PROTOCOL_ID_BOOK_SHELF, this);
//        }
//    }

//    /***
//     * 刷新适配器数据
//     */
//    private void refreshAdapterData() {
//        mBooks.clear();
//        if (mCountBooks.size() > 0) {
//            mBooks.addAll(mCountBooks);
//            mHomeworkAdapter.notifyDataSetChanged();
//        }
//    }

    public void loadIntent(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        mRecyclerView = null ;
//        if (mHomeworkAdapter !=null){
//            mHomeworkAdapter = null ;
//        }
    }
}
