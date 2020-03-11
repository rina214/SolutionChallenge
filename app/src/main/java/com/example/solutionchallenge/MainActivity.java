package com.example.solutionchallenge;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GridLayout gl_timetable;
    private TextView tv_time, tv_schedule;
    private ImageButton btn_bus;
    private Toolbar toolbar;
    private ArrayList<ArrayList<Boolean>> hasSchedule_Group = null;
    private ArrayList<Boolean> hasSchedule_Child = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gl_timetable = findViewById(R.id.gl_timetable);
        tv_time = findViewById(R.id.tv_time);
        tv_schedule = findViewById(R.id.tv_schedule);
        btn_bus = findViewById(R.id.btn_bus);

        hasSchedule_Group = new ArrayList<ArrayList<Boolean>>();
        hasSchedule_Child = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar); //화면 상단에 actionbar 대신 toolbar 이용
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); //기존 타이틀을 보이지 않게 함

        //initializeSchedule();
        for(int i = 1; i < 5; i++) {
            for(int j = 0; j < 6; j++) {
                addSchedule(i, j, 1);
            }
        }

        btn_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent busIntent = new Intent(getApplicationContext(), BusActivity.class);
                startActivity(busIntent);
            }
        });

    }

    private void addSchedule(int row, int column, int size) {
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
        newSchedule.setText("1");
        newSchedule.setTextColor(Color.BLACK);
        newSchedule.setGravity(Gravity.CENTER);
        gl_timetable.addView(newSchedule);
    }

    private void initializeSchedule(){
        for(int i = 6; i < gl_timetable.getChildCount(); i++) {
            gl_timetable.removeViewAt(i);
        }
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
}
