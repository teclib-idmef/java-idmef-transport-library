package org.idmef.transport.server;

import org.idmef.IDMEFObject;

public interface IDMEFHttpMessageHandler {
    void handleMessage(IDMEFObject message);
}
