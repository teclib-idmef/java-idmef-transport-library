import org.idmef.IDMEFObject;
import org.idmef.transport.server.IDMEFHttpMessageHandler;
import org.idmef.transport.server.IDMEFHttpServer;

import java.io.IOException;

public class TestServer {

    public static void main(String[] args) {
        IDMEFHttpMessageHandler handler = new IDMEFHttpMessageHandler() {
            @Override
            public void handleMessage(IDMEFObject message) {
                try {
                    System.out.println(new String(message.serialize()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            IDMEFHttpServer server = new IDMEFHttpServer(9999, "/", handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
