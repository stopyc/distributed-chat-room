package org.example.websocket;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.bo.UserBO;
import org.example.pojo.dto.MessageDTO;
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
import java.net.URI;


/**
 * @program: chat-room
 * @description:
 * @author: stop.yc
 * @create: 2022-09-09 20:11
 **/
@ServerEndpoint(value = "/ws/{userId}/{token}")
@Slf4j
@Component
@EqualsAndHashCode(callSuper = false)
@Getter
@ToString
@Setter
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

    private String token;

    private final Object monitor = new Object();

    private final Object offsetInitLock = new Object();


    /**
     * 鉴权, 统一token认证,并对字段进行赋值
     * * 建立连接,维护服务器中的hashMap
     */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam("token") String token,
                       @PathParam("userId") String userId) {
        //需要对同一个用户的上线下线请求进行同步处理，解决ws连接快速失败问题
        synchronized (monitor) {
            this.session = session;
            checkToken(token, session);
            UserBO userBO = null;
            try {
                userBO = JWTUtils.parseJWT2UserBo(token);
            } catch (BusinessException e) {
                MessageDTO messageDTO = MessageDTO.builder()
                        .message("token已经过期了")
                        .messageType(-1)
                        .build();
                throwError(session, messageDTO);
                //throw new BusinessException("token已经过期了");
            }
            this.userId = userBO.getUserId();
            this.userBO = userBO;
            //session.setMaxIdleTimeout(35);
            publisherUtil.userOnline(this, this);
        }
    }

    private void checkToken(String token, Session session) {
        if (StringUtils.isEmpty(token)) {
            MessageDTO messageDTO = MessageDTO.builder()
                    .messageType(-1)
                    .message("token不能为空")
                    .build();
            throwError(session, messageDTO);
        }
    }


    /**
     * 关闭连接, 维护hashMap
     */
    @OnClose
    public void onClose(Session session,
                        @PathParam("token") String token) throws IOException {
        synchronized (monitor) {
            publisherUtil.userOffline(this, this);
        }
    }

    @OnError
    public void onError(Session session,
                        Throwable throwable,
                        @PathParam("token") String token) throws IOException {
        log.error("ws连接内部报错, 错误为 {}", throwable.getMessage());
        synchronized (monitor) {
            publisherUtil.userOffline(this, this);
        }
    }

    @OnMessage
    public void onMessage(Session session,
                          String message,
                          @PathParam("token") String token) {
        if (!StringUtils.hasText(message)) {
            return;
        }
        WsMessageVO wsMessageVO = null;
        try {
            log.info("message 为: {}", message);
            wsMessageVO = getWsMessageVO(message);
        } catch (Exception e) {
            log.warn("服務器接受ws消息json解析異常，message 为: {}", message);
            return;
        }
        publisherUtil.acceptMessage(this, wsMessageVO);
    }

    private void throwError(Session session, MessageDTO messageDTO) {
        try {
            if (session != null && session.isOpen()) {
                session.getAsyncRemote().sendText(JSONObject.toJSONString(messageDTO));
                String id = session.getId();
                URI requestURI = session.getRequestURI();
                log.info("requestURI 为: {}", requestURI);
                this.session = null;
                session.close();
            }
        } catch (Exception ignored) {
        }
        throw new BusinessException(messageDTO.getMessage());
    }

    private WsMessageVO getWsMessageVO(String message) {
        WsMessageVO wsMessageVO = JSONObject.parseObject(message, WsMessageVO.class);
        wsMessageVO.setFromUserId(this.userId);
        wsMessageVO.setMyWebSocket(this);
        return wsMessageVO;
    }

    @OnMessage
    public void onBinary(Session session, byte[] bytes) {
        log.info("客户端接收的二进制流为 为: ");
        for (byte b : bytes) {
            System.out.print(b);
        }
    }
}