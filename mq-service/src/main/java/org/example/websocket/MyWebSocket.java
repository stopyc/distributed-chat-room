package org.example.websocket;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@ServerEndpoint(value = "/ws/{token}")
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

    /**
     * 鉴权, 统一token认证,并对字段进行赋值
     * * 建立连接,维护服务器中的hashMap
     */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam("token") String token) {
        //需要对同一个用户的上线下线请求进行同步处理，解决ws连接快速失败问题
        synchronized (monitor) {
            checkToken(token, session);
            UserBO userBO = null;
            try {
                userBO = JWTUtils.parseJWT2UserBo(token);
            } catch (BusinessException e) {
                throwError(session, e.getMessage());
                throw new BusinessException(e.getMessage());
            }
            this.userId = userBO.getUserId();
            this.session = session;
            this.userBO = userBO;
            //session.setMaxIdleTimeout(35);
            publisherUtil.userOnline(this, this);
        }
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
        WsMessageVO wsMessageVO = getWsMessageVO(message);
        publisherUtil.acceptMessage(this, wsMessageVO);
    }

    private void throwError(Session session, String message) {
        session.getAsyncRemote().sendText(message);
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException e) {
            throw new BusinessException(e.getMessage());
        }
        throw new BusinessException(message);
    }

    private WsMessageVO getWsMessageVO(String message) {
        WsMessageVO wsMessageVO = JSONObject.parseObject(message, WsMessageVO.class);
        wsMessageVO.setFromUserId(this.userId);
        wsMessageVO.setMyWebSocket(this);
        return wsMessageVO;
    }
}