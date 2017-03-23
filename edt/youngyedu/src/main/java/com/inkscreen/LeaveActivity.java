package com.inkscreen;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;


import com.inkscreen.adapter.MyLeaveAdapter;
import com.inkscreen.model.LeaveInfo;
import com.inkscreen.will.utils.widget.MultiListView;
import com.yougy.ui.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xcz on 2016/11/18.
 */
public class LeaveActivity extends Activity{

    MultiListView leaveListView;
    List<LeaveInfo> leaveLists;
    MyLeaveAdapter myLeaveAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leavework);
        leaveListView = (MultiListView)findViewById(R.id.leavlistview_id);

        leaveLists = new ArrayList<>();
        myLeaveAdapter = new MyLeaveAdapter(this);
        myLeaveAdapter.setList(new ArrayList<LeaveInfo>());
        leaveListView.setAdapter(myLeaveAdapter);

        LoadDataAsyncTask asyncTask = new LoadDataAsyncTask();
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class LoadDataAsyncTask extends AsyncTask<Integer, Integer, List<LeaveInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<LeaveInfo> doInBackground(Integer... params) {
            for (int i = 0; i < 12; i++) {

                LeaveInfo leaveInfo = new LeaveInfo();
                leaveInfo.setPctAge(i+"%");
                leaveLists.add(leaveInfo);
            }
            return leaveLists;
        }

        @Override
        protected void onPostExecute(List<LeaveInfo> subjects) {
            super.onPostExecute(subjects);
            myLeaveAdapter.getList().clear();
            myLeaveAdapter.getList().addAll(subjects);
            myLeaveAdapter.notifyDataSetChanged();

        }
    }



}
