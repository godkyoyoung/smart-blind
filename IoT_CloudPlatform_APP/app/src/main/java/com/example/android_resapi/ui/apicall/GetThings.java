package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.android_resapi.R;
import com.example.android_resapi.httpconnection.GetRequest;
import com.example.android_resapi.ui.DeviceActivity;

public class GetThings extends GetRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;

    // GetThings 클래스의 생성자
    public GetThings(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
    }

    // 백그라운드 작업 실행 전 호출되는 메서드
    @Override
    protected void onPreExecute() {
        try {
            // URL 객체 생성
            url = new URL(urlStr);

        } catch (MalformedURLException e) {
            // URL이 잘못된 경우 에러 메시지 출력 및 액티비티 종료
            Toast.makeText(activity, "URL is invalid:" + urlStr, Toast.LENGTH_SHORT).show();
            activity.finish();
            e.printStackTrace();
        }

        // 조회 중 메시지 표시
        TextView message = activity.findViewById(R.id.message);
        message.setText("조회중...");
    }

    // 백그라운드 작업 완료 후 호출되는 메서드
    @Override
    protected void onPostExecute(String jsonString) {
        // 결과 메시지 표시 TextView
        TextView message = activity.findViewById(R.id.message);

        // JSON 데이터가 없는 경우 또는 빈 문자열인 경우 메시지 표시 및 종료
        if (jsonString == null || jsonString.equals("")) {
            message.setText("디바이스 없음");
            return;
        }

        // 메시지 초기화
        message.setText("");

        // JSON 데이터를 ArrayList<Thing>로 변환
        ArrayList<Thing> arrayList = getArrayListFromJSONString(jsonString);

        // ArrayAdapter를 사용하여 ListView에 데이터 표시
        final ArrayAdapter adapter = new ArrayAdapter(activity,
                android.R.layout.simple_list_item_1,
                arrayList.toArray());
        ListView txtList = activity.findViewById(R.id.txtList);
        txtList.setAdapter(adapter);
        txtList.setDividerHeight(10);

        // ListView 아이템 클릭 이벤트 처리
        txtList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 클릭된 아이템의 Thing 객체 가져오기
                Thing thing = (Thing) adapterView.getAdapter().getItem(i);

                // DeviceActivity로 이동하는 Intent 생성 및 실행
                Intent intent = new Intent(activity, DeviceActivity.class);
                intent.putExtra("thingShadowURL", urlStr + "/" + thing.name);
                activity.startActivity(intent);
            }
        });
    }

    // JSON 문자열을 ArrayList<Thing>로 변환하는 메서드
    protected ArrayList<Thing> getArrayListFromJSONString(String jsonString) {
        // 결과를 저장할 ArrayList 생성
        ArrayList<Thing> output = new ArrayList<>();
        try {
            // JSON 객체 생성
            JSONObject root = new JSONObject(jsonString);

            // "body" 키를 가진 문자열 가져오기
            String bodyString = root.getString("body");

            // "things"라는 키를 가진 배열 가져오기
            JSONArray jsonArray = new JSONObject(bodyString).getJSONArray("things");

            // 배열 순회하며 Thing 객체 생성 및 ArrayList에 추가
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Thing thing = new Thing(
                        jsonObject.getString("thingName"),
                        jsonObject.getString("thingArn")
                );

                output.add(thing);
            }

        } catch (JSONException e) {
            // JSON 파싱 예외 발생 시 에러 로그 출력 및 스택 트레이스 출력
            Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }


    // Thing 클래스 정의
    class Thing {
        String name;
        String arn;
        HashMap<String, String> tags;

        // Thing 클래스의 생성자
        public Thing(String name, String arn) {
            this.name = name;
            this.arn = arn;
            tags = new HashMap<String, String>();
        }

        // 객체를 문자열로 표현하는 메서드
        public String toString() {
            return String.format("이름 = %s \nARN = %s \n", name, arn);
        }
    }
}
