package com.example.solutionchallenge;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.MapView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

public class BusActivity extends AppCompatActivity implements View.OnClickListener  {

    private final String key = "fuhE6LeS3tl%2Fj%2Btqi1ZvtDBb2blv2B6IKRCOlz3dXE8dTk6Gi%2BarNkmwy76tH0rqD%2FWaqGRKnroojEmMhLpImg%3D%3D"; //공공 데이터 포털에서 받아온 인증키
    private final String endPoint = "http://61.43.246.153/openapi-data/service/busanBIMS2";
    private final String lineId = "5291107000"; //금정구7번 버스의 lineId
    private final String TAG = "myTag";

    //xml 변수
    private TextView xmlShowInfo;
    private TextView bstop1, bstop2, bstop3, bstop4, bstop5, bstop6, bstop7, bstop8;

    // 파싱을 위한 필드 선언
    private URL url;
    private InputStream is;
    private XmlPullParserFactory factory;
    private XmlPullParser xpp;
    private String tag;
    private int eventType;

    // xml의 값 입력 변수
    private StringBuffer buffer;

    private String car1, car2, min1, min2, station1, station2;
    private HashMap<String, String> busStop = new HashMap<>();
    private String bstopId;

    MapView mapView = new MapView(this);

    RelativeLayout mapViewContainer = findViewById(R.id.map_view);
    //mapViewContainer.addView(mapView);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없애기
        setContentView(R.layout.activity_bus);

        //xml 아이디 얻어오기
        getXmlId();
        buffer = new StringBuffer();
        initializeBusStop();

