package helloworld;

import com.amazonaws.services.iotdata.AWSIotData;
import com.amazonaws.services.iotdata.AWSIotDataClientBuilder;
import com.amazonaws.services.iotdata.model.GetThingShadowRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * Lambda 함수의 요청 핸들러 클래스입니다.
 */
public class App implements RequestHandler<Event, String> {

    /**
     * Lambda 함수의 핵심 로직이 구현된 메소드
     * @param event Lambda 함수에 전달되는 입력 이벤트 객체
     * @param context Lambda 함수의 실행 컨텍스트
     * @return AWS IoT Thing Shadow의 현재 상태를 나타내는 문자열
     */
    public String handleRequest(final Event event, final Context context) {

        // AWS IoT Data 클라이언트 생성
        AWSIotData iotData = AWSIotDataClientBuilder.standard().build();

        // Thing Shadow의 현재 상태를 가져오기 위한 요청 생성
        GetThingShadowRequest getThingShadowRequest =
                new GetThingShadowRequest()
                        .withThingName(event.device);

        // Thing Shadow의 현재 상태를 가져오고 결과 문자열로 변환
        String output = new String(
                iotData.getThingShadow(getThingShadowRequest).getPayload().array());

        // AWS IoT Thing Shadow의 현재 상태 반환
        return output;
    }
}

/**
 * Lambda 함수에 전달되는 입력 이벤트 클래스
 */
class Event {
    public String device;
}
