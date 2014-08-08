package org.octopus.bean.issue;

import org.nutz.dao.entity.annotation.ColDefine;
import org.octopus.bean.BeanWithTrace;

public class IssueReply extends BeanWithTrace {

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
