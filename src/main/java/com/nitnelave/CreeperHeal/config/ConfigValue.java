package com.nitnelave.CreeperHeal.config;

import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

public abstract class ConfigValue<T> {

    private final String key;
    protected T value;
    private final T defaultValue;
    protected final YamlConfiguration config;
    protected final Logger log = Logger.getLogger ("Minecraft");

    protected ConfigValue (T value, YamlConfiguration config, String key) {
        defaultValue = value;
        this.config = config;
        this.key = key;
        this.value = value;
    }

    protected T getValue () {
        return value;
    }

    protected void setValue (T value) {
        this.value = value;
    }

    protected T getDefaultValue () {
        return defaultValue;
    }

    protected String getKey () {
        return key;
    }

    protected abstract void load ();

    protected void write () {
        config.set (key, value);
    }

}
