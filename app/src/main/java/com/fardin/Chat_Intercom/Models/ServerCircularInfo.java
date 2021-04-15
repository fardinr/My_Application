package com.fardin.Chat_Intercom.Models;

public class ServerCircularInfo {
    private String DPUrl,serverName;

    public ServerCircularInfo(String DPUrl, String serverName) {
        this.DPUrl = DPUrl;
        this.serverName = serverName;
    }

    public ServerCircularInfo() {
    }

    public String getDPUrl() {
        return DPUrl;
    }

    public void setDPUrl(String DPUrl) {
        this.DPUrl = DPUrl;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
