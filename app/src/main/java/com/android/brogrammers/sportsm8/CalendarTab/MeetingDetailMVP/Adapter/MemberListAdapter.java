package com.android.brogrammers.sportsm8.CalendarTab.MeetingDetailMVP.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;

import java.util.List;

/**
 * Created by Korbi on 08.06.2017.
 */

public class MemberListAdapter extends BaseAdapter {

    private Context context;
    private List<UserInfo> list;

    public MemberListAdapter(Context context, List<UserInfo> memberList) {
        this.list = memberList;
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
        View row = inflater.inflate(R.layout.item_listitem, viewGroup, false);
        TextView username = (TextView) row.findViewById(R.id.meeting_username);
        TextView time = (TextView) row.findViewById(R.id.meeting_user_time);
        int end = list.get(i).begin + list.get(i).duration;
        username.setText(list.get(i).username);
        if (list.get(i).confirmed == 1) {
            row.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
        }
        if (list.get(i).mystartTime != null) {
            if (!list.get(i).mystartTime.equals("0000-00-00 00:00:00")) {
                time.setText(list.get(i).getStartDateTime().toString("HH:mm") + " - " + list.get(i).getEndDateTime().toString("HH:mm"));
            }
        }
        return row;
    }

    public void setList(List<UserInfo> list) {
        this.list = list;
    }
}
