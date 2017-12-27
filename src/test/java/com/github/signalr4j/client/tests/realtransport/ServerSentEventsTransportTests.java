/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package com.github.signalr4j.client.tests.realtransport;

import com.github.signalr4j.client.tests.util.TransportType;

public class ServerSentEventsTransportTests extends HttpClientTransportTests {

    @Override
    protected TransportType getTransportType() {
        return TransportType.ServerSentEvents;
    }

}