package com.zang.sftptrans.bean;

import com.zang.sftptrans.enums.EnumSftpAuthType;
import lombok.Data;

/**
 * Sftp服务端配置类
 *
 * @author xssdpgy
 * @version xssdpgy: SftpServerProperties.java,v1.0 2020/5/6 0:05 xssdpgy Exp $$
 * @since 1.0
 */
@Data
public class SftpServerProperties {

    /**
     * sftp服务器安全验证方式，默认口令认证登录
     */
    private EnumSftpAuthType authType = EnumSftpAuthType.PASSWORD;

    /**
     * sftp服务器IP地址
     */
    private String host;

    /**
     * 端口号
     */
    private int port = 22;

    /**
     * sftp用户名
     */
    private String username;

    /**
     * sftp用户密码
     */
    private String password;

    /**
     * sftp服务器根路径
     */
    private String root = "/";

    /**
     * ssh private key文件路径
     */
    private String privateKeyFile;

    /**
     * ssh private key passphrase
     */
    private String passphrase;

    /**
     * socket连接和读取超时时间 5 min
     */
    public int connectTimeout = 300000;

    /**
     * 发送keep-alive消息的间隔（milliseconds）
     * PS：在隧道无通信后，定时发送一个请求给服务器要求服务器响应，保证终端不会因为超时空闲而断开连接
     */
    private int serverAliveInterval = 0;

    /**
     * 向服务器发送保持活动消息的最大次数
     * PS：向服务器发送keep-alive消息，但接收不到任何响应的情况下，达成最大次数限制后，SSH客户端就自动断开连接并退出，将控制权交给你的监控程序
     */
    private int serverAliveCountMax = 1;
}
