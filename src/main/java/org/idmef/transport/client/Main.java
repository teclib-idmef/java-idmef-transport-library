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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.idmef.IDMEFObject;

import java.net.ConnectException;
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

    private static class ClientOptions {
        private Options options;
        String serverURL;
        int retryCount;
        String defaultRetryCount = "10";
        long delay;
        String defaultDelay = "5000";

        ClientOptions() {
            options = new Options();

            Option serverURL = new Option("s", "server", true, "server URL, e.g. http://localhost:8080");
            serverURL.setRequired(true);
            options.addOption(serverURL);

            Option retryCount = new Option("r",
                    "retry",
                    true,
                    ("retry count for server connection attempt, default: " + defaultRetryCount));
            retryCount.setRequired(false);
            options.addOption(retryCount);

            Option delay = new Option("d",
                    "delay",
                    true,
                    ("delay between each message sending, default: " + defaultDelay));
            delay.setRequired(false);
            options.addOption(delay);
        }

        void parse(String[] args) throws ParseException {
            CommandLineParser parser = new DefaultParser();
            HelpFormatter formatter = new HelpFormatter();
            CommandLine cmd = null;

            try {
                cmd = parser.parse(options, args);
            } catch (ParseException e) {
                System.out.println(e.getMessage());
                formatter.printHelp("IDMEFV2 Client", options);
                throw e;
            }

            serverURL = cmd.getOptionValue("server");
            retryCount = Integer.parseInt(cmd.getOptionValue("retry", defaultRetryCount));
            delay = Long.parseLong(cmd.getOptionValue("delay", defaultDelay));
        }
    }

    public static void main(String[] args) {
        ClientOptions opts = new ClientOptions();
        try {
            opts.parse(args);
        } catch (ParseException e) {
            return;
        }

        IDMEFClient client = new IDMEFClient(opts.serverURL);
        int retryCount = opts.retryCount;
        long delay = opts.delay;

        while (true) {
            try {
                client.send(generateMessage());
                Thread.sleep(delay);
            } catch (ConnectException e) {
                retryCount--;
                if (retryCount == 0) {
                    e.printStackTrace();
                    return;    
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
