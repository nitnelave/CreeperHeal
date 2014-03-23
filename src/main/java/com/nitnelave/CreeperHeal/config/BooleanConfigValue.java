package com.nitnelave.CreeperHeal.config;

import org.bukkit.configuration.file.YamlConfiguration;

class BooleanConfigValue extends ConfigValue<Boolean>
{

    /**
     * Instantiates a new boolean config value.
     * 
     * @param val
     *            The value represented.
     * @param file
     *            The configuration file in which the value is stored.
     */
    public BooleanConfigValue(CfgValEnumMember val, YamlConfiguration file)
    {
        super((Boolean) val.getDefaultValue(), file, val.getKey());
    }

    @Override
    protected void load()
    {
        setValue(config.getBoolean(getKey(), getDefaultValue()));
    }

}
