package org.example.websocket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.adapter.MessageDTOAdapter;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.MessageDTO;
import org.example.pojo.exception.BusinessException;
import org.example.pojo.exception.SystemException;
import org.springframework.util.CollectionUtils;

import javax.websocket.Session;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author YC104
 */
@Slf4j
public class GlobalWsMap {

    public static final ConcurrentHashMap<Long, MyWebSocket> WS_GROUP;


    private static final Integer MAX_CONNECT = 1000;


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
        onlineInterval(myWebSocket);
    }

    private static void onlineInterval(MyWebSocket myWebSocket) {
        checkMyWebSocket(myWebSocket);

        if (WS_GROUP.size() >= MAX_CONNECT) {
            MessageDTO messageDTO = MessageDTOAdapter.getMessageDTO("当前连接数已达到最大连接数, 请稍后再试", 3);
            sendText(myWebSocket, messageDTO);
            close(myWebSocket);
            throw new SystemException("当前连接数已达到最大连接数");
        }
        if (WS_GROUP.size() >= MAX_CONNECT * DEFAULT_LOAD_FACTOR) {
            alarm(WS_GROUP.size());
        }
        log.info("用户id 为: {} 上线了", myWebSocket.getUserId());
        WS_GROUP.put(myWebSocket.getUserId(), myWebSocket);
        MessageDTO messageDTO = MessageDTOAdapter.getMessageDTO("欢迎", 3);
        sendText(myWebSocket, messageDTO);
        log.info("map当前在线人数 为: {}", WS_GROUP.size());
    }

    /**
     * 下线
     */
    public static void offline(MyWebSocket myWebSocket) {
        offlineInterval(myWebSocket);
    }

    private static void offlineInterval(MyWebSocket myWebSocket) {
        try {
            checkMyWebSocket(myWebSocket);
            if (WS_GROUP.size() == 0) {
                close(myWebSocket);
                log.warn("错误的下线请求");
            }
        } catch (Exception e) {
            log.warn("下线的参数错误");
            close(myWebSocket);
        } finally {
            if (myWebSocket.getUserId() != null) {
                MyWebSocket remove = WS_GROUP.remove(myWebSocket.getUserId());
                if (remove != null) {
                    log.info("用户id 为: {} 下线了", myWebSocket.getUserId());
                    log.info("map当前在线人数 为: {}", WS_GROUP.size());
                }
            }
        }
    }

    /**
     * 发送给房间中的所有人
     */
    public static void msgToThisRoom(MyWebSocket myWebSocket, MessageBO messageBO) {
    }

    /**
     * 获取该服务器中维护的所有用户连接情况
     */
    public static ConcurrentHashMap<Long, MyWebSocket> getAllOnline() {
        return WS_GROUP;
    }

    private static void alarm(int onlineCount) {
        log.warn("当前连接数已达到最大连接数的 {} %,当前连接数:{}", DEFAULT_LOAD_FACTOR * 100, onlineCount);
        //...通知服务
    }


    public static boolean sendText(MyWebSocket myWebSocket, MessageDTO messageDTO) {
        if (Objects.isNull(myWebSocket)) {
            return false;
        }
        synchronized (myWebSocket.getMonitor()) {
            if (myWebSocket.getSession().isOpen()) {
                try {
                    myWebSocket.getSession().getBasicRemote().sendText(JSONObject.toJSONString(messageDTO));
                    return true;
                } catch (Exception e) {
                    log.error("发送消息失败", e);
                    return false;
                }
            }
        }
        return false;
    }


    public static boolean sendText(Long userId, MessageDTO messageDTO) {
        MyWebSocket myWebSocket = WS_GROUP.get(userId);
        return sendText(myWebSocket, messageDTO);
    }

    private static void close(MyWebSocket myWebSocket) {
        if (myWebSocket == null) {
            return;
        }
        close(myWebSocket.getSession());
    }

    private static void close(Session session) {
        if (session == null || !session.isOpen()) {
            return;
        }
        try {
            session.close();
        } catch (Exception ignored) {
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

    public static boolean sendText(Collection<Long> userIdSet, MessageDTO messageDTO, Long fromUserId) {
        if (CollectionUtils.isEmpty(userIdSet)) {
            return true;
        }
        for (Long userId : userIdSet) {
            if (Objects.equals(userId, fromUserId)) {
                continue;
            }
            return sendText(userId, messageDTO);
        }
        return false;
    }
}