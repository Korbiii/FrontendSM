package com.android.brogrammers.sportsm8.MatchFeedTab.SocialFeedFragment.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.brogrammers.sportsm8.BR;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Match;

import java.util.List;

/**
 * Created by Korbi on 22.06.2017.
 */

public class MatchCardAdapter extends RecyclerView.Adapter<MatchCardAdapter.MatchViewHolder> {
    private Context context;
    private List<Match> matches;
    private TypedArray logoArray;

    public MatchCardAdapter(Context context, List<Match> matches) {
        this.context = context;
        this.matches = matches;
        Resources res = context.getResources();
        logoArray = res.obtainTypedArray(R.array.sportLogos);
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_match, parent, false);
        return new MatchViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(MatchViewHolder holder, int position) {
        ViewDataBinding viewDataBinding = holder.getViewDataBinding();
        viewDataBinding.setVariable(BR.match,matches.get(position));
        viewDataBinding.executePendingBindings();

        holder.time.setText(matches.get(position).getTime().toString("dd.MM.yyyy HH:mm"));
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }


    class MatchViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding mViewDataBinding;
        private TextView time;

        public MatchViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            mViewDataBinding = viewDataBinding;
            time = (TextView) viewDataBinding.getRoot().findViewById(R.id.timeTextView);

        }

        public ViewDataBinding getViewDataBinding() {
            return mViewDataBinding;
        }
    }
}
