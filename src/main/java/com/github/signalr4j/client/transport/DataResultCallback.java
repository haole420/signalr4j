/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package com.github.signalr4j.client.transport;

/**
 * Callback for data result operations
 */
public interface DataResultCallback {

    /**
     * Callback invoked when there is new data from the server
     * 
     * @param data
     *            data
     */
    public void onData(String data);
}
