package com.kami.confluence.issuemacro.jira;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;

@Component
public class DefaultJiraService implements JiraService {
    private static final String JIRA_ISSUE_BASE_URL = "/rest/api/2/issue";
    private static final long PROJECT_ID = 10000L;
    private static final long ISSUE_TYPE_ID = 10000L;
    private static final Gson GSON = new Gson();

    private final ApplicationLinkService applicationLinkService;

    public DefaultJiraService(final ApplicationLinkService applicationLinkService) {
        this.applicationLinkService = applicationLinkService;
    }

    @Override
    public JiraIssue getIssue(final String key) {
        try {
            ApplicationLink applicationLink = applicationLinkService.getPrimaryApplicationLink(JiraApplicationType.class);
            ApplicationLinkRequest request = createRequest(applicationLink, Request.MethodType.GET, String.format("%s/%s?fields=summary", JIRA_ISSUE_BASE_URL, key));
            String response = request.execute();
            JsonObjects.GetIssueResponse getIssueResponse = GSON.fromJson(response, JsonObjects.GetIssueResponse.class);
            return new JiraIssue(getIssueResponse.key, getIssueUrl(applicationLink, key), getIssueResponse.fields.summary);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JiraIssue createIssue(final String summary) {
        try {
            ApplicationLink applicationLink = applicationLinkService.getPrimaryApplicationLink(JiraApplicationType.class);
            ApplicationLinkRequest request = createRequest(applicationLink, Request.MethodType.POST, JIRA_ISSUE_BASE_URL);
            JsonObjects.CreateIssueRequest createIssueRequest = new JsonObjects.CreateIssueRequest(PROJECT_ID, ISSUE_TYPE_ID, summary);
            String json = GSON.toJson(createIssueRequest);
            request.setRequestBody(json);
            String response = request.execute();
            JsonObjects.CreateIssueResponse createIssueResponse = GSON.fromJson(response, JsonObjects.CreateIssueResponse.class);
            return new JiraIssue(createIssueResponse.key, getIssueUrl(applicationLink, createIssueResponse.key), summary);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateIssue(final String key, final String summary) {
        try {
            ApplicationLink applicationLink = applicationLinkService.getPrimaryApplicationLink(JiraApplicationType.class);
            ApplicationLinkRequest request = createRequest(applicationLink, Request.MethodType.PUT, String.format("%s/%s", JIRA_ISSUE_BASE_URL, key));
            JsonObjects.UpdateIssueRequest updateIssueRequest = new JsonObjects.UpdateIssueRequest(summary);
            String json = GSON.toJson(updateIssueRequest);
            request.setRequestBody(json);
            request.execute();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private ApplicationLinkRequest createRequest(final ApplicationLink applicationLink, final Request.MethodType methodType, final String url) {
        try {
            if (applicationLink == null) { throw new RuntimeException("No JIRA application link found."); }
            ApplicationLinkRequest request = applicationLink.createAuthenticatedRequestFactory().createRequest(methodType, applicationLink.getRpcUrl() + url);
            request.addHeader("Content-Type", "application/json");
            return request;
        }
        catch (CredentialsRequiredException e) {
            throw new RuntimeException(e);
        }
    }

    private String getIssueUrl(final ApplicationLink applicationLink, final String key) {
        return String.format("%s/browse/%s", applicationLink.getRpcUrl(), key);
    }
}
