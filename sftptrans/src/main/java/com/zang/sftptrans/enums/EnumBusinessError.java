package com.zang.sftptrans.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务异常类型枚举
 *
 * @author zangjf
 * @version zangjf: EnumBusinessError.java,v1.0 2020/5/6 0:28 zangjf Exp $$
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum EnumBusinessError {
    SFTP_CONNECTION_ERROR("1000","sftp服务连接失败：{}");


    /** 状态码 */
    private String code;

    /** 状态描述 */
    private String msg;

    /**
     * 根据编码查找枚举
     *
     * @param code 编码
     * @return {@link EnumBusinessError } 实例
     **/
    public static EnumBusinessError find(String code) {
        for (EnumBusinessError instance : EnumBusinessError.values()) {
            if (instance.getCode()
                    .equals(code)) {
                return instance;
            }
        }
        return null;
    }
}
