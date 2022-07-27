package com.hcb.lastcomment.listener;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import org.ofbiz.core.entity.GenericEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.component.ComponentAccessor;
import com.hcb.lastcomment.utils.PluginSettingsService;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.issue.util.IssueChangeHolder;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.ModifiedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import javax.inject.Inject;

@Component
public class IssueCommentListener{
    private static final Logger log = LoggerFactory.getLogger(IssueCommentListener.class);
    private PluginSettingsService pluginSettingsService;


    @Inject
    public IssueCommentListener(@JiraImport EventPublisher eventPublisher, PluginSettingsFactory pluginSettingsFactory) {
        log.debug("run IssueCommentListener");
        this.pluginSettingsService = new PluginSettingsService(pluginSettingsFactory);
        eventPublisher.register(this);
    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) throws GenericEntityException {
        Long eventTypeId = issueEvent.getEventTypeId();
        Issue issue = issueEvent.getIssue();
        Comment comm = null;
        if (eventTypeId.equals(EventType.ISSUE_COMMENTED_ID)){
            log.info("New Comment Created");
            comm = issueEvent.getComment();
        } if (comm == null) {
            log.debug("Expected Comment but none found");
        }else{
            log.debug("Comment Created was " + comm.getId());
            this.updateLastUpdatedCustomField(issueEvent);
        }

        log.debug("End of Comment creation");
    }

    private void updateLastUpdatedCustomField(IssueEvent issueEvent) {
        this.updateCustomField(issueEvent, "LastComment", issueEvent.getComment().getBody());
    }

    private void updateCustomField(IssueEvent issueEvent, String type, String newValue) {
        String cfield = this.pluginSettingsService.getPluginSetting(type, (String)null);
        if (cfield != null) {
            CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
            CustomField cf = customFieldManager.getCustomFieldObject(cfield);
            IssueChangeHolder changeHolder = new DefaultIssueChangeHolder();
            Object oldValue = cf.getValue(issueEvent.getIssue());
            if (oldValue != null && oldValue instanceof String && ((String)oldValue).length() > 255) {
                oldValue = ((String)oldValue).substring(0, 255);
            }

            if (newValue != null && newValue.length() > 255) {
                newValue = newValue.substring(0, 255);
            }

            cf.updateValue((FieldLayoutItem)null, issueEvent.getIssue(), new ModifiedValue(oldValue, newValue), changeHolder);
        }

    }

}