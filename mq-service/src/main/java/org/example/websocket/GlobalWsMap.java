package org.example.websocket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.adapter.MessageBO2MessageDTO;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.MessageDTO;
import org.example.pojo.dto.UserChatDTO;
import org.example.pojo.exception.BusinessException;
import org.example.pojo.exception.SystemException;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
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
            MessageDTO messageDTO = MessageBO2MessageDTO.getMessageDTO("当前连接数已达到最大连接数, 请稍后再试", 3);
            sendText(myWebSocket, messageDTO);
            close(myWebSocket);
            throw new SystemException("当前连接数已达到最大连接数");
        }
        if (ONLINE_COUNT.get() >= MAX_CONNECT * DEFAULT_LOAD_FACTOR) {
            alarm(ONLINE_COUNT.get());
        }
        log.info("用户id 为: {} 上线了", myWebSocket.getUserId());
        WS_GROUP.put(myWebSocket.getUserId(), myWebSocket);
        ONLINE_COUNT.incrementAndGet();
        MessageDTO messageDTO = MessageBO2MessageDTO.getMessageDTO("欢迎", 3);
        sendText(myWebSocket, messageDTO);
    }

    /**
     * 下线
     */
    public static void offline(MyWebSocket myWebSocket) {
        checkMyWebSocket(myWebSocket);
        if (ONLINE_COUNT.get() <= 0) {
            MessageDTO messageDTO = MessageBO2MessageDTO.getMessageDTO("错误的下线请求", 3);
            sendText(myWebSocket, messageDTO);
            close(myWebSocket);
            throw new SystemException("错误的下线请求");
        }
        WS_GROUP.remove(myWebSocket.getUserId());
        ONLINE_COUNT.decrementAndGet();
    }

    /**
     * 发送给房间中的所有人
     */
    public static void msgToThisRoom(MyWebSocket myWebSocket, MessageBO messageBO) {
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

    public static void sendText(MyWebSocket myWebSocket, MessageDTO messageDTO) {
        if (Objects.isNull(myWebSocket)) {
            return;
        }
        myWebSocket.getSession().getAsyncRemote().sendText(JSONObject.toJSONString(messageDTO));
    }

    public static void sendText(Long userId, MessageDTO messageDTO) {
        MyWebSocket myWebSocket = WS_GROUP.get(userId);
        sendText(myWebSocket, messageDTO);
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
                || Objects.isNull(myWebSocket.getUserId())) {
            throw new BusinessException("MyWebSocket参数缺失");
        }
    }

    public static MyWebSocket getWebSocketByUserId(Long userId) {
        if (Objects.isNull(userId)) {
            throw new BusinessException("userId不能为空");
        }
        return WS_GROUP.get(userId);
    }

    public static boolean isOnline(Long userId) {
        if (Objects.isNull(userId)) {
            throw new BusinessException("userId不能为空");
        }
        return WS_GROUP.containsKey(userId);
    }

    public static void sendText(Set<Long> userIdSet, MessageDTO messageDTO, Long fromUserId) {
        if (CollectionUtils.isEmpty(userIdSet)) {
            return;
        }
        for (Long userId : userIdSet) {
            if (Objects.equals(userId, fromUserId)) {
                continue;
            }
            sendText(userId, messageDTO);
        }
    }
}