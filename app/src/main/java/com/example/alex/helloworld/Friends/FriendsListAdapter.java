package com.example.alex.helloworld.Friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alex.helloworld.Information;
import com.example.alex.helloworld.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.MyViewHolder> {


    private final static int FADE_DURATION = 300;
    private Context context;
    private ArrayList<Information> data;
    private LayoutInflater inflater;
    private FriendsListFragment friendsListFragment;
    private Boolean selectionMode;
    private int count = 0;


    public FriendsListAdapter(Context context, ArrayList<Information> data, FriendsListFragment friendsListFragment, Boolean selectionMode) {
        this.context = context;
        this.data = data;
        this.friendsListFragment= friendsListFragment;
        this.selectionMode = selectionMode;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_friends_view, parent, false);
        return new MyViewHolder(view, context);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        //Loads profile Picture with Ion Library in an AsyncTask
        Picasso.with(context)
                .load("http://sportsm8.bplaced.net" + data.get(position).PPpath)
                .placeholder(R.drawable.dickbutt)
                .error(R.drawable.dickbutt)
                .into(holder.profileP);
        /* Ion.with(context)
                .load("http://sportsm8.bplaceds.net" + data.get(position).PPpath)
                .noCache()
                .withBitmap()
                .placeholder(R.drawable.dickbutt)
                .error(R.drawable.dickbutt)
                .intoImageView(holder.profileP);


        Ion.with(holder.profileP)
                .placeholder(R.drawable.dickbutt)
                .error(R.drawable.dickbutt)
                .load("http://sportsm8.bplaced.net" + data.get(position).PPpath);*/

        holder.username.setText(data.get(position).username);
        holder.email.setText(data.get(position).email);
        setScaleAnimation(holder.itemView);
    }

    //search is not working right now
    public int search(String search) {
        int posi = 0;
        for (int i = 0; i < getItemCount(); i++) {
            if (data.get(i).getUsername().toLowerCase().startsWith(search.toLowerCase())) {
                posi = i;
            }
        }

        return posi;
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView username;
        TextView email;
        ImageView profileP;
        Context contxt;


        public MyViewHolder(final View itemView, Context ctx) {
            super(itemView);
            this.contxt = ctx;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setSelected(false);
            profileP = (ImageView) itemView.findViewById(R.id.profile_picture);
            username = (TextView) itemView.findViewById(R.id.username_text);
            email = (TextView) itemView.findViewById(R.id.user_email);

        }


        @Override
        public void onClick(View view) {
            if (selectionMode) {
                friendsListFragment.toggle(getAdapterPosition());
                if (!view.isSelected()) {
                    count++;
                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    view.setSelected(true);
                } else if (view.isSelected()) {
                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.cardview_light_background));
                    view.setSelected(false);
                    count--;

                }
                friendsListFragment.updateCount(count);
            }
        }


        @Override
        public boolean onLongClick(View view) {
            if (!selectionMode) {
                selectionMode = true;
                friendsListFragment.toggle(getAdapterPosition());
                if (!view.isSelected()) {
                    count++;
                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    view.setSelected(true);
                } else if (view.isSelected()) {
                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.cardview_light_background));
                    view.setSelected(false);
                    count--;

                }
                friendsListFragment.activateSelectionMode(true, count);
                return true;
            }
            return false;
        }
    }


}


