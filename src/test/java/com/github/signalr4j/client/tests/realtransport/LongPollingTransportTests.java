/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package com.github.signalr4j.client.tests.realtransport;

import com.github.signalr4j.client.tests.util.Sync;
import com.github.signalr4j.client.tests.util.TransportType;

import org.junit.Before;

public class LongPollingTransportTests extends HttpClientTransportTests {

    @Before
    public void setUp() {
        Sync.reset();
    }

  
    @Override
    protected TransportType getTransportType() {
        return TransportType.LongPolling;
    }

}