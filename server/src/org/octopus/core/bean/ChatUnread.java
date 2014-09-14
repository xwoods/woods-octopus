package org.octopus.core.bean;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("t_chat_unread")
@TableIndexes({@Index(name = "t_chat_unread_user", fields = {"user"}, unique = false),
               @Index(name = "t_chat_unread_chatId", fields = {"chatId"}, unique = false),
               @Index(name = "t_chat_unread_historyId", fields = {"historyId"}, unique = false)})
public class ChatUnread {

    @Id
    private long id;

    private String user;

    private long chatId;

    private long historyId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(long historyId) {
        this.historyId = historyId;
    }

}
