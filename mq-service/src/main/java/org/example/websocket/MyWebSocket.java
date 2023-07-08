package org.example.websocket;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.dto.ResultDTO;
import org.example.pojo.dto.UserChatDTO;
import org.example.pojo.vo.WsMessageVO;
import org.example.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import static org.example.constant.ResultEnum.SERVER_INTERNAL_ERROR;


/**
 * @program: chat-room
 * @description:
 * @author: stop.yc
 * @create: 2022-09-09 20:11
 **/
@ServerEndpoint("/ws/{chatRoomId}/{roomOwnerId}/{scriptId}/{needTime}/{userId}")
//@ServerEndpoint("/ws")
@Slf4j
@Component
@EqualsAndHashCode
public class MyWebSocket {

    /**
     * 与客户端建立会话的session
     */
    private Session session;

    /**
     * 房间的id,以房间id进行分组
     */
    private String chatRoomId;

    /**
     * 房主id
     */
    private String roomOwnerId;

    /**
     * 剧本id
     */
    private String scriptId;

    /**
     * 剧本推荐时间
     */
    private String needTime;

    /**
     * 该session所属的用户id
     */
    private String userId;


    /**
     * 鉴权, 统一token认证,并对字段进行赋值
     * * 建立连接,维护服务器中的hashMap
     */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam("chatRoomId") String chatRoomId,
                       @PathParam("roomOwnerId") String roomOwnerId,
                       @PathParam("scriptId") String scriptId,
                       @PathParam("needTime") String needTime,
                       @PathParam("userId") String userId) throws IOException {

        construct(session, chatRoomId, roomOwnerId, scriptId, needTime, userId);

        GlobalWsMap.online(this);
    }


    /**
     * 关闭连接, 维护hashMap
     */
    @OnClose
    public void onClose(Session session,
                        @PathParam("chatRoomId") String chatRoomId,
                        @PathParam("roomOwnerId") String roomOwnerId,
                        @PathParam("scriptId") String scriptId,
                        @PathParam("needTime") String needTime,
                        @PathParam("userId") String userId) throws IOException {
        //关闭连接
        //session.getBasicRemote().sendText("您已离开了房间~");
        session.close();
        GlobalWsMap.leave(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {

        if (!Objects.isNull(session)) {
            if (session.isOpen()) {
                ResultDTO result = ResultDTO.fail(SERVER_INTERNAL_ERROR, throwable.getMessage());
                session.getBasicRemote().sendText(com.alibaba.fastjson2.JSONObject.toJSONString(result));
                session.close();
            }
        }
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(Session session,
                          String message) {

        WsMessageVO wsMessageVO = null;
        try {
            wsMessageVO = JSONObject.parseObject(message, WsMessageVO.class);
        } catch (JSONException e) {
            log.warn("ws中传来的消息json序列化错误");
            session.getAsyncRemote().sendText("您发送的消息格式有误,请重传");
            return;
        }
        GlobalWsMap.msgToThisRoom(this, wsMessageVO);
        //客户端收到消息,需要对其进行转发
        log.info("客户端发送的消息为: \t\n" + message);
    }

    public Session getSession() {
        return session;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public String getRoomOwnerId() {
        return roomOwnerId;
    }

    public String getScriptId() {
        return scriptId;
    }

    public String getNeedTime() {
        return needTime;
    }

    public String getUserId() {
        return userId;
    }

    private void construct(Session session, String chatRoomId, String roomOwnerId, String scriptId, String needTime, String userId) {
        this.chatRoomId = chatRoomId;
        this.roomOwnerId = roomOwnerId;
        this.scriptId = scriptId;
        this.needTime = needTime;
        this.userId = userId;
        this.session = session;
    }
}