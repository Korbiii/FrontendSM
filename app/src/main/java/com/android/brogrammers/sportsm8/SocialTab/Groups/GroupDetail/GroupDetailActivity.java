package com.android.brogrammers.sportsm8.SocialTab.Groups.GroupDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl.DatabaseGroupRepository;
import com.android.brogrammers.sportsm8.SocialTab.Friends.OnlyFriendsView;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class GroupDetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,GroupDetailView {

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int groupID;
    private GroupDetailPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        presenter= new GroupDetailPresenter(AndroidSchedulers.mainThread(),new DatabaseGroupRepository(),this);
        //Variables
        Bundle b = getIntent().getExtras();
        groupID = b.getInt("GroupID");
        String groupName = b.getString("GroupName");
        //Views
        ((TextView) findViewById(R.id.group_name_detailview)).setText(groupName);
        listView = (ListView) findViewById(R.id.lv_group_member);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.meeting_detail_swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        presenter.loadMembers(groupID);
    }

    @OnClick(R.id.add_members_to_group)
    public void addMembers(){
        Bundle bundle = new Bundle();
        bundle.putBoolean("search", false);
        Intent intent = new Intent(this, OnlyFriendsView.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.btn_leave_group)
    public void leaveGroup(){
        presenter.leaveGroup(groupID);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                presenter.addMembersToGroup(groupID,(ArrayList<UserInfo>) bundle.getSerializable("partyList"));
            }
        }
    }

    @Override
    public void onRefresh() {
        presenter.loadMembers(groupID);
    }

    @Override
    public void displayMembers(List<UserInfo> memberList) {
        ListViewAdapter arrayAdapter = new ListViewAdapter(this, memberList);
        listView.setAdapter(arrayAdapter);
        TextView textView = (TextView) findViewById(R.id.tv_group_size);
        textView.setText(memberList.size() + "");
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void leave() {
        finish();
    }


    class ListViewAdapter extends BaseAdapter {

        List<UserInfo> list;
        Context context;

        public ListViewAdapter(Context context, List<UserInfo> listItem) {
            list = listItem;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            TextView textView = (TextView) row.findViewById(android.R.id.text1);
            textView.setText(list.get(i).email);
            if (Integer.valueOf(list.get(i).confirmed) == 1) {
                row.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
            }
            return row;

        }
    }
}
