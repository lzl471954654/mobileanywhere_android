package io.liuzhilin.mobileanywhere.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.liuzhilin.mobileanywhere.bean.User;

public class UserCacheManager {
    private static Map<String, User> userMap = new HashMap<>();
    private static User currentUser = null;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        UserCacheManager.currentUser = currentUser;
    }

    public static User getUser(String id){
        User user = userMap.get(id);
        if (user == null){

        }
        return user;
    }

    public static void addUser(List<User> users){
        for (User user : users) {
            addUser(user);
        }
    }

    public static void addUser(User user){
        userMap.put(user.getUserAccount(),user);
    }
}
