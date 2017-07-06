package com.yougy.message.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.yougy.common.utils.SpUtil;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.message.GlideCircleTransform;
import com.yougy.message.ListUtil;
import com.yougy.message.Pair;
import com.yougy.message.YXClient;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityContactListBinding;
import com.yougy.ui.activity.databinding.ItemContactListBinding;
import com.yougy.ui.activity.databinding.ItemGroupListBinding;
import com.yougy.ui.activity.databinding.ItemMemberListBinding;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by FH on 2017/3/28.
 */

public class ContactListActivity extends MessageBaseActivity{
    ActivityContactListBinding binding;
    ArrayList<Pair<Boolean , Team>> teamList = new ArrayList<>();
    MainListViewAdapter mainAdapter;
    GroupListAdapter groupListAdapter;
    boolean headViewExpanded = false;
    HashMap<String , List<TeamMember>> groupMemberMap = new HashMap<String, List<TeamMember>>();
    ArrayList<TeamMember> hasChoosedMemberList = new ArrayList<TeamMember>();
    YXClient.OnThingsChangedListener<List<Team>> onMyTeamListChangedListener = new YXClient.OnThingsChangedListener<List<Team>>() {
        @Override
        public void onThingChanged(List<Team> thing) {
            ArrayList<Team> newTeamList = YXClient.getInstance().getMyTeamList();
            ArrayList<Pair<Boolean , Team>> tempPairList = new ArrayList<>();
            for (Team newTeam : newTeamList) {
                for (int i = 0 ; i < teamList.size() ; i++) {
                    Pair<Boolean, Team> oldPair = teamList.get(i);
                    if (oldPair.sencond.getId().equals(newTeam.getId())){
                        tempPairList.add(new Pair<Boolean, Team>(oldPair.first.booleanValue() , newTeam));
                        break;
                    }
                    if (i == tempPairList.size() - 1){
                        tempPairList.add(new Pair<Boolean, Team>(false , newTeam));
                    }
                }
            }
            teamList.clear();
            teamList.addAll(tempPairList);
        }
    };
    YXClient.OnThingsChangedListener<Bundle> onUserInfoChangedListener = new YXClient.OnThingsChangedListener<Bundle>() {
        @Override
        public void onThingChanged(Bundle thing) {
            if (mainAdapter != null){
                mainAdapter.notifyDataSetChanged();
            }
        }
    };
    YXClient.OnThingsChangedListener<Pair<String, List<TeamMember>>> onTeamMemberChangeListener = new YXClient.OnThingsChangedListener<Pair<String, List<TeamMember>>>() {
        @Override
        public void onThingChanged(Pair<String, List<TeamMember>> thing) {
            if (mainAdapter != null){
                mainAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void initTitleBar(RelativeLayout titleBarLayout, Button leftBtn, TextView titleTv, Button rightBtn) {
        titleTv.setText("联系人");
        rightBtn.setText("发送");
    }

    @Override
    public void onTitleBarRightBtnClick(View view) {
        if (hasChoosedMemberList.size() == 1){
            String account = hasChoosedMemberList.get(0).getAccount();
            String name = YXClient.getInstance().getUserNameByID(account);
            name = TextUtils.isEmpty(name) ? account : name;
            openActivity(ChattingActivity.class ,
                    "id" , account ,
                    "type" , SessionTypeEnum.P2P.toString() ,
                    "name" , name
            );
        }
        else if (hasChoosedMemberList.size() > 1){
            Intent intent = new Intent(this , MultiChattingActivity.class);
            ArrayList<String> idList = new ArrayList<String>(){
                {
                    for (TeamMember member: hasChoosedMemberList) {
                        add(member.getAccount());
                    }
                }
            };
            ArrayList<String> nameList = new ArrayList<String>(){
                {
                    for (TeamMember member: hasChoosedMemberList) {
                        String name = YXClient.getInstance().getUserNameByID(member.getAccount());
                        name = TextUtils.isEmpty(name) ? member.getAccount() : name;
                        add(name);
                    }
                }
            };
            intent.putExtra("idList" , idList);
            intent.putExtra("nameList" , nameList);
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
    public void loadData() {}

    private void initMainListview(){
        mainAdapter = new MainListViewAdapter();
        groupListAdapter = new GroupListAdapter();
        binding.mainListview.setDividerHeight(0);
        initHeadView();
        binding.mainListview.setAdapter(mainAdapter);
        YXClient.getInstance().with(this).addOnMyTeamListChangeListener(onMyTeamListChangedListener);
        YXClient.getInstance().with(this).addOnUserInfoChangeListener(onUserInfoChangedListener);
        YXClient.getInstance().with(this).addOnTeamMemberChangeListener(onTeamMemberChangeListener);
        ArrayList<Team> originTeamList = YXClient.getInstance().getMyTeamList();
        for (Team team : originTeamList) {
            teamList.add(new Pair(false , team));
        }
        mainAdapter.notifyDataSetChanged();
        groupListAdapter.notifyDataSetChanged();
    }

    private void initHeadView(){
        final ItemContactListBinding tempBinding = DataBindingUtil.inflate(LayoutInflater.from(ContactListActivity.this)
                , R.layout.item_contact_list , binding.mainListview , false);
        binding.mainListview.addHeaderView(tempBinding.getRoot());
        tempBinding.separatorLine.getLayoutParams().height = 10;
        tempBinding.separatorLine.setBackgroundColor(0xffdbdbdb);
        tempBinding.titleTv.setText("群组");
        tempBinding.titleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (headViewExpanded) {
                    tempBinding.subRecyclerview.setVisibility(View.GONE);
                    headViewExpanded = false;
                    tempBinding.arrowImv.setImageResource(R.drawable.img_arrow_right);
                } else {
                    tempBinding.subRecyclerview.setVisibility(View.VISIBLE);
                    headViewExpanded = true;
                    tempBinding.arrowImv.setImageResource(R.drawable.img_arrow_down);
                }
            }
        });
        tempBinding.subRecyclerview.setLayoutManager(new LinearLayoutManager(ContactListActivity.this, LinearLayoutManager.VERTICAL, false));
        tempBinding.subRecyclerview.setAdapter(groupListAdapter);
        tempBinding.subRecyclerview.setOnItemTouchListener(new OnRecyclerItemClickListener(tempBinding.subRecyclerview) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                GroupListAdapter.GroupListAdapterHolder holder = (GroupListAdapter.GroupListAdapterHolder) vh;
                openActivity(ChattingActivity.class ,
                        "id" , holder.binding.getTeam().getId() ,
                        "type" , SessionTypeEnum.Team.toString() ,
                        "name" , holder.binding.getTeam().getName());
            }
        });
    }

