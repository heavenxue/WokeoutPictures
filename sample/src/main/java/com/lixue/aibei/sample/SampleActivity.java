package com.lixue.aibei.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.lixue.aibei.wokeoutpictures.SketchImageView;


public class SampleActivity extends ActionBarActivity {
    private SketchImageView sketchImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        sketchImageView = (SketchImageView) findViewById(R.id.myimageview);
        sketchImageView.displayIamge("http://img839.ph.126.net/iNTKLR3ldmr3_1cPpTMhGA==/1790180851881744413.png");
    }
}
