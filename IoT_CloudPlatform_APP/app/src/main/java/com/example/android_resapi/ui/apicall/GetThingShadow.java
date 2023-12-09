package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.example.android_resapi.R;
import com.example.android_resapi.httpconnection.GetRequest;

public class GetThingShadow extends GetRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;

    // GetThingShadow 클래스의 생성자
    public GetThingShadow(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
    }

    // 백그라운드 작업 실행 전 호출되는 메서드
    @Override
    protected void onPreExecute() {
        try {
            // URL 객체 생성
            Log.e(TAG, urlStr);
            url = new URL(urlStr);

        } catch (MalformedURLException e) {
            // URL이 잘못된 경우 에러 메시지 출력 및 액티비티 종료
            Toast.makeText(activity, "URL is invalid:" + urlStr, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            activity.finish();
        }
    }

    // 백그라운드 작업 완료 후 호출되는 메서드
    @Override
    protected void onPostExecute(String jsonString) {
        // JSON 데이터가 없는 경우 종료
        if (jsonString == null)
            return;

        // JSON 데이터를 가지고 상태 정보를 업데이트하는 메서드 호출
        updateStateViews(jsonString);
    }

    // JSON 문자열에서 상태 정보를 추출하여 화면에 업데이트하는 메서드
    protected void updateStateViews(String jsonString) {
        // 결과를 저장할 Map 객체 생성
        Map<String, String> state = getStateFromJSONString(jsonString);

        // 화면의 TextView 객체 가져오기
        TextView reported_ConTV = activity.findViewById(R.id.reported_Con);
        TextView reported_SunTV = activity.findViewById(R.id.reported_Sun);

        // 가져온 상태 정보로 화면 업데이트
        reported_SunTV.setText(state.get("reported_SunShine"));
        reported_ConTV.setText(state.get("reported_Condition"));

        TextView desired_SunTV = activity.findViewById(R.id.desired_Sun);
        TextView desired_ConTV = activity.findViewById(R.id.desired_Con);

        desired_SunTV.setText(state.get("desired_SunShine"));
        desired_ConTV.setText(state.get("desired_Condition"));
    }

    // JSON 문자열에서 상태 정보를 추출하여 Map으로 반환하는 메서드
    protected Map<String, String> getStateFromJSONString(String jsonString) {
        // 결과를 저장할 Map 객체 생성
        Map<String, String> output = new HashMap<>();
        try {
            // JSON 문자열에서 불필요한 문자 제거
            jsonString = jsonString.substring(1, jsonString.length() - 1);
            jsonString = jsonString.replace("\\\"", "\"");

            // JSON 객체 생성
            JSONObject root = new JSONObject(jsonString);

            // "state" 키를 가진 객체에서 "reported" 키를 가진 객체 가져오기
            JSONObject state = root.getJSONObject("state");
            JSONObject reported = state.getJSONObject("reported");

            // 가져온 값들을 Map에 저장
            String SunValue = reported.getString("SunShine");
            String ConValue = reported.getString("Condition");
            output.put("reported_SunShine", SunValue);
            output.put("reported_Condition", ConValue);

            // "state" 키를 가진 객체에서 "desired" 키를 가진 객체 가져오기
            JSONObject desired = state.getJSONObject("desired");

            // 가져온 값들을 Map에 저장
            String desired_SunValue = desired.getString("SunShine");
            String desired_ConValue = desired.getString("Condition");
            output.put("desired_SunShine", desired_SunValue);
            output.put("desired_Condition", desired_ConValue);

        } catch (JSONException e) {
            // JSON 파싱 예외 발생 시 에러 로그 출력 및 스택 트레이스 출력
            Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }
}
