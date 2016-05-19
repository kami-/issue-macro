package com.kami.confluence.issuemacro.jira;

public class JiraIssue {
    private final String key;
    private final String url;
    private final String summary;

    public JiraIssue(final String key, final String url, final String summary) {
        this.key = key;
        this.url = url;
        this.summary = summary;
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    public String getSummary() {
        return summary;
    }
}
