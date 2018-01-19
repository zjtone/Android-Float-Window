package com.example.zjt.floatrecorder;

import android.content.Context;
import android.databinding.BaseObservable;
import android.view.View;

import com.example.zjt.floatrecorder.databinding.FloatLayoutBinding;

/**
 * Created by zjt on 18-1-19.
 */

public class FloatNormalViewModel extends BaseObservable {
    private Context context;
    private FloatLayoutBinding layoutBinding;
    private OnClickCallback onClickCallback;

    public FloatNormalViewModel(Context context, FloatLayoutBinding layoutBinding, OnClickCallback onClickCallback) {
        this.context = context;
        this.layoutBinding = layoutBinding;
        this.onClickCallback = onClickCallback;
    }

    public void onControlClick(View view){
        if(onClickCallback != null)
            onClickCallback.onClick(view);
    }
}
