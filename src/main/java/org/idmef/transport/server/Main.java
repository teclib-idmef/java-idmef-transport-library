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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.idmef.IDMEFObject;
import org.idmef.transport.server.IDMEFHttpMessageHandler;
import org.idmef.transport.server.IDMEFHttpServer;

import java.io.IOException;

public class Main {

    private static class ServerOptions {
        private Options options;
        int port;

        ServerOptions() {
            options = new Options();

            Option port = new Option("p", "port", true, "server TCP port, e.g. 8080");
            port.setRequired(true);
            options.addOption(port);
        }

        void parse(String[] args) throws ParseException {
            CommandLineParser parser = new DefaultParser();
            HelpFormatter formatter = new HelpFormatter();
            CommandLine cmd = null;

            try {
                cmd = parser.parse(options, args);
            } catch (ParseException e) {
                System.out.println(e.getMessage());
                formatter.printHelp("IDMEFV2 Server", options);
                throw e;
            }

            port = Integer.parseInt(cmd.getOptionValue("port"));
        }
    }

    public static void main(String[] args) {
        ServerOptions opts = new ServerOptions();
        try {
            opts.parse(args);
        } catch (ParseException e) {
            return;
        }

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
            IDMEFHttpServer server = new IDMEFHttpServer(opts.port, "/", handler);

            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
