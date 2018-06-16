package com.giraffe.Users;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import giraffe.com.location.R;
import giraffe.com.location.utils.Locationutils;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MyFriendsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyFriendsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private HashMap<Integer, Integer> myDataset1 = new HashMap<>();
    private HashMap<Integer, String> myDataset2 = new HashMap<>();

    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        Locationutils.FRIENDCONTEXT = MyFriendsActivity.this.getBaseContext();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        //优化设置
        mRecyclerView.setHasFixedSize(true);
        //设置管理器
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(MyFriendsActivity.this, LinearLayoutManager.VERTICAL));
        //传入字符串数组
        FriendsTask friendsTask = new FriendsTask();
        friendsTask.execute();
    }


    private class FriendsTask extends AsyncTask<Void, Void, String> {
        private String url = "http://172.22.38.109:8080/gra_web/FriendServlet";
        private String result = "";
        private OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).build();

        @Override
        protected String doInBackground(Void... voids) {
            Request request = new Request.Builder()
                    .url(url).get()
                    .build();
            try {
                result = client.newCall(request).execute().body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        private int buff = 0;

        @Override
        protected void onPostExecute(String s) {
            ObjectMapper jsmapper = new ObjectMapper();
            try {
                User[] users = jsmapper.readValue(s, User[].class);

                for (User u : users) {
                    Log.v("srtse", buff + "");
                    myDataset1.put(buff, u.getUser_id());
                    myDataset2.put(buff++, u.getAdmin());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//            初始化适配器
            Log.v("ewrqw", myDataset1.toString());
            Log.v("asdfa", myDataset2.toString());

            if (isFirst) {
                mAdapter = new MyFriendsAdapter(myDataset1, myDataset2);
                mRecyclerView.setAdapter(mAdapter);
                isFirst = false;
            }
            mAdapter.update(myDataset1, myDataset2);
        }
    }
}
