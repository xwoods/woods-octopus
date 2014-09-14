package org.octopus.core.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("t_issue_reply")
@TableIndexes({@Index(name = "t_issue_id", fields = {"issueId"}, unique = false),
               @Index(name = "t_issue_ct", fields = {"createTime"}, unique = false)})
public class IssueReply extends BeanCreateModify {

    private String issueId;

    @ColDefine(width = 1024)
    private String content;

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
