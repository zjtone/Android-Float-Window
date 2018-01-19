package com.example.zjt.floatrecorder;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.zjt.floatrecorder.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        MainActivityViewModel viewModel = new MainActivityViewModel(this,mainBinding);
        mainBinding.setViewModel(viewModel);
    }
}
