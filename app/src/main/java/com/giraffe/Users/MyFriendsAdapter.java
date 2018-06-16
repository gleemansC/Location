package com.giraffe.Users;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import giraffe.com.location.FriendsTask;
import giraffe.com.location.R;

/**
 * Created by Mr.Giraffe on 2018/6/4.
 */

public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.ViewHolder> {

    private HashMap<Integer, Integer> mDataset1;
    private HashMap<Integer, String> mDataset2;

    public MyFriendsAdapter(HashMap<Integer, Integer> myDataset1, HashMap<Integer, String> myDataset2) {
        this.mDataset1 = myDataset1;
        this.mDataset2 = myDataset2;
    }

    public void update(HashMap<Integer, Integer> myDataset1, HashMap<Integer, String> myDataset2) {
        this.mDataset1 = myDataset1;
        this.mDataset2 = myDataset2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recyclerview, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.id.setText(String.valueOf(mDataset1.get(position)));
        holder.admin.setText(mDataset2.get(position));
        holder.button.setId(mDataset1.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset1.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView id;
        private TextView admin;
        private Button button;

        private FriendsTask friendsTask;
        public ViewHolder(final View v) {
            super(v);
            id = v.findViewById(R.id.itme_user_id);
            admin = v.findViewById(R.id.itme_admin_id);
            button = v.findViewById(R.id.itme_admin_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("dsfasdf", String.valueOf(view.getId()));
                    friendsTask = new FriendsTask();
                    friendsTask.execute(view.getId());
                }
            });
        }
    }
}
