package com.giraffe.Users;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import giraffe.com.location.MainActivity;
import giraffe.com.location.R;
import giraffe.com.location.utils.Locationutils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FriendsActivity extends AppCompatActivity {

    private ListView listView;
    private Myhandler myhandler = new Myhandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locationutils.FRIENDCONTEXT = FriendsActivity.this.getBaseContext();
        setContentView(R.layout.activity_friends);

        listView = findViewById(R.id.list_layout);

        FriendTask friendTask = new FriendTask();
        friendTask.execute();

        listView.setOnItemClickListener(new ItemListener());

        Myhandler myhandler = new Myhandler();


    }

    class ItemListener implements AdapterView.OnItemClickListener {
        String url = "http://172.22.38.109:8080/gra_web/TestLocation";

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().get().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    ObjectMapper objectMapper = new ObjectMapper();
                    HashMap<String, Double> map = objectMapper.readValue(result, HashMap.class);

                    Intent intent = new Intent(FriendsActivity.this, MainActivity.class);

                    intent.putExtra("lat", map.get("lat"));
                    intent.putExtra("long", map.get("long"));
                    intent.putExtra("showFriend", true);
                    startActivity(intent);
                }
            });

        }
    }

    class Myhandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            String[] users = new String[msg.getData().size()];

            for (int i = 0; i < msg.getData().size(); i++) {
                users[i] = msg.getData().getString(Integer.toString(i));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (FriendsActivity.this, R.layout.support_simple_spinner_dropdown_item, users);
            listView.setAdapter(adapter);
        }

    }

    class test extends ArrayAdapter<User> {

        public test(@NonNull Context context, int resource) {
            super(context, resource);
        }
    }

    private class FriendTask extends AsyncTask<Void, Void, String> {
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

        @Override
        protected void onPostExecute(String s) {
            ObjectMapper jsmapper = new ObjectMapper();
            Message msg = new Message();
            Bundle bundle = new Bundle();
            String[] usr_string = new String[2];
            int i = 0;
            try {
                User[] users = jsmapper.readValue(s, User[].class);

                for (User u : users) {
                    usr_string[0] = Integer.toString(u.getUser_id());
                    usr_string[1] = u.getAdmin();

                    bundle.putString(Integer.toString(i++), usr_string[0] + "       " +
                            "         " + usr_string[1]);

                }

                msg.setData(bundle);
                myhandler.sendMessage(msg);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

//            ArrayAdapter<User[]> adapter = new ArrayAdapter<String>
//                    (FriendsActivity.this, R.layout.support_simple_spinner_dropdown_item, users);
//            listView.setAdapter(adapter);
        }
    }
}
