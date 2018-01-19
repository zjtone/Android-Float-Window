package com.example.zjt.floatrecorder;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.zjt.floatrecorder.databinding.PopupControlViewBinding;

/**
 * Created by zjt on 18-1-19.
 */

public class FloatControlView  extends LinearLayout {
    private Context context = null;
    private ImageView isShowControlView = null;
    private WindowManager.LayoutParams lp;
    private WindowManager windowManager;
    // 移动
    private float downX, downY;
    private float moveX, moveY;
    // 控制
    private boolean isShowing = false;

    private FloatControlViewModel floatControlViewModel;

    public FloatControlView(Context context){
        this(context,null,null);
    }

    public FloatControlView(Context context,
                            OnClickCallback onClickCallback,
                            FloatControlViewModel.OnVisibleChangeListener onVisibleChangeListener) {
        super(context);
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        PopupControlViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.popup_control_view, this, false);
        floatControlViewModel = new FloatControlViewModel(context,binding,onClickCallback,onVisibleChangeListener);
        binding.setControlViewModel(floatControlViewModel);
        addView(binding.getRoot());
        isShowControlView = binding.floatId;
        initLayoutParams();
        initEvent();
    }

    private void initLayoutParams() {
        try {
            lp = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                lp.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            lp.gravity = Gravity.START | Gravity.TOP;
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.format = PixelFormat.TRANSPARENT;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        isShowControlView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = motionEvent.getRawX();
                        downY = motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveX = motionEvent.getRawX() - downX;
                        moveY = motionEvent.getRawY() - downY;
                        downX += moveX;
                        downY += moveY;
                        updateViewPosition();
                        break;
                }
                return false;
            }
        });
    }

    private void updateViewPosition() {
        lp.x += (int) (moveX);
        lp.y += (int) (moveY);
        windowManager.updateViewLayout(this, lp);
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return this.lp;
    }

    public void setLayoutParams(WindowManager.LayoutParams lp) {
        this.lp = lp;
        if (isShowing)
            windowManager.updateViewLayout(this, lp);
    }

    public void show() {
        if (!isShowing) {
            isShowing = true;
            windowManager.addView(this, lp);
        }
    }

    public void dismiss() {
        if (isShowing) {
            isShowing = false;
            windowManager.removeView(this);
        }
    }

    public FloatControlViewModel getViewModel() {
        return floatControlViewModel;
    }
}