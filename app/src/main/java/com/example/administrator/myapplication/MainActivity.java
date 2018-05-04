package com.example.administrator.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication.share.Analyzer;
import com.example.administrator.myapplication.share.app.Douyin;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        String shareUrl = this.getRecvString();
        if (shareUrl != null) {
            Analyzer analyzer = new Analyzer(this);
            analyzer.execute(shareUrl);

        }
    }

    protected String getRecvString()
    {
        Intent intent = getIntent();
        if(intent == null)
            return null;
        Bundle extras = intent.getExtras();

        if(extras == null)
            return null;

        switch (intent.getType()) {
            case "text/plain":
                return extras.get(Intent.EXTRA_TEXT).toString();
        }
        return null;
    }

}
