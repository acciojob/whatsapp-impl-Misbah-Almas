package com.driver;

import java.util.*;

import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashMap<String, String> userDb;
    private HashMap<Integer, Message> messageDb;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userDb = new HashMap<String, String>();
        this.messageDb = new HashMap<Integer, Message>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public boolean isNewUser(String mobile){
        return !userDb.containsKey(mobile);
    }
    public String createUser(String name, String mobile) {
        userDb.put(mobile, name);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        int noOfUser = users.size();
        if(noOfUser == 2){
            Group group = new Group(users.get(1).getName(), noOfUser);
            groupUserMap.put(group, users);
            return group;
        }
        else{
            this.customGroupCount++;
            String groupName = "Group " + this.customGroupCount;
            Group group = new Group(groupName, noOfUser);
            groupUserMap.put(group, users);
            adminMap.put(group, users.get(0));
            return group;
        }
    }

    public int createMessage(String content) {
        this.messageId++;
        Message message = new Message(messageId, content);
        messageDb.put(messageId, message);
        return messageId;

    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if(!groupUserMap.containsKey(group))
            throw new Exception("Group does not exist");
        if(!groupUserMap.get(group).contains(sender))
            throw new Exception("You are not allowed to send message");

        List<Message> chats = groupMessageMap.getOrDefault(group, new ArrayList<Message>());
        chats.add(message);
        groupMessageMap.put(group, chats);
        senderMap.put(message, sender);
        return chats.size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if(!groupUserMap.containsKey(group))
            throw new Exception("Group does not exist");
        if(!adminMap.get(group).equals(approver))
            throw new Exception("Approver does not have rights");
        if(!groupUserMap.get(group).contains(user))
            throw new Exception("User is not a participant");

        adminMap.put(group, user);

        List<User> userList = groupUserMap.get(group);
        int index = userList.indexOf(user);
        userList.set(index, approver);
        userList.set(0, user);

        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception {
        if(isNewUser(user.getMobile())){
            throw new Exception("User not found");
        }
        Group group = new Group();
        for(Group itr : groupUserMap.keySet()){
            if(groupUserMap.get(itr).contains(user)){
                group = itr;
            }
        }
        if(user.equals(groupUserMap.get(group).get(0))){
            throw new Exception("Cannot remove admin");
        }

        List<Message> messageList = new ArrayList<>();
        int userCount = 0;
        int messageCount = 0;
        int totalMessage = 0;
        if(!user.equals(groupUserMap.get(group).get(0))){
            groupUserMap.get(group).remove(user);
            userDb.remove(user.getMobile());
            for(Message message : senderMap.keySet()){
                if(senderMap.get(message).equals(user)){
                    messageList.add(message);
                }
            }
            for(Message message : messageList){
                groupMessageMap.get(group).remove(message);
                senderMap.remove(message);
                messageDb.remove(message.getId());
            }

            if(groupUserMap.get(group).size()<=2){
                this.customGroupCount--;
                if(groupUserMap.get(group).size()<2){
                    groupUserMap.remove(group);
                    groupUserMap.remove(group);
                    adminMap.remove(group);
                }
            }

            if(groupUserMap.containsKey(group)){
                userCount = groupUserMap.get(group).size();
                messageCount = groupMessageMap.get(group).size();
                totalMessage = messageId - messageList.size();
            }
        }
        return (userCount + messageCount + totalMessage);
    }

    public String findMessage(Date start, Date end, int k) throws Exception {
        List<Message> messageList = new ArrayList<>();
        for(Message message : messageDb.values()){
            if(message.getTimestamp().after(start) && message.getTimestamp().before(end)){
                messageList.add(message);
            }
        }
        if(messageList.size() < k){
            throw new Exception("K is greater than the number of messages");
        }
        Collections.sort(messageList, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });

        return messageList.get(k-1).getContent();
    }
}
