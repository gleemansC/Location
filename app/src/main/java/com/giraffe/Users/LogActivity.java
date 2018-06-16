package com.giraffe.Users;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.giraffe.Users.utils.Userutils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import giraffe.com.location.MainActivity;
import giraffe.com.location.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LogActivity extends AppCompatActivity {

    private EditText admin;
    private EditText pwd;

    private String admin_buf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        admin = findViewById(R.id.acnt_id);
        pwd = findViewById(R.id.pwd_id);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.sign_menu_id:
                Intent intent = new Intent(LogActivity.this, SignActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClick(View view) {
        admin_buf = this.admin.getText().toString();
        String pwd = this.pwd.getText().toString();

        LoginClient logClient = new LoginClient();
        logClient.execute(admin_buf, pwd);
    }

    private class LoginClient extends AsyncTask<String, Void, String> {
        private OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).build();

        private String url = "http://172.22.38.109:8080/gra_web/LoginServlet";

        private String bowlingJson(String username, String pwd) {
            return "{\"admin\":\"" + username + "\",\"pwd\":\"" + pwd + "\"}";
        }

        private String result;

        @Override
        protected String doInBackground(String... strings) {
            String json = bowlingJson(strings[0], strings[1]);
            RequestBody body = RequestBody.create(Userutils.JSON, json);
            final Request request = new Request.Builder()
                    .url(url).post(body)
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
            if (s.equals("0")) {
                Toast.makeText(LogActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
            } else if (s.equals("-1")) {
                Toast.makeText(LogActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
            } else {
                //登陆成功，接收字符串S返回用户ID号
                Toast.makeText(LogActivity.this, "登录成功",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LogActivity.this, MainActivity.class);
                intent.putExtra("admin", admin_buf);
                Userutils.setUserid(s);
                startActivity(intent);
            }
        }
    }

}

