package com.example.android_resapi.httpconnection;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class PutRequest extends AsyncTask<JSONObject, Void, String> {
    protected Activity activity;
    protected URL url;

    public PutRequest(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(JSONObject... postDataParams) {

        try {
            // HTTP 연결 설정
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(10000 /* milliseconds */);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // JSON 데이터 전송
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            String str = postDataParams[0].toString();
            Log.e("params", "Post String = " + str);
            writer.write(str);

            writer.flush();
            writer.close();
            os.close();

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                // 성공적으로 응답을 받았을 때 데이터 수신
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            } else {
                // 서버 오류 발생 시 에러 메시지 반환
                return new String("Server Error : " + responseCode);
            }
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
        }
        return null;
    }
}
