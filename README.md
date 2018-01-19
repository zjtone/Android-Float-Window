# Android-Float-Window
### 前言
最近在一个项目中，需要制作录屏的功能，原先是在应用中有录屏/控制的按钮，思考之下觉得这种效果并不好，因此就想制作一个可以悬浮的悬浮窗，这样不论手机在什么界面中都可以对录屏功能进行控制。

这里就来构建一个桌面的悬浮窗，使用了DataBinding的MVVM模式，这些方面就不再多提。

### FloatNormalView
这个是一个普通的悬浮窗，悬浮窗只有一个按钮，点击按钮显示更多的按钮。
首先是页面布局：
```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">
    <data>
        <variable
            name="viewModel"
            type="com.example.zjt.floatrecorder.FloatNormalViewModel"/>
    </data>

    <LinearLayout
        android:layout_width="50dp"
        android:layout_height="50dp"
        android:gravity="center">

        <RelativeLayout
            android:id="@+id/root"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="vertical">
            <!-- 图标，点击后弹出后面的按钮 -->
            <ImageView
                android:id="@+id/float_id"
                android:layout_width="40dp"
                android:layout_height="40dp"
                android:background="@drawable/ic_launcher_background"
                android:onClick="@{viewModel::onControlClick}"/>
        </RelativeLayout>
    </LinearLayout>
</layout>
```

下面一步步的介绍这个悬浮窗的创建。

#### 1 悬浮窗的显示

```
// 创建WindowManager对象
private WindowManager windowManager;
windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

// 创建悬浮窗的LayoutParams
    private void initLayoutParams() {
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
            lp = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                lp.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            lp.gravity = Gravity.START | Gravity.TOP;
            lp.x = screenWidth - view.getLayoutParams().width * 2;
            lp.y = 0;
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.format = PixelFormat.TRANSPARENT;
        } catch (Exception e) {
        }
    }
```
上面分别创建了控制悬浮窗显示的WindowManager和控制悬浮窗布局的LayoutParams
然后使用如下代码就可展示悬浮窗了：
```
    public void show() {
        if (!isShowing) {
            isShowing = true;
            windowManager.addView(this, lp);
        }
    }
```
想要移除悬浮窗也很简单，如下代码：
```
    public void dismiss() {
        if (isShowing) {
            isShowing = false;
            windowManager.removeView(this);
        }
    }
```

#### 2 触摸事件
触摸事件可以使得悬浮窗跟随手指进行移动
```
// 界面
FloatLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.float_layout,this,false);
FloatNormalViewModel floatNormalViewModel = new FloatNormalViewModel(context,layoutBinding,onClickCallback);
layoutBinding.setViewModel(floatNormalViewModel);
addView(layoutBinding.getRoot());
view = layoutBinding.root;
isShowControlView = layoutBinding.floatId;//这就是控制按钮

// 控制的变量
private float downX, downY;
private float moveX, moveY;

// 触摸事件
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
private void updateViewPosition() {
     lp.x += (int) (moveX);
     lp.y += (int) (moveY);
     windowManager.updateViewLayout(this, lp);
}
```

#### 3 点击事件
点击事件是实现了一个回调函数，因为点击事件的逻辑不应该在此处完成，应当交给主布局进行控制，所以定义了一个点击接口。
这里事件的处理顺序是：点击了按钮后，按钮将点击事件通过回调函数来处理，而回调函数是由创建这个View的Activity或者Fragment、Service等提供的，就将事件处理交到了外部。
```
// 点击的接口
public interface OnClickCallback {
    public void onClick(View view);
}
// 控制按钮点击事件
public void onControlClick(View view){
    if(onClickCallback != null)
       onClickCallback.onClick(view);
}
```

### 多功能悬浮窗

多功能悬浮窗与上面类似，只不过在点击事件上较多而已。
而如何完成两个悬浮窗的切换呢，就可以利用之前所使用的OnClickCallback回调接口了，将一个显示、另一个隐藏即可，且两个悬浮窗若采用同一个LayoutParams就可以让两个显示在同一个位置。
```
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
```
