package com.android.brogrammers.sportsm8.UserClasses;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.ZZOldClassers.ActivitiesFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountPage extends Fragment {

    Activity parentActivity;
    private AccountPage.OnFragmentInteractionListener mListener;

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

    public AccountPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ActivitiesFragment.
     */
    public static AccountPage newInstance(String param1, String param2) {
        return new AccountPage();
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account_page, container, false);
        //toolbar problem needs to be solved first
        ButterKnife.bind(this,rootView);
        //createList();
        return rootView;
    }
   /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);


        SharedPreferences sharedPrefs = getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
        String email = sharedPrefs.getString("email","");
        emailTV.setFilterText(email);


        String params[]= {"IndexAccounts.php","function","getAccountInfo","email",email};
        Database db = new Database(this,getApplicationContext());
        db.execute(params);



    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActivitiesFragment.OnFragmentInteractionListener) {
            mListener = (AccountPage.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
