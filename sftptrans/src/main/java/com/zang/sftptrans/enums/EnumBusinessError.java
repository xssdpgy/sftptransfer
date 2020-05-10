package com.zang.sftptrans.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务异常类型枚举
 *
 * @author xssdpgy
 * @version xssdpgy: EnumBusinessError.java,v1.0 2020/5/6 0:28 xssdpgy Exp $$
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum EnumBusinessError {
    SFTP_CONNECTION_ERROR("1000", "sftp服务连接失败：{}"),
    SFTP_OPERATION_FAILURE("1001", "sftp操作失败"),
    SFTP_UPLOAD_FILE_FAILURE("1002", "sftp上传文件失败"),
    SFTP_DOWNLOAD_FILE_FAILURE("1003", "sftp下载文件失败"),
    SFTP_DELETE_FILE_FAILURE("1004", "sftp删除文件失败"),
    SFTP_NOT_INITIALIZED("1005", "获取sftp服务会话失败"),
    SFTP_GET_CLIENT_FAILURE("1006", "获取sftp实例失败"),

    ;


    /**
     * 状态码
     */
    private String code;

    /**
     * 状态描述
     */
    private String msg;

    /**
     * 根据编码查找枚举
     *
     * @param code 编码
     * @return {@link EnumBusinessError } 实例
     **/
    public static EnumBusinessError find(String code) {
        for (EnumBusinessError instance : EnumBusinessError.values()) {
            if (instance.getCode().equals(code)) {
                return instance;
            }
        }
        return null;
    }
}
