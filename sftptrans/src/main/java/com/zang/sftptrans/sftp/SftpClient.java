package com.zang.sftptrans.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.zang.sftptrans.bean.SftpAuthKeyUserInfo;
import com.zang.sftptrans.bean.SftpServerProperties;
import com.zang.sftptrans.enums.EnumBusinessError;
import com.zang.sftptrans.exception.BusinessException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * sftp客户端
 *
 * @author zangjf
 * @version zangjf: SftpClient.java,v1.0 2020/5/6 0:20 zangjf Exp $$
 * @since 1.0
 */
@Slf4j
@Data
public class SftpClient {

    private volatile AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     * sftp root目录
     */
    private String rootDirectory;

    /**
     * JSch server session
     */
    private Session session;
    /**
     * JSch sftp channel
     */
    private ChannelSftp channel;

    public SftpClient(SftpServerProperties properties) {
        init(properties);
        initialized.compareAndSet(false, true);
    }

    private void init(SftpServerProperties properties){
        try {
            JSch jsch = new JSch();
            if(StringUtils.isNotBlank(properties.getPrivateKeyFile())){
                jsch.addIdentity(properties.getPrivateKeyFile());
            }
            Session session = jsch.getSession (properties.getUsername (), properties.getHost (), properties.getPort ());
            switch (properties.getAuthType ()) {
                case PASSWORD:
                    Objects.requireNonNull (properties.getPassword ());
                    session.setPassword (properties.getPassword ());
                    break;
                case PUBLIC_KEY:
                    Objects.requireNonNull (properties.getPrivateKeyFile ());
                    if (StringUtils.isBlank (properties.getPassphrase ())) {
                        throw new IllegalArgumentException ("口令不为能空（私钥未加密时填任意值）");
                    }
                    session.setUserInfo(new SftpAuthKeyUserInfo(properties.getPassphrase ()));
                    break;
            }
            // 设置timeout时间
            session.setTimeout (properties.getConnectTimeout ());
            // 设置keep-alive消息发送间隔（milliseconds）
            session.setServerAliveCountMax (properties.getServerAliveCountMax ());
            // 设置发送keep-alive消息的最大次数
            session.setServerAliveInterval (properties.getServerAliveInterval ());
            //第一次登陆时候，是否需要提示信息
            session.setConfig ("StrictHostKeyChecking", "no");
            //设置ssh的DH秘钥交换
            session.setConfig ("kex", "diffie-hellman-group1-sha1");
            //跳过Kerberos username 身份验证提示
            session.setConfig ("PreferredAuthentications", "publickey,keyboard-interactive,password");
            // 通过Session建立链接
            synchronized (properties) {
                session.connect ();
            }
            // 打开SFTP通道
            ChannelSftp channel = (ChannelSftp) session.openChannel ("sftp");
            // 建立SFTP通道的连接
            channel.connect ();
            if (log.isDebugEnabled ()) {
                log.debug ("SSH Channel connected.session={},channel={}", session, channel);
            }
            this.rootDirectory = properties.getRoot ();
            this.session = session;
            this.channel = channel;
        } catch (Exception e) {
            log.error ("连接SFTP服务失败！打印配置：{}", properties);
            throw new BusinessException(e, EnumBusinessError.SFTP_CONNECTION_ERROR.getCode (), EnumBusinessError.SFTP_CONNECTION_ERROR.getMsg (), e.getMessage ());

        }
    }

    //TODO other methods
}
