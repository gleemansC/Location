package giraffe.com.location;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class BuffActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buff);
        textView = findViewById(R.id.buff_ids);
        Intent intent = getIntent();
        String buff = intent.getStringExtra("buff");
        textView.setText(buff);
    }
}
