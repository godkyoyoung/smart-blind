package helloworld;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.iotdata.AWSIotData;
import com.amazonaws.services.iotdata.AWSIotDataClientBuilder;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import com.amazonaws.services.iotdata.model.UpdateThingShadowResult;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Lambda 함수의 요청 핸들러 클래스입니다.
 */
public class App implements RequestHandler<Event, String> {

    /**
     * Lambda 함수의 핵심 로직이 구현된 메소드
     * @param event Lambda 함수에 전달되는 입력 이벤트 객체
     * @param context Lambda 함수의 실행 컨텍스트
     * @return AWS IoT Thing Shadow 업데이트 결과 문자열
     */
    public String handleRequest(final Event event, final Context context) {
        // AWS IoT Data 클라이언트 생성
        AWSIotData iotData = AWSIotDataClientBuilder.standard().build();

        // 업데이트할 Shadow의 payload 문자열 생성
        String payload = getPayload(event.tags);

        // Thing Shadow 업데이트 요청 생성
        UpdateThingShadowRequest updateThingShadowRequest  =
                new UpdateThingShadowRequest()
                        .withThingName(event.device)
                        .withPayload(ByteBuffer.wrap(payload.getBytes()));

        // Thing Shadow 업데이트 수행 및 결과 얻기
        UpdateThingShadowResult result = iotData.updateThingShadow(updateThingShadowRequest);
        byte[] bytes = new byte[result.getPayload().remaining()];
        result.getPayload().get(bytes);
        String output = new String(bytes);

        // AWS IoT Thing Shadow 업데이트 결과 반환
        return output;
    }

    /**
     * Thing Shadow 업데이트를 위한 payload 문자열 생성 메소드
     * @param tags 업데이트할 태그 목록
     * @return Thing Shadow 업데이트를 위한 payload 문자열
     */
    private String getPayload(ArrayList<Tag> tags) {
        // 태그 목록을 JSON 형식의 문자열로 변환
        String tagstr = "";
        for (int i=0; i < tags.size(); i++) {
            if (i !=  0) tagstr += ", ";
            tagstr += String.format("\"%s\" : \"%s\"", tags.get(i).tagName, tags.get(i).tagValue);
        }
        return String.format("{ \"state\": { \"desired\": { %s } } }", tagstr);
    }
}

/**
 * Lambda 함수에 전달되는 입력 이벤트 클래스
 */
class Event {
    public String device;
    public ArrayList<Tag> tags;

    /**
     * 기본 생성자를 포함하는 생성자
     */
    public Event() {
        tags = new ArrayList<Tag>();
    }
}

/**
 * AWS IoT Thing Shadow의 태그 정보를 나타내는 클래스
 */
class Tag {
    public String tagName;
    public String tagValue;

    /**
     * Jackson JSON 라이브러리에서 사용하기 위한 생성자
     */
    @JsonCreator
    public Tag() {
    }

    /**
     * 태그 이름과 값으로 초기화하는 생성자
     * @param n 태그 이름
     * @param v 태그 값
     */
    public Tag(String n, String v) {
        tagName = n;
        tagValue = v;
    }
}
