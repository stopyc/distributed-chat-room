package org.example.websocket;

import cn.hutool.extra.spring.SpringUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.bo.UserBO;
import org.example.pojo.exception.BusinessException;
import org.example.pojo.vo.WsMessageVO;
import org.example.util.JWTUtils;
import org.example.utils.PublisherUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;


/**
 * @program: chat-room
 * @description:
 * @author: stop.yc
 * @create: 2022-09-09 20:11
 **/
@ServerEndpoint(value = "/ws/{token}/{type}")
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
                       @PathParam("token") String token) {
        checkToken(token, session);
        UserBO userBO = JWTUtils.parseJWT2UserBo(token);
        this.userId = userBO.getUserId();
        this.session = session;
        this.userBO = userBO;
        publisherUtil.userOnline(this, this);
    }

    private void checkToken(String token, Session session) {
        if (StringUtils.isEmpty(token)) {
            throwError(session, "token不能为空");
        }
    }


    /**
     * 关闭连接, 维护hashMap
     */
    @OnClose
    public void onClose(Session session,
                        @PathParam("token") String token) throws IOException {
        publisherUtil.userOffline(this, this);
    }

    @OnError
    public void onError(Session session,
                        Throwable throwable,
                        @PathParam("token") String token) throws IOException {
        log.error("ws连接内部报错, 错误为 {}", throwable.getMessage());
    }

    @OnMessage
    public void onMessage(Session session,
                          String message,
                          @PathParam("token") String token,
                          @PathParam("type") Integer messageType) {
        WsMessageVO wsMessageVO = WsMessageVO.builder()
                .fromUserId(this.userId)
                .messageType(messageType)
                .message(message)
                .serverTime(System.currentTimeMillis())
                .build();
        publisherUtil.acceptMessage(this, wsMessageVO);
    }

    @OnMessage
    public void onBinaryMessage(byte[] message,
                                Session session,
                                @PathParam("token") String token,
                                @PathParam("type") Integer messageType) throws IOException {
        // 处理二进制消息
        WsMessageVO wsMessageVO = WsMessageVO.builder()
                .fromUserId(this.userId)
                .messageType(messageType)
                .binaryMessage(message)
                .serverTime(System.currentTimeMillis())
                .build();
        publisherUtil.acceptMessage(this, wsMessageVO);
    }


    private void throwError(Session session, String message) {
        session.getAsyncRemote().sendText(message);
        throw new BusinessException(message);
    }
}