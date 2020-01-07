package com.zang.sftptrans.bean;

import lombok.Data;

/**
 * SFTP连接对象实体
 *
 * @author zangjf
 * @version zangjf: PartnerFileFTPBean.java,v1.0 2020/1/7 20:50 zangjf Exp $$
 * @since 1.0
 */
@Data
public class PartnerFileFTPBean {

    /**
     * 合作方
     */
    public String partner;
    /**
     * 合作方FTP服务器登陆主机
     */
    public String ip;
    /**
     * 合作方FTP服务器登陆主机端口
     */
    public String port;
    /**
     * 合作方FTP服务器登陆用户名
     */
    public String userName;
    /**
     * 合作方FTP服务器登陆口令
     */
    public String password;
    /**
     * 合作方FTP服务器文件路径
     */
    public String serverRoot;
    /**
     * DMZ区文件保存路径
     */
    public String localRoot;
    /**
     * 代理主机IP
     */
    public String proxyIp;
    /**
     * 代理主机端口
     */
    public String proxyPort;
    /**
     * 业务类型
     */
    public String partnerBusiType;
    /**
     * 是否证书登陆，1证书登陆，2密码登陆
     */
    public String isCert;
    /**
     * 密钥证书路径
     */
    public String certPath;
    /**
     * 证书登录口令
     */
    public String passphrase;
    /**
     * 扩展信息
     */
    public String ext;
}
