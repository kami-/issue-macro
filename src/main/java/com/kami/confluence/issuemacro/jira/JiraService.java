package com.kami.confluence.issuemacro.jira;

public interface JiraService {
    JiraIssue getIssue(final String key);
    JiraIssue createIssue(final String summary);
    void updateIssue(final String key, final String summary);
}
