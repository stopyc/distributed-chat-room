package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.dto.ResultDTO;
import org.example.pojo.po.ChatRoom;

import java.util.List;

/**
 * @author YC104
 */
public interface IChatRoomService extends IService<ChatRoom> {

    ResultDTO listUsersInChatRoom(String chatroomId);

    List<Long> getUserSetByChatRoomId(Long chatRoomId);
}