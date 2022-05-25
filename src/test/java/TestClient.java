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
import org.idmef.transport.client.IDMEFClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.fail;

public class TestClient {

    static HttpResponse<String> send(IDMEFObject msg) {
        IDMEFClient client = new IDMEFClient("http://127.0.0.1:9999");
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
