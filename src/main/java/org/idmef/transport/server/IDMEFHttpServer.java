/*
 * Copyright (C) 2022 Teclib'
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

/**
 * Server part of the IDMEF transport.
 *
 *  This implementation provides:
 *
 *  <ul>
 *      <li>IDMEF message reception over HTTP</li>
 *  </ul>
 */
public class IDMEFHttpServer {
    private HttpServer server;

    /**
     * Initializes a server.
     *
     * Creates the underlying HttpServer and its associated handler.
     *
     * The HTTP request handler:
     * <ul>
     *     <li>deserializes the received JSON bytes; if deserialization fails, no further processing is performed</li>
     *     <li>validates the deserialized IDMEFObject</li>
     *     <li>if message is valid, calls the message handler</li>
     * </ul>
     *
     * @param port the TCP port on which server will listen, for instance 9999
     * @param context the context to which server will answer, for instance "/" or "/api"
     * @param messageHandler an instance of a class implementing IDMEFHttpMessageHandler
     * @throws IOException if an error occurred during HttpServer creation, for instance port is not available.
     */
    public IDMEFHttpServer(int port, String context, IDMEFHttpMessageHandler messageHandler) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(context, new MyHandler(messageHandler));
        server.setExecutor(null); // creates a default executor
    }

    /**
     * Start the HTTP server.
     *
     * This method blocks until server exits or is interrupted.
     */
    public void start() {
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
