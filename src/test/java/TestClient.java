import org.idmef.IDMEFObject;
import org.idmef.transport.client.IDMEFClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class TestClient {

    static void send(IDMEFObject msg) {
        IDMEFClient client = new IDMEFClient("http://127.0.0.1:9999");
        try {
            client.send(msg);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testClient1() {
        send(Util.message1());
    }
}
