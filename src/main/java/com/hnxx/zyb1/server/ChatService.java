package com.hnxx.zyb1.server;


import com.hnxx.zyb1.model.ChatMessage;
import com.hnxx.zyb1.model.ChatPointMessage;

import java.util.List;

/**
 * @author 黑鲨
 * @date 2020/3/25 17:51
 */
public interface ChatService {

    void  chatSave(ChatMessage chatMessage);

    List<ChatMessage> allChat(Integer type);

    void chatPointSave(ChatPointMessage chatPointMessage);

    List<ChatPointMessage>  pointStateMessage(String sender );

    void  clearPointMessage(ChatPointMessage chatPointMessage);

}
