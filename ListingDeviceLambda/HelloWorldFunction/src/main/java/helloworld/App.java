package helloworld;

import java.util.List;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClientBuilder;
import com.amazonaws.services.iot.model.ListThingsRequest;
import com.amazonaws.services.iot.model.ListThingsResult;
import com.amazonaws.services.iot.model.ThingAttribute;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import java.util.Map;
import java.util.HashMap;

public class App implements RequestHandler<Object, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(Object input, Context context) {

        // AWSIot 객체를 얻는다.
        AWSIot iot = AWSIotClientBuilder.standard().build();

        // ListThingsRequest 객체 설정.
        ListThingsRequest listThingsRequest = new ListThingsRequest();

        // listThings 메소드 호출하여 결과 얻음.
        ListThingsResult result = iot.listThings(listThingsRequest);

        // API Gateway Proxy Response를 위한 헤더 설정
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        // API Gateway Proxy Response 객체 생성
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        // 결과 문자열 생성 및 응답모델에 반영
        return response.withStatusCode(200).withBody(getResultStr(result));
    }

    // ListThingsResult를 받아와서 JSON 형태의 문자열로 변환하는 메소드
    private String getResultStr(ListThingsResult result) {
        List<ThingAttribute> things = result.getThings();

        // JSON 형태의 결과 문자열 생성
        String resultString = "{ \"things\": [";
        for (int i = 0; i < things.size(); i++) {
            if (i != 0)
                resultString += ",";
            resultString += String.format("{\"thingName\":\"%s\", \"thingArn\":\"%s\"}",
                    things.get(i).getThingName(),
                    things.get(i).getThingArn());
        }
        resultString += "]}";
        return resultString;
    }
}
