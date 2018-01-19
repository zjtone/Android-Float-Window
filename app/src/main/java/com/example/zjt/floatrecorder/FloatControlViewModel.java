package com.example.zjt.floatrecorder;

import android.content.Context;
import android.databinding.BaseObservable;
import android.view.View;

import com.example.zjt.floatrecorder.databinding.PopupControlViewBinding;

/**
 * Created by zjt on 18-1-19.
 */

public class FloatControlViewModel extends BaseObservable {
    private Context context;
    private PopupControlViewBinding binding;
    private OnClickCallback onClickCallback;
    private OnVisibleChangeListener onVisibleChangeListener;

    public FloatControlViewModel(Context context, PopupControlViewBinding binding,
                                 OnClickCallback onClickCallback,
                                 OnVisibleChangeListener onVisibleChangeListener) {
        this.context = context;
        this.binding = binding;
        this.onClickCallback = onClickCallback;
        this.onVisibleChangeListener = onVisibleChangeListener;
    }

    public void onVisibleChange(View view){
        if(onClickCallback != null)
            onClickCallback.onClick(view);
    }

    public void onPlay(View view) {

    }

    public void onPause(View view) {

    }

    public static interface OnVisibleChangeListener{
        public void onChange(boolean isVisible);
    }
}