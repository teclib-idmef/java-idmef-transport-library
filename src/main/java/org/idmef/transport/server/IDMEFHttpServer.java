package org.idmef.transport.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.idmef.IDMEFException;
import org.idmef.IDMEFObject;
import org.idmef.IDMEFValidator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class IDMEFHttpServer {

    public IDMEFHttpServer(int port, String context, IDMEFHttpMessageHandler messageHandler) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(context, new MyHandler(messageHandler));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        private IDMEFHttpMessageHandler messageHandler;

        MyHandler(IDMEFHttpMessageHandler messageHandler) {
            this.messageHandler = messageHandler;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            boolean ok = processRequest(t);
            String response = ok ? "valid IDMEF message": "invalid message";
            int code = ok ? 200 : 500;
            t.sendResponseHeaders(code, response.length());

            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private boolean processRequest(HttpExchange t) throws IDMEFException, IOException {
            String bodyStr = this.readRequestBodyToString(t.getRequestBody());

            IDMEFObject message = IDMEFObject.deserialize(bodyStr.getBytes());
            IDMEFValidator idmefValidator = new IDMEFValidator();

            if (!idmefValidator.validate(message))
                return false;

            messageHandler.handleMessage(message);

            return true;
        }

        private String readRequestBodyToString (InputStream inputStream) throws IOException {
            int bufferSize = 1024;
            char[] buffer = new char[bufferSize];
            StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
                out.append(buffer, 0, numRead);
            }

            return out.toString();
        }
    }
}
