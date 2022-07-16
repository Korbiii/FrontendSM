package com.android.brogrammers.sportsm8.UserClasses.AccountPage;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl.DatabaseAccountRepository;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.UserClasses.LoginScreen;
import com.android.brogrammers.sportsm8.ZZOldClassers.ActivitiesFragment;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class AccountPage extends AppCompatActivity implements AccountPageView {
    @BindView(R.id.email_accountpage)
    TextView emailTV;
    @BindView(R.id.username_accountpage)
    TextView usernameTV;
    @BindView(R.id.accountpage_pp)
    CircleImageView circleImageView;
    @BindView(R.id.friend_number)
    TextView friendcount;
    @BindView(R.id.group_memberships_number)
    TextView groupcount;
    @BindView(R.id.meetings_number)
    TextView meetingsNumeber;
    private AccountPagePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        presenter = new AccountPagePresenter(AndroidSchedulers.mainThread(), new DatabaseAccountRepository(), this);
        presenter.loadUserData();
    }


    @Override
    public void displayUserInfo(UserInfo userInfo) {
        emailTV.setText(LoginScreen.getRealEmail());
        usernameTV.setText(userInfo.username);
        groupcount.setText(String.valueOf(userInfo.groupcount));
        friendcount.setText(String.valueOf(userInfo.friendcount));
        meetingsNumeber.setText(String.valueOf(userInfo.meetingcount));
        Picasso.with(this)
                .load("http://sportsm8.bplaced.net" + userInfo.PPpath)
                .error(R.drawable.dickbutt)
               // .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                .into(circleImageView);
    }
}
