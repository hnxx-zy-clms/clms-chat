package com.hnxx.zyb1.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 黑鲨
 * @date 2020/3/25 17:23
 */
@Data
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = -8724518310083269150L;
    /**
     * 消息类型
     */
    private MessageType type;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息发送者
     */
    private String sender; /**
     * 消息接收者
     */
    private String receiver;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createdTime;
    /**
     * 头像信息
     */
    private String icon;

    /**
     * 三种状态
     */
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
