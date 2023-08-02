package org.example.service;

import org.example.pojo.dto.ResultDTO;

/**
 * @author YC104
 */
public interface IUserStatusService {
    ResultDTO getChatRoomUserStatus(Long chatRoomId);

    ResultDTO getSingleChatUserStatus(Long toUserId);
}
