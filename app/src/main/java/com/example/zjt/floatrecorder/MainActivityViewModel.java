package com.example.zjt.floatrecorder;

import android.content.Context;
import android.databinding.BaseObservable;
import android.view.View;

import com.example.zjt.floatrecorder.databinding.ActivityMainBinding;

/**
 * Created by zjt on 18-1-19.
 */

public class MainActivityViewModel extends BaseObservable {
    private Context context;
    private ActivityMainBinding mainBinding;
    private FloatNormalView floatNormalView;
    private FloatControlView floatControlView;
    private boolean isControlVisible = false;

    public MainActivityViewModel(Context context, ActivityMainBinding mainBinding) {
        this.context = context;
        this.mainBinding = mainBinding;
        init();
    }

    private void init() {
        floatNormalView = new FloatNormalView(context, new OnClickCallback() {
            @Override
            public void onClick(View view) {
                floatControlView.setLayoutParams(floatNormalView.getLayoutParams());
                floatControlView.show();
                floatNormalView.dismiss();
            }
        });
        floatControlView = new FloatControlView(context, new OnClickCallback() {
            @Override
            public void onClick(View view) {
                floatNormalView.setLayoutParams(floatControlView.getLayoutParams());
                floatNormalView.show();
                floatControlView.dismiss();
            }
        }, new FloatControlViewModel.OnVisibleChangeListener() {
            @Override
            public void onChange(boolean isVisible) {
                if (isControlVisible) {
                    floatControlView.show();
                    floatNormalView.dismiss();
                } else {
                    floatControlView.dismiss();
                    floatNormalView.show();
                }
            }
        });
        floatNormalView.show();
    }
}
