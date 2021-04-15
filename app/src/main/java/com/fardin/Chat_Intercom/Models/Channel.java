package com.fardin.Chat_Intercom.Models;

public class Channel {
    private String channelName;
    private String channelUID;

    public Channel() {
    }

    public Channel(String channelName,String channelUID) {
        this.channelName = channelName;
        this.channelUID = channelUID;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelUID() {
        return channelUID;
    }

    public void setChannelUID(String channelUID) {
        this.channelUID = channelUID;
    }
}
