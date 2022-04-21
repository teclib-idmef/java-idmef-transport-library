package org.idmef.transport.server;

import org.idmef.IDMEFObject;

/**
 * A handler which is invoked to process a received IDMEF message;
 *
 */
public interface IDMEFHttpMessageHandler {
    /**
     *
     * @param message the message to process; message has already been validated against JSON schema
     */
    void handleMessage(IDMEFObject message);
}
