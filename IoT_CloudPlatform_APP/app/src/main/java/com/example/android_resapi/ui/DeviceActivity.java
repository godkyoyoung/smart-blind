package com.example.android_resapi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android_resapi.R;
import com.example.android_resapi.ui.apicall.GetThingShadow;
import com.example.android_resapi.ui.apicall.UpdateShadow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceActivity extends AppCompatActivity {
    String urlStr;
    final static String TAG = "AndroidAPITest";
    Timer timer;
    Button startGetBtn;
    Button stopGetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        // Intent에서 Thing Shadow의 URL을 받아옴
        Intent intent = getIntent();
        urlStr = intent.getStringExtra("thingShadowURL");

        // 각종 UI 요소 초기화
        startGetBtn = findViewById(R.id.startGetBtn);
        startGetBtn.setEnabled(true);
        startGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 주기적으로 Thing Shadow 정보를 가져오는 타이머 시작
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        new GetThingShadow(DeviceActivity.this, urlStr).execute();
                    }
                }, 0, 2000);

                // 버튼 상태 업데이트
                startGetBtn.setEnabled(false);
                stopGetBtn.setEnabled(true);
            }
        });

        stopGetBtn = findViewById(R.id.stopGetBtn);
        stopGetBtn.setEnabled(false);
        stopGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 타이머가 존재하면 종료
                if (timer != null)
                    timer.cancel();

                // 화면의 TextView 초기화 및 버튼 상태 업데이트
                clearTextView();
                startGetBtn.setEnabled(true);
                stopGetBtn.setEnabled(false);
            }
        });

        // 상태 정보 업데이트 버튼 설정
        Button updateBtn = findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // EditText에서 입력된 값을 가져와서 JSON 형태로 만듦
                EditText edit_temp = findViewById(R.id.edit_temp);
                EditText edit_led = findViewById(R.id.edit_led);
                JSONObject payload = new JSONObject();

                try {
                    JSONArray jsonArray = new JSONArray();

                    // 온도 정보가 입력되었으면 JSON에 추가
                    String temp_input = edit_temp.getText().toString();
                    if (temp_input != null && !temp_input.equals("")) {
                        JSONObject tag1 = new JSONObject();
                        tag1.put("tagName", "SunShine");
                        tag1.put("tagValue", temp_input);
                        jsonArray.put(tag1);
                    }

                    // LED 정보가 입력되었으면 JSON에 추가
                    String led_input = edit_led.getText().toString();
                    if (led_input != null && !led_input.equals("")) {
                        JSONObject tag2 = new JSONObject();
                        tag2.put("tagName", "Condition");
                        tag2.put("tagValue", led_input);
                        jsonArray.put(tag2);
                    }

                    // 최종적으로 JSON에 추가된 정보가 있으면 payload에 추가
                    if (jsonArray.length() > 0)
                        payload.put("tags", jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException");
                }

                // payload에 정보가 있으면 업데이트 요청 수행
                if (payload.length() > 0)
                    new UpdateShadow(DeviceActivity.this, urlStr).execute(payload);
                else
                    Toast.makeText(DeviceActivity.this, "변경할 상태 정보 입력이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 화면의 TextView 초기화 메서드
    private void clearTextView() {
        TextView reported_ledTV = findViewById(R.id.reported_Sun);
        TextView reported_tempTV = findViewById(R.id.reported_Con);
        reported_tempTV.setText("");
        reported_ledTV.setText("");

        TextView desired_ledTV = findViewById(R.id.desired_Sun);
        TextView desired_tempTV = findViewById(R.id.desired_Con);
        desired_tempTV.setText("");
        desired_ledTV.setText("");
    }
}
