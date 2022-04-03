package com.happyzombie.springinitializr.api.socket;

import com.happyzombie.springinitializr.api.socket.handler.SocketResponseHandler;
import com.happyzombie.springinitializr.api.socket.handler.TransactionsListByAccountIdResponseHandler;
import com.happyzombie.springinitializr.api.socket.handler.WelComeHandler;
import com.happyzombie.springinitializr.common.bean.WebBrowserConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.extensions.IExtension;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.protocols.Protocol;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 与backend后台建立webSocket连接
 * Handlers负责处理不同类型的消息（暂时放到该类中）
 *
 * @author admin
 */
@Slf4j
@Component
public class NearExplorerBackendSocket implements InitializingBean {

    @Value("${near.explorer.backend.mainnet.ws}")
    String backendWsUri;

    private static volatile WebSocketClient client = null;

    private static final Lock LOCK = new ReentrantLock();
    private static final Condition WAIT_FOR_WELCOME = LOCK.newCondition();

    // todo 考虑使用合成音
    private static AudioClip audioClip = null;

    static {
        try {
            audioClip = Applet.newAudioClip(new File("src/main/resources/未识别场景.wav").toURI().toURL());
        } catch (Throwable e) {
            log.error("audioClip error", e);
        }
    }

    @SneakyThrows
    private void sound() {
        audioClip.play();
    }

    /**
     * 获取连接
     *
     * @return WebSocketClient
     */
    public WebSocketClient getWebSocket() {
        if (client == null || client.isClosed()) {
            synchronized (NearExplorerBackendSocket.class) {
                if (client == null || client.isClosed()) {
                    // 这个方法搞成一个如果返回空，就park，当有值时就unpark
                    // 条件执行：unpark条件是收到welcome
                    client = getClient(backendWsUri);
                }
            }
        }
        return Optional.ofNullable(client).orElseThrow(() -> new RuntimeException("与near-backend建立webSocket失败"));
    }

    /**
     * wamp规则，第一个1表示sayhello
     *
     * @see com.happyzombie.springinitializr.api.WampMessageCodes
     */
    public static String HELLO_MESSAGE = "[1,\"near-explorer\",{\"roles\":{\"caller\":{\"features\":{\"caller_identification\":true,\"call_canceling\":true,\"progressive_call_results\":true}},\"callee\":{\"features\":{\"caller_identification\":true,\"pattern_based_registration\":true,\"shared_registration\":true,\"progressive_call_results\":true,\"registration_revocation\":true}},\"publisher\":{\"features\":{\"publisher_identification\":true,\"subscriber_blackwhite_listing\":true,\"publisher_exclusion\":true}},\"subscriber\":{\"features\":{\"publisher_identification\":true,\"pattern_based_subscription\":true,\"subscription_revocation\":true}}}}]";

    @Autowired
    TransactionsListByAccountIdResponseHandler transactionsListByAccountIdResponseHandler;

    /**
     * 处理器
     */
    private List<SocketResponseHandler> handlers = null;

    @Override
    public void afterPropertiesSet() {
        if (transactionsListByAccountIdResponseHandler != null) {
            handlers = Arrays.asList(new WelComeHandler(LOCK, WAIT_FOR_WELCOME), transactionsListByAccountIdResponseHandler);
        } else {
            log.error("transactionsListByAccountIdResponseHandler is null");
        }
    }

    /**
     * Accept-Encoding:gzip, deflate, br
     * Accept-Language:zh-CN,zh;q=0.9,en;q=0.8
     * Cache-Control:no-cache
     * Connection:Upgrade
     * Host:near-explorer-wamp.onrender.com
     * Origin:
     * Pragma:no-cache
     * Sec-WebSocket-Protocol:wamp.2.json, wamp.2.msgpack
     * Sec-WebSocket-Version:13
     * Sec-WebSocket-Extensions:permessage-deflate; client_max_window_bits
     * Upgrade:websocket
     * User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.74 Safari/537.36
     * <p>
     * 可能要生成
     * Sec-WebSocket-Key:随机字符串
     */
    static String HOST = "wamp.onrender.com";
    static String ORIGIN = "https://explorer.near.org";

