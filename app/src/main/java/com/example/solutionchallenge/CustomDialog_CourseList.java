package com.example.solutionchallenge;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class CustomDialog_CourseList extends Dialog {

    CustomDialog_CourseList m_oDialog;
    private ListView lv_list;

    final FirebaseFirestore db = FirebaseFirestore.getInstance(); //파이어스토어

    public CustomDialog_CourseList(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.custom_dialog);
        lv_list = findViewById(R.id.lv_list);

        m_oDialog = this;

        CustomListviewAdapter adapter = new CustomListviewAdapter();
        lv_list.setAdapter(adapter);

        adapter.addItem("전공선택","", "", null);
        adapter.addItem("전공필수","", "", null);

        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomListviewItem item = (CustomListviewItem) parent.getItemAtPosition(position);
                final String name = item.getName();
                if (name.equals("전공필수")) {
                    final ArrayList<CustomListviewItem> itemList = new ArrayList<>();
                    db.collection("CourseList")
                            .document(name)
                            .collection("정보컴퓨터공학전공")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful())
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            ArrayList<Long> longDay = (ArrayList<Long>)document.getData().get("Day");
                                            String day = "";
                                            if (longDay.contains(Long.parseLong("1")))
                                                day += "월";
                                            if (longDay.contains(Long.parseLong("2")))
                                                day += "화";
                                            if (longDay.contains(Long.parseLong("3")))
                                                day += "수";
                                            if (longDay.contains(Long.parseLong("4")))
                                                day += "목";
                                            if (longDay.contains(Long.parseLong("5")))
                                                day += "금";
                                            String info = document.getString("Prof") + "\n"
                                                    + document.getString("Building") + " - " + document.getString("Classroom") + "\n"
                                                    + day + " " + document.getString("Start") + "(" + document.get("Time") + ")";
                                            itemList.add(new CustomListviewItem(document.getString("Name"), info, document.getId(), document.getData()));
                                        }
                                    else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                    CustomDialog2 oDialog = new CustomDialog2(getContext(), name, itemList);
                                    oDialog.show();
                                }
                            });
                    dismiss();
                }

            }
        });


        //TextView oView =  this.findViewById(R.id.textView);
        //oView.setText("Custom Dialog\n테스트입니다.");

        /*Button oBtn = this.findViewById(R.id.btnOK);
        oBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickBtn(v);
            }
        }); */
    }

/*public void onClickBtn(View _oView)
    {
        this.dismiss();
    }*/
}