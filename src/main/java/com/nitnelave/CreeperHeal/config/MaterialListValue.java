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
        String[] split = tmp_str1.split(",");
        for (String elem : split)
        {
            elem = elem.trim();
            Material material = matchMaterial(elem.toUpperCase());
            if (material != null)
                set.add(material);
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
            return "AIR";
        StringBuilder b = new StringBuilder();
        for (Material material : value)
        {
            b.append(material.name());
            b.append(", ");
        }

        return b.delete(b.length() - 2, b.length()).toString();

    }

    /**
     * Gets a Material from a material name. Will return null if no matches are found.
     *
     * @param materialName
     *          the name of the Material
     * @return
     *          the Material, or `null` if no matching material is found
     */
    @SuppressWarnings("deprecation")
    static Material matchMaterial(String materialName) {
        materialName = materialName.trim();
        Material material = Material.matchMaterial(materialName.toUpperCase());
        if (material != null)
            return material;

        // Convert legacy numeric IDs
        int id;
        byte data;

        try
        {
            if (materialName.indexOf(':') >= 0)
            {
                String[] elemSplit = materialName.split(":");
                id = Integer.valueOf(elemSplit[0]);
                data = Byte.valueOf(elemSplit[1]);
            }
            else
            {
                id = Integer.valueOf(materialName);
                data = 0;
            }
        }
        catch (NumberFormatException e)
        {
            return null;
        }

        for (Material legacy : Material.values())
        {
            if (!legacy.name().startsWith("LEAGACY_"))
                continue;
            if (legacy.getId() == id)
                return legacy.getNewData(data).getItemType();
        }

        return null;
    }

}
