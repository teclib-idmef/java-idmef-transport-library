# Java IDMEF transport library

![GitHub top language](https://img.shields.io/github/languages/top/teclib-idmef/java-idmef-transport-library) 
![GitHub](https://img.shields.io/github/license/teclib-idmef/java-idmef-transport-library) 
![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/teclib-idmef/java-idmef-transport-library) 
![GitHub release (latest by date)](https://img.shields.io/github/v/release/teclib-idmef/java-idmef-transport-library)
![GitHub issues](https://img.shields.io/github/issues/teclib-idmef/java-idmef-transport-library)
[![](https://jitpack.io/v/teclib-idmef/java-idmef-library.svg)](https://jitpack.io/#teclib-idmef/java-idmef-transport-library)

A Java library for transporting JSON IDMEFv2 messages. It can be used to transfer Incident Detection Message Exchange Format (IDMEFv2) messages for exchange with other systems.

IDMEFv2 messages can be generated, validated and serialized/deserialized using the [`java-idmef-library`](https://github.com/teclib-idmef/java-idmef-library).

This code is currently in an experimental status and is regularly kept in sync with the development status of the IDMEFv2 format, as part of the [SECurity Exchange Format project](https://www.secef.net/).

The latest revision of the IDMEFv2 format specification can be found there: https://github.com/IDMEFv2/IDMEFv2-Specification

You can find more information about the previous version (v1) of the Intrusion Detection Message Exchange Format in [RFC 4765](https://tools.ietf.org/html/rfc4765).

## Compiling the library

The following prerequisites must be installed on your system to install and use this library:

* Java: version 11 or above

The library has the following third-party dependencies:

* java-idmef-library: https://github.com/teclib-idmef/java-idmef-library
* Jackson (aka JSON for Java): https://github.com/FasterXML/jackson
* Networknt Java JSON Schema Validator: https://github.com/networknt/json-schema-validator

**Note**: building using gradle automaticaly pulls the needed dependencies.

To compile the library:

``` shell
./gradlew build
``` 

This will build a JAR archive located in `./build/libs`.

## Using the library

### Add the library to your project dependencies

The library is published on https://jitpack.io. Using the library is therefore very simple:

**Step 1**. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

**Step 2**. Add the dependency

```
	dependencies {
	        implementation 'com.github.teclib-idmef:java-idmef-transport-library:V1.0.2'
	}
```

### Client

A new client can be created by instantiating the `org.idmef.transport.client.IDMEFClient` class. Once created, message can be send using the `send()` method.

``` java
import org.idmef.IDMEFObject;
import org.idmef.transport.client.IDMEFClient;

class Test {
    static IDMEFObject message1() {
        IDMEFObject msg = new IDMEFObject();
        msg.put("Version", "2.0.3");
        msg.put("ID", "09db946e-673e-49af-b4b2-a8cd9da58de6");
        msg.put("CreateTime", "2021-11-22T14:42:51.881033Z");

        IDMEFObject analyzer = new IDMEFObject();
        analyzer.put("IP", "127.0.0.1");
        analyzer.put("Name", "foobar");
        analyzer.put("Model", "generic");
        analyzer.put("Category", new String[]{"LOG"});
        analyzer.put("Data", new String[]{"Log"});
        analyzer.put("Method", new String[]{"Monitor"});

        msg.put("Analyzer", analyzer);

        return msg;
    }

    public static void main(String[] args)
    {
        IDMEFObject msg1 = message1();

	IDMEFClient client = new IDMEFClient("http://127.0.0.1:9999");

        try {
	    client.send(msg);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

	System.out.println("Message send OK");
    }
}
```

### Server

A new server can be created by instantiating the `org.idmef.transport.server.IDMEFServer` class. Once created, server loop message can be started using the `start()` method. This method will loop processing messages received by the server. A handler must be setup before creating the server object.

``` java
import org.idmef.IDMEFObject;
import org.idmef.transport.server.IDMEFHttpMessageHandler;
import org.idmef.transport.server.IDMEFHttpServer;

public class TestServer {

    public static void main(String[] args) {
        IDMEFHttpMessageHandler handler = new IDMEFHttpMessageHandler() {
            @Override
            public void handleMessage(IDMEFObject message) {
                try {
                    System.out.println("Received message with ID: " + message.get("ID"));
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
```

## Contributions

All contributions must be licensed under the Apache-2.0 license. See the LICENSE file inside this repository for more information.

