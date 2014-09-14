package org.octopus.core.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.runner.NutLock;
import org.nutz.runner.NutRunner;
import org.octopus.core.bean.ChatMember;
import org.octopus.core.bean.User;

public class UserCache {
    private static Dao dao;
    private static List<User> userList = new ArrayList<User>();
    private static Map<String, User> users = new ConcurrentHashMap<String, User>();
    private static Map<String, Boolean> isOnline = new ConcurrentHashMap<String, Boolean>();
    private static Map<String, Date> lastPing = new ConcurrentHashMap<String, Date>();
    private static long srDration = 1000 * 5;
    private static long pingDration = 1000 * 30;
    private static NutLock statusLock;
    private static NutRunner statusRunner = new NutRunner("r.user.status") {
        @Override
        public long exec() throws Exception {
            long now = System.currentTimeMillis();
            for (String unm : users.keySet()) {
                long lp = lastPing.get(unm).getTime();
                boolean online = (now - lp <= pingDration);
                if (isOnline.get(unm) != online) {
                    log.infof("User[%s] is %s", unm, online ? "Online" : "Offline");
                    isOnline.put(unm, online);
                }
            }
            return srDration;
        }
    };

    public static void init(Dao ndao) {
        dao = ndao;
        for (User u : dao.query(User.class, null)) {
            addUser(u);
        }
    }

    public static void startRunner() {
        statusLock = statusRunner.getLock();
        new Thread(statusRunner).start();
    }

    public static void stopRunner() {
        statusLock.stop().wakeup();
    }

    private static boolean checkUser(User user) {
        if (user != null) {
            if (!users.containsKey(user.getName())) {
                addUser(user);
            }
            return true;
        }
        return false;
    }

    public static User getUser(String userName) {
        return users.get(userName);
    }

    public static void addUser(User user) {
        if (!users.containsKey(user.getName())) {
            user.setPassword("U Want Know ?!");
            users.put(user.getName(), user);
            userList.add(user);
            isOnline.put(user.getName(), false);
            lastPing.put(user.getName(), new Date(user.getLastLogin() != null ? user.getLastLogin()
                                                                                    .getTime()
                                                                             : user.getCreateTime()
                                                                                   .getTime()));
        }
    }

    public static void ping(User user) {
        if (checkUser(user)) {
            lastPing.put(user.getName(), new Date());
        }
    }

    public static boolean isOnline(String userName) {
        return isOnline.containsKey(userName) ? isOnline.get(userName) : false;
    }

    public static Set<String> getMyFriendsName(User me) {
        List<ChatMember> cmList = dao.query(ChatMember.class,
                                            Cnd.where("fromUser", "=", me.getName()));
        Set<String> myFriendsName = new HashSet<String>();
        for (ChatMember cm : cmList) {
            myFriendsName.add(cm.getToUser());
        }
        return myFriendsName;
    }

    public static List<User> getMyFriends(User me) {
        Set<String> myFriendsName = getMyFriendsName(me);
        List<User> myFriends = new ArrayList<User>();
        for (User user : userList) {
            if (myFriendsName.contains(user.getName())) {
                myFriends.add(user);
            }
        }
        return myFriends;
    }

    public static Map<String, Boolean> getMyFriendsOnline(List<String> getMyFriendsName) {
        Map<String, Boolean> onlineMap = new HashMap<String, Boolean>();
        for (String nm : getMyFriendsName) {
            onlineMap.put(nm, isOnline.get(nm));
        }
        return onlineMap;
    }

}
