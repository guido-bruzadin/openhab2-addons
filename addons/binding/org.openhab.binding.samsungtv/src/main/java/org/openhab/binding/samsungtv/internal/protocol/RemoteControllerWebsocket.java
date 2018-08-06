/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 * <p>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.samsungtv.internal.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * The {@link RemoteControllerWebsocket} is responsible for sending key codes to the
 * Samsung TV.
 *
 * @author Guido Bruzadin Neto
 */
public class RemoteControllerWebsocket implements RemoteController {

    private final Logger logger = LoggerFactory.getLogger(RemoteControllerWebsocket.class);

    private static final String CONNECTION_STRING = "ws://%s:%d/api/v2/channels/samsung.remote.control?name=%s";
    private static final String COMMAND = "{\"method\":\"ms.remote.control\",\"params\":{\"Cmd\":\"Click\",\"DataOfCmd\":\"%s\",\"Option\":\"false\",\"TypeOfRemote\":\"SendRemoteKey\"}}";

    private static final int CONNECTION_TIMEOUT = 500;

    private String host;
    private int port;
    private String appName;
    private String uniqueId;

    private WebsocketClientEndpoint websocketClientEndpoint;

    /**
     * Create and initialize remote controller instance.
     *
     * @param host     Host name of the Samsung TV.
     * @param port     TCP port of the remote controller protocol.
     * @param appName  Application name used to send key codes.
     * @param uniqueId Unique Id used to send key codes.
     */
    public RemoteControllerWebsocket(String host, int port, String appName, String uniqueId) {
        this.host = host;
        this.port = port;
        this.appName = appName != null ? appName : "";
        this.uniqueId = uniqueId != null ? uniqueId : "";
    }

    /**
     * Open Connection to Samsung TV.
     *
     * @throws RemoteControllerException
     */
    public void openConnection() throws RemoteControllerException {
        logger.debug("Open connection to host '{}:{}', using websocket '{}'", host, port);

        openConnectionWebsocket();
    }

    private void openConnectionWebsocket() throws RemoteControllerException {
        if (port == 0) {
            port = 8001;
        }

        final String uriStr = String.format(CONNECTION_STRING, host, port, appName);

        logger.debug("Trying to open websocket to: {} ", uriStr);
        try {
            final URI endpointURI = new URI(uriStr);

            // open websocket
            websocketClientEndpoint = new WebsocketClientEndpoint(endpointURI);

            // add listener
            websocketClientEndpoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    logger.debug("Message from SamsungTV: {} ", message);
                }
            });
        } catch (URISyntaxException e) {
            throw new RemoteControllerException(e);
        }
    }

    /**
     * Close connection to Samsung TV.
     *
     * @throws RemoteControllerException
     */
    public void closeConnection() throws RemoteControllerException {
        try {
            websocketClientEndpoint.userSession.close();
        } catch (IOException e) {
            throw new RemoteControllerException(e);
        }
    }

    /**
     * Send key code to Samsung TV.
     *
     * @param key Key code to send.
     * @throws RemoteControllerException
     */
    public void sendKey(KeyCode key) throws RemoteControllerException {
        logger.debug("Try to send command: {}", key);

        if (!isConnected()) {
            openConnection();
        }

        try {
            sendKeyData(key);
        } catch (RemoteControllerException e) {
            logger.debug("Couldn't send command", e);
            logger.debug("Retry one time...");

            closeConnection();
            openConnection();

            sendKeyData(key);
        }

        logger.debug("Command successfully sent");
    }

    /**
     * Send sequence of key codes to Samsung TV.
     *
     * @param keys List of key codes to send.
     * @throws RemoteControllerException
     */
    public void sendKeys(List<KeyCode> keys) throws RemoteControllerException {
        sendKeys(keys, 300);
    }

    /**
     * Send sequence of key codes to Samsung TV.
     *
     * @param keys      List of key codes to send.
     * @param sleepInMs Sleep between key code sending in milliseconds.
     * @throws RemoteControllerException
     */
    public void sendKeys(List<KeyCode> keys, int sleepInMs) throws RemoteControllerException {
        logger.debug("Try to send sequence of commands: {}", keys);

        if (!isConnected()) {
            openConnection();
        }

        for (int i = 0; i < keys.size(); i++) {
            KeyCode key = keys.get(i);
            try {
                sendKeyData(key);
            } catch (RemoteControllerException e) {
                logger.debug("Couldn't send command", e);
                logger.debug("Retry one time...");

                closeConnection();
                openConnection();

                sendKeyData(key);
            }

            if ((keys.size() - 1) != i) {
                // Sleep a while between commands
                try {
                    Thread.sleep(sleepInMs);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        logger.debug("Command(s) successfully sent");
    }

    private boolean isConnected() {
        return websocketClientEndpoint != null &&
                websocketClientEndpoint.userSession != null &&
                websocketClientEndpoint.userSession.isOpen();
    }

    private void sendKeyData(KeyCode key) throws RemoteControllerException {
        logger.debug("Sending key code {}", key.getValue());

        final String command = String.format(COMMAND, "KEY_VOLUP");
        // send message to websocket
        websocketClientEndpoint.sendMessage(command);
    }

}
