package org.example.route;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.adapter.MessageDTOAdapter;
import org.example.constant.RedisKey;
import org.example.dao.MsgReader;
import org.example.dao.MsgWriter;
import org.example.factory.MessageFactory;
import org.example.mq.push.MqWriter;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.MessageDTO;
import org.example.pojo.vo.WsMessageVO;
import org.example.websocket.GlobalWsMap;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: util
 * @description: 消息接收缓冲区
 * @author: stop.yc
 * @create: 2023-08-03 15:20
 **/
@Component
@Slf4j
@SuppressWarnings("all")
public class MessageBuffer {

    private static final Map<Long, Long> offsetMap = new ConcurrentHashMap<>(600);

    private static final long BUFFER_SIZE = 50;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private MsgWriter msgWriter;

    @Resource
    private MqWriter mqWriter;

    @Resource
    private MsgReader msgReader;

    private MessageBuffer me() {
        return applicationContext.getBean(MessageBuffer.class);
    }

    public void handleMsg(WsMessageVO wsMessageVO) {
        log.info("消息接收缓冲区开始处理用户id为：{} 的消息，消息id为： {}", wsMessageVO.getFromUserId(), wsMessageVO.getClientMessageId());
        msgSlidingWindow(wsMessageVO);
    }

    private void msgSlidingWindow(WsMessageVO wsMessageVO) {
        Long clientMessageId = wsMessageVO.getClientMessageId();
        //1. 获取offset，offset表示服务器已经收到有序消息的最大值，不代表已经收到的值。
        OffsetPair offsetPair = getOffset(wsMessageVO);

        // 注意：不需要重复，直接保存redis，通过hash进行去重，无需手动抛弃，

        // 消息正确顺序接收的偏移量
        Long offsetAck;

        //2. 需要判断是否溢出缓冲区，
        //  如果溢出，需要返回offset ack, 让客户端重发，直到客户端消息接收，并移动滑动窗口
        if (clientMessageId - offsetPair.getOffset() > BUFFER_SIZE) {
            log.warn("用户id 为 {} 发送的消息id为 {} 超出缓存区，直接抛弃", wsMessageVO.getFromUserId(), clientMessageId);
            offsetAck = offsetPair.getOffset();
        }
        // 如果没有溢出，则需要保存，并返回offset ack
        else {
            //3. 如果没有溢出，直接保存，去重工作通过hash去做，保存的时候，需要看一下消息是否有序到达
            msgWriter.saveDurableMsg(wsMessageVO);
            //  如果有序到达，则需要把后面的所有消息发送出去，并更新offset的值，同时把所有的消息填入ack 队列中,并返回最后一个offset ack
            if (clientMessageId == offsetPair.getOffset() + 1) {
                log.info("用户id 为 {} 发送的消息id为 {} 有序到达，开始发送消息", wsMessageVO.getFromUserId(), clientMessageId);
                offsetAck = getLastOffset(offsetPair.getMsgList(), wsMessageVO);
                log.info("消息id 为{} 的消息有序到达，把后面的消息全部进行推送，最新的offset为 {}", wsMessageVO.getClientMessageId(), offsetAck);
                me().push2Mq(offsetPair.getMsgList(), wsMessageVO, offsetAck);
                // 更新offset
                offsetMap.put(wsMessageVO.getFromUserId(), offsetAck);
            } else if (clientMessageId <= offsetPair.getOffset()) {
                log.warn("注意下游服务是否阻塞，用户id 为 {} 发送的消息id为 {} 重复消息，正在从缓存中取出消息，并继续放入ack队列中", wsMessageVO.getFromUserId(), clientMessageId);
                // 获取ok持久队列中的消息
                MessageBO durableMsgByScore = msgReader.getDurableMsgByScore(RedisKey.OK_MESSAGE_KEY, wsMessageVO.getFromUserId(), wsMessageVO.getClientMessageId(), MessageBO.class);
                offsetAck = offsetPair.getOffset();
                mqWriter.pushWsMsg2Mq(durableMsgByScore);
                //继续放入Redis的Ack队列，等待key 超时重试即可。
                msgWriter.saveAckMsg(durableMsgByScore);
            }
            //  如果无序到达，则直接保存即可，并返回offset ack
            else {
                offsetAck = offsetPair.getOffset();
            }
        }

        // offset ack
        MessageBO messageBO = BeanUtil.copyProperties(wsMessageVO, MessageBO.class);
        log.info("设置offset ack 为: {}, 并发送给用户id 为{}", offsetAck, messageBO.getFromUserId());
        messageBO.setClientMessageId(offsetAck);
        MessageDTO messageAck = MessageDTOAdapter.getMessageAck(messageBO);
        GlobalWsMap.sendText(messageBO.getFromUserId(), messageAck);
    }

    @Async
    void push2Mq(List<ZSetOperations.TypedTuple<Object>> msgList, WsMessageVO wsMessageVO, long offsetAck) {
        log.info("开始异步推送消息到mq");
        //生成封装消息传输对象
        MessageBO messageBO = MessageFactory.generateMessageVo(wsMessageVO);

        log.info("推送用户id 为 {} 的消息id 为{} 的消息到mq", wsMessageVO.getFromUserId(), wsMessageVO.getClientMessageId());
        pushAndSave(messageBO);
        if (CollectionUtils.isEmpty(msgList)) {
            getWindowMsg(msgList, wsMessageVO);
        }
        for (ZSetOperations.TypedTuple<Object> tuple : msgList) {
            //排除最新的
            if (tuple == null || tuple.getScore().doubleValue() <= wsMessageVO.getClientMessageId()) {
                continue;
            }
            if (tuple.getScore().doubleValue() > offsetAck) {
                break;
            }
            Object jsonString = tuple.getValue();
            wsMessageVO = JSONObject.parseObject(jsonString.toString(), WsMessageVO.class);
            messageBO = MessageFactory.generateMessageVo(wsMessageVO);
            log.info("推送用户id 为 {} 的消息id 为{} 的消息到mq", wsMessageVO.getFromUserId(), wsMessageVO.getClientMessageId());
            pushAndSave(messageBO);
        }
    }

