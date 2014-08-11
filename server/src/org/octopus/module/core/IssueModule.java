package org.octopus.module.core;

import java.util.Date;

import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
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
import org.octopus.Keys;
import org.octopus.bean.core.Domain;
import org.octopus.bean.core.Issue;
import org.octopus.bean.core.IssueReply;
import org.octopus.bean.core.User;
import org.octopus.module.AbstractBaseModule;
import org.octopus.module.core.query.IssueCndMaker;

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
    public AjaxReturn queryIssue(@Param("kwd") String kwd,
                                 @Param("pgnm") int pgnm,
                                 @Param("pgsz") int pgsz,
                                 @Param("orderby") String orderby,
                                 @Param("asc") boolean asc,
                                 @Param("domain") String domain,
                                 @Param("user") String user) {
        QueryResult qr = new IssueCndMaker().queryResult(dao,
                                                         Issue.class,
                                                         null,
                                                         pgnm,
                                                         pgsz,
                                                         orderby,
                                                         asc,
                                                         kwd,
                                                         domain,
                                                         user);
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
        return Ajax.ok();
    }

    @At("/reply/list")
    public AjaxReturn listReply(@Param("issueId") String issueId) {
        return Ajax.ok().setData(dao.query(IssueReply.class, Cnd.where("issueId", "=", issueId)
                                                                .asc("createTime")));
    }
}
