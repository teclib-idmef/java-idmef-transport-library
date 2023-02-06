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

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.http.HttpResponse;

import org.idmef.IDMEFObject;
import org.idmef.transport.client.IDMEFClient;
import org.idmef.transport.server.IDMEFHttpMessageHandler;
import org.idmef.transport.server.IDMEFHttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestClient {

    private IDMEFHttpServer server;
    private IDMEFClient client;

    HttpResponse<String> send(IDMEFObject msg) {
        try {
            return client.send(msg);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return null;
    }

    @BeforeAll
    void startServerAndClient() {
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
            server = new IDMEFHttpServer(9999, "/", handler);

            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        client = new IDMEFClient("http://127.0.0.1:9999");
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
