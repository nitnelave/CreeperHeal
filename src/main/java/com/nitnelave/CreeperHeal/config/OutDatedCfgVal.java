package com.nitnelave.CreeperHeal.config;

public enum OutDatedCfgVal {
    OP_ENFORCE("op-have-all-permissions", true),
    DROP_REPLACED_BLOCKS("replacement-conflict.drop-overwritten-blocks", true),
    PREVENT_FALL("performance.prevent-block-fall", true),
    LIGHTWEIHGTMODE("lightweight-mode", true);

    private final String key;
    private final boolean advanced;

    private OutDatedCfgVal (String key, boolean adv) {
        this.key = key;
        advanced = adv;
    }

    public String getKey () {
        return key;
    }

    public boolean isAdvanced () {
        return advanced;
    }

}
