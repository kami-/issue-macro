package com.kami.confluence.issuemacro.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.google.gson.Gson;
import com.kami.confluence.issuemacro.jira.JiraIssue;
import com.kami.confluence.issuemacro.jira.JiraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IssueMacro implements Macro {
    private static final Logger LOGGER = LoggerFactory.getLogger(IssueMacro.class);
    private static final String TEMPLATE_PATH = "templates/issue-macro.vm";
    private static final String MACRO_NAME = "issue-macro";

    private final XhtmlContent xhtmlContent;
    private final JiraService jiraService;

    public IssueMacro(final XhtmlContent xhtmlContent, final JiraService jiraService) {
        this.xhtmlContent = xhtmlContent;
        this.jiraService = jiraService;
    }

    public String execute(Map<String, String> parameters, String macroBody, ConversionContext conversionContext) throws MacroExecutionException {
        final String body = conversionContext.getEntity().getBodyAsString();
        final String issueKey = parameters.get("issueKey");
        final String summary = parameters.get("summary");
        LOGGER.info("Macro issueKey is '{}'.", issueKey);
        LOGGER.info("Macro summary is '{}'.", summary);
        final Map<String, Object> context = MacroUtils.defaultVelocityContext();
        try {
            xhtmlContent.updateMacroDefinitions(body, conversionContext, new MacroDefinitionUpdater() {
                @Override
                public MacroDefinition update(MacroDefinition macroDefinition) {
                    if (MACRO_NAME.equals(macroDefinition.getName())) {
                        if (issueKey == null) {
                            //JiraIssue issue = jiraService.createIssue(summary);
                            Map<String, Object> typedParameters = macroDefinition.getTypedParameters();
                            LOGGER.info("params before '{}'", new Gson().toJson(macroDefinition.getParameters()));
                            LOGGER.info("typeaparams before '{}'", new Gson().toJson(typedParameters));
                            if (typedParameters.get("issueKey") == null && macroDefinition.getParameter("summary").equals(summary)) {
                                //typedParameters.put("issueKey", issue.getKey());
                                typedParameters.put("issueKey", "keeeeeeeeeeeeeeeeeey");
                            }
                            typedParameters.put("summary", "111111111111111111111111111111");
                            macroDefinition.setTypedParameters(typedParameters);
                            LOGGER.info("typeaparams after '{}'", new Gson().toJson(macroDefinition.getTypedParameters()));
                            macroDefinition.setParameter("summary", "111111111111111111111111111111");
                            LOGGER.info("params after '{}'", new Gson().toJson(macroDefinition.getParameters()));
                            //context.put("issue", issue);
                        } else {
                            JiraIssue getIssue = jiraService.getIssue(issueKey);
                        /*if (!getIssue.getSummary().equals(summary)) {
                            jiraService.updateIssue(issue.getKey(), issue.getSummary() + "#");
                        }*/
                            context.put("issue", getIssue);
                        }
                    }
                    macroDefinition.setParameter("summary", "111111111111111111111111111111");
                    return macroDefinition;
                }
            });
        } catch (XhtmlException e) {
            e.printStackTrace();
        }
        parameters.put("summary", "2222222222222222222222222222222222");
        return VelocityUtils.getRenderedTemplate(TEMPLATE_PATH, context);
    }

    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }
}
