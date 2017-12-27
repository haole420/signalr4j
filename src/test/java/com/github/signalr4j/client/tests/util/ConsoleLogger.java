/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package com.github.signalr4j.client.tests.util;

import com.github.signalr4j.client.LogLevel;
import com.github.signalr4j.client.Logger;

public class ConsoleLogger implements Logger {

    @Override
    public void log(String message, LogLevel level) {
        System.out.println(level.toString() + " - " + message);
    }

}
