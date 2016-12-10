package com.example.alex.helloworld.Friends;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alex.helloworld.Information;
import com.example.alex.helloworld.R;
import com.example.alex.helloworld.databaseConnection.Database;
import com.example.alex.helloworld.databaseConnection.UIthread;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;


public class FriendsListAdapter extends SelectableAdapter<FriendsListAdapter.MyViewHolder> {


    private final static int FADE_DURATION = 500;
    private Context context;
    private ArrayList<Information> data, backup;
    private LayoutInflater inflater;
    private FriendsListFragment friendsListFragment;
    private OnlyFriendsView searchNewFriends;
    private Boolean addToMeetingMode, addFriendMode, creatGroupMode = false;
    private int count = 0;
    //Instance of clickListener for handling clickevents through Friends.java
    private ClickListener clickListener;


    public FriendsListAdapter(Context context, ClickListener clickListener, ArrayList<Information> data, Boolean addFriendMode) {
        super();
        this.clickListener = clickListener;
        this.context = context;
        this.data = data;
        this.backup = data;
        //Is for when, adapter is used for searching new Friends
        this.addFriendMode = addFriendMode;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_friends_view, parent, false);
        return new MyViewHolder(view, context, clickListener);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //sets Background light blue, when card is selected
        holder.relativeLayoutCardview.setBackgroundColor(isSelected(position) ? ContextCompat.getColor(context, R.color.lightblue) : ContextCompat.getColor(context, R.color.cardview_light_background));
        //shows imagebutton, when used inside search new Friends view
        holder.addToFriends.setVisibility(addFriendMode ? View.VISIBLE:View.GONE);
        //shows friendsrequestbar, for unconfirmed Friends
        holder.friendsrequest.setVisibility(Integer.valueOf(data.get(position).confirmed) == 0&&!addFriendMode? View.VISIBLE : View.GONE);

        //Loads profile Picture with Ion Library in an AsyncTask
        Picasso.with(context)
                .load("http://sportsm8.bplaced.net" + data.get(position).PPpath)
                .placeholder(R.drawable.dickbutt)
                .error(R.drawable.dickbutt)
                // .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                .into(holder.profileP);

        holder.username.setText(data.get(position).username);
        holder.email.setText(data.get(position).email);
        // setScaleAnimation(holder.itemView);

    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);

    }

    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }
    //removes cards. Used for removing unconfirmed friends while selecting friends
    public void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            data.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    //called by setRefeernceMethod in Friends.java to connect Clicklistener
    public void setClicklistener(Friends clicklistener) {
        this.clickListener = clicklistener;
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, UIthread {
        TextView username;
        TextView email;
        ImageView profileP;
        CardView cardView;
        Context contxt;
        ImageButton accept, decline, addToFriends;
        LinearLayout friendsrequest;
        RelativeLayout relativeLayoutCardview;

        ClickListener listener;


        public MyViewHolder(final View itemView, Context ctx, ClickListener listener) {
            super(itemView);
            this.contxt = ctx;
            friendsrequest = (LinearLayout) itemView.findViewById(R.id.linearL_friendsrequest);
            accept = (ImageButton) itemView.findViewById(R.id.accept_friendship);
            decline = (ImageButton) itemView.findViewById(R.id.decline_friendship);
            addToFriends = (ImageButton) itemView.findViewById(R.id.add_to_friends);
            cardView = (CardView) itemView.findViewById(R.id.cardview_friends);
            profileP = (ImageView) itemView.findViewById(R.id.profile_picture3);
            username = (TextView) itemView.findViewById(R.id.username_text);
            email = (TextView) itemView.findViewById(R.id.user_email);
            relativeLayoutCardview = (RelativeLayout) itemView.findViewById(R.id.rL_cardview);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setSelected(false);
            accept.setOnClickListener(this);
            addToFriends.setOnClickListener(this);
            decline.setOnClickListener(this);
            //attaching OnitemClicklistener to every Card
            this.listener = listener;


        }

        @Override
        public void onClick(View view) {
            Database db = new Database(this, context);
            SharedPreferences sharedPrefs = contxt.getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
            String emailString = sharedPrefs.getString("email", "");
            String[] params;
            switch (view.getId()) {
                case R.id.accept_friendship:
                    friendsrequest.setVisibility(View.GONE);
                    params = new String[]{"IndexFriendship.php", "function", "confirmFriend", "email", emailString, "friendemail", data.get(getAdapterPosition()).email};
                    db.execute(params);
                    break;
                case R.id.decline_friendship:
                    params = new String[]{"IndexFriendship.php", "function", "deleteFriend", "email", emailString, "friendemail", data.get(getAdapterPosition()).email};
                    db.execute(params);
                    data.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    break;
                case R.id.add_to_friends:
                    params = new String[]{"IndexFriendship.php", "function", "setFriend", "email", emailString, "friendemail", data.get(getAdapterPosition()).email};
                    db.execute(params);
                    data.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    break;
                case R.id.cardview_friends:  //OnItemClickEvent calls Interface method to handle click inside Friends.java
                    if (listener != null) {
                        listener.onItemClicked(getAdapterPosition(), false);
                    }
                    break;
            }
        }


        @Override
        public boolean onLongClick(View view) {
            if (listener != null) {
                return listener.onItemLongClicked(getAdapterPosition(), false); //Calls Interfacemethod to handle click inside Friends.java
            }
            return false;
        }

        @Override
        public void updateUI() {

        }

        @Override
        public void updateUI(String answer) {

        }

    }


}


