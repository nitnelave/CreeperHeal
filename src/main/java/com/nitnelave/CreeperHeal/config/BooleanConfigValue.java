package com.nitnelave.CreeperHeal.config;

import org.bukkit.configuration.file.YamlConfiguration;

class BooleanConfigValue extends ConfigValue<Boolean> {

    /**
     * Instantiates a new boolean config value.
     * 
     * @param val
     *            The value represented.
     * @param file
     *            The configuration file in which the value is stored.
     */
    public BooleanConfigValue (CfgValEnumMember val, YamlConfiguration file) {
        super ((Boolean) val.getDefaultValue (), file, val.getKey ());
    }

    @Override
    protected void load () {
        try
        {
            setValue (config.getBoolean (getKey (), getDefaultValue ()));
        } catch (Exception e)
        {
            log.warning ("[CreeperHeal] Wrong value for " + getKey () + " field in file " + config.getName () + ". Defaulting to "
                    + Boolean.toString (getDefaultValue ()));
            setValue (getDefaultValue ());
        }

    }

}
