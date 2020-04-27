package com.example.solutionchallenge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CourseInfoActivity extends AppCompatActivity {

    private String code;
    private TextView tv_courseName, tv_courseBuilding, tv_courseProf, tv_courseTime, tv_uniqueMemo, tv_normalMemo;
    private ListView lv_courseMemo;
    private List<String> data;
    private final String TAG = "myTag";

    final FirebaseFirestore db = FirebaseFirestore.getInstance(); //파이어스토어

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        getXmlId();

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        String semester = intent.getStringExtra("semester");
        setInfo(intent);
        getData(code, semester);

        //과목 메모를 위한 list
        data = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        lv_courseMemo.setAdapter(adapter);
        db.collection("User")
                .document("201527516") //사용자 학번으로 바꿔야 함
                .collection(semester)
                .document("Timetable")
                .collection(code)
                .document("Course")
                .collection("Index")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    }
                });

        data.add("리나");
        adapter.notifyDataSetChanged(); // 저장
    }

    //강의의 기본 정보 세팅
    public void setInfo(Intent intent) {
        tv_courseName.setText("< " + intent.getStringExtra("name") + " >");
        String place = intent.getStringExtra("building") + " - " + intent.getStringExtra("classroom");
        tv_courseBuilding.setText(place);
        tv_courseProf.setText(intent.getStringExtra("prof"));
        String time = "";
        ArrayList<Integer> day = intent.getIntegerArrayListExtra("day");
        for(int i = 0; i < day.size(); i++) {
            if(day.get(i) == 1) {
                time += "월";
            } else if (day.get(i) == 2) {
                time += "화";
            } else if (day.get(i) == 3) {
                time += "수";
            } else if (day.get(i) == 4) {
                time += "목";
            } else if (day.get(i) == 5) {
                time += "금";
            }
            if(i + 1 != day.size()) {
                time += ", ";
            }
        }
        time = time + " " + intent.getStringExtra("start") + " ~ ";
        SimpleDateFormat transFormat = new SimpleDateFormat("HH:mm");
        try {
            Date startTime = transFormat.parse(intent.getStringExtra("start"));
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTime(startTime);
            mCalendar.add(Calendar.MINUTE, (int)intent.getLongExtra("time", 1));
            Date endTime =  mCalendar.getTime();
            time +=  transFormat.format(endTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_courseTime.setText(time);
    }

    public void getData(String code, String semester) {
        db.collection("User")
                .document("201527516") //사용자 학번으로 바꿔야 함
                .collection(semester) //날짜를 입력으로 받아야 함
                .document("Timetable")
                .collection(code)
                .document("Course")
                .collection("Index")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    }
                });
    }

    public void getXmlId() {
        tv_courseName = findViewById(R.id.tv_courseName);
        tv_courseBuilding = findViewById(R.id.tv_courseBuilding);
        tv_courseProf = findViewById(R.id.tv_courseProf);
        tv_courseTime = findViewById(R.id.tv_courseTime);
        tv_uniqueMemo = findViewById(R.id.tv_uniqueMemo);
        tv_normalMemo = findViewById(R.id.tv_normalMemo);
        lv_courseMemo = findViewById(R.id.lv_courseMemo);
    }
}
