package com.giraffe.Users;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import giraffe.com.location.R;

public class SignActivity extends AppCompatActivity {

    private EditText admin;
    private EditText pwd;
    private EditText pwd2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        admin = findViewById(R.id.admin_sign_id);
        pwd = findViewById(R.id.pwd_sign_id);
        pwd2 = findViewById(R.id.pwd2_sign_id);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClick(View view) {
        String admin = this.admin.getText().toString();
        String pwd = this.pwd.getText().toString();
        String pwd2 = this.pwd2.getText().toString();
        if(!pwd.equals(pwd2)) {
            Toast.makeText(this, "密码输入错误", Toast.LENGTH_SHORT).show();
            return;
        }
        SignTask sign = new SignTask();
        sign.execute(admin, pwd);
        admin = null; pwd = null;pwd2 = null;
    }

    class SignTask extends AsyncTask<String, Void, String> {

        String result = "";
        String url = "http://172.22.38.109:8080/gra_web/SignServlet";

        @Override
        protected String doInBackground(String... strings) {

            url = url + "?admin=" + strings[0] + "&pwd=" + strings[1];
            try {

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(200);

                if(connection.getContentLength() == -1) {
                    Toast.makeText(SignActivity.this, "服务器关闭", Toast.LENGTH_SHORT).show();
                    return null;
                }

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Toast.makeText(SignActivity.this, "链接Web端失败", Toast.LENGTH_SHORT).show();
                }

                InputStream stream = connection.getInputStream();
                byte[] buff = new byte[connection.getContentLength()];
                stream.read(buff);
                stream.close();

                result = new String(buff);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("1")) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(SignActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignActivity.this, LogActivity.class);
                startActivity(intent);
            } else if(s.equals("0")) {
                Toast.makeText(SignActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignActivity.this, "账号已注册", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


