package com.nitnelave.CreeperHeal.config;

import org.bukkit.configuration.file.YamlConfiguration;

class IntegerConfigValue extends ConfigValue<Integer> {

    /**
     * Instantiates a new boolean config value.
     * 
     * @param val
     *            The value represented.
     * @param file
     *            The configuration file in which the value is stored.
     */
    public IntegerConfigValue (CfgValEnumMember val, YamlConfiguration file) {
        super ((Integer) val.getDefaultValue (), file, val.getKey ());
    }

    @Override
    protected void load () {
        setValue (config.getInt (getKey (), getDefaultValue ()));
    }

}
