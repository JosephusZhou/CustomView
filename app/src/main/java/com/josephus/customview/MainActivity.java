package com.josephus.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.josephus.customview.view.CountdownView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((CountdownView) findViewById(R.id.cdv)).setTime(122279);
    }
}
