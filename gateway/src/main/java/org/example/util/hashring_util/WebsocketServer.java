package org.example.util.hashring_util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * websocket哈希环接口
 * @author YC104
 */
@Component
@Qualifier("websocketServer")
public class WebsocketServer implements Server {

    private static final String SERVER_NAME = "ws-service";

    @Override
    public String getServername() {
        return SERVER_NAME;
    }
}