        String url ="daummaps://open";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);

    }

    // Url, XmlPullParser 객체 생성 및 초기화
    public void setUrlNParser(String quary) {
        try {
            url = new URL(quary); //문자열로 된 요청 url을 URL객체로 생성
            is = url.openStream();

            factory = XmlPullParserFactory.newInstance();
            xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8")); //inputStream으로부터 xml입력받기

            xpp.next();
            eventType = xpp.getEventType();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // UI ID 얻는 함수
    public void getXmlId() {
        xmlShowInfo = findViewById(R.id.showInfo);
        bstop1 = findViewById(R.id.bstop1);
        bstop1.setOnClickListener(this);
        bstop2 = findViewById(R.id.bstop2);
        bstop2.setOnClickListener(this);
        bstop3 = findViewById(R.id.bstop3);
        bstop3.setOnClickListener(this);
        bstop4 = findViewById(R.id.bstop4);
        bstop4.setOnClickListener(this);
        bstop5 = findViewById(R.id.bstop5);
        bstop5.setOnClickListener(this);
        bstop6 = findViewById(R.id.bstop6);
        bstop6.setOnClickListener(this);
        bstop7 = findViewById(R.id.bstop7);
        bstop7.setOnClickListener(this);
        bstop8 = findViewById(R.id.bstop8);
        bstop8.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) { //텍스트뷰를 클릭했을 때
        switch (v.getId()){
            case R.id.bstop1:
                bstopId = busStop.get("부산대역");
                break;
            case R.id.bstop2:
                bstopId = busStop.get("부산은행");
                break;
            case R.id.bstop3:
                bstopId = busStop.get("부산대정문");
                break;
            case R.id.bstop4:
                bstopId = busStop.get("부산대본관");
                break;
            case R.id.bstop5:
                bstopId = busStop.get("부산대문창회관");
                break;
            case R.id.bstop6:
                bstopId = busStop.get("새벽벌도서관");
                break;
            case R.id.bstop7:
                bstopId = busStop.get("부산대사회관");
                break;
            case R.id.bstop8:
                bstopId = busStop.get("부산대법학관");
                break;
        }
        showInformation();
    }

    public void getBus() {
        String dataUrl = endPoint + "/busStopArr?bstopid=" + bstopId + "&lineid=" + lineId + "&ServiceKey=" + key;
        Log.d(TAG, dataUrl);
        try {
            setUrlNParser(dataUrl);

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();

                        if (tag.equals("item")) ; //첫번째 검색 결과
                        else if (tag.equals("carNo1")) {
                            xpp.next();
                            car1 = xpp.getText();
                        } else if (tag.equals("min1")) {
                            xpp.next();
                            min1 = xpp.getText();
                        } else if (tag.equals("station1")) {
                            xpp.next();
                            station1 = xpp.getText();
                        } else if (tag.equals("carNo2")) {
                            xpp.next();
                            car2 = xpp.getText();
                        } else if (tag.equals("min2")) {
                            xpp.next();
                            min2 = xpp.getText();
                        } else if (tag.equals("station2")) {
                            xpp.next();
                            station2 = xpp.getText();
                        }else if (tag.equals("bstopId")) ;
                        else if (tag.equals("nodeNm")) ;
                        else if (tag.equals("companyid")) ;
                        else if (tag.equals("gpsX")) ;
                        else if (tag.equals("gpsY")) ;
                        else if (tag.equals("bustype")) ;
                        else if (tag.equals("lineid")) ;
                        else if (tag.equals("bstopidx")) ;
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        if (tag.equals("item")); // 첫번째 검색 결과 종료.. 줄바꿈
                        break;
                } //end of switch~case
                eventType = xpp.next();
            } //end of while
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showInformation() {
        car1 = min1 = station1 = car2 = min2 = station2 = null;
        buffer = null;
        buffer = new StringBuffer();
        xmlShowInfo.setText("");

        new Thread(new Runnable() {
            @Override
            public void run() {

                //버스가 언제오는지 확인
                getBus();

                // UI setText 하는 곳..
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, car1 + " " + min1 + " " + station1);
                        Log.d(TAG, car2 + " " + min2 + " " + station2);
                        if(car1 == null) {
                            buffer.append("도착 정보 없음");
                        } else {
                            buffer.append("첫번째 차량 도착 정보\n");
                            buffer.append("차량 번호 : " + car1 + " \n");
                            buffer.append("남은 시간 : " + min1 + " 분 \n");
                            buffer.append("남은 구간 : " + station1 + "정거장\n");
                        }
                        // 두번째 도착 차량은 null이 아닐 경우에만 출력
                        if(car2 != null) {
                            buffer.append("-------------------------\n");
                            buffer.append("두번째 차량 도착 정보\n");
                            buffer.append("차량 번호 : " + car2 + " \n");
                            buffer.append("남은 시간 : " + min2 + "분 \n");
                            buffer.append("남은 구간 : " + station2 + "정거장 \n");
                        }
                        xmlShowInfo.setText(buffer.toString());
                    }
                });
            }
        }).start();
    }

    public void initializeBusStop() {
        busStop.put("부산대역", "175710101");
        busStop.put("부산은행", "175620401");
        busStop.put("부산대정문", "175780401");
        busStop.put("부산대본관", "176460301");
        busStop.put("부산대문창회관", "176470201");
        busStop.put("새벽벌도서관", "176490201"); //부산대제2도서관
        busStop.put("부산대사회관", "176500301");
        busStop.put("부산대법학관", "212540201");
        busStop.put("부산대화학관", "212540202");
        busStop.put("부산대예술관", "176450201");
        busStop.put("부산대미술관", "212560101");
        busStop.put("학생회관", "212560102"); //부산대음악관 ??
        busStop.put("부산대경암체육관", "217560101");
        busStop.put("부산대음악관2", "212550102");
        busStop.put("부산대미술관2", "212550101");
        busStop.put("부산대예술관2", "212560201");
        busStop.put("부산대생활환경관", "176420101");
        busStop.put("부산대화학관2", "176450301");
        busStop.put("부산대법학관2", "212540101");
        busStop.put("부산대사회관2", "176510101");
        busStop.put("새벽벌도서관2", "176500101"); //부산대제2도서관
        busStop.put("부산대문창회관2", "176480201");
        busStop.put("부산대본관2", "217370101");
        busStop.put("부산대정문2", "175790101");
        busStop.put("금정등기소", "175780302");
        busStop.put("부산대후문", "175830202");
        busStop.put("신한은행", "175830203");
    }
}
