package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.util.Log;
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

import com.example.android_resapi.R;
import com.example.android_resapi.httpconnection.GetRequest;

public class GetLog extends GetRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;

    // GetLog 클래스의 생성자
    public GetLog(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
    }

    // 백그라운드 작업 실행 전 호출되는 메서드
    @Override
    protected void onPreExecute() {
        try {
            // 화면에서 날짜 및 시간을 가져와서 API 호출에 필요한 파라미터 생성
            TextView textView_Date1 = activity.findViewById(R.id.textView_date1);
            TextView textView_Time1 = activity.findViewById(R.id.textView_time1);
            TextView textView_Date2 = activity.findViewById(R.id.textView_date2);
            TextView textView_Time2 = activity.findViewById(R.id.textView_time2);

            // API 호출에 필요한 파라미터 조합
            String params = String.format("?from=%s:00&to=%s:00",
                    textView_Date1.getText().toString() + textView_Time1.getText().toString(),
                    textView_Date2.getText().toString() + textView_Time2.getText().toString());

            // 전체 URL 조합 및 URL 객체 생성
            Log.i(TAG, "urlStr=" + urlStr + params);
            url = new URL(urlStr + params);

        } catch (MalformedURLException e) {
            // URL이 잘못된 경우 에러 메시지 출력 및 스택 트레이스 출력
            Toast.makeText(activity, "URL is invalid:" + urlStr, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // 조회 중 메시지 표시
        TextView message = activity.findViewById(R.id.message2);
        message.setText("조회중...");
    }

    // 백그라운드 작업 완료 후 호출되는 메서드
    @Override
    protected void onPostExecute(String jsonString) {
        // 결과 메시지 표시 TextView
        TextView message = activity.findViewById(R.id.message2);

        // JSON 데이터가 없는 경우 메시지 표시 및 종료
        if (jsonString == null) {
            message.setText("로그 없음");
            return;
        }

        // 메시지 초기화
        message.setText("");

        // JSON 데이터를 ArrayList로 변환
        ArrayList<Tag> arrayList = getArrayListFromJSONString(jsonString);

        // ArrayAdapter를 사용하여 ListView에 데이터 표시
        final ArrayAdapter adapter = new ArrayAdapter(activity,
                android.R.layout.simple_list_item_1,
                arrayList.toArray());
        ListView txtList = activity.findViewById(R.id.logList);
        txtList.setAdapter(adapter);
        txtList.setDividerHeight(10);
    }

    // JSON 문자열을 ArrayList<Tag>로 변환하는 메서드
    protected ArrayList<Tag> getArrayListFromJSONString(String jsonString) {
        // 결과를 저장할 ArrayList 생성
        ArrayList<Tag> output = new ArrayList();
        try {
            // JSON 문자열에서 불필요한 문자 제거
            jsonString = jsonString.substring(1, jsonString.length() - 1);
            jsonString = jsonString.replace("\\\"", "\"");

            // 로그 확인을 위한 로그 출력
            Log.i(TAG, "jsonString=" + jsonString);

            // JSON 객체 생성
            JSONObject root = new JSONObject(jsonString);

            // "data"라는 키를 가진 배열 가져오기
            JSONArray jsonArray = root.getJSONArray("data");

            // 배열 순회하며 Tag 객체 생성 및 ArrayList에 추가
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Tag thing = new Tag(jsonObject.getString("SunShine"),
                        jsonObject.getString("Condition"),
                        jsonObject.getString("timestamp"));

                output.add(thing);
            }

        } catch (JSONException e) {
            // JSON 파싱 예외 발생 시 에러 로그 출력 및 스택 트레이스 출력
            e.printStackTrace();
        }
        return output;
    }

    // JSON 데이터를 담을 Tag 클래스 정의
    class Tag {
        String Sunshine;
        String Condition;
        String timestamp;

        // Tag 클래스의 생성자
        public Tag(String sunshine, String condition, String time) {
            Sunshine = sunshine;
            Condition = condition;
            timestamp = time;
        }

        // 객체를 문자열로 표현하는 메서드
        public String toString() {
            return String.format("[%s] SunShine: %s, Condition: %s", timestamp, Sunshine, Condition);
        }
    }
}
