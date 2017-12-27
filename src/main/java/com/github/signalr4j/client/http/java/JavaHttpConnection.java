/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package com.github.signalr4j.client.http.java;

import com.github.signalr4j.client.LogLevel;
import com.github.signalr4j.client.Logger;
import com.github.signalr4j.client.Platform;
import com.github.signalr4j.client.http.HttpConnection;
import com.github.signalr4j.client.http.HttpConnectionFuture;
import com.github.signalr4j.client.http.Request;
import com.github.signalr4j.client.http.HttpConnectionFuture.ResponseCallback;

/**
 * Java HttpConnection implementation, based on HttpURLConnection and threads
 * async operations
 */
public class JavaHttpConnection implements HttpConnection {

	/**
	 * User agent header name
	 */
	private static final String USER_AGENT_HEADER = "User-Agent";

	private Logger mLogger;

	/**
	 * Initializes the JavaHttpConnection
	 * 
	 * @param logger
	 *            logger to log activity
	 */
	public JavaHttpConnection(Logger logger) {
		mLogger = logger;
	}

	@Override
	public HttpConnectionFuture execute(final Request request, final ResponseCallback callback) {

		if (request.getHeaderField(USER_AGENT_HEADER) == null) {
			request.addHeader(USER_AGENT_HEADER, Platform.getUserAgent());
		}

		mLogger.log("Create new thread for HTTP Connection", LogLevel.Verbose);

		HttpConnectionFuture future = new HttpConnectionFuture();

		final NetworkRunnable target = new NetworkRunnable(mLogger, request, future, callback);
		final NetworkThread networkThread = new NetworkThread(target) {
			@Override
			void releaseAndStop() {
				try {
					target.closeStreamAndConnection();
				} catch (Throwable error) {
				}
			}
		};

		future.onCancelled(new Runnable() {

			@Override
			public void run() {
				networkThread.releaseAndStop();
			}
		});

		networkThread.start();

		return future;
	}
}
