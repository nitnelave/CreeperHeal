package com.nitnelave.CreeperHeal.config;

import org.bukkit.configuration.file.YamlConfiguration;

class StringConfigValue extends ConfigValue<String> {

    /**
     * Instantiates a new boolean config value.
     * 
     * @param val
     *            The value represented.
     * @param file
     *            The configuration file in which the value is stored.
     */
    public StringConfigValue(CfgVal val, YamlConfiguration file) {
        super((String) val.getDefaultValue(), file, val.getKey());
    }

    @Override
    protected void load() {
        setValue(config.getString(getKey(), getDefaultValue()));
    }

}
