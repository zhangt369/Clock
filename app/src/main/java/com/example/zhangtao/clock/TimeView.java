package com.example.zhangtao.clock;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by zhangtao on 2016/4/20.
 */
public class TimeView extends LinearLayout {

    private TextView tvTime;

    public TimeView(Context context) {
        super(context);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //在初始化完成之后
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvTime.setText("Hello");
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            timeHandler.sendEmptyMessage(0);  //让Handler执行
        } else {
            timeHandler.removeMessages(0);  //不可见时，移除所有的消息
        }
    }

    private void refreshTime() {
        Calendar c = Calendar.getInstance();
        tvTime.setText(String.format("%d:%d:%d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND)));

    }

    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            refreshTime();
            if (getVisibility() == View.VISIBLE) {
                timeHandler.sendEmptyMessageDelayed(0, 1000);  //延迟1000ms发送消息,也就是每隔1秒执行一次
            }
        }
    };
}
