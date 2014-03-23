package com.nitnelave.CreeperHeal.config;

interface CfgValEnumMember {

    /**
     * Gets the default value.
     * 
     * @return The default value.
     */
    public Object getDefaultValue();

    /**
     * Gets the configuration path to the value.
     * 
     * @return The path.
     */
    public String getKey();
}
