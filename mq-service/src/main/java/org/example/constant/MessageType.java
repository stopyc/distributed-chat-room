package org.example.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YC104
 */

@Getter
@AllArgsConstructor
public enum MessageType {

    BROADCAST(0, "广播类型"),

    UNICAST(1, "单播类型"),

    REQUEST(2, "请求类型"),

    RESPONSE(3, "响应类型"),

    HEARTBEAT(4, "心跳类型"),

    DISCONNECT(5, "断开连接类型"),

    CHAT_ROOM(6, "群聊"),

    SINGLE_CHAT(7, "单聊");

    private final Integer messageType;

    private final String messageTypeName;

    public static boolean isBeat(Integer messageType) {
        return HEARTBEAT.messageType.equals(messageType);
    }

    public static boolean isChatGroup(Integer messageType) {
        return CHAT_ROOM.messageType.equals(messageType);
    }

    public static boolean isSingleChat(Integer messageType) {
        return SINGLE_CHAT.messageType.equals(messageType);
    }

    public static boolean isUnicast(Integer messageType) {
        return UNICAST.messageType.equals(messageType);
    }
}
