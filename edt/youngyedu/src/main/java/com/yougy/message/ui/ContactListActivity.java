package com.yougy.message.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.yougy.common.utils.SpUtils;
import com.yougy.message.ListUtil;
import com.yougy.message.Pair;
import com.yougy.message.YXClient;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityContactListBinding;
import com.yougy.ui.activity.databinding.ItemContactListBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by FH on 2017/3/28.
 */

public class ContactListActivity extends MessageBaseActivity{
    ActivityContactListBinding binding;
    MainListViewAdapter mainAdapter = new MainListViewAdapter();

    HashMap<String , List<TeamMember>> allTeamMemberMap = new HashMap<String, List<TeamMember>>();

    ArrayList<String> hasChoosedIdList = new ArrayList<String>();

    @Override
    protected void initTitleBar(RelativeLayout titleBarLayout, Button leftBtn, TextView titleTv, Button rightBtn) {
        titleTv.setText("联系人");
        rightBtn.setText("发送");
        rightBtn.getLayoutParams().width = rightBtn.getLayoutParams().WRAP_CONTENT;
        rightBtn.getLayoutParams().height = rightBtn.getLayoutParams().WRAP_CONTENT;
    }

    @Override
    public void onTitleBarRightBtnClick(View view) {
        if (hasChoosedIdList.size() == 1){
            String account = hasChoosedIdList.get(0);
            String name = YXClient.getInstance().getUserNameByID(account);
            name = TextUtils.isEmpty(name) ? account : name;
            openActivity(ChattingActivity.class ,
                    "id" , account ,
                    "type" , SessionTypeEnum.P2P.toString() ,
                    "name" , name
            );
        }
        else if (hasChoosedIdList.size() > 1){
            Intent intent = new Intent(this , MultiChattingActivity.class);
            ArrayList<String> idList = new ArrayList<String>(){{
                addAll(hasChoosedIdList);
            }};
            ArrayList<String> nameList = new ArrayList<String>(){
                {
                    for (String id : hasChoosedIdList) {
                        String name = YXClient.getInstance().getUserNameByID(id);
                        name = TextUtils.isEmpty(name) ? id : name;
                        add(name);
                    }
                }
            };
            intent.putExtra("idList" , idList);
            intent.putExtra("nameList" , nameList);
            intent.putExtra("type" , SessionTypeEnum.P2P.toString());
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.bind(setLayoutRes(R.layout.activity_contact_list));
        initMainListview();
    }

    @Override
    public void init() {}

    @Override
    public void loadData() {
        YXClient.getInstance().with(this).addOnMyTeamListChangeListener(new YXClient.OnThingsChangedListener<List<Team>>() {
            @Override
            public void onThingChanged(List<Team> thing, int type) {
                Log.v("FH" , "ContactListActivity 收到我的群列表变更size : " + thing.size() + " type : " + type);
                switch (type){
                    case YXClient.ALL:
                        allTeamMemberMap.clear();
                        for (Team team : thing) {
                            allTeamMemberMap.put(team.getId() , YXClient.getInstance().getTeamMemberByID(team.getId()));
                        }
                        processData();
                        break;
                    case YXClient.DELETE:
                        for (Team team : thing) {
                            allTeamMemberMap.remove(team.getId());
                        }
                        processData();
                        break;
                    case YXClient.NEW:
                        for (Team team : thing) {
                            List<TeamMember> list = YXClient.getInstance().getTeamMemberByID(team.getId());
                            if (list != null){
                                allTeamMemberMap.put(team.getId() , YXClient.getInstance().getTeamMemberByID(team.getId()));
                            }
                        }
                        processData();
                        break;
                }
            }
        });
        YXClient.getInstance().with(this).addOnUserInfoChangeListener(new YXClient.OnThingsChangedListener<Bundle>() {
            @Override
            public void onThingChanged(Bundle thing , int type) {
                Log.v("FH" , "ContactListActivity 收到用户资料变更 type : " + type);
                mainAdapter.notifyDataSetChanged();
            }
        });
        YXClient.getInstance().with(this).addOnTeamMemberChangeListener(new YXClient.OnThingsChangedListener<Pair<String, List<TeamMember>>>() {
            @Override
            public void onThingChanged(Pair<String, List<TeamMember>> thing , int type) {
                Log.v("FH" , "ContactListActivity 收到群成员变更size : " + thing.sencond.size() + " type : " + type + " 群ID :　" + thing.first);
                switch (type){
                    case YXClient.ALL:
                        allTeamMemberMap.put(thing.first , thing.sencond);
                        processData();
                        break;
                    case YXClient.DELETE:
                        List<TeamMember> tempList1 = allTeamMemberMap.get(thing.first);
                        if (tempList1 != null){
                            for (final TeamMember deletedMember : thing.sencond) {
                                ListUtil.conditionalRemove(tempList1, new ListUtil.ConditionJudger<TeamMember>() {
                                    @Override
                                    public boolean isMatchCondition(TeamMember nodeInList) {
                                        return nodeInList.getAccount().equals(deletedMember.getAccount());
                                    }
                                });
                            }
                            processData();
                        }
                        break;
                    case YXClient.NEW:
                        List<TeamMember> tempList2 = allTeamMemberMap.get(thing.first);
                        if (tempList2 != null){
                            tempList2.addAll(thing.sencond);
                        }
                        processData();
                        break;
                }
            }
        });
        ArrayList<Team> teamList = YXClient.getInstance().getMyTeamList();
        for (Team team : teamList) {
            allTeamMemberMap.put(team.getId() , YXClient.getInstance().getTeamMemberByID(team.getId()));
        }
        processData();
    }

    public void processData(){
        mainAdapter.idList.clear();
        Iterator<String> iterator = allTeamMemberMap.keySet().iterator();
        while (iterator.hasNext()){
            List<TeamMember> teamMemberList = allTeamMemberMap.get(iterator.next());
            for (final TeamMember teamMember : teamMemberList) {
                if (isTeacher(teamMember) && !teamMember.getAccount().equals(String.valueOf(SpUtils.getUserId()))){
                    if (!ListUtil.conditionalContains(mainAdapter.idList, new ListUtil.ConditionJudger<String>() {
                        @Override
                        public boolean isMatchCondition(String nodeInList) {
                            return nodeInList.equals(teamMember.getAccount());
                        }
                    })){
                        mainAdapter.idList.add(teamMember.getAccount());
                    }
                }
            }
        }
        mainAdapter.notifyDataSetChanged();
    }

    private boolean isTeacher(TeamMember member){
        //TODO fh 需要判断是否是老师,根据id号,具体规则还未定
        return true;
    }

    private void initMainListview(){
        binding.mainListview.setAdapter(mainAdapter);
        binding.mainListview.setDividerHeight(2);
        binding.mainListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                ImageButton checkbox = ((ItemContactListBinding)view.getTag()).checkbox;
                if (checkbox.isSelected()){
                    ListUtil.conditionalRemove(hasChoosedIdList, new ListUtil.ConditionJudger<String>() {
                        @Override
                        public boolean isMatchCondition(String nodeInList) {
                            return nodeInList.equals(mainAdapter.idList.get(position));
                        }
                    });
                }
                else {
                    hasChoosedIdList.add(mainAdapter.idList.get(position));
                }
                checkbox.setSelected(!checkbox.isSelected());
            }
        });
    }

    private class MainListViewAdapter extends BaseAdapter {
        ArrayList<String> idList = new ArrayList<String>();
        @Override
        public int getCount() {
            return idList.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(ContactListActivity.this).inflate(R.layout.item_contact_list, parent , false);
                convertView.setTag(DataBindingUtil.bind(convertView));
            }
            String id = idList.get(position);
            ItemContactListBinding binding = (ItemContactListBinding) convertView.getTag();
            binding.memberNameTv.setText(YXClient.getInstance().getUserNameByID(id));
            binding.avatarImv.setImageResource(R.drawable.icon_teacher_medium);
            boolean hasChoosed = ListUtil.conditionalContains(hasChoosedIdList, new ListUtil.ConditionJudger<String>() {
                @Override
                public boolean isMatchCondition(String nodeInList) {
                    return nodeInList.equals(id);
                }
            });
            binding.checkbox.setSelected(hasChoosed);
            return convertView;
        }
    }
}
