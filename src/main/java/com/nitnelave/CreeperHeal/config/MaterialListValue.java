package com.nitnelave.CreeperHeal.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashSet;

class MaterialListValue extends ConfigValue<HashSet<Material>>
{

    MaterialListValue(CfgValEnumMember v, YamlConfiguration file)
    {
        //noinspection unchecked
        super((HashSet<Material>) v.getDefaultValue(), file, v.getKey());
    }

    @Override
    protected void load()
    {
        HashSet<Material> set = new HashSet<>();
        String tmp_str1 = config.getString(getKey(), "").trim();
        String[] split = tmp_str1.split(",\\s?");
        for (String elem : split)
        {
            Material material = Material.getMaterial(elem.toUpperCase());
            if (material != null) {
                set.add(material);
            }
        }
        setValue(set);
    }

    @Override
    protected void write()
    {
        config.set(getKey(), formatList());
    }

    private String formatList()
    {
        if (value.isEmpty())
            return "0";
        StringBuilder b = new StringBuilder();
        for (Material material : value)
        {
            b.append(material.name());
            b.append(", ");
        }

        String blocklist = b.toString();
        return blocklist.substring(0, blocklist.length() - 2);

    }

}
