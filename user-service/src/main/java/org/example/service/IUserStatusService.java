package org.example.service;

import org.example.pojo.dto.ResultDTO;
import org.example.pojo.dto.UserStatusDTO;

import java.util.Collection;

/**
 * @author YC104
 */
public interface IUserStatusService {
    ResultDTO getChatRoomUserStatus(Long chatRoomId);

    ResultDTO getSingleChatUserStatus(Long toUserId);

    Collection<UserStatusDTO> getChatRoomUserStatusCollection(Long chatRoomId);
}
