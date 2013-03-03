package com.nitnelave.CreeperHeal.config;

import org.bukkit.configuration.file.YamlConfiguration;

class IntegerConfigValue extends ConfigValue<Integer> {

    public IntegerConfigValue (CfgValEnumMember v, YamlConfiguration file) {
        super ((Integer) v.getDefaultValue (), file, v.getKey ());
    }

    @Override
    protected void load () {
        try
        {
            setValue (config.getInt (getKey (), getDefaultValue ()));
        } catch (Exception e)
        {
            log.warning ("[CreeperHeal] Wrong value for " + getKey () + " field in file " + config.getName () + ". Defaulting to "
                    + Integer.toString (getDefaultValue ()));
            setValue (getDefaultValue ());
        }
    }

}
