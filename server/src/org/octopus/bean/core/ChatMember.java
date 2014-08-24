package org.octopus.bean.core;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("t_chat_member")
@TableIndexes({@Index(name = "t_chat_member_fromuser", fields = {"fromUser"}, unique = false),
               @Index(name = "t_chat_member_touser", fields = {"toUser"}, unique = false)})
public class ChatMember {

    @Id
    private long id;

    private long chatId;

    private String chatAlias;

    private String fromUser;

    private String toUser;

    public String getChatAlias() {
        return chatAlias;
    }

    public void setChatAlias(String chatAlias) {
        this.chatAlias = chatAlias;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

}
