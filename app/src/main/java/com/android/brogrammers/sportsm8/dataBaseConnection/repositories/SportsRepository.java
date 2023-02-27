package com.android.brogrammers.sportsm8.dataBaseConnection.repositories;

import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Sport;

import java.util.List;

import io.reactivex.Single;

public interface SportsRepository {

    Single<List<Sport>> getSports();
}
