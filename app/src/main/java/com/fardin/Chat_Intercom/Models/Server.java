package com.fardin.Chat_Intercom.Models;

import java.util.HashMap;

public class Server {
    private String serverUID;
    private String name;
    private String owner;
    private String profileImage;
    private HashMap<String , Object> members;

    public Server(String serverUID, String name, String owner, String profileImage, HashMap<String, Object> members) {
        this.serverUID = serverUID;
        this.name = name;
        this.owner = owner;
        this.profileImage = profileImage;
        this.members = members;
    }

    public Server(String serverUID, String name, String owner, String profileImage) {
        this.serverUID = serverUID;
        this.name = name;
        this.owner = owner;
        this.profileImage = profileImage;
    }

    public Server() {
    }

    public HashMap<String, Object> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, Object> members) {
        this.members = members;
    }

    public String getServerUID() {
        return serverUID;
    }

    public void setServerUID(String serverUID) {
        this.serverUID = serverUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