    private void pushAndSave(MessageBO messageBO) {
        msgWriter.saveDurableMsg(messageBO);
        //Redis的Ack队列
        msgWriter.saveAckMsg(messageBO);
        mqWriter.pushWsMsg2Mq(messageBO);
    }


    private OffsetPair getOffset(WsMessageVO wsMessageVO) {
        Long offset = offsetMap.get(wsMessageVO.getFromUserId());
        List<ZSetOperations.TypedTuple<Object>> list = new ArrayList<>((int) BUFFER_SIZE);
        //如果offset为空，可能是服务刚重启或者新启动或者是其他服务器宕机，用户落点在这台服务器，需要从redis中获取
        if (offset == null) {
            synchronized (wsMessageVO.getMyWebSocket().getOffsetInitLock()) {
                offset = offsetMap.get(wsMessageVO.getFromUserId());
                if (offset == null) {
                    //这是从最新的消息中获取20条消息
                    Set<ZSetOperations.TypedTuple<Object>> zget = msgReader.getWindowMsg(RedisKey.MESSAGE_KEY, wsMessageVO.getFromUserId(), Long.MAX_VALUE, 0, BUFFER_SIZE, WsMessageVO.class);
                    if (zget == null || zget.size() == 0) {
                        //如果redis中没有消息，直接返回0，定时的线程会定期把redis中的数据部分写入mysql，
                        //redis中的数据会保留部分。所以会保证redis中只有第一次的用户才会记录会空。
                        offset = 0L;
                        offsetMap.put(wsMessageVO.getFromUserId(), offset);
                        return new OffsetPair(offset, list);
                    }
                    //倒序调用，因为redis中的数据是按照时间戳排序的，所以倒序调用，可以保证消息的顺序
                    list = new ArrayList<>((int) BUFFER_SIZE);
                    list.addAll(zget);
                    Collections.reverse(list);
                    log.info("线程 {} 从redis中获取消息", Thread.currentThread().getName());
                    //保证消息接收的顺序，1 2 3 4
                    double pre = list.get(0).getScore();
                    //如果消息不足20条，说明是第一次接收消息，pre需要从0开始，所以才能0 1 2 保证顺序
                    int startIndex = 1;
                    if (list.size() < BUFFER_SIZE) {
                        pre = 0;
                        startIndex = 0;
                    }
                    double cur;

                    for (int i = startIndex, n = list.size(); i < n; ++i) {
                        cur = list.get(i).getScore();
                        //判断是否有序
                        if (cur - pre != 1) {
                            offset = (long) pre;
                            break;
                        }
                        pre = cur;
                        offset = (long) pre;
                    }
                    offsetMap.put(wsMessageVO.getFromUserId(), offset);
                }
            }
        }
        return new OffsetPair(offset, list);
    }

    private Long getLastOffset(List<ZSetOperations.TypedTuple<Object>> msgList, WsMessageVO wsMessageVO) {
        Long offset = wsMessageVO.getClientMessageId();
        if (CollectionUtils.isEmpty(msgList)) {
            getWindowMsg(msgList, wsMessageVO);
        }
        if (CollectionUtils.isEmpty(msgList)) {
            return offset;
        }
        double pre = msgList.get(0).getScore();
        //如果消息不足20条，说明是第一次接收消息，pre需要从0开始，所以才能0 1 2 保证顺序
        int startIndex = 1;
        if (msgList.size() < BUFFER_SIZE) {
            pre = 0;
            startIndex = 0;
        }
        double cur;
        for (int i = startIndex, n = msgList.size(); i < n; ++i) {
            cur = msgList.get(i).getScore();
            //判断是否有序
            if (cur - pre != 1) {
                offset = (long) pre;
                break;
            }
            pre = cur;
            offset = (long) pre;
        }
        return offset;
    }

    private void getWindowMsg(List<ZSetOperations.TypedTuple<Object>> msgList, WsMessageVO wsMessageVO) {
        Set<ZSetOperations.TypedTuple<Object>> zget = msgReader.getWindowMsg(RedisKey.MESSAGE_KEY, wsMessageVO.getFromUserId(), Long.MAX_VALUE, 0, BUFFER_SIZE, WsMessageVO.class);
        if (zget == null || zget.size() == 0) {
            return;
        }
        msgList.addAll(zget);
        Collections.reverse(msgList);
    }

    private class OffsetPair {
        private Long offset;
        private List<ZSetOperations.TypedTuple<Object>> msgList;

        public OffsetPair(Long offset, List<ZSetOperations.TypedTuple<Object>> msgList) {
            this.offset = offset;
            this.msgList = msgList;
        }

        public Long getOffset() {
            return offset;
        }

        public List<ZSetOperations.TypedTuple<Object>> getMsgList() {
            return msgList;
        }
    }
}

