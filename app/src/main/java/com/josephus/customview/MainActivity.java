package com.josephus.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.josephus.customview.view.CountdownView;
import com.josephus.customview.view.DepthView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((CountdownView) findViewById(R.id.cdv)).setTime(122279);

        DepthView.DepthViewData depthViewData = new DepthView.DepthViewData();
        depthViewData.bidCoinPrice = "3,865,000";
        depthViewData.bidCurrencyPrice = "3,865,000 KRW";
        depthViewData.bidAmount = "0.09";
        depthViewData.bidProgress = 100;
        depthViewData.bidUpOrDown = 0;
        depthViewData.askCoinPrice = "4,030,000";
        depthViewData.askCurrencyPrice = "4,030,000 KRW";
        depthViewData.askAmount = "90,013.4883";
        depthViewData.askProgress = 69;
        depthViewData.askUpOrDown = 1;
        ((DepthView) findViewById(R.id.dv)).setData(depthViewData);
    }
}
