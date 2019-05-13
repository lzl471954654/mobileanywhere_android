package io.liuzhilin.mobileanywhere.bean;

import java.util.Date;

public class BlogComment {
    private String blogCommentsId;

    private String blogCommentsOwner;

    private Date blogCommentsCreateTime;

    private Short blogCommentsToType;

    private Short blogCommentsType;

    private String blogId;

    private String blogOtherCommentsId;

    private String blogCommentsMediaUrl;

    private String blogCommentsTextData;

    public String getBlogCommentsId() {
        return blogCommentsId;
    }

    public void setBlogCommentsId(String blogCommentsId) {
        this.blogCommentsId = blogCommentsId == null ? null : blogCommentsId.trim();
    }

    public String getBlogCommentsOwner() {
        return blogCommentsOwner;
    }

    public void setBlogCommentsOwner(String blogCommentsOwner) {
        this.blogCommentsOwner = blogCommentsOwner == null ? null : blogCommentsOwner.trim();
    }

    public Date getBlogCommentsCreateTime() {
        return blogCommentsCreateTime;
    }

    public void setBlogCommentsCreateTime(Date blogCommentsCreateTime) {
        this.blogCommentsCreateTime = blogCommentsCreateTime;
    }

    public Short getBlogCommentsToType() {
        return blogCommentsToType;
    }

    public void setBlogCommentsToType(Short blogCommentsToType) {
        this.blogCommentsToType = blogCommentsToType;
    }

    public Short getBlogCommentsType() {
        return blogCommentsType;
    }

    public void setBlogCommentsType(Short blogCommentsType) {
        this.blogCommentsType = blogCommentsType;
    }

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId == null ? null : blogId.trim();
    }

    public String getBlogOtherCommentsId() {
        return blogOtherCommentsId;
    }

    public void setBlogOtherCommentsId(String blogOtherCommentsId) {
        this.blogOtherCommentsId = blogOtherCommentsId == null ? null : blogOtherCommentsId.trim();
    }

    public String getBlogCommentsMediaUrl() {
        return blogCommentsMediaUrl;
    }

    public void setBlogCommentsMediaUrl(String blogCommentsMediaUrl) {
        this.blogCommentsMediaUrl = blogCommentsMediaUrl == null ? null : blogCommentsMediaUrl.trim();
    }

    public String getBlogCommentsTextData() {
        return blogCommentsTextData;
    }

    public void setBlogCommentsTextData(String blogCommentsTextData) {
        this.blogCommentsTextData = blogCommentsTextData == null ? null : blogCommentsTextData.trim();
    }
}