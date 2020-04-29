package com.example.solutionchallenge;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.MoreObjects;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private String code;
    private TextView tv_courseName, tv_courseBuilding, tv_courseProf, tv_courseTime, tv_uniqueMemo, tv_normalMemo, tv_courseMemo;
    private final String TAG = "myTag";
    private String semester, date;

    final FirebaseFirestore db = FirebaseFirestore.getInstance(); //파이어스토어

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        getXmlId();

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        semester = intent.getStringExtra("semester");
        date = intent.getStringExtra("date");
        setInfo(intent);
        getData(code, semester);

        tv_courseMemo.setOnClickListener(this);
        tv_normalMemo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_courseMemo : {
                    final EditText edittext = new EditText(CourseInfoActivity.this);
                    edittext.setText(tv_courseMemo.getText());
                    AlertDialog.Builder builder = new AlertDialog.Builder(CourseInfoActivity.this);
                    builder.setTitle("과목 메모");
                    builder.setView(edittext);
                    builder.setPositiveButton("입력",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    tv_courseMemo.setText(edittext.getText().toString());
                                    Map<String, String> map = new HashMap<>();
                                    map.put("Memo", edittext.getText().toString());
                                    Log.d(TAG, "code: " + code);
                                    db.collection("User")
                                            .document("201527516")
                                            .collection(semester)
                                            .document("Timetable")
                                            .collection(code)
                                            .document("Course")
                                            .update("Memo", edittext.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error updating document", e);
                                                }
                                            });
                                }
                            });
                    builder.setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.show();
            }
                break;

            case R.id.tv_normalMemo : {
                final EditText edittext = new EditText(CourseInfoActivity.this);
                edittext.setText(tv_normalMemo.getText());
                AlertDialog.Builder builder = new AlertDialog.Builder(CourseInfoActivity.this);
                builder.setTitle("메모");
                builder.setView(edittext);
                builder.setPositiveButton("입력",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                tv_normalMemo.setText(edittext.getText().toString());
                                Map<String, String> map = new HashMap<>();
                                map.put("Memo", edittext.getText().toString());
                                Log.d(TAG, "code: " + code);
                                db.collection("User")
                                        .document("201527516")
                                        .collection(semester)
                                        .document("Timetable")
                                        .collection(code)
                                        .document("Daily")
                                        .collection("Date")
                                        .document(date)
                                        .update("Memo", edittext.getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error updating document", e);
                                            }
                                        });
                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }
                break;

            default:
                break;
        }
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

    //메모 불러옴
    public void getData(String code, String semester) {
        //강의 메모 불러옴
        db.collection("User")
                .document("201527516")
                .collection(semester)
                .document("Timetable")
                .collection(code)
                .document("Course")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Document found in the offline cache
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, "Cached document data: " + document.getData());
                            tv_courseMemo.setText(document.getString("Memo"));
                        } else {
                            Log.d(TAG, "Cached get failed: ", task.getException());
                        }
                    }
                });

        //해당 날짜 메모 불러옴
        db.collection("User")
                .document("201527516")
                .collection(semester)
                .document("Timetable")
                .collection(code)
                .document("Daily")
                .collection("Date")
                .document(date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Document found in the offline cache
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, "Cached document data: " + document.getData());
                            tv_normalMemo.setText(document.getString("Memo"));
                        } else {
                            Log.d(TAG, "Cached get failed: ", task.getException());
                        }
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
        tv_courseMemo = findViewById(R.id.tv_courseMemo);
    }
}
