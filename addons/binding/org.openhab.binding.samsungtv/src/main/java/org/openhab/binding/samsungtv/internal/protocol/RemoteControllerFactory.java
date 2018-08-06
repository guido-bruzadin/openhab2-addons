/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 * <p>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.samsungtv.internal.protocol;

/**
 * The {@link RemoteControllerFactory} is responsible for sending key codes to the
 * Samsung TV.
 *
 * @author Guido Bruzadin Neto - Initial contribution
 */
public class RemoteControllerFactory {

    public static RemoteController createRemoteController(String host, Integer port, Boolean useWebsocket, String appName, String uniqueId) {
        if (useWebsocket) {
            return new RemoteControllerWebsocket(host, port, appName, uniqueId);
        } else {
            return new RemoteControllerSocket(host, port, appName, uniqueId);
        }
    }

}
