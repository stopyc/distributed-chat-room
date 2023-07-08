package org.example.constant;

/**
 * 缓存中的键名
 * @author : Ice'Clean
 * @date : 2022-08-12
 */
public class RedisKey {

    /** 用户登录缓存 */
    public static final String LOGOUT_KEY = "logout:";


    public static final String REQUEST_ID_PREFIX = "requestId:";

    /** 房间号key  */
    public static final String PREFIX_CHAT_ROOM = "room:";

    /** 聊天室在线  */
    public static final String PREFIX_ONLINE = "online:";

    /** 聊天记录  */
    public static final String PREFIX_CHAT_CONTENT = "content:";

    /** 阅读剧本 */
    public static final String PREFIX_SCRIPT = "script:";

    /**
     * 幕次
     */
    public static final String PREFIX_SCENE = "scene:";

    /**
     * 脏话词典
     */
    public static final String PREFIX_BAD_LANGUAGE = "bad:";

    public static String getKey(String prefix, Object chatRoomId, Object userId, Object scriptId, Object gameTime) {
        return prefix + chatRoomId + ":" + userId + ":" + scriptId + ":" + gameTime + ":";
    }

}
