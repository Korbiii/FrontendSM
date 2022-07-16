package com.android.brogrammers.sportsm8.ZZOldClassers.TestDirectory;

import com.android.brogrammers.sportsm8.R;

/**
 * Created by iiro on 7.6.2016.
 */
public class TabMessage {
    public static String get(int menuItemId, boolean isReselection) {
        String message = "Content for ";

        switch (menuItemId) {
            case R.id.tab_account:
                message += "favorites";
                break;
            case R.id.tab_calendar:
                message += "nearby";
                break;
            case R.id.tab_friends:
                message += "friends";
                break;
        }

        if (isReselection) {
            message += " WAS RESELECTED! YAY!";
        }

        return message;
    }
}
