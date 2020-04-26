package com.example.solutionchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GridLayout gl_timetable;
    private TextView tv_time, tv_schedule, tv_dayOfMon, tv_dayOfTue, tv_dayOfWed, tv_dayOfThu, tv_dayOfFri, tv_toolbar;
    private ImageButton btn_bus, btn_before_week, btn_next_week;
    private Toolbar toolbar;
    private final String TAG = "myTag";
    private ArrayList<Course> myTimeTable;
    private ArrayList<Integer> hasTime_main, hasTime_sub;
    private HashMap<Integer, Integer> indexToTimetable;
    private int myTimetableIndex;

    final FirebaseFirestore db = FirebaseFirestore.getInstance(); //파이어스토어

    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getXmlId();

        myTimeTable = new ArrayList<>();
        hasTime_main = new ArrayList<>();
        hasTime_sub = new ArrayList<>();
        indexToTimetable = new HashMap<>();

         //화면 상단에 actionbar 대신 toolbar 이용;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); //기존 타이틀을 보이지 않게 함

        mCalendar = Calendar.getInstance();
        //Date today = mCalendar.getTime(); //오늘
        updateThisWeek(); //이번주 날짜 설정
        getSchedule();
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                initializeSchedule();
                showSchedule();
            }
        }, 6000); // 3초후

        btn_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent busIntent = new Intent(getApplicationContext(), BusActivity.class);
                startActivity(busIntent);
            }
        });

        btn_before_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLastWeek();
            }
        });

        btn_next_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNextWeek();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(MainActivity.this, myTimeTable.get(v.getId()).getName(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), CourseInfoActivity.class);
        intent.putExtra("code", myTimeTable.get(v.getId()).getCode());
        intent.putExtra("name", myTimeTable.get(v.getId()).getName());
        intent.putExtra("building", myTimeTable.get(v.getId()).getBuilding());
        intent.putExtra("classroom", myTimeTable.get(v.getId()).getClassroom());
        intent.putExtra("prof", myTimeTable.get(v.getId()).getProf());
        intent.putExtra("day", myTimeTable.get(v.getId()).getDay());
        intent.putExtra("start", myTimeTable.get(v.getId()).getStart());
        intent.putExtra("time", myTimeTable.get(v.getId()).getTime());
        startActivity(intent);
    }

    public void addOneSchedule(int row, int column, int size, String info, int id) {
        GridLayout.LayoutParams params;
        if(column == 0) {
            params = new GridLayout.LayoutParams(tv_time.getLayoutParams());
        } else {
            params = new GridLayout.LayoutParams(tv_schedule.getLayoutParams());
        }
        params.rowSpec = GridLayout.spec(row, size, GridLayout.FILL);
        params.columnSpec = GridLayout.spec(column, GridLayout.FILL);
        TextView newSchedule = new TextView(this);
        newSchedule.setLayoutParams(params);
        newSchedule.setText(info);
        newSchedule.setTextColor(Color.BLACK);
        if(column == 0) {
            newSchedule.setGravity(Gravity.CENTER_HORIZONTAL);
            newSchedule.setTextSize(10);
        } else {
            newSchedule.setGravity(Gravity.CENTER_HORIZONTAL);
            if(id != -1) {
                newSchedule.setId(id);
                newSchedule.setOnClickListener(this);
            }
        }
        if (id != -1)
            newSchedule.setBackgroundResource(R.drawable.border_background);
        gl_timetable.addView(newSchedule);
    }

    public void initializeSchedule(){
        for(int i = 0; i < 6; i++) {
            addOneSchedule(0, i, 1, "", -1);
        }
        for(int i = 6; i < gl_timetable.getChildCount(); i++) {
            gl_timetable.removeViewAt(i);
        }
    }

    public void getSchedule() {
        db.collection("User")
                .document("201527516") //사용자 학번으로 바꿔야 함
                .collection("2020.01") //날짜를 입력으로 받아야 함
                .document("Timetable")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Document found in the offline cache
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, "Cached document data: " + document.getData());
                            String allTimetable = document.getData().toString();
                            allTimetable = allTimetable.replace("{", "");
                            allTimetable = allTimetable.replace("}", "");
                            allTimetable = allTimetable.replace(" ", "");
                            allTimetable = allTimetable.replaceAll("=", ".");
                            final String[] arrayTimetable = allTimetable.split(",");
                            myTimetableIndex = 0;
                            for(int i = 0; i < arrayTimetable.length; i++) {
                                db.collection("User")
                                        .document("201527516") //사용자 학번으로 바꿔야 함
                                        .collection("2020.01") //날짜를 입력으로 받아야 함
                                        .document("Timetable")
                                        .collection(arrayTimetable[i])
                                        .document("Info")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    // Document found in the offline cache
                                                    DocumentSnapshot document = task.getResult();
                                                    Log.d(TAG, "Cached document data: " + document.getData());
                                                    ArrayList<Long> longDay = (ArrayList<Long>)document.getData().get("Day");
                                                    ArrayList<Integer> intDay = new ArrayList<>();
                                                    for(int i = 0; i < longDay.size(); i++) {
                                                        intDay.add(Integer.decode((longDay.get(i)).toString()));
                                                    }
                                                    myTimeTable.add(new Course( document.getString("Building"),
                                                            document.getString("Classroom"),
                                                            intDay,
                                                            document.getString("Name"),
                                                            document.getString("Prof"),
                                                            document.getString("Start"),
                                                            (long)document.getData().get("Time"),
                                                            arrayTimetable[myTimetableIndex]));
                                                    addTime((ArrayList<Long>)document.getData().get("Day"), document.getString("Start"), (long)document.getData().get("Time"), myTimetableIndex++);
                                                } else {
                                                Log.d(TAG, "Cached get failed: ", task.getException());
                                            }
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Cached get failed: ", task.getException());
                        }
                    }
                });
    }

    public void showSchedule() {
        for(int row = 0; row < 30; row++) {
            for(int col = 0; col < 6; col++) {
                if (col == 0) {
                    if (row % 2 == 0) {
                        addOneSchedule(row + 1, col, 2, String.valueOf(row / 2 + 9), -1); //시간을 보여주는 칸
                    }
                } else if (hasTime_main.contains(5 * row + col)) {
                    int id = indexToTimetable.get(5 * row + col);
                    double lectureTime = myTimeTable.get(id).getTime();
                    lectureTime = Math.ceil(lectureTime / 30);
                    String lectureName = myTimeTable.get(id).getName();
                    addOneSchedule(row + 1, col, (int)lectureTime, lectureName, id);
                } else if (hasTime_sub.contains(5 * row + col)) {
                    //넘어가기
                } else {
                    addOneSchedule(row + 1, col, 1, "", -1);
                }
            }
        }
    }

    //시간표에서 해당 시간에 수업이 있는지를 확인할 수 있는 인덱스 추가
    public void addTime(ArrayList<Long> day, String startTime, long lectureTime, int myTimetableIndex) {
        int intTime = 0;
        if (day.contains(Long.parseLong("1"))) {
            intTime += 1;
            intTime += changeStartTime(startTime);
            hasTime_main.add(intTime);
            indexToTimetable.put(intTime, myTimetableIndex);
            for(int i = 30; i < lectureTime; i += 30) {
                hasTime_sub.add(intTime + i / 6);
            }
        }
        intTime = 0;
        if (day.contains(Long.parseLong("2"))) {
            intTime += 2;
            intTime += changeStartTime(startTime);
            hasTime_main.add(intTime);
            indexToTimetable.put(intTime, myTimetableIndex);
            for(int i = 30; i < lectureTime; i += 30) {
                hasTime_sub.add(intTime + i / 6);
            }
        }
        intTime = 0;
        if (day.contains(Long.parseLong("3"))) {
            intTime += 3;
            intTime += changeStartTime(startTime);
            hasTime_main.add(intTime);
            indexToTimetable.put(intTime, myTimetableIndex);
            for(int i = 30; i < lectureTime; i += 30) {
                hasTime_sub.add(intTime + i / 6);
            }
        }
        intTime = 0;
        if (day.contains(Long.parseLong("4"))) {
            intTime += 4;
            intTime += changeStartTime(startTime);
            hasTime_main.add(intTime);
            indexToTimetable.put(intTime, myTimetableIndex);
            for(int i = 30; i < lectureTime; i += 30) {
                hasTime_sub.add(intTime + i / 6);
            }
        }
        intTime = 0;
        if (day.contains(Long.parseLong("5"))) {
            intTime += 5;
            intTime += changeStartTime(startTime);
            hasTime_main.add(intTime);
            indexToTimetable.put(intTime, myTimetableIndex);
            for(int i = 30; i < lectureTime; i += 30) {
                hasTime_sub.add(intTime + i / 6);
            }
        }
    }

    public int changeStartTime(String startTime) {
        SimpleDateFormat transFormat = new SimpleDateFormat("HH:mm");
        try {
            Date startTimeDate = transFormat.parse(startTime);
            if (startTimeDate.after(transFormat.parse("08:59")) && startTimeDate.before(transFormat.parse("09:30"))) {
                return 0;
            } else if (startTimeDate.after(transFormat.parse("09:29")) && startTimeDate.before(transFormat.parse("10:00"))) {
                return 5;
            } else if (startTimeDate.after(transFormat.parse("09:59")) && startTimeDate.before(transFormat.parse("10:30"))) {
                return 10;
            } else if (startTimeDate.after(transFormat.parse("10:29")) && startTimeDate.before(transFormat.parse("11:00"))) {
                return 15;
            } else if (startTimeDate.after(transFormat.parse("10:59")) && startTimeDate.before(transFormat.parse("11:30"))) {
                return 20;
            } else if (startTimeDate.after(transFormat.parse("11:29")) && startTimeDate.before(transFormat.parse("12:00"))) {
                return 25;
            } else if (startTimeDate.after(transFormat.parse("11:59")) && startTimeDate.before(transFormat.parse("12:30"))) {
                return 30;
            } else if (startTimeDate.after(transFormat.parse("12:29")) && startTimeDate.before(transFormat.parse("13:00"))) {
                return 35;
            } else if (startTimeDate.after(transFormat.parse("12:59")) && startTimeDate.before(transFormat.parse("13:30"))) {
                return 40;
            } else if (startTimeDate.after(transFormat.parse("13:29")) && startTimeDate.before(transFormat.parse("14:00"))) {
                return 45;
            } else if (startTimeDate.after(transFormat.parse("13:59")) && startTimeDate.before(transFormat.parse("14:30"))) {
                return 50;
            } else if (startTimeDate.after(transFormat.parse("14:29")) && startTimeDate.before(transFormat.parse("15:00"))) {
                return 55;
            } else if (startTimeDate.after(transFormat.parse("14:59")) && startTimeDate.before(transFormat.parse("15:30"))) {
                return 60;
            } else if (startTimeDate.after(transFormat.parse("15:29")) && startTimeDate.before(transFormat.parse("16:00"))) {
                return 65;
            } else if (startTimeDate.after(transFormat.parse("15:59")) && startTimeDate.before(transFormat.parse("16:30"))) {
                return 70;
            } else if (startTimeDate.after(transFormat.parse("16:29")) && startTimeDate.before(transFormat.parse("17:00"))) {
                return 75;
            } else if (startTimeDate.after(transFormat.parse("16:59")) && startTimeDate.before(transFormat.parse("17:30"))) {
                return 80;
            } else if (startTimeDate.after(transFormat.parse("17:29")) && startTimeDate.before(transFormat.parse("18:00"))) {
                return 85;
            } else if (startTimeDate.after(transFormat.parse("17:59")) && startTimeDate.before(transFormat.parse("18:30"))) {
                return 90;
            } else if (startTimeDate.after(transFormat.parse("18:29")) && startTimeDate.before(transFormat.parse("19:00"))) {
                return 95;
            } else if (startTimeDate.after(transFormat.parse("18:59")) && startTimeDate.before(transFormat.parse("19:30"))) {
                return 100;
            } else if (startTimeDate.after(transFormat.parse("19:29")) && startTimeDate.before(transFormat.parse("20:00"))) {
                return 105;
            } else if (startTimeDate.after(transFormat.parse("19:59")) && startTimeDate.before(transFormat.parse("20:30"))) {
                return 110;
            } else if (startTimeDate.after(transFormat.parse("20:29")) && startTimeDate.before(transFormat.parse("21:00"))) {
                return 115;
            } else if (startTimeDate.after(transFormat.parse("20:59")) && startTimeDate.before(transFormat.parse("21:30"))) {
                return 120;
            } else if (startTimeDate.after(transFormat.parse("21:29")) && startTimeDate.before(transFormat.parse("22:00"))) {
                return 125;
            } else if (startTimeDate.after(transFormat.parse("21:59")) && startTimeDate.before(transFormat.parse("22:30"))) {
                return 130;
            } else if (startTimeDate.after(transFormat.parse("22:29")) && startTimeDate.before(transFormat.parse("23:00"))) {
                return 135;
            } else if (startTimeDate.after(transFormat.parse("22:59")) && startTimeDate.before(transFormat.parse("23:30"))) {
                return 140;
            } else if (startTimeDate.after(transFormat.parse("23:29")) && startTimeDate.before(transFormat.parse("24:00"))) {
                return 145;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 150;
    }

    //이번주 날짜를 받아옴
    public void updateThisWeek() {
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY); //월요일
        Date dayOfWeek = mCalendar.getTime();
        tv_dayOfMon.setText(new SimpleDateFormat("dd").format(dayOfWeek));
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.TUESDAY);
        dayOfWeek = mCalendar.getTime();
        tv_dayOfTue.setText(new SimpleDateFormat("dd").format(dayOfWeek));
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY);
        dayOfWeek = mCalendar.getTime();
        tv_dayOfWed.setText(new SimpleDateFormat("dd").format(dayOfWeek));
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY);
        dayOfWeek = mCalendar.getTime();
        tv_dayOfThu.setText(new SimpleDateFormat("dd").format(dayOfWeek));
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
        dayOfWeek = mCalendar.getTime();
        tv_dayOfFri.setText(new SimpleDateFormat("dd").format(dayOfWeek));
    }

    //저번주 날짜를 받아옴
    public void updateLastWeek() {
        mCalendar.add(Calendar.DAY_OF_WEEK, -7); //7일 전
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        Date dayOfWeek = mCalendar.getTime();
        tv_dayOfMon.setText(new SimpleDateFormat("dd").format(dayOfWeek));
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.TUESDAY);
        dayOfWeek = mCalendar.getTime();
        tv_dayOfTue.setText(new SimpleDateFormat("dd").format(dayOfWeek));
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY);
        dayOfWeek = mCalendar.getTime();
        tv_dayOfWed.setText(new SimpleDateFormat("dd").format(dayOfWeek));
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY);
        dayOfWeek = mCalendar.getTime();
        tv_dayOfThu.setText(new SimpleDateFormat("dd").format(dayOfWeek));
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
        dayOfWeek = mCalendar.getTime();
        tv_dayOfFri.setText(new SimpleDateFormat("dd").format(dayOfWeek));
    }

    //다음주 날짜를 받아옴
    public void updateNextWeek() {
        mCalendar.add(Calendar.DAY_OF_WEEK, 7); //7일 후
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        Date dayOfWeek = mCalendar.getTime();
        tv_dayOfMon.setText(new SimpleDateFormat("dd").format(dayOfWeek));
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.TUESDAY);
        dayOfWeek = mCalendar.getTime();
        tv_dayOfTue.setText(new SimpleDateFormat("dd").format(dayOfWeek));
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY);
        dayOfWeek = mCalendar.getTime();
        tv_dayOfWed.setText(new SimpleDateFormat("dd").format(dayOfWeek));
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY);
        dayOfWeek = mCalendar.getTime();
        tv_dayOfThu.setText(new SimpleDateFormat("dd").format(dayOfWeek));
        mCalendar.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
        dayOfWeek = mCalendar.getTime();
        tv_dayOfFri.setText(new SimpleDateFormat("dd").format(dayOfWeek));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //메뉴를 얻어옴 (오른쪽 상단에 있는 메뉴 버튼)
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu1: { //과목 추가를 눌렀을 때

                break;
            }
            case R.id.action_menu2: { //학기 변경을 눌렀을 때

                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getXmlId() {
        gl_timetable = findViewById(R.id.gl_timetable);
        tv_time = findViewById(R.id.tv_time);
        tv_schedule = findViewById(R.id.tv_schedule);
        btn_bus = findViewById(R.id.btn_bus);
        btn_before_week = findViewById(R.id.btn_before_week);
        btn_next_week = findViewById(R.id.btn_next_week);
        toolbar = findViewById(R.id.toolbar);
        tv_dayOfMon = findViewById(R.id.dayOfMon);
        tv_dayOfTue = findViewById(R.id.dayOfTue);
        tv_dayOfWed = findViewById(R.id.dayOfWed);
        tv_dayOfThu = findViewById(R.id.dayOfThu);
        tv_dayOfFri = findViewById(R.id.dayOfFri);
        tv_toolbar = findViewById(R.id.tv_toolbar);
    }
}
