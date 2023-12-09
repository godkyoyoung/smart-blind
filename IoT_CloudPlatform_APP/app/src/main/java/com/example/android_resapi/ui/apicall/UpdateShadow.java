package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.android_resapi.httpconnection.PutRequest;

public class UpdateShadow extends PutRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;

    // UpdateShadow 클래스의 생성자
    public UpdateShadow(Activity activity, String urlStr) {
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
            e.printStackTrace();
            Toast.makeText(activity, "URL is invalid:" + urlStr, Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }

    // 백그라운드 작업 완료 후 호출되는 메서드
    @Override
    protected void onPostExecute(String result) {
        // 결과 메시지를 토스트로 출력
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
    }

}
