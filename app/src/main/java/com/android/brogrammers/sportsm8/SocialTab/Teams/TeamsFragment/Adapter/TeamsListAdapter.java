package com.android.brogrammers.sportsm8.SocialTab.Teams.TeamsFragment.Adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.SocialTab.ClickListener;
import com.android.brogrammers.sportsm8.SocialTab.Adapter.SelectableAdapter;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Team;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;


public class TeamsListAdapter extends SelectableAdapter<TeamsListAdapter.MyViewHolder> {

    private Context context;
    private List<Team> teams;
    private LayoutInflater inflater;
    private ClickListener clickListener;
    private TypedArray logoArray;
    private final PublishSubject<Team> onClickSubject = PublishSubject.create();

    public TeamsListAdapter(Context context, ClickListener clickListener, List<Team> data) {
        this.context = context;
        this.clickListener = clickListener;
        this.teams = data;
        inflater = LayoutInflater.from(context);
        logoArray = context.getResources().obtainTypedArray(R.array.sportLogos);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_team_view, parent, false);
        return new MyViewHolder(view, context, clickListener);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.teamName.setText(teams.get(position).teamName);
        holder.itemView.setBackgroundColor(isSelected(position) ? ContextCompat.getColor(context, R.color.lightblue) : ContextCompat.getColor(context, R.color.cardview_light_background));
        if (teams.get(position).sportID != 8008)
            holder.sportLogo.setImageResource(logoArray.getResourceId(teams.get(position).sportID, R.drawable.beachball));
        //(setScaleAnimation(holder.itemView);
    }

    public Observable<Team> getPositionClicks() {
        return onClickSubject.hide();
    }


    @Override
    public int getItemCount() {
        return teams.size();
    }

    public void setTeamList(List<Team> teamList) {
        this.teams = teamList;
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @BindView(R.id.team_name)
        TextView teamName;
        @BindView(R.id.team_imageview)
        ImageView sportLogo;
        Context contxt;

        ClickListener listener;

        MyViewHolder(final View itemView, Context ctx, ClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.contxt = ctx;

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
            itemView.setSelected(false);
            this.listener = listener;
        }


        @Override
        public void onClick(View view) {
            onClickSubject.onNext(teams.get(getAdapterPosition()));
        }


        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    }


}


