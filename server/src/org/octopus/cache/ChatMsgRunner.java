package org.octopus.cache;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.nutz.dao.Dao;
import org.nutz.runner.NutRunner;
import org.octopus.bean.core.ChatHistory;
import org.octopus.bean.core.ChatUnread;

public class ChatMsgRunner extends NutRunner {

    private ArrayBlockingQueue<ChatHistory> chQueue;
    private long chatId;
    private boolean stopSend = false;
    private Dao dao;

    public ChatMsgRunner(long chatId, ArrayBlockingQueue<ChatHistory> chQueue, Dao dao) {
        super("r.chat." + chatId);
        this.chatId = chatId;
        this.chQueue = chQueue;
        this.dao = dao;
    }

    public void stopSend() {
        stopSend = true;
        this.lock.stop().wakeup();
    }

    @Override
    public long exec() throws Exception {
        while (!stopSend) {
            try {
                ChatHistory ch = chQueue.poll(1, TimeUnit.SECONDS);
                if (ch == null) {
                    continue;
                }
                Set<String> chatUsers = ChatCache.getChatUsers(chatId);
                // FIXME 将来改成, 直接返回页面, 持久化操作稍后进行
                for (String cuser : chatUsers) {
                    ChatUnread cu = new ChatUnread();
                    cu.setChatId(chatId);
                    cu.setHistoryId(ch.getId());
                    cu.setUser(cuser);
                    dao.insert(cu);
                    ChatCache.setUnread(cuser, true);
                }
            }
            catch (Exception e) {
                log.error(e);
            }
        }
        return 1;
    }
}
