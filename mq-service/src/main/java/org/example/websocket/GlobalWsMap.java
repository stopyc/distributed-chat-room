package org.example.websocket;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.RedisKey;
import org.example.pojo.dto.UserChatDTO;
import org.example.pojo.exception.BusinessException;
import org.example.pojo.exception.SystemException;
import org.example.pojo.vo.WsMessageVO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import static org.example.constant.RedisKey.*;


/**
 * @author YC104
 */
@Slf4j
public class GlobalWsMap {

    public static final ConcurrentHashMap<String, CopyOnWriteArraySet<MyWebSocket>> wsGroup;

    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    private static final Integer MAX_CONNECT = 300;

    static {
        wsGroup = new ConcurrentHashMap<>(MAX_CONNECT);
    }

    /**
     * 发送给房间中的所有人
     */
    public static void msgToThisRoom(MyWebSocket myWebSocket, WsMessageVO wsMessageVO) {
        //1. 获取房间key
        String chatRoomKey = getChatRoomKey(myWebSocket);

        //2. 获取房间所有用户会话
        CopyOnWriteArraySet<MyWebSocket> myWebSockets = wsGroup.get(chatRoomKey);

        //3. 遍历,发送消息
        for (MyWebSocket webSocket : myWebSockets) {
            webSocket.getSession().getAsyncRemote().sendText(JSONObject.toJSONString(wsMessageVO));
        }
    }

    /**
     * 获取该服务器中维护的所有用户连接情况
     */
    public static ConcurrentHashMap<String, CopyOnWriteArraySet<UserChatDTO>> getAllOnline() {
        //1. 获取该服务器中所维护的map对象
        Set<Map.Entry<String, CopyOnWriteArraySet<MyWebSocket>>> entrySet = wsGroup.entrySet();

        ConcurrentHashMap<String, CopyOnWriteArraySet<UserChatDTO>> userChatMap = new ConcurrentHashMap<>(wsGroup.size());

        //2. 遍历所有key,把key进行解析,分别解析为:房间id、房主id、剧本id、需要时间、
        for (Map.Entry<String, CopyOnWriteArraySet<MyWebSocket>> entry : entrySet) {

            //2.1 获取房间key
            String chatRoomKey = entry.getKey();

            //2.2 获取房间id
            String chatRoomId = getChatRoomIdByChatRoomKey(chatRoomKey);

            //2.3 获取房间所有用户
            CopyOnWriteArraySet<UserChatDTO> userChatDTOCopyOnWriteArraySet = userChatMap.get(chatRoomId);

            //2.4 如果该房间为空
            if (userChatDTOCopyOnWriteArraySet == null) {
                //2.4.1 初始化
                userChatDTOCopyOnWriteArraySet = new CopyOnWriteArraySet<>();

                //2.4.2 添加房间
                userChatMap.put(chatRoomId, userChatDTOCopyOnWriteArraySet);
            }

            UserChatDTO userChatDTO;

            //2.5. 通过房间id进行分组,对同一个房间中的用户,放置在同一个容器中,进行展示用户id
            for (MyWebSocket myWebSocket : entry.getValue()) {
                userChatDTO = UserChatDTO.builder()
                        .userId(Long.parseLong(myWebSocket.getUserId()))
                        .build();
                //2.5 添加用户
                userChatDTOCopyOnWriteArraySet.add(userChatDTO);
            }
        }
        return userChatMap;
    }

    /**
     * 上线
     */
    public static void online(MyWebSocket myWebSocket) {

        if (wsGroup.size() > MAX_CONNECT) {
            throw new SystemException("系统连接数以达到上限, 请稍后再试");
        }

        //1. 获取在线key
        String chatRoomKey = getChatRoomKey(myWebSocket);

        //2. 获取房间中的所有在线用户
        CopyOnWriteArraySet<MyWebSocket> websocketSet = wsGroup.get(chatRoomKey);

        //3. 如果为空,表示没有该房间
        if (Objects.isNull(websocketSet)) {
            //3.1 初始化房间
            websocketSet = new CopyOnWriteArraySet<>();

            //3.2 添加房间
            wsGroup.put(chatRoomKey, websocketSet);
        }

        //4. 添加用户
        websocketSet.add(myWebSocket);

        //5. 在线人数+1
        int onlineCount = ONLINE_COUNT.incrementAndGet();

        log.info("用户id为 {} 的用户上线了, 当前在线用户人数为: {}", myWebSocket.getUserId(), onlineCount);
        log.info("当前在线的房间数量为: {}", wsGroup.size());
        log.info("该服务器的房间拓扑图为: \t\n" + com.alibaba.fastjson.JSONObject.toJSONString(getAllOnline(), true));
    }

