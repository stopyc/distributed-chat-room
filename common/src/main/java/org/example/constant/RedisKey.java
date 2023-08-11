package org.example.constant;

/**
 * 缓存中的键名
 *
 * @author : stop.yc
 * @date : 2022-08-12
 */
public class RedisKey {

    /**
     * 用户登录缓存
     */
    public static final String LOGOUT_KEY = "logout:";

    public static final String REQUEST_ID_PREFIX = "requestId:";

    public static final String MESSAGE_KEY = "message:";

    public static final String OK_MESSAGE_KEY = "okmessage:";

    public static final String ACK_MESSAGE_KEY = "ack:message:";

    public static final String SINGLE_CHAT = "singlechat:";

    public static final String GROUP_CHAT = "groupchat:";

    public static final String USER_ONLINE = "user:online:";

    public static final Long ACK_EXPIRATION_TIME = 1000L;

    public static final Long USER_STATUS_EXPIRATION_TIME = 10L;

    public static final String CHATROOM = "chatroom:";

    public static final String AT_KEY = "at:";

}
