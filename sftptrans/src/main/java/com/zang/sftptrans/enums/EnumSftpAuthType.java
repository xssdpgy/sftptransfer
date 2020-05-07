package com.zang.sftptrans.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * sftp服务器安全验证方式
 *
 * @author zangjf
 * @version zangjf: EnumSftpAuthType.java,v1.0 2020/5/6 0:13 zangjf Exp $$
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum EnumSftpAuthType {
    PASSWORD("1", "口令认证登录"),
    PUBLIC_KEY("2", "密匙认证登录");

    /**
     * 状态码
     */
    private String code;

    /**
     * 状态描述
     */
    private String description;

    /**
     * 根据编码查找枚举
     *
     * @param code 编码
     * @return {@link EnumSftpAuthType } 实例
     **/
    public static EnumSftpAuthType find(String code) {
        for (EnumSftpAuthType instance : EnumSftpAuthType.values()) {
            if (instance.getCode().equals(code)) {
                return instance;
            }
        }
        return null;
    }

}
