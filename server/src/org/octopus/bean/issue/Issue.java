package org.octopus.bean.issue;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.octopus.bean.BeanCreateModify;

@Table("t_issue")
@TableIndexes({@Index(name = "t_issue_domain", fields = {"domain"}, unique = false)})
public class Issue extends BeanCreateModify {

    private String domain;

    @ColDefine(width = 2048)
    private String content;

    private String status;

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
