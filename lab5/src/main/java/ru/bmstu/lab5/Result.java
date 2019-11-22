package ru.bmstu.lab5;

public class Result {
    private String url;
    private Long responseTime;

    public Result(String url, Long responseTime) {
        this.url = url;
        this.responseTime = responseTime;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public String getUrl() {
        return url;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
