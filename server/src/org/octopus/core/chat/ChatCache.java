package org.octopus.core.chat;

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
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.octopus.core.bean.Chat;
import org.octopus.core.bean.ChatHistory;
import org.octopus.core.bean.ChatMember;
import org.octopus.core.bean.ChatUnread;
import org.octopus.core.bean.Domain;
import org.octopus.core.bean.DomainUser;
import org.octopus.core.bean.User;

public class ChatCache {

    private static Log log = Logs.get();
    private static Dao dao;
    // chatId -> chatHistoryList
    private static Map<Long, ArrayBlockingQueue<ChatHistory>> chatHistoryMap = new ConcurrentHashMap<Long, ArrayBlockingQueue<ChatHistory>>();
    // chatId -> Runner
    private static Map<Long, ChatMsgRunner> chatRunnerMap = new ConcurrentHashMap<Long, ChatMsgRunner>();
    // chatId -> Receiver
    private static Map<Long, Set<String>> chatUserMap = new ConcurrentHashMap<Long, Set<String>>();
    // user -> hasUnread
    private static Map<String, Integer> userHasUnreadMap = new ConcurrentHashMap<String, Integer>();
    // chatId.user -> chat(name更换为alias)
    private static Map<String, Chat> chatMap = new ConcurrentHashMap<String, Chat>();

    public static void init(Dao ndao) {
        dao = ndao;
        // 检查所有的域成员间的chat
        List<Domain> dList = dao.query(Domain.class, null);
        for (Domain dmn : dList) {
            checkChatMember(dmn.getName(), false);
        }
        // 初始化所有chat队列
        List<Chat> cList = dao.query(Chat.class, null);
        for (Chat chat : cList) {
            initChatQueue(chat);
            addRunner(chat.getId(), false);
            addChatUser(chat.getId(), null);
        }
        // 检查unread
        dao.each(User.class, null, new Each<User>() {
            @Override
            public void invoke(int index, User ele, int length) throws ExitLoop, ContinueLoop,
                    LoopException {
                setUnread(ele.getName(), 0);
            }

        });
        dao.each(ChatUnread.class, null, new Each<ChatUnread>() {
            @Override
            public void invoke(int index, ChatUnread ele, int length) throws ExitLoop,
                    ContinueLoop, LoopException {
                setUnread(ele.getUser(), 1);
            }
        });
    }

    public static void afterAddNewUser(String domainName) {
        checkChatMember(domainName, true);
    }

    public static Chat getChatByIdAndUser(long chatId, String toUserName) {
        return chatMap.get(chatId + toUserName);
    }

    public static void setUnread(String userName, int mnum) {
        if (mnum == 0) {
            userHasUnreadMap.put(userName, mnum);
        } else {
            Integer cum = hasUnread(userName);
            userHasUnreadMap.put(userName, cum + mnum);
        }
    }

    public static int hasUnread(String userName) {
        Integer cum = userHasUnreadMap.get(userName);
        if (cum == null) {
            cum = 0;
            userHasUnreadMap.put(userName, cum);
        }
        return cum;
    }

    private static void addChatUser(long chatId, String userName) {
        Set<String> users = chatUserMap.get(chatId);
        if (users == null) {
            users = new HashSet<String>();
            chatUserMap.put(chatId, users);
        }
        if (Strings.isBlank(userName)) {
            List<ChatMember> cmList = dao.query(ChatMember.class, Cnd.where("chatId", "=", chatId));
            for (ChatMember cm : cmList) {
                users.add(cm.getToUser());
                // 添加chat
                Chat fc = new Chat();
                fc.setId(chatId);
                fc.setName(cm.getChatAlias());
                chatMap.put(chatId + cm.getToUser(), fc);
            }
        } else {
            users.add(userName);
            ChatMember cm = dao.fetch(ChatMember.class,
                                      Cnd.where("chatId", "=", chatId).and("fromUser",
                                                                           "=",
                                                                           userName));
            if (cm != null) {
                users.add(cm.getToUser());
                // 添加chat
                Chat fc = new Chat();
                fc.setId(chatId);
                fc.setName(cm.getChatAlias());
                chatMap.put(chatId + cm.getToUser(), fc);
            } else {
                log.errorf("Can't Find ChatMember[%d, From %s]", chatId, userName);
            }

            ChatMember cm2 = dao.fetch(ChatMember.class,
                                       Cnd.where("chatId", "=", chatId)
                                          .and("toUser", "=", userName));
            if (cm2 != null) {
                users.add(cm2.getFromUser());
                // 添加chat
                Chat fc = new Chat();
                fc.setId(chatId);
                fc.setName(cm2.getChatAlias());
                chatMap.put(chatId + cm2.getToUser(), fc);
            } else {
                log.errorf("Can't Find ChatMember[%d, To %s]", chatId, userName);
            }
        }
    }

