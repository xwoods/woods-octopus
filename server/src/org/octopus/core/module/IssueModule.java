package org.octopus.core.module;

import java.util.Date;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.Strings;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.web.ajax.Ajax;
import org.nutz.web.ajax.AjaxReturn;
import org.nutz.web.fliter.CheckNotLogin;
import org.nutz.web.query.CndMaker;
import org.nutz.web.query.Query;
import org.nutz.web.query.QueryStr;
import org.octopus.core.Keys;
import org.octopus.core.bean.Domain;
import org.octopus.core.bean.Issue;
import org.octopus.core.bean.IssueReply;
import org.octopus.core.bean.User;

@Filters({@By(type = CheckNotLogin.class, args = {Keys.SESSION_USER, "/login"})})
@At("/issue")
@Ok("ajax")
public class IssueModule extends AbstractBaseModule {

    // private Log log = Logs.get();

    @At("/add")
    public AjaxReturn addIssue(@Param("content") String content,
                               @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me,
                               @Attr(scope = Scope.SESSION, value = Keys.SESSION_DOMAIN) Domain domain) {
        Issue issue = new Issue();
        issue.setContent(content);
        issue.setCreateTime(new Date());
        issue.setCreateUser(me.getName());
        issue.setDomain(domain.getName());
        dao.insert(issue);
        return Ajax.ok();
    }

    @At("/query")
    public AjaxReturn queryIssue(@Param("..") Query q,
                                 @Param("domain") String domain,
                                 @Param("user") String user) {
        q.tableSet(dao, Issue.class, null);
        q.cndSet(domain, user);
        QueryResult qr = CndMaker.queryResult(new QueryStr() {
            public void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd) {
                if (otherQCnd != null && otherQCnd.length >= 1 && !Strings.isBlank(otherQCnd[0])) {
                    sc.where().andEquals("domain", otherQCnd[0]);
                }
                if (otherQCnd != null && otherQCnd.length >= 2 && !Strings.isBlank(otherQCnd[1])) {
                    sc.where().andEquals("createUser", otherQCnd[1]);
                }
                if (!Strings.isBlank(kwd)) {
                    sc.where().orEquals("content", kwd);
                }
            }
        }, q);
        return Ajax.ok().setData(qr);
    }

    @At("/reply/add")
    public AjaxReturn addReply(@Param("issueId") String issueId,
                               @Param("content") String content,
                               @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        IssueReply issueReply = new IssueReply();
        issueReply.setIssueId(issueId);
        issueReply.setContent(content);
        issueReply.setCreateTime(new Date());
        issueReply.setCreateUser(me.getName());
        dao.insert(issueReply);
        int replyNum = dao.count(IssueReply.class, Cnd.where("issueId", "=", issueId));
        dao.update(Issue.class, Chain.make("replyNum", replyNum), Cnd.where("id", "=", issueId));
        return Ajax.ok();
    }

    @At("/reply/list")
    public AjaxReturn listReply(@Param("issueId") String issueId) {
        return Ajax.ok().setData(dao.query(IssueReply.class, Cnd.where("issueId", "=", issueId)
                                                                .asc("createTime")));
    }
}
