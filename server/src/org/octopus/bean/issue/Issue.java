package org.octopus.bean.issue;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Table;
import org.octopus.bean.BeanWithTrace;

@Table("t_issue")
public class Issue extends BeanWithTrace {

    private String domainId;

    private String domainName;

    @ColDefine(width = 1024)
    private String content;

    private String status;

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
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
