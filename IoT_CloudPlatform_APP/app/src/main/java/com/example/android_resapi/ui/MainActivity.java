package com.example.android_resapi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android_resapi.R;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "AndroidAPITest";
    EditText listThingsURL, thingShadowURL, getLogsURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 각각의 입력 필드와 버튼을 레이아웃에서 가져옴
        listThingsURL = findViewById(R.id.listThingsURL);
        thingShadowURL = findViewById(R.id.thingShadowURL);
        getLogsURL = findViewById(R.id.getLogsURL);

        // '사물목록 조회' 버튼 설정
        Button listThingsBtn = findViewById(R.id.listThingsBtn);
        listThingsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 입력된 API URI 가져오기
                String urlstr = listThingsURL.getText().toString();
                Log.i(TAG, "listThingsURL=" + urlstr);
                if (urlstr == null || urlstr.equals("")) {
                    // URI가 입력되지 않은 경우 Toast 메시지 표시
                    Toast.makeText(MainActivity.this, "사물목록 조회 API URI 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // ListThingsActivity로 이동하면서 API URI 전달
                Intent intent = new Intent(MainActivity.this, ListThingsActivity.class);
                intent.putExtra("listThingsURL", listThingsURL.getText().toString());
                startActivity(intent);
            }
        });

        // '사물상태 조회/변경' 버튼 설정
        Button thingShadowBtn = findViewById(R.id.thingShadowBtn);
        thingShadowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력된 API URI 가져오기
                String urlstr = thingShadowURL.getText().toString();
                if (urlstr == null || urlstr.equals("")) {
                    // URI가 입력되지 않은 경우 Toast 메시지 표시
                    Toast.makeText(MainActivity.this, "사물상태 조회/변경 API URI 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // DeviceActivity로 이동하면서 API URI 전달
                Intent intent = new Intent(MainActivity.this, DeviceActivity.class);
                intent.putExtra("thingShadowURL", thingShadowURL.getText().toString());
                startActivity(intent);
            }
        });

        // '사물로그 조회' 버튼 설정
        Button listLogsBtn = findViewById(R.id.listLogsBtn);
        listLogsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력된 API URI 가져오기
                String urlstr = getLogsURL.getText().toString();
                if (urlstr == null || urlstr.equals("")) {
                    // URI가 입력되지 않은 경우 Toast 메시지 표시
                    Toast.makeText(MainActivity.this, "사물로그 조회 API URI 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // LogActivity로 이동하면서 API URI 전달
                Intent intent = new Intent(MainActivity.this, LogActivity.class);
                intent.putExtra("getLogsURL", getLogsURL.getText().toString());
                startActivity(intent);
            }
        });
    }
}
