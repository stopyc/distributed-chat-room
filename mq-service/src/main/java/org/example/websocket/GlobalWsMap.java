package org.example.websocket;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.dto.UserChatDTO;
import org.example.pojo.exception.BusinessException;
import org.example.pojo.exception.SystemException;
import org.example.pojo.vo.WsMessageVO;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author YC104
 */
@Slf4j
public class GlobalWsMap {

    public static final ConcurrentHashMap<Long, MyWebSocket> WS_GROUP;

    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    private static final Integer MAX_CONNECT = 500;

    /**
     * 默认负载因子,当连接数达到最大连接数的75%时,开始报警
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    static {
        WS_GROUP = new ConcurrentHashMap<>(MAX_CONNECT);
    }

    /**
     * 上线
     */
    public static void online(MyWebSocket myWebSocket) {
        checkMyWebSocket(myWebSocket);

        if (ONLINE_COUNT.get() >= MAX_CONNECT) {
            sendText(myWebSocket, "当前连接数已达到最大连接数, 请稍后再试");
            close(myWebSocket);
            throw new SystemException("当前连接数已达到最大连接数");
        }
        if (ONLINE_COUNT.get() >= MAX_CONNECT * DEFAULT_LOAD_FACTOR) {
            alarm(ONLINE_COUNT.get());
        }
        WS_GROUP.put(myWebSocket.getUserId(), myWebSocket);
        ONLINE_COUNT.incrementAndGet();
    }

    /**
     * 下线
     */
    public static void offline(MyWebSocket myWebSocket) {

    }

    /**
     * 发送给房间中的所有人
     */
    public static void msgToThisRoom(MyWebSocket myWebSocket, WsMessageVO wsMessageVO) {
    }

    /**
     * 获取该服务器中维护的所有用户连接情况
     */
    public static ConcurrentHashMap<String, CopyOnWriteArraySet<UserChatDTO>> getAllOnline() {
        return null;
    }

    private static void alarm(int onlineCount) {
        log.warn("当前连接数已达到最大连接数的75%,当前连接数:{}", onlineCount);
        //...
    }

    private static void sendText(MyWebSocket myWebSocket, String text) {
        myWebSocket.getSession().getAsyncRemote().sendText(text);
    }

    private static void close(MyWebSocket myWebSocket) {
        if (myWebSocket.getSession().isOpen()) {
            try {
                myWebSocket.getSession().close();
            } catch (IOException e) {
                throw new SystemException(e.getMessage());
            }
        }
    }

    private static void checkMyWebSocket(MyWebSocket myWebSocket) {
        if (Objects.isNull(myWebSocket)
                || Objects.isNull(myWebSocket.getSession())
                || Objects.isNull(myWebSocket.getUserId())
                || Objects.isNull(myWebSocket.getChatRoomId())) {
            throw new BusinessException("MyWebSocket参数缺失");
        }
    }
}