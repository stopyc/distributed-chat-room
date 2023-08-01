package org.example.event_listener;

import lombok.extern.slf4j.Slf4j;
import org.example.adapter.MessageDTOAdapter;
import org.example.event.PushWsMessageEvent;
import org.example.pojo.dto.MessageDTO;
import org.example.pojo.vo.WsMessageVO;
import org.example.route.DownLinkMessageRoute;
import org.example.util.Assert;
import org.example.websocket.GlobalWsMap;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: util
 * @description: 用户接受ws消息并推送消息到队列的监听器
 * @author: stop.yc
 * @create: 2023-07-18 09:46
 **/
@Component
@Slf4j
public class PushWsMessageListener {

    @Resource
    private DownLinkMessageRoute downLinkMessageRoute;

    private static void pong(WsMessageVO wsMessageVO) {
        //心跳消息直接返回ack
        MessageDTO pong = MessageDTOAdapter.getBeatPong(wsMessageVO);
        GlobalWsMap.sendText(wsMessageVO.getFromUserId(), pong);
    }

    @Async
    @EventListener(classes = PushWsMessageEvent.class)
    public void handleEvent(PushWsMessageEvent pushWsMessageEvent) {
        //服务器收到消息,先判断这个消息是否之前已经收到了
        WsMessageVO wsMessageVO = pushWsMessageEvent.getWsMessageVO();
        Assert.assertNotNull(wsMessageVO.getFromUserId(), "消息发送者id不能为空!");
        //必要检测
        if (isBeat(wsMessageVO)) {
            pong(wsMessageVO);
            return;
        }
        wsMessageVO.validate();
        //下行消息推送
        downLinkMessageRoute.downLinkMessagePush(wsMessageVO);
    }

    private boolean isBeat(WsMessageVO wsMessageVO) {
        return wsMessageVO.getMessageType() == 4;
    }
}
