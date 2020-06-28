package com.hnxx.zyb1.mapper;

import com.hnxx.zyb1.model.ChatMessage;
import com.hnxx.zyb1.model.ChatPointMessage;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * @author 黑鲨
 * @date 2020/3/25 17:52
 */
@Mapper
public interface ChatMapper {

    @Insert("  INSERT INTO cl_chat(chat_user, chat_content,chat_type,chat_icon,created_time) " +
            "   VALUES (#{sender},#{content},#{type},#{icon},#{createdTime})  ")
    void chatSave(ChatMessage chatMessage);

    @Select(" SELECT c.chat_user sender ,c.chat_content content, c.chat_type type,  c.chat_icon  icon, c.created_time  createdTime   " +
            " FROM cl_chat c " +
            " where c.created_time BETWEEN #{startTime} and #{endTime} " +
            " ORDER BY  c.created_time asc  ")
    List<ChatMessage> allChat(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Insert("  INSERT INTO cl_chat_point(chat_user,chat_receiver, chat_content,chat_type,chat_icon,chat_state,created_time) " +
            "   VALUES (#{sender},#{receiver},#{content},#{type},#{icon},#{state},#{createdTime})  ")
    void chatPointSave(ChatPointMessage chatPointMessage);

    @Select(" select  c.chat_user sender,c.chat_receiver receiver, c.chat_content content, c.chat_type type,  c.chat_icon  icon,c.chat_state state, c.created_time  createdTime " +
            " from  cl_chat_point c " +
            " where  chat_receiver =#{sender}  and chat_state=0 ")
    List<ChatPointMessage> pointStateMessage(String sender);

    @Update("update   cl_chat_point c  set  c.chat_state=1    where c.chat_user=#{sender} and  c.chat_receiver=#{receiver} ")
    void deletePointMeaasge(@Param("sender")String sender, @Param("receiver")String receiver);
}
