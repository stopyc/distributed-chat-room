package org.example.event;

import lombok.Getter;
import org.example.pojo.vo.WsMessageVO;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

/**
 * @author YC104
 */
@Getter
public class PushWsMessageEvent extends ApplicationEvent {

    private final WsMessageVO wsMessageVO;

    private CompletableFuture<WsMessageVO> future;

    public PushWsMessageEvent(Object source, CompletableFuture<WsMessageVO> future, WsMessageVO wsMessageVO) {
        super(source);
        this.wsMessageVO = wsMessageVO;
        this.future = future;
    }

    public PushWsMessageEvent(Object source, WsMessageVO wsMessageVO) {
        super(source);
        this.wsMessageVO = wsMessageVO;
    }
}
