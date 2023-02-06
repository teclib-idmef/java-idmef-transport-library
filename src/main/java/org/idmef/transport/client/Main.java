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

package org.idmef.transport.client;

import org.idmef.IDMEFObject;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Main {
    private static String now() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    private static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private static IDMEFObject generateMessage() {
        IDMEFObject msg = new IDMEFObject();
        msg.put("Version", "2.0.3");
        msg.put("ID", generateUUID());
        msg.put("CreateTime", now());

        IDMEFObject analyzer = new IDMEFObject();
        analyzer.put("IP", "127.0.0.1");
        analyzer.put("Name", "foobar");
        analyzer.put("Model", "generic");
        analyzer.put("Category", new String[] { "LOG" });
        analyzer.put("Data", new String[] { "Log" });
        analyzer.put("Method", new String[] { "Monitor" });

        msg.put("Analyzer", analyzer);

        return msg;
    }

    public static void main(String[] args) {
        IDMEFClient client = new IDMEFClient(args[0]);
        long millis = Long.parseLong(args[1]);

        try {
            while (true) {
                client.send(generateMessage());
                Thread.sleep(millis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
