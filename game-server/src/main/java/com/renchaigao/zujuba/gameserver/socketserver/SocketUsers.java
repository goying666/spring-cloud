package com.renchaigao.zujuba.gameserver.socketserver;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.PlatformDependent;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

@Getter
@Setter
public class SocketUsers {


    private static final ConcurrentMap<String, Channel> USERS = PlatformDependent.newConcurrentHashMap();

    private static SocketUsers ourInstance = new SocketUsers();

    private SocketUsers() {
    }

    public static SocketUsers getInstance() {
        return ourInstance;
    }

    public static void put(String key, Channel channel) {
        USERS.put(key, channel);
    }

    public static boolean remove(Channel channel) {
        String key = null;
        boolean b = USERS.containsValue(channel);
        if (b) {
            Set<Map.Entry<String, Channel>> entries = USERS.entrySet();
            for (Map.Entry<String, Channel> entry : entries) {
                Channel value = entry.getValue();
                if (value.equals(channel)) {
                    key = entry.getKey();
                    break;
                }
            }
        } else {
            return true;
        }
        return remove(channel);
    }

    public static boolean remove(String key) {
        Channel remove = USERS.remove(key);
        return USERS.containsValue(remove);
    }

    public static ConcurrentMap<String, Channel> getUSERS() {
        return USERS;
    }

    /**
     * 群发消息
     *
     * @param message 消息内容
     */
    public static void sendMessageToUsers(String message) {
        Collection<Channel> values = USERS.values();
        for (Channel value : values) {
            value.write(new TextWebSocketFrame(message));
            value.flush();
        }
    }

    /**
     * 给某个人发送消息
     *
     * @param userName key
     * @param message  消息
     */
    public static void sendMessageToUser(String userName, String message) {
        Channel channel = USERS.get(userName);
        channel.write(new TextWebSocketFrame(message));
        channel.flush();
    }
}
