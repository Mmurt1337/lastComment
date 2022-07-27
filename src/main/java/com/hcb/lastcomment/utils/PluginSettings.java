package com.hcb.lastcomment.utils;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;



public class PluginSettings {
    private final PluginSettingsFactory pluginSettingsFactory;

    public PluginSettings(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    public void setPluginSetting(String key, String value) {
        this.pluginSettingsFactory.createGlobalSettings().put("customFiled_" + key, value);
    }

    public String getPluginSetting(String key, String defaultValue) {
        Object value = this.pluginSettingsFactory.createGlobalSettings().get("customFiled_" + key);
        return value == null ? defaultValue : value.toString();
    }

}


