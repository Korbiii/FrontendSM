package com.example.alex.helloworld.DisplayWeekActivity;

/**
 * Created by Korbi on 10/30/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alex.helloworld.Information;
import com.example.alex.helloworld.R;
import com.example.alex.helloworld.databaseConnection.Database;
import com.example.alex.helloworld.databaseConnection.UIthread;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by Korbi on 22.10.2016.
 */

public class DisplayWeekActivityAdapter extends RecyclerView.Adapter<DisplayWeekActivityAdapter.MyViewHolder> implements UIthread {

    Context context;
    ArrayList<Information> data;
    LayoutInflater inflater;
    DateTimeFormatter formatter;
    MyViewHolder myViewHolder;

    public DisplayWeekActivityAdapter(Context context, ArrayList<Information> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss");
        View view = inflater.inflate(R.layout.item_invites, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {


        DateTime time = formatter.parseDateTime(data.get(position).startTime);
        myViewHolder.start.setText(time.toString("HH:mm"));
        time = formatter.parseDateTime(data.get(position).endTime);
        myViewHolder.stopp.setText(time.toString("HH:mm"));
        myViewHolder.textview.setText(time.toString("dd.MM.YYYY"));
        Resources res = context.getResources();
        String[] array = res.getStringArray(R.array.sportarten);
        int x = Integer.valueOf(data.get(position).sportID);
        myViewHolder.meetingName.setText(array[x]);
        final Information infoData = data.get(position);
        if (Integer.valueOf(data.get(position).confirmed) == 0) {
            myViewHolder.indicator.setBackgroundColor(ContextCompat.getColor(context, R.color.red));

        } else if (Integer.valueOf(data.get(position).status) == 0) {
            myViewHolder.indicator.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
            myViewHolder.decline.setVisibility(View.GONE);
            myViewHolder.accept.setVisibility(View.GONE);

        } else {
            myViewHolder.indicator.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
            myViewHolder.decline.setVisibility(View.GONE);
            myViewHolder.accept.setVisibility(View.GONE);
        }

        onClickEvents(myViewHolder, position, infoData);

    }

    private void onClickEvents(final MyViewHolder myViewHolder, final int position, final Information infoData) {
        myViewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm(position, infoData, v, myViewHolder);
                myViewHolder.indicator.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
            }
        });
        myViewHolder.decline_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position, infoData, v);
            }
        });

        myViewHolder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position, infoData, v);
            }
        });
        myViewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (Integer.valueOf(data.get(position).confirmed) == 1) {
                    myViewHolder.decline_2.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
    }

    private void removeItem(int pos, Information infoData, View view) {
        SharedPreferences sharedPrefs = context.getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
        String email = sharedPrefs.getString("email", "");
        String[] params = {"IndexMeetings.php", "function", "declineAtt", "meetingID", data.get(pos).MeetingID, "email", email};
        Database db = new Database(this, context);
        db.execute(params);
        int position = data.indexOf(infoData);
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void confirm(int pos, Information infoData, View view, MyViewHolder myViewHolder) {
        SharedPreferences sharedPrefs = context.getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
        String email = sharedPrefs.getString("email", "");
        String[] params = {"IndexMeetings.php", "function", "confirmAtt", "meetingID", data.get(pos).MeetingID, "email", email};
        Database db = new Database(this, context);
        db.execute(params);
        view.setVisibility(View.GONE);
        myViewHolder.decline.setVisibility(View.GONE);
        data.get(pos).confirmed = "1";
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void updateUI() {

    }

    @Override
    public void updateUI(String answer) {

    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        View indicator;
        TextView textview, start, stopp, meetingName;
        Button decline, decline_2, accept;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            meetingName = (TextView) itemView.findViewById(R.id.meeting_name);
            indicator = itemView.findViewById(R.id.indicator_view);
            decline_2 = (Button) itemView.findViewById(R.id.decline_meeting_button_2);
            cardView = (CardView) itemView.findViewById(R.id.meeting_card);
            textview = (TextView) itemView.findViewById(R.id.date_textview);
            start = (TextView) itemView.findViewById(R.id.startzeit);
            stopp = (TextView) itemView.findViewById(R.id.stoppzeit);
            accept = (Button) itemView.findViewById(R.id.accept_meeting_button);
            decline = (Button) itemView.findViewById(R.id.decline_meeting_button);
        }
    }
}