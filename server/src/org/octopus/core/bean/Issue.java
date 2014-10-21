package org.octopus.core.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("t_issue")
@TableIndexes({@Index(name = "t_issue_domain", fields = {"domain"}, unique = false),
               @Index(name = "t_issue_content", fields = {"content"}, unique = false),
               @Index(name = "t_issue_ctuser", fields = {"createUser"}, unique = false)})
public class Issue extends BeanCreateModify {

    private String domain;

    @ColDefine(width = 2048)
    private String content;

    // 问题状态
    private String status;

    // 问题类型
    private String type;

    // 回复数量
    private int replyNum;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(int replyNum) {
        this.replyNum = replyNum;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
