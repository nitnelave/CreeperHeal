package com.nitnelave.CreeperHeal.config;

import com.nitnelave.CreeperHeal.config.WCfgVal.CONFIG_FILES;

public enum OutDatedWCfgVal
{
    ;

    private OutDatedWCfgVal(String key, CONFIG_FILES file)
    {
        this.key = key;
        this.file = file;
    }

    private final String key;
    private final CONFIG_FILES file;

    public String getKey()
    {
        return key;
    }

    protected CONFIG_FILES getFile()
    {
        return file;
    }
}
