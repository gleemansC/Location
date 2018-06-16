package giraffe.com.location;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giraffe.Users.utils.Userutils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import giraffe.com.location.utils.Locationutils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Mr.Giraffe on 2018/5/29.
 */

public class UpLoadTask extends AsyncTask<Double, Void, String> {
    private String url = "http://172.22.38.109:8080/gra_web/LatLngServlet";
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date date = new Date();

    private ObjectMapper jsmapper = new ObjectMapper();
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    private String result;

    @Override
    protected String doInBackground(Double... doubles) {
        HashMap<String, String> map = new HashMap<>();

        map.put("id", Userutils.getUserid());
        map.put("lat", doubles[0].toString());
        map.put("lng", doubles[1].toString());
        map.put("time", df.format(date));

        try {
            String buf = jsmapper.writeValueAsString(map);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), buf);
            Request request = new Request
                    .Builder()
                    .post(body)
                    .url(url).build();

            result = client.newCall(request).execute().body().string();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s1) {
        Log.v("asdfa", s1);
        Log.v("werwqr", String.valueOf(s1.equals("3")));
        if (s1.equals("3")) {

            Toast.makeText(Locationutils.MAINCONTEXT, "上传成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Locationutils.MAINCONTEXT, "上传成功", Toast.LENGTH_SHORT).show();
        }
    }
}