    private class MainListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return teamList.size();
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
            final Team team = teamList.get(position).sencond;
            if (convertView == null){
                convertView = LayoutInflater.from(ContactListActivity.this).inflate(R.layout.item_contact_list , parent , false);
                ItemContactListBinding tempBinding = DataBindingUtil.bind(convertView);
                convertView.setTag(tempBinding);
                tempBinding.separatorLine.getLayoutParams().height = 1;
                tempBinding.separatorLine.setBackgroundColor(0xff000000);
                tempBinding.subRecyclerview.setAdapter(new MemberAdapter(YXClient.getInstance().getTeamMemberByID(team.getId())));
                tempBinding.subRecyclerview.setLayoutManager(new GridLayoutManager(ContactListActivity.this , 4));
                tempBinding.subRecyclerview.setOnItemTouchListener(new OnRecyclerItemClickListener(tempBinding.subRecyclerview) {
                    @Override
                    public void onItemClick(RecyclerView.ViewHolder vh) {
                        MemberAdapter.MemberAdapterHolder holder = (MemberAdapter.MemberAdapterHolder) vh;
                        boolean hasFound = false;
                        for (int i = 0 ; i < hasChoosedMemberList.size() ; i++){
                            TeamMember member = hasChoosedMemberList.get(i);
                            if (holder.binding.getTeamMember() != null
                                    && member.getAccount().equals(holder.binding.getTeamMember().getAccount())){
                                hasChoosedMemberList.remove(member);
                                hasFound = true;
                                break;
                            }
                        }
                        if (!hasFound){
                            hasChoosedMemberList.add(holder.binding.getTeamMember());
                        }
                        mainAdapter.notifyDataSetChanged();
                    }
                });
                AutoUtils.auto(convertView);
            }