    private Map<String, String> getHeaders() {
        final HashMap<String, String> map = new HashMap<>();
        map.put("Accept-Encoding", WebBrowserConstant.ACCEPT_ENCODING);
        map.put("Accept-Language", WebBrowserConstant.CHINA_ACCEPT_LANGUAGE);
        map.put("Cache-Control", WebBrowserConstant.NO_CACHE);
        map.put("Host", HOST);
        map.put("Origin", ORIGIN);
        map.put("Pragma", WebBrowserConstant.NO_CACHE);
        map.put("User-Agent", WebBrowserConstant.USER_AGENT);
        // webSocket相关
        // 通知服务器协议升级
        // map.put("Connection", "Upgrade");
        // 协议升级为webSocket
        // map.put("Upgrade", "websocket");
        // webSocket协议版本
        // map.put("Sec-WebSocket-Version", "13");
        // 子协议（应用层协议）
        // map.put("Sec-WebSocket-Protocol", "wamp.2.json, wamp.2.msgpack");
        // 扩展协议
        // map.put("Sec-WebSocket-Extensions", "permessage-deflate; client_max_window_bits");
        // Sec-WebSocket-Key是base64编码的16字节的字符串
        // map.put("Sec-WebSocket-Key", WebBrowserConstant.getSecWebSocketKey());
        return map;
    }

    private WebSocketClient getClient(String uri) {
        try {
            // 请求头
            final Map<String, String> headers = getHeaders();

            /**
             * Draft_6455中包含了Connection，Upgrade,Sec-WebSocket-Version这些头信息
             * WAMP的服务端子协议只支持wamp.2.json和wamp.2.msgpack
             */
            final Draft_6455 draft6455 = new Draft_6455(Collections.<IExtension>emptyList(),
                    Arrays.asList(new Protocol("wamp.2.json"), new Protocol("wamp.2.msgpack")));

            // 创建客户端连接对象
            WebSocketClient socketClient = new WebSocketClient(new URI(uri), draft6455, headers) {
                /**
                 * 建立连接调用
                 * @param serverHandshake
                 */
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    log.info("===webSocket===onOpen");
                }

                /**
                 * 收到服务端消息调用
                 * @param response
                 */
                @Override
                public void onMessage(String response) {

                    final boolean anyMatch = handlers.stream().anyMatch(socketResponseHandler -> {
                        if (socketResponseHandler.isMatch(response)) {
                            socketResponseHandler.handler(response);
                            return true;
                        }
                        return false;
                    });

                    if (!anyMatch) {
                        // no handler match
                        log.info("未识别场景:{}", response);
                        sound();
                    }
                }

                /**
                 * 断开连接调用
                 * @param i
                 * @param s
                 * @param b
                 */
                @Override
                public void onClose(int i, String s, boolean b) {
                    log.info("关闭连接:::" + "i = " + i + ":::s = " + s + ":::b = " + b);
                    sound();
                }

                /**
                 * 连接报错调用
                 * @param e
                 */
                @Override
                public void onError(Exception e) {
                    log.error("====出现错误====" + e.getMessage());
                }
            };

            // 请求与服务端建立连接
            socketClient.connect();

            // 判断连接状态，0为请求中  1为已建立  其它值都是建立失败
            while (socketClient.getReadyState().ordinal() == 0) {
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    log.warn("延迟操作出现问题，但并不影响功能");
                }
                log.info("连接中.......");
            }

            // 连接状态不再是0请求中，判断建立结果是不是1已建立
            if (socketClient.getReadyState().ordinal() == 1) {
                /**
                 * say hello to near server,这里有一点要注意，在没收到hello的相应时，如果直接使用client发送消息，wampServer会断开连接
                 */
                LOCK.lock();
                try {
                    socketClient.send(HELLO_MESSAGE);
                    WAIT_FOR_WELCOME.await();
                } catch (Exception e) {
                    log.error("wait wampServer welcome error", e);
                } finally {
                    LOCK.unlock();
                }

                return socketClient;
            }
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
        return null;
    }


}
