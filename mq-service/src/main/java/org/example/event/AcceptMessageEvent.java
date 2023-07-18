package org.example.event;

import lombok.Getter;
import org.example.pojo.vo.WsMessageVO;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

/**
 * @author YC104
 */
@Getter
public class AcceptMessageEvent extends ApplicationEvent {

    private final WsMessageVO wsMessageVO;

    private CompletableFuture<WsMessageVO> future;

    public AcceptMessageEvent(Object source, CompletableFuture<WsMessageVO> future, WsMessageVO wsMessageVO) {
        super(source);
        this.wsMessageVO = wsMessageVO;
        this.future = future;
    }

    public AcceptMessageEvent(Object source, WsMessageVO wsMessageVO) {
        super(source);
        this.wsMessageVO = wsMessageVO;
    }
}
