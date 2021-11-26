import org.idmef.IDMEFObject;
import org.idmef.transport.client.IDMEFClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.fail;

public class TestClient {

    static HttpResponse<String> send(IDMEFObject msg) {
        IDMEFClient client = new IDMEFClient("http://127.0.0.1:8000/server");
        try {
            return client.send(msg);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return null;
    }

    @Test
    void testClient1() {
        HttpResponse<String> httpResponse = send(Util.message1());
        Assertions.assertEquals(200, httpResponse.statusCode(), httpResponse.body());
    }

    @Test
    void testClient2() {
        HttpResponse<String> httpResponse = send(Util.message2());
        Assertions.assertEquals(200, httpResponse.statusCode(), httpResponse.body());
    }

    @Test
    void testClient3() {
        HttpResponse<String> httpResponse = send(Util.message3());
        Assertions.assertEquals(500, httpResponse.statusCode(), httpResponse.body());
    }
}
