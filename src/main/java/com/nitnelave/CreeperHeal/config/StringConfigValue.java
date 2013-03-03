package com.nitnelave.CreeperHeal.config;

import org.bukkit.configuration.file.YamlConfiguration;

class StringConfigValue extends ConfigValue<String> {

    public StringConfigValue (CfgVal v, YamlConfiguration file) {
        super ((String) v.getDefaultValue (), file, v.getKey ());
    }

    @Override
    protected void load () {
        setValue (config.getString (getKey (), getDefaultValue ()));
    }

}
