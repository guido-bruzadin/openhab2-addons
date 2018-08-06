/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 * <p>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.samsungtv.internal.protocol;

import java.util.List;

/**
 * The {@link RemoteController} is responsible for sending key codes to the
 * Samsung TV.
 *
 * @author Guido Bruzadin Neto - Initial contribution
 */
public interface RemoteController {

    /**
     * Open Connection to Samsung TV.
     *
     * @throws RemoteControllerException
     */
    void openConnection() throws RemoteControllerException;

    /**
     * Close connection to Samsung TV.
     *
     * @throws RemoteControllerException
     */
    void closeConnection() throws RemoteControllerException;

    /**
     * Send key code to Samsung TV.
     *
     * @param key Key code to send.
     * @throws RemoteControllerException
     */
    void sendKey(KeyCode key) throws RemoteControllerException;

    /**
     * Send sequence of key codes to Samsung TV.
     *
     * @param keys List of key codes to send.
     * @throws RemoteControllerException
     */
    void sendKeys(List<KeyCode> keys) throws RemoteControllerException;

    /**
     * Send sequence of key codes to Samsung TV.
     *
     * @param keys List of key codes to send.
     * @param sleepInMs Sleep between key code sending in milliseconds.
     * @throws RemoteControllerException
     */
    void sendKeys(List<KeyCode> keys, int sleepInMs) throws RemoteControllerException;

}
