package com.nitnelave.CreeperHeal.config;

import java.util.HashSet;

import org.bukkit.configuration.file.YamlConfiguration;

import com.nitnelave.CreeperHeal.block.BlockId;

class BlockIdListValue extends ConfigValue<HashSet<BlockId>> {

    protected BlockIdListValue(CfgValEnumMember v, YamlConfiguration file) {
        super((HashSet<BlockId>) v.getDefaultValue(), file, v.getKey());
    }

    @Override
    protected void load() {
        HashSet<BlockId> set = new HashSet<BlockId>();
        String tmp_str1 = config.getString(getKey(), "").trim();
        String[] split = tmp_str1.split(",");
        try
        {
            for (String elem : split)
            {
                BlockId bId = new BlockId(elem);
                if (bId.getId() != 0)
                    set.add(bId);
            }
            setValue(set);
        } catch (NumberFormatException e)
        {
            setValue(getDefaultValue());
        }
    }

    @Override
    protected void write() {
        config.set(getKey(), formatList());
    }

    private String formatList() {
        if (value.isEmpty())
            return "0";
        StringBuilder b = new StringBuilder();
        for (BlockId block : value)
        {
            b.append(block.toString());
            b.append(", ");
        }

        String blocklist = b.toString();
        return blocklist.substring(0, blocklist.length() - 2);

    }

}
