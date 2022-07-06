package com.example.alex.helloworld.CalendarActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.alex.helloworld.Information;
import com.example.alex.helloworld.R;
import java.util.ArrayList;

/**
 * Created by Korbi on 10/30/2016.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MeetingsViewHolder>{

    Context context;
    ArrayList<Information> meetingsOnDay;

    public RecyclerViewAdapter(Context context, ArrayList<Information> meetingsOnDay){
        this.context = context;
        this.meetingsOnDay = meetingsOnDay;
    }

    //doesnt need an int position because the card looks the same for all; still this is iteratad through before and just like onBindViewHolder!
    @Override
    public MeetingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //this is what the recyclerViewAdapter returns to the recyclerView. Equivalent to FragmentPagerAdapter's getItem which returns the Fragment

        View view = LayoutInflater.from(context).inflate(R.layout.item_invites, parent, false);
        return new MeetingsViewHolder(view);
    }

    //onBindViewholder seems to be iterated through, probably getItemCount() times!!
    @Override
    public void onBindViewHolder(MeetingsViewHolder meetingsViewHolder, int position) {

        meetingsViewHolder.textview.setText(meetingsOnDay.get(position).MeetingID);
        meetingsViewHolder.start.setText(meetingsOnDay.get(position).startTime.substring(11, 16));
        meetingsViewHolder.stopp.setText(meetingsOnDay.get(position).endTime.substring(11, 16));

        final int currentPosition = position;
        final Information infoData = meetingsOnDay.get(position);

        meetingsViewHolder.keineZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(infoData);
            }
        });

        meetingsViewHolder.habZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(infoData);
            }
        });
    }

    public void removeItem(Information meeting){
        int position = meetingsOnDay.indexOf(meeting);
        meetingsOnDay.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(int pos, Information meeting) {
        meetingsOnDay.add(pos, meeting);
        notifyItemInserted(pos);
    }

    @Override
    public int getItemCount() {
        return meetingsOnDay.size();
    }

    class MeetingsViewHolder extends RecyclerView.ViewHolder {

        TextView textview;
        TextView start;
        TextView stopp;
        Button habZ;
        Button keineZ;

        public MeetingsViewHolder(View itemView) {
            super(itemView);
            textview = (TextView) itemView.findViewById(R.id.meeting_name);
            start = (TextView) itemView.findViewById(R.id.startzeit);
            stopp = (TextView) itemView.findViewById(R.id.stoppzeit);
            habZ = (Button) itemView.findViewById(R.id.accept_meeting_button);
            keineZ = (Button) itemView.findViewById(R.id.decline_meeting_button);
        }
    }
}
