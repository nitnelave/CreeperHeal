package com.nitnelave.CreeperHeal.config;

import org.bukkit.configuration.file.YamlConfiguration;

class BooleanConfigValue extends ConfigValue<Boolean> {

    public BooleanConfigValue (CfgValEnumMember v, YamlConfiguration file) {
        super ((Boolean) v.getDefaultValue (), file, v.getKey ());
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
