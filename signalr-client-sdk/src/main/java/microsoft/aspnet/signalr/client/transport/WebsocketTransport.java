/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package microsoft.aspnet.signalr.client.transport;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;

import com.google.gson.Gson;

import microsoft.aspnet.signalr.client.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.util.Charsetfunctions;

import microsoft.aspnet.signalr.client.http.HttpConnection;

import javax.net.ssl.SSLSocketFactory;

/**
 * Implements the WebsocketTransport for the Java SignalR library
 * Created by stas on 07/07/14.
 */
public class WebsocketTransport extends HttpClientTransport {

    private static final String HTTP_SCHEME = "http";
    private static final String SECURE_HTTP_SCHEME = "https";

    private static final String WEBSCOCKET_SCHEME = "ws";
    private static final String SECURE_WEBSOCKET_SCHEME = "wss";

    private static final String HTTP_URL_START = HTTP_SCHEME + "://";
    private static final String SECURE_HTTP_URL_START = SECURE_HTTP_SCHEME + "://";

    private static final String WEBSOCKET_URL_START = WEBSCOCKET_SCHEME + "://";
    private static final String SECURE_WEBSOCKET_URL_START = SECURE_WEBSOCKET_SCHEME + "://";

    private String mPrefix;
    private static final Gson gson = new Gson();
    WebSocketClient mWebSocketClient;
    private UpdateableCancellableFuture<Void> mConnectionFuture;

    public WebsocketTransport(Logger logger) {
        super(logger);
    }

    public WebsocketTransport(Logger logger, HttpConnection httpConnection) {
        super(logger, httpConnection);
    }

    @Override
    public String getName() {
        return "webSockets";
    }

    @Override
    public boolean supportKeepAlive() {
        return true;
    }

    @Override
    public SignalRFuture<Void> start(ConnectionBase connection, ConnectionType connectionType, final DataResultCallback callback) {
        final String ConnectionUrl = connection.getUrl().replace(HTTP_URL_START, WEBSOCKET_URL_START).replace(SECURE_HTTP_URL_START, SECURE_WEBSOCKET_URL_START);
        final String connectionString = connectionType == ConnectionType.InitialConnection ? "connect" : "reconnect";

        final String transport = getName();
        final String connectionToken = connection.getConnectionToken();
        final String messageId = connection.getMessageId() != null ? connection.getMessageId() : "";
        final String groupsToken = connection.getGroupsToken() != null ? connection.getGroupsToken() : "";
        final String connectionData = connection.getConnectionData() != null ? connection.getConnectionData() : "";


        String url = null;
        try {
            url = ConnectionUrl + connectionString + '?'
                    + "connectionData=" + URLEncoder.encode(URLEncoder.encode(connectionData, "UTF-8"), "UTF-8")
                    + "&connectionToken=" + URLEncoder.encode(URLEncoder.encode(connectionToken, "UTF-8"), "UTF-8")
                    + "&groupsToken=" + URLEncoder.encode(groupsToken, "UTF-8")
                    + "&messageId=" + URLEncoder.encode(messageId, "UTF-8")
                    + "&transport=" + URLEncoder.encode(transport, "UTF-8")
                    + ((connection.getQueryString() != null) ? connection.getQueryString() : "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mConnectionFuture = new UpdateableCancellableFuture<Void>(null);

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            mConnectionFuture.triggerError(e);
            return mConnectionFuture;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17(), null, CONNECTION_TIMEOUT_MS) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                mConnectionFuture.setResult(null);
            }

            @Override
            public void onMessage(String s) {
                callback.onData(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                mWebSocketClient.close();
            }

            @Override
            public void onError(Exception e) {
                mWebSocketClient.close();
            }

            @Override
            public void onFragment(Framedata frame) {
                try {
                    String decodedString = Charsetfunctions.stringUtf8(frame.getPayloadData());

                    if(decodedString.equals("]}")){
                        return;
                    }

                    if(decodedString.endsWith(":[") || null == mPrefix){
                        mPrefix = decodedString;
                        return;
                    }

                    String simpleConcatenate = mPrefix + decodedString;

                    if(isJSONValid(simpleConcatenate)){
                        onMessage(simpleConcatenate);
                    }else{
                        String extendedConcatenate = simpleConcatenate + "]}";
                        if (isJSONValid(extendedConcatenate)) {
                            onMessage(extendedConcatenate);
                        } else {
                            log("invalid json received:" + decodedString, LogLevel.Critical);
                        }
                    }
                } catch (InvalidDataException e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            Socket socket = createConnectedSocket(uri);
            mWebSocketClient.setSocket(socket);
        } catch (IOException e) {
            mConnectionFuture.triggerError(e);
        }
        mWebSocketClient.connect();

        connection.closed(new Runnable() {
            @Override
            public void run() {
                mWebSocketClient.close();
            }
        });

        return mConnectionFuture;
    }

    private Socket createConnectedSocket(URI uri) throws IOException {
        String host = uri.getHost();
        boolean isSecureSocket = uri.toString().startsWith(SECURE_WEBSOCKET_URL_START);
        int port = isSecureSocket ? 443 : 80;

        boolean useProxy = Platform.useProxy();
        String proxyHost = Platform.getProxyHost();
        int proxyPort = Platform.getProxyPort();
        Proxy proxy = useProxy ? new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort)) : Proxy.NO_PROXY;

        if (isSecureSocket){
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            if (useProxy){
                Socket underlyingSocket = new Socket(proxy);
                underlyingSocket.connect(new InetSocketAddress(host, port), CONNECTION_TIMEOUT_MS);
                return factory.createSocket(underlyingSocket, proxyHost, proxyPort, true);
            } else {
                Socket socket = factory.createSocket();
                socket.connect(new InetSocketAddress(host, port), CONNECTION_TIMEOUT_MS);
                return socket;
            }
        } else {
            Socket socket = new Socket(proxy);
            socket.connect(new InetSocketAddress(host, port), CONNECTION_TIMEOUT_MS);
            return socket;
        }
    }

    @Override
    public SignalRFuture<Void> send(ConnectionBase connection, String data, DataResultCallback callback) {
        mWebSocketClient.send(data);
        return new UpdateableCancellableFuture<Void>(null);
    }

    private boolean isJSONValid(String test){
        try {
            gson.fromJson(test, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }
}