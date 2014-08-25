package org.octopus.module.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.web.ajax.Ajax;
import org.nutz.web.ajax.AjaxReturn;
import org.nutz.web.comet.Comet;
import org.nutz.web.fliter.CheckNotLogin;
import org.octopus.Keys;
import org.octopus.bean.core.ChatHistory;
import org.octopus.bean.core.ChatMember;
import org.octopus.bean.core.ChatUnread;
import org.octopus.bean.core.User;
import org.octopus.cache.ChatCache;
import org.octopus.module.AbstractBaseModule;

@Filters({@By(type = CheckNotLogin.class, args = {Keys.SESSION_USER, "/login"})})
@At("/chat")
@Ok("ajax")
public class ChatModule extends AbstractBaseModule {

    private Log log = Logs.get();

    @At("/friends")
    public AjaxReturn getFriendChat(@Param("friends") String fsNames,
                                    @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        String[] nms = fsNames.split(",");
        NutMap reuslt = new NutMap();
        for (String fnm : nms) {
            ChatMember cm = dao.fetch(ChatMember.class, Cnd.where("fromUser", "=", me.getName())
                                                           .and("toUser", "=", fnm));
            if (cm != null) {
                reuslt.put(fnm, cm);
            }
        }
        return Ajax.ok().setData(reuslt);
    }

    // 使用comet进行长连接
    @At("/unread/longcheck")
    @Ok("void")
    public void checkUnread(@Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me,
                            HttpServletResponse resp,
                            HttpSession session) {
        String myName = me.getName();
        try {
            while (session.getAttribute(Keys.SESSION_USER) != null
                   && Comet.replyByES(resp, String.valueOf(ChatCache.hasUnread(myName)))) {
                Lang.quiteSleep(1000);
            }
        }
        catch (Exception e) {
            log.errorf("LongCheck %s's Unread-Number, Has Error", myName);
            log.error(e);
        }
    }

    @At("/unread/check")
    public AjaxReturn checkUnread(@Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        String myName = me.getName();
        if (ChatCache.hasUnread(myName) > 0) {
            // FIXME 这里可能有bug, 同步问题, 但是减少了一次读取数据库, 可以经常访问
            List<ChatUnread> cu = dao.query(ChatUnread.class,
                                            Cnd.where("user", "=", myName)
                                               .desc("chatId")
                                               .asc("historyId"));
            // 清零
            ChatCache.setUnread(myName, true);
            // 这里返回对应的数量
            // Map<Long, Chat> chatMap = new HashMap<Long, Chat>();
            Map<Long, Integer> unreadMap = new HashMap<Long, Integer>();
            for (ChatUnread chatUnread : cu) {
                long chatId = chatUnread.getChatId();
                // Chat tChat = chatMap.get(chatId);
                // if (tChat == null) {
                // tChat = ChatCache.getChatByIdAndUser(chatId,
                // chatUnread.getUser());
                // chatMap.put(chatId, tChat);
                // }
                Integer unum = unreadMap.get(chatId);
                if (unum == null) {
                    unum = 0;
                }
                unreadMap.put(chatId, unum + 1);
            }
            //
            // NutMap result = new NutMap();
            // result.setv("chat", chatMap);
            // result.setv("unread", unreadMap);
            return Ajax.ok().setData(unreadMap);
        }
        return Ajax.ok();
    }

    @At("/msg/get")
    public AjaxReturn getUnread(@Param("chatId") long chatId,
                                @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        List<ChatUnread> cu = dao.query(ChatUnread.class,
                                        Cnd.where("user", "=", me.getName())
                                           .and("chatId", "=", chatId)
                                           .asc("historyId"));
        // 因为historyId是升序, 所以拿到的第一个就是最早的一个未读的
        if (cu.size() > 0) {
            ChatUnread firstUr = cu.get(0);
            ChatHistory firstCh = dao.fetch(ChatHistory.class,
                                            Cnd.where("id", "=", firstUr.getHistoryId()));
            List<ChatHistory> chList = dao.query(ChatHistory.class,
                                                 Cnd.where("chatId", "=", firstCh.getChatId())
                                                    .and("createTime",
                                                         ">=",
                                                         firstCh.getCreateTime())
                                                    .asc("createTime"));
            // 依次删除chlist里面的history的unread
            for (ChatHistory ch : chList) {
                dao.clear(ChatUnread.class,
                          Cnd.where("user", "=", me.getName())
                             .and("chatId", "=", chatId)
                             .and("historyId", "=", ch.getId()));
            }
            return Ajax.ok().setData(chList);
        }
        return Ajax.ok();
    }

    @At("/msg/check")
    public boolean checkMessage(@Param("chatId") long chatId,
                                @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        return dao.count(ChatUnread.class,
                         Cnd.where("user", "=", me.getName()).and("chatId", "=", chatId)) > 0;
    }

    @At("/msg/send")
    public AjaxReturn sendMsessage(@Param("chatId") long chatId,
                                   @Param("content") String content,
                                   @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        ChatCache.receiveMsg(chatId, me.getName(), content);
        return Ajax.ok();
    }
}
