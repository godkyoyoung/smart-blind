package com.example.android_resapi.httpconnection;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

// AsyncTask를 상속받아 구현한 GetRequest 클래스
abstract public class GetRequest extends AsyncTask<String, Void, String> {
    final static String TAG = "AndroidAPITest";
    protected Activity activity;
    protected URL url;

    // 생성자: 활동(Activity)을 전달받아 멤버 변수에 할당
    public GetRequest(Activity activity) {
        this.activity = activity;
    }

    // 백그라운드 스레드에서 수행될 작업을 정의한 메서드
    @Override
    protected String doInBackground(String... strings) {
        // 결과를 저장할 StringBuffer 객체 생성
        StringBuffer output = new StringBuffer();

        try {
            // URL이 null인 경우 에러 로그 출력 후 null 반환
            if (url == null) {
                Log.e(TAG, "Error: URL is null ");
                return null;
            }

            // HttpURLConnection 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // HttpURLConnection이 null인 경우 에러 로그 출력 후 null 반환
            if (conn == null) {
                Log.e(TAG, "HttpsURLConnection Error");
                return null;
            }

            // 연결 시간 제한 설정 및 GET 메서드 설정
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(false);

            // 응답 코드 확인
            int resCode = conn.getResponseCode();

            // 응답 코드가 OK가 아닌 경우 에러 로그 출력 후 연결 종료 및 null 반환
            if (resCode != HttpsURLConnection.HTTP_OK) {
                Log.e(TAG, "HttpsURLConnection ResponseCode: " + resCode);
                conn.disconnect();
                return null;
            }

            // 응답 데이터를 읽어오기 위한 BufferedReader 객체 생성
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;

            // 라인 단위로 읽어와서 StringBuffer에 추가
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                output.append(line);
            }

            // BufferedReader와 연결 종료
            reader.close();
            conn.disconnect();

        } catch (IOException ex) {
            // IOException 발생 시 에러 로그 출력 및 스택 트레이스 출력
            Log.e(TAG, "Exception in processing response.", ex);
            ex.printStackTrace();
        }

        // 결과를 문자열로 반환
        return output.toString();
    }
}
