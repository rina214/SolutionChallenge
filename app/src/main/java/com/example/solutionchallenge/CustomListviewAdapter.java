package com.example.solutionchallenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListviewAdapter extends BaseAdapter {

    //adpater에 추가된 데이터를 저장하기 위한 arraylist
    public ArrayList<CustomListviewItem> listViewItemList = new ArrayList<>();

    //ListViewAdapter의 생성자
    public CustomListviewAdapter(){}

    // Adapter에 사용되는 데이터의 개수를 리턴 : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴 : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        //"listview_item" Layout을 infalte하여 convertView 참조 획득
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview, parent, false);
        }

        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView tv_name = convertView.findViewById(R.id.tv_name);
        TextView tv_info = convertView.findViewById(R.id.tv_info);

        //Data Sheet(listviewItemList)에서 position에 위치한 데이터 참조 획득
        CustomListviewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        tv_name.setText(listViewItem.getName());
        tv_info.setText(listViewItem.getInfo());

        return convertView;
    }
    //지정한 위치(position)에 있는 데이터와 관계된 아이템의 ID를 리턴 : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    //아이템 데이터 추가를 위한 함수
    public void addItem(String name, String info, String code, Object object){
        CustomListviewItem item = new CustomListviewItem();

        item.setName(name);
        item.setInfo(info);
        item.setCode(code);
        item.setObject(object);

        listViewItemList.add(item);
    }


}