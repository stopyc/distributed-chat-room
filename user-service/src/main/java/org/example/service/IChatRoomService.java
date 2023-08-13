package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.dto.ResultDTO;
import org.example.pojo.po.ChatRoom;

/**
 * @author YC104
 */
public interface IChatRoomService extends IService<ChatRoom> {

    ResultDTO listUsersInChatRoom(String chatroomId);
}