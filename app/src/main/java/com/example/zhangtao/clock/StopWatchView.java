package com.example.zhangtao.clock;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangtao on 2016/4/21.
 */
public class StopWatchView extends LinearLayout {

    public StopWatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvHour = (TextView) findViewById(R.id.timeHour);
        tvMinute = (TextView) findViewById(R.id.timeMinute);
        tvSecond = (TextView) findViewById(R.id.timeSecond);
        tvMSecond = (TextView) findViewById(R.id.timeMSecond);

        tvHour.setText("0");
        tvMinute.setText("0");
        tvSecond.setText("0");
        tvMSecond.setText("0");

        btnStart = (Button) findViewById(R.id.btnSWStart);
        btnPause = (Button) findViewById(R.id.btnSWPause);
        btnResume = (Button) findViewById(R.id.btnSWResume);
        btnLap = (Button) findViewById(R.id.btnSWLap);
        btnReset = (Button) findViewById(R.id.btnSWReset);

        //开始按钮
        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                btnStart.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
                btnLap.setVisibility(View.VISIBLE);
            }
        });
        //暂停按钮
        btnPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                btnPause.setVisibility(View.GONE);
                btnResume.setVisibility(View.VISIBLE);
                btnLap.setVisibility(View.GONE);
                btnReset.setVisibility(View.VISIBLE);
            }
        });
        //继续按钮
        btnResume.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                btnResume.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.GONE);
                btnLap.setVisibility(View.VISIBLE);
            }
        });
        //重置按钮
        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                tenMSec=0;
                adapter.clear();  //重置时去掉列表
                btnPause.setVisibility(View.GONE);
                btnResume.setVisibility(View.GONE);
                btnLap.setVisibility(View.GONE);
                btnReset.setVisibility(View.GONE);
                btnStart.setVisibility(View.VISIBLE);
            }
        });
        //计时按钮
        btnLap.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //计时
                adapter.insert(String.format("%d:%d:%d.%d",tenMSec/100/60/60,tenMSec/100/60%60,tenMSec/100%60,tenMSec%100),0);
            }
        });

        btnPause.setVisibility(View.GONE);
        btnResume.setVisibility(View.GONE);
        btnLap.setVisibility(View.GONE);
        btnReset.setVisibility(View.GONE);

        lvTimeList = (ListView) findViewById(R.id.lvWatchTime);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        lvTimeList.setAdapter(adapter);

        showTimerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MSG_WHAT_SHOW_TIME);
            }
        };
        timer.schedule(showTimerTask, 200, 200);

    }

    private void startTimer() {
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    tenMSec++;
                }
            };
            timer.schedule(timerTask, 10, 10);
        }
    }

    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask=null;
        }
    }

    private int tenMSec = 0;
    private Timer timer = new Timer();
    private TimerTask timerTask = null;
    private TimerTask showTimerTask = null;

    private TextView tvHour, tvMinute, tvSecond, tvMSecond;
    private Button btnStart, btnPause, btnResume, btnReset, btnLap;
    private ListView lvTimeList;
    private ArrayAdapter<String> adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_WHAT_SHOW_TIME:
                    tvHour.setText(tenMSec / 100 / 60 / 60 + "");
                    tvMinute.setText((tenMSec / 100 / 60) % 60 + "");
                    tvSecond.setText(tenMSec / 100 % 60 + "");
                    tvMSecond.setText(tenMSec % 100 + "");
                    break;
                default:
                    break;
            }
        }
    };
    private static final int MSG_WHAT_SHOW_TIME = 1;

    public void onDestory() {
        timer.cancel();
    }
}
