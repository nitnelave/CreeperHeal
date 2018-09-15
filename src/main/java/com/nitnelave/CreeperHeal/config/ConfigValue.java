package com.nitnelave.CreeperHeal.config;

import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

abstract class ConfigValue<T>
{

    private final String key;
    T value;
    private final T defaultValue;
    final YamlConfiguration config;
    protected final Logger log = Logger.getLogger("Minecraft");

    ConfigValue(T value, YamlConfiguration config, String key)
    {
        defaultValue = value;
        this.config = config;
        this.key = key;
        this.value = value;
    }

    T getValue()
    {
        return value;
    }

    void setValue(T value)
    {
        this.value = value;
    }

    T getDefaultValue()
    {
        return defaultValue;
    }

    String getKey()
    {
        return key;
    }

    protected abstract void load();

    void write()
    {
        config.set(key, value);
    }

}
