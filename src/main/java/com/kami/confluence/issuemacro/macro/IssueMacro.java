package com.kami.confluence.issuemacro.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Scanned
public class IssueMacro implements Macro{
    private final XhtmlContent xhtmlContent;

    @Autowired
    public IssueMacro(@ComponentImport final XhtmlContent xhtmlContent) {
        this.xhtmlContent = xhtmlContent;
    }

    public String execute(Map<String, String> parameters, String bodyContent, ConversionContext conversionContext) throws MacroExecutionException {
        String body  = conversionContext.getEntity().getBodyAsString();
        final List<MacroDefinition> macros = new ArrayList<MacroDefinition>();
        try {
            xhtmlContent.handleMacroDefinitions(body, conversionContext, new MacroDefinitionHandler() {
                public void handle(MacroDefinition macroDefinition) {
                    macros.add(macroDefinition);
                }
            });
        } catch (XhtmlException e) {
            throw new MacroExecutionException(e);
        }

        String content = "<p>";
        if (!macros.isEmpty())
        {
            content += "<table width=\"50%\">";
            content += "<tr><th>Macro Name</th><th>Has Body?</th></tr>";
            for (MacroDefinition definition : macros)
            {
                content += "<tr>";
                content += String.format("<td>%s</td><td>%s</td>", definition.getName(), definition.hasBody());
                content += "</tr>";
            }
            content += "</table>";
        }
        else
        {
            content += "You've done built yourself a macro! Nice work.";
        }
        return content + "</p>";
    }

    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }
}
