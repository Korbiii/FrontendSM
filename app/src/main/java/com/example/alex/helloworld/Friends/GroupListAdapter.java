package com.example.alex.helloworld.Friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alex.helloworld.Information;
import com.example.alex.helloworld.R;
import java.util.ArrayList;


public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.MyViewHolder> {

    Context context;
    ArrayList<Information> data;
    ArrayList<Information> backup;
    LayoutInflater inflater;
    String email;

    public GroupListAdapter(Context context, ArrayList<Information> data) {
        this.context = context;
        this.data = data;
        backup = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_group_view, parent, false);
        return new MyViewHolder(view, context, data);
    }
    private final static int FADE_DURATION = 300;

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.groupname.setText(data.get(position).GroupName);
        setScaleAnimation(holder.itemView);
    }

    public int search(String search){
        int posi=0;
        for(int i=0;i<getItemCount();i++){
            if(data.get(i).getUsername().toLowerCase().startsWith(search.toLowerCase())){
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



    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView groupname;
        TextView email;
        ArrayList<Information> informations = new ArrayList<>();
        Context contxt;


        public MyViewHolder(final View itemView, Context ctx, ArrayList<Information> info) {
            super(itemView);
            this.informations = info;
            this.contxt = ctx;
            itemView.setOnClickListener(this);
            itemView.setSelected(false);
            groupname = (TextView) itemView.findViewById(R.id.group_name);


        }


        @Override
        public void onClick(View view) {

            if(!view.isSelected()) {
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                view.setSelected(true);
            }else if(view.isSelected()){
                view.setBackgroundColor(ContextCompat.getColor(context,R.color.cardview_light_background));
                view.setSelected(false);
            }
        }
    }


}


