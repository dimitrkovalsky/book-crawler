package com.liberty.book.crawler.common;

/**
 * Created by user on 02.06.2017.
 */
public class DayBornResponse {
    private Integer errorCode;
    private String content;
    private Boolean endData;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getEndData() {
        return endData;
    }

    public void setEndData(Boolean endData) {
        this.endData = endData;
    }
}
