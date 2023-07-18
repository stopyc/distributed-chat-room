package org.example.websocket;

import cn.hutool.extra.spring.SpringUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.bo.UserBO;
import org.example.pojo.dto.ResultDTO;
import org.example.pojo.exception.BusinessException;
import org.example.util.JWTUtils;
import org.example.utils.PublisherUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;

import static org.example.constant.ResultEnum.SERVER_INTERNAL_ERROR;


/**
 * @program: chat-room
 * @description:
 * @author: stop.yc
 * @create: 2022-09-09 20:11
 **/
@ServerEndpoint(value = "/ws/{chatRoomId}/{token}")
@Slf4j
@Component
@EqualsAndHashCode(callSuper = false)
@Getter
public class MyWebSocket {

    private static PublisherUtil publisherUtil;

    static {
        MyWebSocket.publisherUtil = SpringUtil.getBean(PublisherUtil.class);
    }

    /**
     * 与客户端建立会话的session
     */
    private Session session;

    /**
     * 房间的id,以房间id进行分组
     */
    private String chatRoomId;

    /**
     * 该session所属的用户id
     */
    private Long userId;


    private UserBO userBO;

    /**
     * 鉴权, 统一token认证,并对字段进行赋值
     * * 建立连接,维护服务器中的hashMap
     */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam("chatRoomId") String chatRoomId,
                       @PathParam("token") String token) {
        checkTokenAndRoomId(token, chatRoomId, session);
        UserBO userBO = JWTUtils.parseJWT2UserBo(token);
        this.userId = userBO.getUserId();
        this.chatRoomId = chatRoomId;
        this.session = session;
        this.userBO = userBO;
        publisherUtil.userOnline(this, this);
    }

    private void checkTokenAndRoomId(String token, String chatRoomId, Session session) {
        if (StringUtils.isEmpty(token)) {
            throwError(session, "token不能为空");
        }
        if (StringUtils.isEmpty(chatRoomId)) {
            throwError(session, "chatRoomId不能为空");
        }
    }


    /**
     * 关闭连接, 维护hashMap
     */
    @OnClose
    public void onClose(Session session,
                        @PathParam("chatRoomId") String chatRoomId,
                        @PathParam("token") String token) throws IOException {

    }

    @OnError
    public void onError(Session session,
                        Throwable throwable,
                        @PathParam("chatRoomId") String chatRoomId,
                        @PathParam("token") String token) throws IOException {

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
                          String message,
                          @PathParam("chatRoomId") String chatRoomId,
                          @PathParam("token") String token) {
    }


    private void throwError(Session session, String message) {
        session.getAsyncRemote().sendText(message);
        throw new BusinessException(message);
    }
}