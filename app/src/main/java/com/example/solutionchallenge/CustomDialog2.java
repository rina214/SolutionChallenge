package com.example.solutionchallenge;

import android.app.Dialog;
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


public class CustomDialog2 extends Dialog {

    CustomDialog2 m_oDialog;
    private ListView lv_list;
    private String name;
    ArrayList<CustomListviewItem> itemList;

    final FirebaseFirestore db = FirebaseFirestore.getInstance(); //파이어스토어

    public CustomDialog2(Context context, String courseName, ArrayList<CustomListviewItem> itemlist) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        name = courseName;
        itemList = new ArrayList<>();
        itemList = itemlist;
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

        final CustomListviewAdapter adapter = new CustomListviewAdapter();
        lv_list.setAdapter(adapter);
        for (int i = 0; i < itemList.size(); i++) {
            adapter.addItem(itemList.get(i).getName(), itemList.get(i).getInfo(), itemList.get(i).getCode(), itemList.get(i).getObject());
        }

        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomListviewItem item = (CustomListviewItem) parent.getItemAtPosition(position);
                ((MainActivity)MainActivity.mContext).addCourse(item.getCode(), item.getObject());
                dismiss();

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