package io.liuzhilin.mobileanywhere.bean;

import java.util.Date;

public class Blog {
    private String blogId;

    private String blogOwner;

    private Date blogCreateTime;

    private Short blogType;

    private String blogMediaUrl;

    private String blogPointId;

    private String blogTextData;

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId == null ? null : blogId.trim();
    }

    public String getBlogOwner() {
        return blogOwner;
    }

    public void setBlogOwner(String blogOwner) {
        this.blogOwner = blogOwner == null ? null : blogOwner.trim();
    }

    public Date getBlogCreateTime() {
        return blogCreateTime;
    }

    public void setBlogCreateTime(Date blogCreateTime) {
        this.blogCreateTime = blogCreateTime;
    }

    public Short getBlogType() {
        return blogType;
    }

    public void setBlogType(Short blogType) {
        this.blogType = blogType;
    }

    public String getBlogMediaUrl() {
        return blogMediaUrl;
    }

    public void setBlogMediaUrl(String blogMediaUrl) {
        this.blogMediaUrl = blogMediaUrl == null ? null : blogMediaUrl.trim();
    }

    public String getBlogPointId() {
        return blogPointId;
    }

    public void setBlogPointId(String blogPointId) {
        this.blogPointId = blogPointId == null ? null : blogPointId.trim();
    }

    public String getBlogTextData() {
        return blogTextData;
    }

    public void setBlogTextData(String blogTextData) {
        this.blogTextData = blogTextData == null ? null : blogTextData.trim();
    }
}