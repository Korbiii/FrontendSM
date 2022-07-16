package com.android.brogrammers.sportsm8.MatchFeedTab.SocialFeedFragment;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Match;

import java.util.List;

/**
 * Created by Korbi on 22.06.2017.
 */

public interface SocialFeedView {

    void displayMatches(List<Match> matches);
}
