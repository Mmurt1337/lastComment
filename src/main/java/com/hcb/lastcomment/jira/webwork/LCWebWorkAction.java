package com.hcb.lastcomment.jira.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.hcb.lastcomment.utils.PluginSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webwork.action.ServletActionContext;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@Named
public class LCWebWorkAction extends JiraWebActionSupport {
    private static final Logger log = LoggerFactory.getLogger(LCWebWorkAction.class);
    private final PluginSettingsService pluginSettingsService;

    @Inject
    public LCWebWorkAction (PluginSettingsFactory pluginSettingsFactory) {
        log.warn("created");
        this.pluginSettingsService = new PluginSettingsService(pluginSettingsFactory);
    }

    public String execute() throws Exception {
        HttpServletRequest req = ServletActionContext.getRequest();
        if ("POST".equalsIgnoreCase(req.getMethod())){
            String cfID = req.getParameter("cfID");
            log.warn(cfID);
            this.pluginSettingsService.setPluginSetting("LastComment", cfID);
            String cfield = this.pluginSettingsService.getPluginSetting("LastComment", (String)null);
            log.warn(cfield);
        }
        return super.execute();
    }

    public String doUpdate() {
        this.getRedirect("/secure/action/LCWebWorkAction.jspa");
        return null;
    }

}
