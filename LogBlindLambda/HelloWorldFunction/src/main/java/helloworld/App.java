package helloworld;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.TimeZone;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

/**
 * Lambda 함수의 요청 핸들러 클래스입니다.
 */
public class App implements RequestHandler<Event, String> {
    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME = "BlindLogging";

    public String handleRequest(final Event input, final Context context) {

        // DynamoDB 클라이언트 초기화
        this.initDynamoDbClient();
        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME);

        long from=0;
        long to=0;
        try {
            // 입력된 날짜 형식 변환
            SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            from = sdf.parse(input.from).getTime() / 1000;
            to = sdf.parse(input.to).getTime() / 1000;
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        // DynamoDB 쿼리 스펙 생성
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("deviceId = :v_id and #t between :from and :to")
                .withNameMap(new NameMap().with("#t", "time"))
                .withValueMap(new ValueMap().withString(":v_id", input.device).withNumber(":from", from).withNumber(":to", to));

        ItemCollection<QueryOutcome> items = null;
        try {
            // 테이블 쿼리 실행
            items = table.query(querySpec);
        }
        catch (Exception e) {
            System.err.println("테이블 쿼리 실패:");
            System.err.println(e.getMessage());
        }

        // 응답 데이터 생성
        String output = getResponse(items);

        return output;
    }

    // 쿼리 결과를 JSON 형태의 문자열로 변환하는 메소드
    private String getResponse(ItemCollection<QueryOutcome> items) {

        Iterator<Item> iter = items.iterator();
        String response = "{ \"data\": [";
        for (int i = 0; iter.hasNext(); i++) {
            if (i != 0)
                response += ",";
            response += iter.next().toJSON();
        }
        response += "]}";
        return response;
    }

    // DynamoDB 클라이언트 초기화 메소드
    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

        this.dynamoDb = new DynamoDB(client);
    }

}

// Lambda 함수의 입력 이벤트 클래스
class Event {
    public String device;
    public String from;
    public String to;
}
