/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package com.github.signalr4j.client.tests.util;

import java.util.ArrayList;
import java.util.List;

import com.github.signalr4j.client.ConnectionState;
import com.github.signalr4j.client.SignalRFuture;

public class MultiResult {
    public boolean booleanResult = false;
    public int intResult = 0;
    public String stringResult = null;
    public SignalRFuture<?> futureResult = null;
    public List<Object> listResult = new ArrayList<Object>();
    public List<Throwable> errorsResult = new ArrayList<Throwable>();
    public List<ConnectionState> statesResult = new ArrayList<ConnectionState>();
}