    public static Set<String> getChatUsers(long chatId) {
        return chatUserMap.get(chatId);
    }

    public static void startRunner() {
        for (ChatMsgRunner cr : chatRunnerMap.values()) {
            if (!cr.isAlive()) {
                new Thread(cr).start();
            }
        }
    }

    public static void stopRunner() {
        for (ChatMsgRunner cr : chatRunnerMap.values()) {
            if (cr.isAlive()) {
                cr.stopSend();
            }
        }
    }

    public static void addRunner(long chatId, boolean startRun) {
        ChatMsgRunner cr = new ChatMsgRunner(chatId, chatHistoryMap.get(chatId), dao);
        chatRunnerMap.put(chatId, cr);
        if (startRun && !cr.isAlive()) {
            new Thread(cr).start();
        }
    }

    public static void checkChatMember(String domainName, boolean startRun) {
        List<DomainUser> duList = dao.query(DomainUser.class, Cnd.where("domain", "=", domainName));
        for (DomainUser du1 : duList) {
            for (DomainUser du2 : duList) {
                String u1 = du1.getUser();
                String u2 = du2.getUser();
                if (!u1.equals(u2)) {
                    if (dao.count(ChatMember.class,
                                  Cnd.where("fromUser", "=", u1).and("toUser", "=", u2)) == 0) {
                        // 建立两者的对话关系
                        createChat(u1, u2, startRun);
                    }
                }
            }
        }
    }

    private static void createChat(String u1, String u2, boolean startRun) {
        log.infof("Create new Chat[ %s <-> %s]", u1, u2);
        Chat chat = new Chat();
        chat.setName(u1 + "," + u2);
        dao.insert(chat);
        ChatMember cm1 = new ChatMember();
        cm1.setChatId(chat.getId());
        cm1.setChatAlias(String.format("与 %s 的对话", UserCache.getUser(u2).getName()));
        cm1.setFromUser(u1);
        cm1.setToUser(u2);
        dao.insert(cm1);
        ChatMember cm2 = new ChatMember();
        cm2.setChatId(chat.getId());
        cm2.setChatAlias(String.format("与 %s 的对话", UserCache.getUser(u1).getName()));
        cm2.setFromUser(u2);
        cm2.setToUser(u1);
        dao.insert(cm2);

        if (startRun) {
            initChatQueue(chat);
            addRunner(chat.getId(), true);
            addChatUser(chat.getId(), u1); // u1跟u2都可以
        }
    }

    public static void initChatQueue(Chat ct) {
        if (!chatHistoryMap.containsKey(ct.getId())) {
            ArrayBlockingQueue<ChatHistory> historyQueue = new ArrayBlockingQueue<ChatHistory>(500);
            chatHistoryMap.put(ct.getId(), historyQueue);
        }
    }

    public static void receiveMsg(long chatId, String myName, String content) {
        ChatHistory ch = new ChatHistory();
        ch.setChatId(chatId);
        ch.setUser(myName);
        ch.setCreateTime(new Date());
        ch.setContent(content);
        dao.insert(ch);
        if (chatHistoryMap.containsKey(chatId)) {
            Queue<ChatHistory> historyQueue = chatHistoryMap.get(chatId);
            historyQueue.add(ch);
        } else {
            log.errorf("Chat[%d] Not Inited!, Lost Message!", chatId);
        }
    }

}
