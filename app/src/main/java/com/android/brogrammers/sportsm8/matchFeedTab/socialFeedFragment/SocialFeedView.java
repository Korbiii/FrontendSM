package com.android.brogrammers.sportsm8.matchFeedTab.socialFeedFragment;

import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Match;

import java.util.List;

/**
 * Created by Korbi on 22.06.2017.
 */

public interface SocialFeedView {

    void displayMatches(List<Match> matches);
}
