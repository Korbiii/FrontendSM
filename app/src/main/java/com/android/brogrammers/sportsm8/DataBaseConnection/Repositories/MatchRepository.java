package com.android.brogrammers.sportsm8.DataBaseConnection.Repositories;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Match;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Korbi on 22.06.2017.
 */

public interface MatchRepository {
    Single<List<Match>> getMatches();
}
