package ru.homedepot.entity;

public class Link {

    private String linkUrl;
    private String linkName;

    public Link(String url, String name){
        this.linkUrl = url;
        this.linkName = name;
    }


    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }
}
