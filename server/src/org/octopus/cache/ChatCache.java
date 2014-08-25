package org.octopus.cache;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.octopus.bean.core.Chat;
import org.octopus.bean.core.ChatHistory;
import org.octopus.bean.core.ChatMember;
import org.octopus.bean.core.Domain;
import org.octopus.bean.core.DomainUser;

public class ChatCache {

    private static Log log = Logs.get();
    private static Dao dao;
    private static Map<Long, ArrayBlockingQueue<ChatHistory>> chatMap = new ConcurrentHashMap<Long, ArrayBlockingQueue<ChatHistory>>();
    private static Map<Long, ChatMsgRunner> chatRunnerMap = new ConcurrentHashMap<Long, ChatMsgRunner>();
    private static Map<Long, Set<String>> chatUserMap = new ConcurrentHashMap<Long, Set<String>>();

    public static void init(Dao ndao) {
        dao = ndao;
        // 检查所有的域成员间的chat
        List<Domain> dList = dao.query(Domain.class, null);
        for (Domain dmn : dList) {
            checkChatMember(dmn.getName());
        }
        // 初始化所有chat队列
        List<Chat> cList = dao.query(Chat.class, null);
        for (Chat chat : cList) {
            initChatQueue(chat);
            addRunner(chat.getId(), false);
            addChatUser(chat.getId(), null);
        }
    }

    public static void addChatUser(long chatId, String userName) {
        Set<String> users = chatUserMap.get(chatId);
        if (users == null) {
            users = new HashSet<String>();
            chatUserMap.put(chatId, users);
        }
        if (Strings.isBlank(userName)) {
            List<ChatMember> cmList = dao.query(ChatMember.class, Cnd.where("chatId", "=", chatId));
            for (ChatMember cm : cmList) {
                users.add(cm.getToUser());
            }
        } else {
            users.add(userName);
        }
    }

    public static Set<String> getChatUsers(long chatId) {
        return chatUserMap.get(chatId);
    }

    public static void startRunner() {
        for (ChatMsgRunner cr : chatRunnerMap.values()) {
            if (!cr.isRunning()) {
                new Thread(cr).start();
            }
        }
    }

    public static void stopRunner() {
        for (ChatMsgRunner cr : chatRunnerMap.values()) {
            if (cr.isRunning()) {
                cr.stopSend();
            }
        }
    }

    public static void addRunner(long chatId, boolean startRun) {
        ChatMsgRunner cr = new ChatMsgRunner(chatId, chatMap.get(chatId), dao);
        chatRunnerMap.put(chatId, cr);
        if (startRun && !cr.isRunning()) {
            new Thread(cr).start();
        }
    }

    public static void checkChatMember(String domainName) {
        List<DomainUser> duList = dao.query(DomainUser.class, Cnd.where("domain", "=", domainName));
        for (DomainUser du1 : duList) {
            for (DomainUser du2 : duList) {
                String u1 = du1.getUser();
                String u2 = du2.getUser();
                if (!u1.equals(u2)) {
                    if (dao.count(ChatMember.class,
                                  Cnd.where("fromUser", "=", u1).and("toUser", "=", u2)) == 0) {
                        // 建立两者的对话关系
                        createChat(u1, u2);
                    }
                }
            }
        }
    }

    public static void createChat(String u1, String u2) {
        log.infof("Create new Chat[ %s <-> %s]", u1, u2);
        Chat chat = new Chat();
        chat.setName(u1 + "," + u2);
        dao.insert(chat);
        ChatMember cm1 = new ChatMember();
        cm1.setChatId(chat.getId());
        cm1.setChatAlias(String.format("与%s的对话", u2));
        cm1.setFromUser(u1);
        cm1.setToUser(u2);
        dao.insert(cm1);
        ChatMember cm2 = new ChatMember();
        cm2.setChatId(chat.getId());
        cm2.setChatAlias(String.format("与%s的对话", u1));
        cm2.setFromUser(u2);
        cm2.setToUser(u1);
        dao.insert(cm2);

        initChatQueue(chat);

    }

    public static void initChatQueue(Chat ct) {
        if (!chatMap.containsKey(ct.getId())) {
            ArrayBlockingQueue<ChatHistory> historyQueue = new ArrayBlockingQueue<ChatHistory>(500);
            chatMap.put(ct.getId(), historyQueue);
        }
    }

    public static void receiveMsg(long chatId, String myName, String content) {
        ChatHistory ch = new ChatHistory();
        ch.setChatId(chatId);
        ch.setUser(myName);
        ch.setCreateTime(new Date());
        ch.setContent(content);
        dao.insert(ch);
        if (chatMap.containsKey(chatId)) {
            Queue<ChatHistory> historyQueue = chatMap.get(chatId);
            historyQueue.add(ch);
        } else {
            log.errorf("Chat[%d] Not Inited!, Lost Message!", chatId);
        }
    }

}
