package com.example.solutionchallenge;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.firebase.firestore.FieldValue;
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

    private String code, semester, date;
    private TextView tv_courseName, tv_courseBuilding, tv_courseProf, tv_courseTime, tv_uniqueMemo, tv_normalMemo, tv_courseMemo;
    private final String TAG = "myTag";
    private Button btn_delete;
    final FirebaseFirestore db = FirebaseFirestore.getInstance(); //파이어스토어
    public static Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        getXmlId();
        activity = CourseInfoActivity.this;

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        semester = intent.getStringExtra("semester");
        date = intent.getStringExtra("date");
        setInfo(intent);
        getData(code, semester);

        tv_courseMemo.setOnClickListener(this);
        tv_uniqueMemo.setOnClickListener(this);
        tv_normalMemo.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
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
                                    Log.d(TAG, "code: " + code);
                                    db.collection("User")
                                            .document(MainActivity.id)
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

            case R.id.tv_uniqueMemo : {
                final EditText edittext = new EditText(CourseInfoActivity.this);
                edittext.setText(tv_uniqueMemo.getText());
                AlertDialog.Builder builder = new AlertDialog.Builder(CourseInfoActivity.this);
                builder.setTitle("일정");
                builder.setView(edittext);
                builder.setPositiveButton("입력",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String inputStr = edittext.getText().toString();
                                tv_uniqueMemo.setText(inputStr);
                                Map<String, String> map = new HashMap<>();
                                if (inputStr.equals("")) {
                                    db.collection("User")
                                            .document(MainActivity.id)
                                            .collection(semester)
                                            .document("Timetable")
                                            .collection(code)
                                            .document("Daily")
                                            .collection("DueDate")
                                            .document(date)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error deleting document", e);
                                                }
                                            });
                                } else {
                                    map.put("DueMemo", edittext.getText().toString());
                                    Log.d(TAG, "code: " + code);
                                    db.collection("User")
                                            .document(MainActivity.id)
                                            .collection(semester)
                                            .document("Timetable")
                                            .collection(code)
                                            .document("Daily")
                                            .collection("DueDate")
                                            .document(date)
                                            .set(map);
                                }
                                ((MainActivity)MainActivity.mContext).getDueDate();
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
                                        .document(MainActivity.id)
                                        .collection(semester)
                                        .document("Timetable")
                                        .collection(code)
                                        .document("Daily")
                                        .collection("Date")
                                        .document(date)
                                        .set(map);
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
            case R.id.btn_delete : {
                AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog);
                new AlertDialog.Builder(CourseInfoActivity.this) //다이얼로그를 띄움
                        .setTitle(tv_courseName.getText()).setMessage("삭제 하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                db.collection("User")
                                        .document(MainActivity.id)
                                        .collection(semester)
                                        .document("Timetable")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                        if (document.getData().size() == 1) {
                                                            Map<String,Object> updates = new HashMap<>();
                                                            updates.put("temp", "true");
                                                            db.collection("User")
                                                                    .document(MainActivity.id)
                                                                    .collection(semester)
                                                                    .document("Timetable")
                                                                    .set(updates);
                                                        }
                                                    } else {
                                                        Log.d(TAG, "No such document");
                                                    }
                                                } else {
                                                    Log.d(TAG, "get failed with ", task.getException());
                                                }
                                            }
                                        });
                                Map<String,Object> updates = new HashMap<>();
                                updates.put(code.substring(0, 7), FieldValue.delete());
                                Log.d(TAG, code.substring(0, 7));
                                db.collection("User")
                                        .document(MainActivity.id)
                                        .collection(semester)
                                        .document("Timetable")
                                        .update(updates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            }
                                        });
                                ((MainActivity)MainActivity.mContext).deleteTimetable();
                                ((MainActivity)MainActivity.mContext).getSchedule(semester);
                                ((MainActivity)MainActivity.mContext).showSchedule();
                                CourseInfoActivity CIA = (CourseInfoActivity)CourseInfoActivity.activity;
                                CIA.finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                        .show();
            }

            default:
                break;
        }
    }

    //강의의 기본 정보 세팅
    public void setInfo(Intent intent) {
        tv_courseName.setText(intent.getStringExtra("name"));
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
                .document(MainActivity.id)
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
                .document(MainActivity.id)
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

        //해당 날짜 일정 불러옴
        db.collection("User")
                .document(MainActivity.id)
                .collection(semester)
                .document("Timetable")
                .collection(code)
                .document("Daily")
                .collection("DueDate")
                .document(date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Document found in the offline cache
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, "Cached document data: " + document.getData());
                            tv_uniqueMemo.setText(document.getString("DueMemo"));
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
        btn_delete = findViewById(R.id.btn_delete);
    }
}
