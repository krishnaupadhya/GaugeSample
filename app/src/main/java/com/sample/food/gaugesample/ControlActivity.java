package com.sample.food.gaugesample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

public class ControlActivity extends AppCompatActivity {

    RiskView riskView;
    SeekBar seekBar;
    TextView textSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        riskView = (RiskView) findViewById(R.id.awesomeRiskometer);
        riskView.RiskTo(RiskView.RISKMODE.MEDIUM_RISK_MODE);

    }

    public void lowRisk(View view) {
        riskView.RiskTo(RiskView.RISKMODE.LOW_RISK_MODE);
    }

    public void lowModerateRisk(View view) {
        riskView.RiskTo(RiskView.RISKMODE.LOW_MODERATE_RISK_MODE);
    }

    public void mediumRisk(View view) {
        riskView.RiskTo(RiskView.RISKMODE.MEDIUM_RISK_MODE);
    }

    public void moderatelyHighRisk(View view) {
        riskView.RiskTo(RiskView.RISKMODE.MODERATELY_HIGH_RISK_MODE);
    }

    public void highRisk(View view) {
        riskView.RiskTo(RiskView.RISKMODE.HIGH_RISK_MODE);
    }


}
