package com.hnxx.zyb1.controller;


import com.hnxx.zyb1.model.ChatMessage;
import com.hnxx.zyb1.model.ChatPointMessage;
import com.hnxx.zyb1.server.ChatService;
import com.hnxx.zyb1.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author 黑鲨
 * @date 2020/3/25 16:27
 */

@Controller
@RequestMapping("/chat")
public class ChatController {
    private final Logger logger = LoggerFactory.getLogger(ChatController.class);
    public static Set<String> currentUserSet = new HashSet<String>();
    @Resource
    private ChatService chatService;

 /*   @Resource
    private SimpMessageSendingOperations messagingTemplate;*/

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;


    /**
     * 群发给所有在聊天室的用户   并将信息存储置数据库用于消息记录
     *
     * @param chatMessage
     * @return
     */
    @MessageMapping("/chat.sendMessage")
    @RequestMapping("/sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatService.chatSave(chatMessage);
        return chatMessage;
    }

    /**
     * 用户上线，查看卫读取的私聊信息  新进入聊天室 的用户  并将信息存储置数据库用于消息记录
     *
     * @param chatMessage
     * @param
     * @return
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage) {
        String sender = chatMessage.getSender();
        String type = chatMessage.getType() + "";
        if ("JOIN".equals(type)) {
            chatMessage.setContent("进入聊天室！！");
            currentUserSet.add(sender);
            chatService.chatSave(chatMessage);
        }
        return chatMessage;
    }

    @MessageMapping("/chat.leftUser")
    @SendTo("/topic/public")
    public ChatMessage leftUser(@Payload ChatMessage chatMessage) {
        String sender = chatMessage.getSender();
        String type = chatMessage.getType() + "";
        if ("LEAVE".equals(type)) {
            chatMessage.setContent("离开聊天室！！");
            currentUserSet.remove(sender);
            chatService.chatSave(chatMessage);
        }
        return chatMessage;
    }

    /**
     * 获取聊天记录
     * type   为1   代表查询一个小时以前的聊天信息
     * 为2  代表查询2个小时以前的聊天信息
     * 为3 代表查询当天的聊天信息
     *
     * @return
     * @RequestMapping("/chatMessage")
     * @ResponseBody public Result<Object> chatMessage(@RequestParam("type") Integer type) {
     * @GetMapping("/chatMessage/{type}")
     * @ResponseBody public Result<Object>  chatMessage(@PathVariable("type") Integer type){
     */
    @GetMapping("/chatMessage/{type}")
    @ResponseBody
    public List<ChatMessage> chatMessage(@PathVariable("type") Integer type) {

        return chatService.allChat(type);
    }

    /***
     * 获取当前在线用户
     * @return
     */
    @RequestMapping("/chatUser")
    @ResponseBody
    public Set<String> chatUser() {
        return currentUserSet;
    }



    /***
     * 实现点对点聊天
     */
    @MessageMapping("/chat.oneToOne")
    public void oneToOneChat(@Payload ChatPointMessage chatPointMessage) {
        String receiver = chatPointMessage.getReceiver();
        String sender = chatPointMessage.getSender();
        if (!StringUtils.isNullOrEmpty(receiver)) {
            chatService.chatPointSave(chatPointMessage);
            simpMessagingTemplate.convertAndSend("/chat/point/" + sender, chatPointMessage);
            simpMessagingTemplate.convertAndSend("/chat/point/" + receiver, chatPointMessage);
        }
    }

    /**
     * 用户上线，查看未读取的私聊信息
     *
     * @param chatPointMessage
     * @param
     * @return
     */
    @MessageMapping("/chat.userUp")
//    @SendTo("/topic/public")
    public void userUp(@Payload ChatPointMessage chatPointMessage) {
        String sender = chatPointMessage.getSender();
        String type = chatPointMessage.getType() + "";
        List<ChatPointMessage> pointMessageList = null;
        if ("JOIN".equals(type)) {
            pointMessageList = chatService.pointStateMessage(sender);
        }
        for (ChatPointMessage pointMessage : pointMessageList) {
            simpMessagingTemplate.convertAndSend("/chat/point/" + sender, pointMessage);
            simpMessagingTemplate.convertAndSend("/chat/point/" + pointMessage.getSender(), pointMessage);
        }
    }
    /***
     * 标记已经查看的消息
     * @return
     */
    @RequestMapping("/stateMessage")
    public void stateMessage(@RequestBody List<ChatPointMessage> oneToOneMessage) {
        for (ChatPointMessage chatPointMessage : oneToOneMessage) {
            chatService.clearPointMessage(chatPointMessage);
        }
    }


}