    /**
     * 下线
     */
    public static void leave(MyWebSocket myWebSocket) {
        //1. 获取在线key
        String chatRoomKey = getChatRoomKey(myWebSocket);
        //2. 获取房间中的所有用户连接
        CopyOnWriteArraySet<MyWebSocket> websocketSet = wsGroup.get(chatRoomKey);
        //3. 找到对应的user的会话
        if (!CollectionUtils.isEmpty(websocketSet)) {
            //4. 进行移除
            websocketSet.remove(myWebSocket);
            //4.1 如果房间中的人全部离开,则删除房间
            if (websocketSet.size() == 0) {
                wsGroup.remove(chatRoomKey);
            }
            //5. 在线人数-1
            int onlineCount = ONLINE_COUNT.decrementAndGet();
            log.info("用户id为 {} 的用户下线了, 当前在线用户人数为: {}", myWebSocket.getUserId(), onlineCount);
            log.info("当前在线的房间数量为: {}", wsGroup.size());
        } else {
            throw new BusinessException("找不到对应的ws会话");
        }
    }

    /**
     * 通过session获取用户在线的key
     */
    private static String getOnlineKey(MyWebSocket myWebSocket) {

        //1. 校验参数
        checkWebsocketParam(myWebSocket);

        //2. 获取key
        return getOnlineKey(PREFIX_ONLINE, myWebSocket);
    }

    /**
     * 通过前缀和session获取在线的key
     */
    private static String getOnlineKey(String prefixOnline, MyWebSocket myWebSocket) {
        String chatRoomId = myWebSocket.getChatRoomId();
        String userId = myWebSocket.getUserId();
        String scriptId = myWebSocket.getScriptId();
        String needTime = myWebSocket.getNeedTime();
        return getKey(prefixOnline, chatRoomId, userId, scriptId, needTime);
    }

    /**
     * 通过session获取房间的key,一般为redis
     */
    private static String getChatRoomKey(MyWebSocket myWebSocket)  {

        //1. 校验参数
        checkWebsocketParam(myWebSocket);

        //2. 获取key
        return getChatRoomKey(PREFIX_CHAT_ROOM, myWebSocket);
    }

    /**
     * 校验session中的参数是否合法
     */
    private static void checkWebsocketParam(MyWebSocket myWebSocket)  {
        String chatRoomId = myWebSocket.getChatRoomId();
        String roomOwnerId = myWebSocket.getRoomOwnerId();
        String scriptId = myWebSocket.getScriptId();
        String needTime = myWebSocket.getNeedTime();

        //1. 校验参数
        if (StringUtils.isEmpty(chatRoomId) ||
                StringUtils.isEmpty(roomOwnerId) ||
                StringUtils.isEmpty(scriptId) ||
                StringUtils.isEmpty(needTime)) {

            WsMessageVO vo = WsMessageVO.builder()
                    .messageType(-1)
                    .message("错误的参数")
                    .build();
            try {
                myWebSocket.getSession().getBasicRemote().sendText(JSONObject.toJSONString(vo));
                myWebSocket.getSession().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new BusinessException("错误的参数");
        }
    }

    /**
     * 通过参数获取key
     */
    private static String getKey(String prefix, Object chatRoomId, Object userId, Object scriptId, Object needTime) {
        return RedisKey.getKey(prefix, chatRoomId, userId, scriptId, needTime);
    }

    /**
     * 通过前缀和session获取房间key
     */
    private static String getChatRoomKey(String prefix, MyWebSocket myWebSocket) {
        String chatRoomId = myWebSocket.getChatRoomId();
        String roomOwnerId = myWebSocket.getRoomOwnerId();
        String scriptId = myWebSocket.getScriptId();
        String needTime = myWebSocket.getNeedTime();
        return getKey(prefix, chatRoomId, roomOwnerId, scriptId, needTime);
    }

    /**
     * 通过房间key获取房间id
     */
    private static String getChatRoomIdByChatRoomKey(String chatRoomKey) {

        String[] split = checkKeyFormat(chatRoomKey);

        return split[1];
    }

    /**
     * 通过房间key获取房主id
     */
    private static String getRoomOwnerIdByChatRoomKey(String chatRoomKey) {

        String[] split = checkKeyFormat(chatRoomKey);

        return split[2];
    }

    /**
     * 通过房间key获取剧本id
     */
    private static String getScriptIdByChatRoomKey(String chatRoomKey) {

        String[] split = checkKeyFormat(chatRoomKey);

        return split[3];
    }

    /**
     * 通过房间key获取推荐游戏时间
     */
    private static String getNeedTimeByChatRoomKey(String chatRoomKey) {

        String[] split = checkKeyFormat(chatRoomKey);

        return split[4];
    }

    /**
     * 检查key是否正确
     */
    private static String[] checkKeyFormat(String key) {

        if (!StringUtils.hasText(key)) {
            throw new BusinessException("不能为空");
        }

        String[] split = key.split(":");
        if (split.length != 5) {
            throw new BusinessException("格式错误");
        }

        return split;
    }
}