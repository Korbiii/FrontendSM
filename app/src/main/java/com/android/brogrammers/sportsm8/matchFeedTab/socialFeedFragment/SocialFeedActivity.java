package com.android.brogrammers.sportsm8.matchFeedTab.socialFeedFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.matchFeedTab.socialFeedFragment.adapter.MatchCardAdapter;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Match;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl.DatabaseMatchRepository;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Korbi on 22.06.2017.
 */

public class SocialFeedActivity extends Fragment implements SocialFeedView {


    private SocialFeedPresenter socialFeedPresenter;
    private List<Match> matches = new ArrayList<>();
    private MatchCardAdapter matchCardAdapter;
    @BindView(R.id.social_feed_recyclerview)
    RecyclerView socialFeedRecyclerView;

    public SocialFeedActivity() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_social_feed, container, false);
        ButterKnife.bind(this, rootView);
        socialFeedPresenter = new SocialFeedPresenter(new DatabaseMatchRepository(), this, AndroidSchedulers.mainThread());
        socialFeedPresenter.loadMatches();
        matchCardAdapter = new MatchCardAdapter(getContext(), matches);
        socialFeedRecyclerView.setAdapter(matchCardAdapter);
        socialFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return rootView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void displayMatches(List<Match> matches) {
        matchCardAdapter = new MatchCardAdapter(getContext(), matches);
        socialFeedRecyclerView.setAdapter(matchCardAdapter);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
