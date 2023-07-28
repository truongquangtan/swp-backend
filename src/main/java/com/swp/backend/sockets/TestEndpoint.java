package com.swp.backend.sockets;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/ws/admin-notification")
public class TestEndpoint {
    private Session session;
    private static Set<String> sessionIds = new CopyOnWriteArraySet<>();
    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        sessionIds.add(session.getId());
        String message = "Get message";
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        //broadcast
    }

    @OnClose
    public void onClose(Session session) throws IOException {

    }
    @OnError
    public void onError(Session session, Throwable throwable){

    }

}
