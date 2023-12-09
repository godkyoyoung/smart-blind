#include <ArduinoBearSSL.h>
#include <ArduinoECCX08.h>
#include <ArduinoMqttClient.h>
#include <WiFiNINA.h>
#include "arduino_secrets.h"
#include <Servo.h>

Servo servo;

int cds_value = 0;      // CdS 광센서 값을 저장하는 변수
int condition = 0;      // 서보모터에 전달할 조건 값을 저장하는 변수
int condition_n = 0;    // MQTT 메시지로부터 수신한 조건 값을 저장하는 변수

#include <ArduinoJson.h>

const char ssid[] = SECRET_SSID;
const char pass[] = SECRET_PASS;
const char broker[] = SECRET_BROKER;
const char* certificate = SECRET_CERTIFICATE;

WiFiClient wifiClient;             // TCP 소켓 연결에 사용
BearSSLClient sslClient(wifiClient); // SSL/TLS 연결에 사용, ECC508과 통합
MqttClient mqttClient(sslClient);

unsigned long lastMillis = 0;     // 마지막으로 메시지를 게시한 시간을 저장하는 변수

void setup() {
  servo.attach(7);     // 서보모터를 7번 핀에 연결
  servo.write(0);      // 초기 위치로 설정
  Serial.begin(115200);
  while (!Serial);

  if (!ECCX08.begin()) {
    Serial.println("No ECCX08 present!");
    while (1);
  }
  ArduinoBearSSL.onGetTime(getTime);
  sslClient.setEccSlot(0, certificate);
  mqttClient.onMessage(onMessageReceived);
}

void loop() {
  if (WiFi.status() != WL_CONNECTED) {
    connectWiFi();      // WiFi 연결 상태 확인 및 연결
  }
  cds_value = analogRead(A1) / 4;    // CdS 광센서 값을 읽어옴
  if (!mqttClient.connected()) {
    connectMQTT();      // MQTT 클라이언트 연결 상태 확인 및 연결
  }

  // 조건에 따라 서보모터 제어
  if (condition == 1) {
    servo.write(170);
  } else if (condition == 2) {
    servo.write(135);
  } else if (condition == 3) {
    servo.write(90);
  } else {
    // CdS 광센서 값에 따라 서보모터 각도 설정
    if (cds_value >= 170) {
      servo.write(170);
    } else if (cds_value <= 169 && cds_value >= 100) {
      servo.write(135);
    } else if (cds_value <= 99) {
      servo.write(90);
    }
  }

  mqttClient.poll();    // 새로운 MQTT 메시지 폴링 및 Keep-alive 전송

  // 대략 5초마다 메시지 게시
  if (millis() - lastMillis > 5000) {
    lastMillis = millis();
    char payload[512];
    getDeviceStatus(payload);
    sendMessage(payload);
  }
}

unsigned long getTime() {
  // 현재 시간을 WiFi 모듈에서 가져옴  
  return WiFi.getTime();
}

void connectWiFi() {
  Serial.print("Attempting to connect to SSID: ");
  Serial.print(ssid);
  Serial.print(" ");

  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // 실패하면 재시도
    Serial.print(".");
    delay(5000);
  }
  Serial.println();

  Serial.println("You're connected to the network");
  Serial.println();
}

void connectMQTT() {
  Serial.print("Attempting to MQTT broker:  ");
  Serial.print(broker);
  Serial.println(" ");

  while (!mqttClient.connect(broker, 8883)) {
    // 실패하면 재시도
    Serial.print(".");
    delay(5000);
  }
  Serial.println();

  Serial.println("You're connected to the MQTT broker");
  Serial.println();

  // 토픽에 구독
  mqttClient.subscribe("$aws/things/MyMKRWiFi1010/shadow/update/delta");
}

void getDeviceStatus(char* payload) {
  // CdS 광센서 값 및 현재 조건을 메시지 페이로드에 설정
  int cds_value = analogRead(A1) / 4;

  if (condition_n == 1) {
    condition = 1;
  } else if (condition_n == 2) {
    condition = 2;
  } else if (condition_n == 3) {
    condition = 3;
  } else {
    condition = 0;
  }

  sprintf(payload, "{\"state\":{\"reported\":{\"SunShine\":\"%d\",\"Condition\":\"%d\"}}}", cds_value, condition);
}

void sendMessage(char* payload) {
  char TOPIC_NAME[] = "$aws/things/MyMKRWiFi1010/shadow/update";

  Serial.print("게시한 메시지:");
  Serial.println(payload);
  mqttClient.beginMessage(TOPIC_NAME);
  mqttClient.print(payload);
  mqttClient.endMessage();
}

void onMessageReceived(int messageSize) {
  // 메시지를 수신하면 토픽과 내용 출력
  Serial.print("Received a message with topic '");
  Serial.print(mqttClient.messageTopic());
  Serial.print("', length ");
  Serial.print(messageSize);
  Serial.println(" bytes:");

  // 수신한 메시지를 버퍼에 저장
  char buffer[512];
  int count = 0;
  while (mqttClient.available()) {
    buffer[count++] = (char)mqttClient.read();
  }
  buffer[count] = '\0'; // 버퍼의 마지막에 null 캐릭터 삽입
  Serial.println(buffer);
  Serial.println();

  DynamicJsonDocument doc(1024);
  deserializeJson(doc, buffer);
  JsonObject root = doc.as<JsonObject>();
  JsonObject state = root["state"];
  const char* condition_c = state["Condition"];
  Serial.println(condition_c);

  char payload[512];

  // 수신한 조건 값에 따라 상태를 갱신하고 응답 메시지를 전송
  if (strcmp(condition_c, "1") == 0) {
    condition_n = 1;
    sprintf(payload, "{\"state\":{\"reported\":{\"Condition\":\"%s\"}}}", "1");
    sendMessage(payload);
  } else if (strcmp(condition_c, "2") == 0) {
    condition_n = 2;
    sprintf(payload, "{\"state\":{\"reported\":{\"Condition\":\"%s\"}}}", "2");
    sendMessage(payload);
  } else if (strcmp(condition_c, "3") == 0) {
    condition_n = 3;
    sprintf(payload, "{\"state\":{\"reported\":{\"Condition\":\"%s\"}}}", "3");
    sendMessage(payload);
  } else if (strcmp(condition_c, "0") == 0) {
    condition_n = 0;
    sprintf(payload, "{\"state\":{\"reported\":{\"Condition\":\"%s\"}}}", "0");
    sendMessage(payload);
  }
}
