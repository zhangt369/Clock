package com.example.zhangtao.clock;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangtao on 2016/4/21.
 */
public class TimerView extends LinearLayout {
    public TimerView(Context context) {
        super(context);
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        btnStart = (Button) findViewById(R.id.btn_start);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnResume = (Button) findViewById(R.id.btn_resume);
        btnReset = (Button) findViewById(R.id.btn_reset);

        //开始
        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                btnStart.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
            }
        });
        //暂停
        btnPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                btnPause.setVisibility(View.GONE);
                btnResume.setVisibility(View.VISIBLE);
            }
        });
        //再次开始
        btnResume.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                btnResume.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
            }
        });
        //重置
        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                etHour.setText("0");
                etMinute.setText("0");
                etSecond.setText("0");
                btnPause.setVisibility(View.GONE);
                btnResume.setVisibility(View.GONE);
                btnReset.setVisibility(View.GONE);
                btnStart.setVisibility(View.VISIBLE);
            }
        });


        etHour = (EditText) findViewById(R.id.etHour);
        etMinute = (EditText) findViewById(R.id.etMinute);
        etSecond = (EditText) findViewById(R.id.etSecond);

        etHour.setText("00");
        etHour.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {

                    int value = Integer.parseInt(s.toString());
                    if (value > 23) {
                        etHour.setText("23");
                    } else if (value < 0) {
                        etHour.setText(0);
                    }
                }
                checkToEnableBtnStart();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etMinute.setText("00");
        etMinute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(s)) {
                    int value = Integer.parseInt(s.toString());
                    if (value > 59) {
                        etMinute.setText("59");
                    } else if (value < 0) {
                        etMinute.setText(0);
                    }
                }
                checkToEnableBtnStart();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etSecond.setText("00");
        etSecond.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    int value = Integer.parseInt(s.toString());
                    if (value > 59) {
                        etSecond.setText("59");
                    } else if (value < 0) {
                        etSecond.setText(0);
                    }
                }
                checkToEnableBtnStart();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnStart.setVisibility(View.VISIBLE);
        btnStart.setEnabled(false);
        btnPause.setVisibility(View.GONE);
        btnResume.setVisibility(View.GONE);
        btnReset.setVisibility(View.GONE);
    }

    private void checkToEnableBtnStart() {
        btnStart.setEnabled(!TextUtils.isEmpty(etHour.getText()) && Integer.parseInt(etHour.getText().toString()) > 0
                || (!TextUtils.isEmpty(etMinute.getText()) && Integer.parseInt(etMinute.getText().toString()) > 0)
                || (!TextUtils.isEmpty(etSecond.getText()) && Integer.parseInt(etSecond.getText().toString()) > 0));
    }

    private void startTimer() {
        if (timerTask == null) {
            allTimerCount = Integer.parseInt(etHour.getText().toString()) * 60 * 60 +
                    Integer.parseInt(etMinute.getText().toString()) * 60 + Integer.parseInt(etSecond.getText().toString());
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    allTimerCount--;
                    handler.sendEmptyMessage(MSG_WHAT_TIME_TICK);
                    if (allTimerCount <= 0) {
                        handler.sendEmptyMessage(MSG_WHAT_TIME_IS_UP);
                        stopTimer();
                    }
                }
            };
            timer.schedule(timerTask, 1000, 1000);  //之后run方法每隔一秒就可以执行一次了

        }

    }

    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_TIME_IS_UP:
                    new AlertDialog.Builder(getContext()).setTitle("Time is up!").
                            setMessage("Time is up").setNegativeButton("cancel", null).show();

                    break;
                case MSG_WHAT_TIME_TICK:
                    int hour = allTimerCount / 60 / 60;
                    int minute = (allTimerCount / 60) % 60;
                    int second = allTimerCount % 60;
                    etHour.setText(hour + "");
                    etMinute.setText(minute + "");
                    etSecond.setText(second + "");
                    break;
            }
        }
    };

    private static final int MSG_WHAT_TIME_IS_UP = 1;
    private static final int MSG_WHAT_TIME_TICK = 2;  //每隔1秒动态的呈现在文本框里面
    private int allTimerCount = 0;
    private Timer timer = new Timer();
    private TimerTask timerTask = null;
    private Button btnStart, btnPause, btnResume, btnReset;
    private EditText etHour, etMinute, etSecond;
}
