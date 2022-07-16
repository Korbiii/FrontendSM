package com.android.brogrammers.sportsm8.CalendarTab.MeetingDetailMVP.Adapter;

import android.content.Context;
import android.databinding.Bindable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.brogrammers.sportsm8.CalendarTab.MeetingDetailMVP.MeetingDetailActivity;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Korbi on 22.09.2017.
 */

public class MemberListAdapterRV extends RecyclerView.Adapter<MemberListAdapterRV.ViewHolder> {


    private final Context context;
    private List<UserInfo> memberList;

    public MemberListAdapterRV(Context context, List<UserInfo> memberList) {
        this.context = context;
        this.memberList = memberList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_profile_picture, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UserInfo temp = memberList.get(position);

        Picasso.with(context)
                .load("http://sportsm8.bplaced.net" +temp.PPpath)
                .placeholder(R.drawable.profilepicture)
                .into(holder.picture);
        if (temp.mystartTime.getYear() != -1) {
            holder.start.setText(memberList.get(position).getStartDateTime().toString("HH:mm -"));
            holder.end.setText(memberList.get(position).getEndDateTime().toString("HH:mm"));
        }
        if (temp.confirmed == 1) {
            holder.picture.setBorderColor(ContextCompat.getColor(context,R.color.green));
        }
        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MeetingDetailActivity) context).showUsername(temp.username);
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public void setList(List<UserInfo> list) {
        this.memberList = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView start, end;
        CircleImageView picture, indicator;

        ViewHolder(View itemView) {
            super(itemView);
            start = itemView.findViewById(R.id.tv_pp_starttime);
            end =  itemView.findViewById(R.id.tv_pp_endtime);
            picture =  itemView.findViewById(R.id.profile_picture);
            indicator =  itemView.findViewById(R.id.indicator_participation);
        }
    }
}
