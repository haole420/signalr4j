/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package com.github.signalr4j.client.tests.realtransport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import com.github.signalr4j.client.tests.util.TransportType;
import com.github.signalr4j.client.tests.util.Utils;
import com.github.signalr4j.client.Connection;
import com.github.signalr4j.client.Platform;
import com.github.signalr4j.client.SignalRFuture;
import com.github.signalr4j.client.tests.util.ConsoleLogger;
import com.github.signalr4j.client.transport.ClientTransport;
import com.github.signalr4j.client.transport.DataResultCallback;
import com.github.signalr4j.client.transport.NegotiationResponse;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public abstract class HttpClientTransportTests {

    protected abstract TransportType getTransportType();

    @Test
    @Ignore
    public void testNegotiate() throws Exception {
        ClientTransport transport = Utils.createTransport(getTransportType(), Platform.createHttpConnection(new ConsoleLogger()));

        Connection connection = new Connection(TestData.SERVER_URL, TestData.CONNECTION_QUERYSTRING, TestData.getLogger());
        SignalRFuture<NegotiationResponse> future = transport.negotiate(connection);

        NegotiationResponse negotiationResponse = future.get();

        assertNotNull(negotiationResponse);
        assertNotNull(negotiationResponse.getConnectionId());
        assertNotNull(negotiationResponse.getConnectionToken());
        assertNotNull(negotiationResponse.getDisconnectTimeout());
        assertNotNull(negotiationResponse.getKeepAliveTimeout());
        assertEquals("1.3", negotiationResponse.getProtocolVersion());
        assertNotNull(negotiationResponse.getUrl());
    }

    @Test
    @Ignore
    public void testSend() throws Exception {
        ClientTransport transport = Utils.createTransport(getTransportType(), Platform.createHttpConnection(new ConsoleLogger()));
        Connection connection = new Connection(TestData.SERVER_URL, TestData.CONNECTION_QUERYSTRING, TestData.getLogger());

        connection.start(transport).get();
        
        String dataToSend = UUID.randomUUID().toString();

        transport.send(connection, dataToSend, new DataResultCallback() {
            
            @Override
            public void onData(String data) {
                // TODO Auto-generated method stub
                
            }
        }).get();

        String lastSentData = TestData.getLastSentData();
        
        assertEquals(dataToSend, lastSentData);
    }
}