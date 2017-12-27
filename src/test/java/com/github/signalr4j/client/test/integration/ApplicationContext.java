/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package com.github.signalr4j.client.test.integration;

import java.util.concurrent.Future;

import com.github.signalr4j.client.test.integration.framework.TestExecutionCallback;
import com.github.signalr4j.client.hubs.HubConnection;
import com.github.signalr4j.client.test.integration.framework.TestCase;
import com.github.signalr4j.client.transport.AutomaticTransport;
import com.github.signalr4j.client.transport.ClientTransport;
import com.github.signalr4j.client.transport.LongPollingTransport;
import com.github.signalr4j.client.transport.ServerSentEventsTransport;

public class ApplicationContext {
	
    private static TestPlatformContext mTestPlatformContext;
    
    public static void setTestPlatformContext(TestPlatformContext testPlatformContext) {
        mTestPlatformContext = testPlatformContext;
    }
    
	public static void sleep() throws Exception {
	    mTestPlatformContext.sleep(3);
	}
	
	public static void sleep(int seconds) throws Exception {
        mTestPlatformContext.sleep(seconds);
    }
	
	public static HubConnection createHubConnection() {
		String url = getServerUrl();
		
		HubConnection connection = new HubConnection(url,"", true, mTestPlatformContext.getLogger());
		
		return connection;
	}
	
	public static HubConnection createHubConnectionWithInvalidURL() {
        String url = "http://signalr.net/fake";
        
        HubConnection connection = new HubConnection(url,"", true, mTestPlatformContext.getLogger());
        
        return connection;
    }

	public static String getServerUrl() {
		return mTestPlatformContext.getServerUrl();
	}

	public static String getLogPostURL() {
		return mTestPlatformContext.getLogPostUrl();
	}
	

    public static ClientTransport createTransport(TransportType transportType) {
        switch (transportType) {
        case Auto:
            return new AutomaticTransport(mTestPlatformContext.getLogger());

        case LongPolling:
            return new LongPollingTransport(mTestPlatformContext.getLogger());
            
        case ServerSentEvents:
            return new ServerSentEventsTransport(mTestPlatformContext.getLogger());
        default:
            return null;
        }
    }

    public static Future<Void> showMessage(String message) {
        return mTestPlatformContext.showMessage(message);
    }
    
    public static void executeTest(TestCase testCase, TestExecutionCallback callback) {
        mTestPlatformContext.executeTest(testCase, callback);
    }
}