            final ItemContactListBinding tempBinding = (ItemContactListBinding) convertView.getTag();
            tempBinding.titleTv.setText(team.getName());
            if (teamList.get(position).first){
                ((MemberAdapter)tempBinding.subRecyclerview.getAdapter()).members.clear();
                List<TeamMember> teamMemberList = YXClient.getInstance().getTeamMemberByID(team.getId());
                if (teamMemberList != null){
                    teamMemberList = ListUtil.conditionalSubList(teamMemberList,
                            new ListUtil.ConditionJudger<TeamMember>() {
                                @Override
                                public boolean isMatchCondition(TeamMember nodeInList) {
                                    return !nodeInList.getAccount().equals(SpUtil.justForTest());
                                }
                            }
                    );
                    ((MemberAdapter)tempBinding.subRecyclerview.getAdapter()).members.addAll(teamMemberList);
                }
                tempBinding.subRecyclerview.getAdapter().notifyDataSetChanged();
                tempBinding.subRecyclerview.setVisibility(View.VISIBLE);
                tempBinding.arrowImv.setImageResource(R.drawable.img_arrow_down);
            }
            else {
                tempBinding.subRecyclerview.setVisibility(View.GONE);
                tempBinding.arrowImv.setImageResource(R.drawable.img_arrow_right);
            }

            tempBinding.titleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    teamList.get(position).first = !teamList.get(position).first;
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }

    private class GroupListAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ContactListActivity.this).inflate(R.layout.item_group_list , parent , false);
            AutoUtils.auto(view);
            return new GroupListAdapterHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemGroupListBinding binding = ((GroupListAdapterHolder) holder).binding;
            Team team = teamList.get(position).sencond;
            binding.setTeam(team);
        }

        @Override
        public int getItemCount() {
            return teamList.size();
        }

        public class GroupListAdapterHolder extends RecyclerView.ViewHolder{
            ItemGroupListBinding binding;
            public GroupListAdapterHolder(View itemView) {
                super(itemView);
                binding = DataBindingUtil.bind(itemView);
            }
        }
    }


    private class MemberAdapter extends RecyclerView.Adapter{
        List<TeamMember> members = new ArrayList<TeamMember>();

        public MemberAdapter(List<TeamMember> members) {
            if (members != null){
                this.members.addAll(members);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ContactListActivity.this).inflate(R.layout.item_member_list , parent , false);
            AutoUtils.auto(view);
            return new MemberAdapterHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ItemMemberListBinding binding = ((MemberAdapterHolder)holder).binding;
            final TeamMember member = members.get(position);
            binding.setTeamMember(member);
            binding.memberNameTv.setText(YXClient.getInstance().getUserNameByID(member.getAccount()));
            String myavatarPath = YXClient.getInstance().getUserAvatarByID(member.getAccount());
            Glide.with(ContactListActivity.this)
                    .load(myavatarPath)
                    .placeholder(R.drawable.icon_wenda)
                    .transform(new GlideCircleTransform(ContactListActivity.this))
                    .into(binding.avatarImv);

            boolean hasFound = false;
            for (int i = 0 ; i < hasChoosedMemberList.size() ; i++){
                TeamMember tempMember = hasChoosedMemberList.get(i);
                if (tempMember.getAccount().equals(member.getAccount())){
                    hasFound = true;
                    break;
                }
            }
            binding.checkbox.setSelected(hasFound);
        }

        @Override
        public int getItemCount() {
            if (members == null){
                return 0;
            }
            else {
                return members.size();
            }
        }

        public class MemberAdapterHolder extends RecyclerView.ViewHolder{
            ItemMemberListBinding binding;
            public MemberAdapterHolder(View itemView) {
                super(itemView);
                binding = DataBindingUtil.bind(itemView);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
