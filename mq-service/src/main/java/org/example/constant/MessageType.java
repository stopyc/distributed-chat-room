package org.example.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YC104
 */

@Getter
@AllArgsConstructor
public enum MessageType {

    OFFSET_ACK(0, "offset ack"),

    BUSINESS_ACK(1, "業務ack"),

    REQUEST(2, "请求类型"),

    RESPONSE(3, "响应类型"),

    HEARTBEAT(4, "心跳类型"),

    DISCONNECT(5, "断开连接类型"),

    CHAT_ROOM(6, "群聊"),

    SINGLE_CHAT(7, "单聊"),

    TEXT(8, "文本"),

    VIDEO(9, "视频"),

    AUDIO(10, "音频"),

    file(11, "文件"),

    picture(12, "图片"),

    pat(13, "拍一拍"),

    AT(14, "艾特消息"),


    ;


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

    public static boolean isBusinessAck(Integer messageType) {
        return BUSINESS_ACK.messageType.equals(messageType);
    }

    public static boolean isText(Integer messageType) {
        return TEXT.messageType.equals(messageType);
    }


}
