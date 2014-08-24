package org.octopus.bean.core;

import java.util.Date;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("t_chat_history")
@TableIndexes({@Index(name = "t_chat_history_chatId", fields = {"chatId"}, unique = false),
               @Index(name = "t_chat_history_createTime", fields = {"createTime"}, unique = false),
               @Index(name = "t_chat_history_user", fields = {"user"}, unique = false)})
public class ChatHistory {

    @Id
    private long id;

    private long chatId;

    private Date createTime;

    private String user;

    private String content;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
