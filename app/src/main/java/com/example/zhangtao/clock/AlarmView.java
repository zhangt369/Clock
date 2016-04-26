package com.example.zhangtao.clock;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhangtao on 2016/4/20.
 */
public class AlarmView extends LinearLayout {
    public AlarmView(Context context) {
        super(context);
        init();
    }

    public AlarmView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AlarmView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        btnAddAlram = (Button) findViewById(R.id.btn_add_alarm);
        lvAlarmList = (ListView) findViewById(R.id.lvAlarmList);
        adapter = new ArrayAdapter<AlarmData>(getContext(), android.R.layout.simple_list_item_1);
        lvAlarmList.setAdapter(adapter);
//        adapter.add(new AlarmData(System.currentTimeMillis()));
        readSaveAlarmList();
        btnAddAlram.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlarm();
            }
        });
        lvAlarmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //弹出一个对话框
                new AlertDialog.Builder(getContext()).setTitle("操作选项").setItems(new
                        CharSequence[]{"删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                deleteAlarm(position);
                                break;
                        }
                    }
                }).setNegativeButton("取消", null).show();
                return true;  //成功执行
            }
        });
    }

    private void deleteAlarm(int position) {
        AlarmData ad=adapter.getItem(position);
        adapter.remove(ad);
        saveAlarmList();
        alarmManager.cancel(PendingIntent.getBroadcast(getContext(),ad.getId(),new Intent(getContext(),AlarmReceiver.class),0));
    }

    private void addAlarm() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);

                Calendar currentTime = Calendar.getInstance();
                if (calendar.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                    calendar.setTimeInMillis(calendar.getTimeInMillis() + 20 * 60 * 60 * 1000);
                }
                AlarmData ad = new AlarmData(calendar.getTimeInMillis());
                adapter.add(ad);
                alarmManager.setRepeating(alarmManager.RTC_WAKEUP,
                        ad.getTime(),
                        5*60*1000,
                        PendingIntent.getBroadcast(getContext(),ad.getId(),new Intent(getContext(),AlarmReceiver.class),0));   //PendingIntent是一个挂起的intent
                saveAlarmList();
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();

    }

    private void saveAlarmList() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(AlarmView.class
                .getName(), Context.MODE_PRIVATE).edit();

        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < adapter.getCount(); i++) {
            stringBuffer.append(adapter.getItem(i).getTime()).append(",");
        }

        if (stringBuffer.length()>1){
        String content = stringBuffer.toString().substring(0, stringBuffer.length() - 1);
        editor.putString(KEY_ALARM_LIST, content);
        System.out.println("content = " + content);
        }else {
            editor.putString(KEY_ALARM_LIST,null);
        }

        editor.commit();
    }

    private void readSaveAlarmList() {
        SharedPreferences sp = getContext().getSharedPreferences(AlarmView.class.getName(), Context.MODE_PRIVATE);
        String content = sp.getString(KEY_ALARM_LIST, null);
        if (content != null) {
            String[] timeStrings = content.split(",");
            for (String string : timeStrings) {
                adapter.add(new AlarmData(Long.parseLong(string)));   //读取已经保存号的数据
            }

        }
    }

    private ListView lvAlarmList;
    private Button btnAddAlram;
    private ArrayAdapter<AlarmData> adapter;
    private static final String KEY_ALARM_LIST = "alarmlist";
    private AlarmManager alarmManager;

    private static class AlarmData {
        private long time = 0;
        private String timelable = "";
        private Calendar date;

        public AlarmData(long time) {   //time---->闹钟响起的时间
            this.time = time;
            date = Calendar.getInstance();
            date.setTimeInMillis(time);
            timelable = String.format("%d月%d日 %d:%d"
                    , date.get(Calendar.MONTH) + 1
                    , date.get(Calendar.DATE)
                    , date.get(Calendar.HOUR_OF_DAY)
                    , date.get(Calendar.MINUTE));
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getTimelable() {
            return timelable;
        }
        public int getId(){
            return (int) (getTime()/1000/60);
        }

        @Override
        public String toString() {
            return getTimelable();
        }
    }
}
