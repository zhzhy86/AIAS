package me.aias.common.enums;

import lombok.Getter;

/**
 * 状态枚举
 * Status enumeration
 *
 * @author Calvin
 * @date 2021-06-20
 **/
@Getter
public enum ResEnum {
    SUCCESS("0000", "success"),
    ZIP_FILE_FAIL("0001", "Incorrect compression package type"),
    DECOMPRESSION_FAIL("0002", "Exception occurred during decompression of the compression package"),
    SYSTEM_ERROR("1001", "Internal system error");
    public String KEY;
    public String VALUE;

    private ResEnum(String key, String value) {
        this.KEY = key;
        this.VALUE = value;
    }
}
