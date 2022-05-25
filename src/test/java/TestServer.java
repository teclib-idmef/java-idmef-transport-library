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

            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
