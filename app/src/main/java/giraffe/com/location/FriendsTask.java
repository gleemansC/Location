package giraffe.com.location;

import android.content.Intent;
import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

import giraffe.com.location.utils.Locationutils;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Mr.Giraffe on 2018/6/5.
 */

public class FriendsTask extends AsyncTask<Integer, Void, String> {
    private String url = "http://172.22.38.109:8080/gra_web/TestLocation";

    private String result;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(Integer... integers) {
        url = url + "?id=" + integers[0];
        Request request = new Request.Builder().get().url(url).build();

        try {
            result = client.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    ObjectMapper objectMapper = new ObjectMapper();
    HashMap<String, Double> map = null;

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            map = objectMapper.readValue(result, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Locationutils.FRIENDCONTEXT, MainActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("lat", map.get("lat"));
        intent.putExtra("long", map.get("long"));
        intent.putExtra("showFriend", true);
        Locationutils.FRIENDCONTEXT.startActivity(intent);
    }
}
