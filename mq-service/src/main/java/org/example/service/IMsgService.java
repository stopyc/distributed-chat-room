package org.example.service;

import org.example.pojo.dto.MessageDTO;
import org.example.pojo.dto.ScrollingPaginationDTO;
import org.example.pojo.vo.ChatRoomScrollVO;
import org.example.pojo.vo.SingleScrollVO;

/**
 * @author YC104
 */
public interface IMsgService {
    /**
     * 获取群聊消息
     *
     * @param chatRoomScrollVO：群聊消息VO
     * @return： 消息分页对象
     */
    ScrollingPaginationDTO<MessageDTO> getChatRoomMsg(ChatRoomScrollVO chatRoomScrollVO);

    /**
     * 获取单聊消息
     *
     * @param singleScrollVO：单聊消息VO
     * @return： 消息分页对象
     */
    ScrollingPaginationDTO<MessageDTO> getSingleMsg(SingleScrollVO singleScrollVO);
}
