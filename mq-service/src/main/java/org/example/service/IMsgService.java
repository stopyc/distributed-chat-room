package org.example.service;

import org.example.pojo.dto.MessageDTO;
import org.example.pojo.dto.ScrollingPaginationDTO;
import org.example.pojo.vo.ScrollingPaginationVO;

/**
 * @author YC104
 */
public interface IMsgService {
    ScrollingPaginationDTO<MessageDTO> getMsg(ScrollingPaginationVO scrollingPaginationVO);
}
