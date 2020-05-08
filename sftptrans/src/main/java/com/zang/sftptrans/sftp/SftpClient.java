package com.zang.sftptrans.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.zang.sftptrans.bean.SftpAuthKeyUserInfo;
import com.zang.sftptrans.bean.SftpServerProperties;
import com.zang.sftptrans.enums.EnumBusinessError;
import com.zang.sftptrans.exception.BusinessException;
import com.zang.sftptrans.file.FileProgressMonitor;
import com.zang.sftptrans.util.PathUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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

    /**
     * 初始化sftp连接
     *
     * @param properties
     */
    private void init(SftpServerProperties properties) {
        try {
            JSch jsch = new JSch();
            if (StringUtils.isNotBlank(properties.getPrivateKeyFile())) {
                jsch.addIdentity(properties.getPrivateKeyFile());
            }
            Session session = jsch.getSession(properties.getUsername(), properties.getHost(), properties.getPort());
            switch (properties.getAuthType()) {
                case PASSWORD:
                    Objects.requireNonNull(properties.getPassword());
                    session.setPassword(properties.getPassword());
                    break;
                case PUBLIC_KEY:
                    Objects.requireNonNull(properties.getPrivateKeyFile());
                    if (StringUtils.isBlank(properties.getPassphrase())) {
                        throw new IllegalArgumentException("口令不为能空（私钥未加密时填任意值）");
                    }
                    session.setUserInfo(new SftpAuthKeyUserInfo(properties.getPassphrase()));
                    break;
            }
            // 设置timeout时间
            session.setTimeout(properties.getConnectTimeout());
            // 设置keep-alive消息发送间隔（milliseconds）
            session.setServerAliveCountMax(properties.getServerAliveCountMax());
            // 设置发送keep-alive消息的最大次数
            session.setServerAliveInterval(properties.getServerAliveInterval());
            //第一次登陆时候，是否需要提示信息
            session.setConfig("StrictHostKeyChecking", "no");
            //设置ssh的DH秘钥交换
            session.setConfig("kex", "diffie-hellman-group1-sha1");
            //跳过Kerberos username 身份验证提示
            session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
            // 通过Session建立链接
            synchronized (properties) {
                session.connect();
            }
            // 打开SFTP通道
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            // 建立SFTP通道的连接
            channel.connect();
            if (log.isDebugEnabled()) {
                log.debug("SSH Channel connected.session={},channel={}", session, channel);
            }
            this.rootDirectory = properties.getRoot();
            this.session = session;
            this.channel = channel;
        } catch (Exception e) {
            log.error("连接SFTP服务失败！打印配置：{}", properties);
            throw new BusinessException(e, EnumBusinessError.SFTP_CONNECTION_ERROR.getCode(), EnumBusinessError.SFTP_CONNECTION_ERROR.getMsg(), e.getMessage());

        }
    }

    //TODO other methods

    /**
     * 将文件流上传到sftp相对目录下，并返回全路径
     *
     * @param in            上传文件输入流
     * @param sftpDirectory 上传目录的相对路径
     * @param filename      上传文件名
     * @return
     */
    public Path uploadStreamToRelativePath(InputStream in, String sftpDirectory, String filename) {
        changeToDirectories(rootDirectory);
        if (StringUtils.isNotBlank(sftpDirectory)) {
            // 判断是否存在此目录不存在则新建此目录
            stretchIntoDirectories(PathUtils.convertToSubdirectory(sftpDirectory));
        }
        Path sftpPath = PathUtils.toPath(rootDirectory, sftpDirectory, filename);
        try (OutputStream out = channel.put(filename, new FileProgressMonitor(), ChannelSftp.OVERWRITE)) {
            IOUtils.copy(in, out);
            return sftpPath;
        } catch (SftpException | IOException e) {
            log.error("上传SFTP文件{}失败！原因：{}", sftpPath, e.getMessage());
            throw new BusinessException(e, EnumBusinessError.SFTP_UPLOAD_FILE_FAILURE.getCode(), EnumBusinessError.SFTP_UPLOAD_FILE_FAILURE.getMsg());
        }
    }

    /**
     * 将本地文件上传到sftp相对目录下，并返回全路径
     *
     * @param localFilePath 本地文件
     * @param sftpDirectory 上传目录的相对路径
     * @return
     */
    public Path uploadFileToRelativePath(Path localFilePath, String sftpDirectory) {
        PathUtils.checkReadable(localFilePath);
        changeToDirectories(rootDirectory);
        if (StringUtils.isNotBlank(sftpDirectory)) {
            // 判断是否存在此目录，不存在则新建此目录
            stretchIntoDirectories(PathUtils.convertToSubdirectory(sftpDirectory));
        }
        String filename = localFilePath.getFileName().toString();
        Path sftpPath = PathUtils.toPath(rootDirectory, sftpDirectory, filename);
        try (InputStream in = Files.newInputStream(localFilePath, StandardOpenOption.READ);
             OutputStream out = channel.put(filename, new FileProgressMonitor(), ChannelSftp.OVERWRITE)) {
            IOUtils.copy(in, out);
            return sftpPath;
        } catch (SftpException | IOException e) {
            log.error("上传SFTP文件{}失败！原因：{}", sftpPath, e.getMessage());
            throw new BusinessException(e, EnumBusinessError.SFTP_UPLOAD_FILE_FAILURE.getCode(), EnumBusinessError.SFTP_UPLOAD_FILE_FAILURE.getMsg());
        }
    }

    /**
     * 下载SFTP远程目录下的单个文件，并保存在本地
     *
     * @param sftpDirectory 目标文件所在目录的相对路径
     * @param sftpFilename  目标文件名
     * @param localSavePath 本地保存文件路径
     * @return void
     */
    public void downloadAsFile(String sftpDirectory, String sftpFilename, Path localSavePath) {
        PathUtils.checkWritable(localSavePath);
        changeToDirectories(rootDirectory);
        try (OutputStream os = Files.newOutputStream(localSavePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            channel.cd(PathUtils.convertToSubdirectory(sftpDirectory));
            channel.get(sftpFilename, os);
        } catch (SftpException | IOException e) {
            log.error("下载SFTP文件{}/{}/{}至本地{}失败！原因：{}", rootDirectory, sftpDirectory, sftpFilename, localSavePath, e.getMessage());
            throw new BusinessException(e, EnumBusinessError.SFTP_DOWNLOAD_FILE_FAILURE.getCode(), EnumBusinessError.SFTP_DOWNLOAD_FILE_FAILURE.getMsg());
        }
    }

    /**
     * 下载SFTP远程目录下的单个文件，并保存在本地
     *
     * @param sftpFilePath  目标文件的全路径
     * @param localSavePath 本地保存文件路径
     * @return void
     */
    public void downloadAsFile(String sftpFilePath, Path localSavePath) {
        PathUtils.checkWritable(localSavePath);
        changeToDirectories(rootDirectory);
        try (OutputStream os = Files.newOutputStream(localSavePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            channel.get(PathUtils.convertToSubdirectory(sftpFilePath), os);
        } catch (SftpException | IOException e) {
            log.error("下载SFTP文件{}至本地{}失败！原因：{}", sftpFilePath, localSavePath, e.getMessage());
            throw new BusinessException(e, EnumBusinessError.SFTP_DOWNLOAD_FILE_FAILURE.getCode(), EnumBusinessError.SFTP_DOWNLOAD_FILE_FAILURE.getMsg());
        }
    }

    /**
     * 下载SFTP远程目录下的单个文件
     *
     * @param relativeSftpDirectory 目标文件所在目录的相对路径
     * @param sftpFilename          目标文件名
     * @return java.io.InputStream
     */
    public InputStream downloadAsStream(String relativeSftpDirectory, String sftpFilename) {
        changeToDirectories(rootDirectory);
        try {
            channel.cd(PathUtils.convertToSubdirectory(relativeSftpDirectory));
            return channel.get(sftpFilename);
        } catch (SftpException e) {
            log.error("下载SFTP远程文件{}/{}/{}失败！原因：{}", rootDirectory, relativeSftpDirectory, sftpFilename, e.getMessage());
            throw new BusinessException(e, EnumBusinessError.SFTP_DOWNLOAD_FILE_FAILURE.getCode(), EnumBusinessError.SFTP_DOWNLOAD_FILE_FAILURE.getMsg());
        }
    }

    /**
     * 下载SFTP远程目录下的单个文件
     *
     * @param sftpFilePath 目标文件的全路径
     * @return java.io.InputStream
     */
    public InputStream downloadAsStream(String sftpFilePath) {
        changeToDirectories(rootDirectory);
        try {
            return channel.get(PathUtils.convertToSubdirectory(sftpFilePath), new FileProgressMonitor());
        } catch (SftpException e) {
            log.error("下载SFTP远程文件{}失败！原因：{}", sftpFilePath, e.getMessage());
            throw new BusinessException(e, EnumBusinessError.SFTP_DOWNLOAD_FILE_FAILURE.getCode(), EnumBusinessError.SFTP_DOWNLOAD_FILE_FAILURE.getMsg());
        }
    }


    /**
     * 删除sftp中的文件
     *
     * @param sftpFilePath 目标文件的全路径
     * @return void
     */
    public void delete(String sftpFilePath) {
        changeToDirectories(rootDirectory);
        try {
            channel.rm(PathUtils.convertToSubdirectory(sftpFilePath));
        } catch (SftpException ex) {
            log.error("删除SFTP远程文件{}失败！原因：{}", sftpFilePath, ex.getMessage());
            throw new BusinessException(ex, EnumBusinessError.SFTP_DELETE_FILE_FAILURE.getCode(), EnumBusinessError.SFTP_DELETE_FILE_FAILURE.getMsg());
        }
    }

    /**
     * 删除sftp中的文件
     *
     * @param sftpDirectory 目标文件所在目录的相对路径
     * @param filename      目标文件名称
     * @return void
     */
    public void delete(String sftpDirectory, String filename) {
        changeToDirectories(rootDirectory);
        try {
            channel.cd(PathUtils.convertToSubdirectory(sftpDirectory));
            channel.rm(filename);
        } catch (SftpException ex) {
            log.error("删除SFTP远程文件{}/{}/{}失败！原因：{}", rootDirectory, sftpDirectory, filename, ex.getMessage());
            throw new BusinessException(ex, EnumBusinessError.SFTP_DELETE_FILE_FAILURE.getCode(), EnumBusinessError.SFTP_DELETE_FILE_FAILURE.getMsg());
        }
    }

    /**
     * 查询文件属性
     *
     * @param sftpDirectory
     * @param filename
     * @return com.jcraft.jsch.SftpATTRS
     */
    public SftpATTRS getFileAttrs(String sftpDirectory, String filename) {
        changeToDirectories(rootDirectory);
        try {
            channel.cd(PathUtils.convertToSubdirectory(sftpDirectory));
            return channel.lstat(filename);
        } catch (SftpException ex) {
            log.error("查询SFTP文件{}/{}/{}属性失败！原因：{}", rootDirectory, sftpDirectory, filename, ex.getMessage());
            throw new BusinessException(ex, EnumBusinessError.SFTP_OPERATION_FAILURE.getCode(), EnumBusinessError.SFTP_OPERATION_FAILURE.getMsg());
        }
    }

    /**
     * 列出目录下的文件
     *
     * @param sftpDirectory 要列出的目录
     * @return UnmodifiableList<ChannelSftp.LsEntry>
     */
    public List<ChannelSftp.LsEntry> listFiles(String sftpDirectory) {
        changeToDirectories(rootDirectory);
        final List<ChannelSftp.LsEntry> rptFiles = new LinkedList<>();
        ChannelSftp.LsEntrySelector selector = entry -> {
            rptFiles.add(entry);
            return ChannelSftp.LsEntrySelector.CONTINUE;
        };
        try {
            channel.ls(PathUtils.convertToSubdirectory(sftpDirectory), selector);
            return Collections.unmodifiableList(rptFiles);
        } catch (SftpException ex) {
            log.error("查询SFTP目录{}所有文件列表失败！原因：{}", sftpDirectory, ex.getMessage());
            return null;
        }
    }

    /**
     * 列出目录下的名称以xx开头的第一个文件
     *
     * @param sftpDirectory  要列出的目录
     * @param filenamePrefix 文件名称xx开头
     * @return ChannelSftp.LsEntry
     */
    public ChannelSftp.LsEntry firstFileWithPrefix(String sftpDirectory, String filenamePrefix) {
        changeToDirectories(rootDirectory);
        final List<ChannelSftp.LsEntry> rptFiles = new LinkedList<>();
        ChannelSftp.LsEntrySelector selector = entry -> {
            SftpATTRS attrs = entry.getAttrs();
            if (!attrs.isDir() && !attrs.isLink()
                    && StringUtils.startsWith(entry.getFilename(), filenamePrefix)) {
                rptFiles.add(entry);
                return ChannelSftp.LsEntrySelector.BREAK;
            }
            return ChannelSftp.LsEntrySelector.CONTINUE;
        };
        try {
            channel.ls(PathUtils.convertToSubdirectory(sftpDirectory), selector);
            return rptFiles.size() > 0 ? rptFiles.get(0) : null;
        } catch (SftpException ex) {
            log.error("查询SFTP目录{}/{}下以{}开头的第一个文件失败！原因：{}", rootDirectory, sftpDirectory, filenamePrefix, ex.getMessage());
            return null;
        }
    }

    /**
     * 列出目录下的名称以xx开头的所有文件
     *
     * @param sftpDirectory  要列出的目录
     * @param filenamePrefix 文件名称xx开头
     * @return UnmodifiableList<ChannelSftp.LsEntry>
     */
    public List<ChannelSftp.LsEntry> listFilesWithPrefix(String sftpDirectory, String filenamePrefix) {
        changeToDirectories(rootDirectory);
        final List<ChannelSftp.LsEntry> rptFiles = new LinkedList<>();
        ChannelSftp.LsEntrySelector selector = entry -> {
            SftpATTRS attrs = entry.getAttrs();
            if (!attrs.isDir() && !attrs.isLink()
                    && StringUtils.startsWith(entry.getFilename(), filenamePrefix)) {
                rptFiles.add(entry);
            }
            return ChannelSftp.LsEntrySelector.CONTINUE;
        };
        try {
            channel.ls(sftpDirectory, selector);
            return Collections.unmodifiableList(rptFiles);
        } catch (SftpException ex) {
            log.error("查询SFTP目录{}/{}下以{}开头的所有文件列表失败！原因：{}", rootDirectory, sftpDirectory, filenamePrefix, ex.getMessage());
            return null;
        }
    }


    /**
     * 跳转至指定目录，并在目录不存在时尝试创建目录
     *
     * @param targetDirectories
     */
    public void changeToDirectories(String targetDirectories) {
        try {
            channel.cd(targetDirectories);
        } catch (SftpException e) {
            log.error("无法跳转至sftp目录：{}，原因：{}", targetDirectories, e.getMessage());
            throw new BusinessException(e, EnumBusinessError.SFTP_OPERATION_FAILURE.getCode(), EnumBusinessError.SFTP_OPERATION_FAILURE.getMsg());
        }
    }

    /**
     * 跳转至指定目录，并在目录不存在时尝试创建目录
     *
     * @param targetDirectories
     * @return void
     */
    public void stretchIntoDirectories(String targetDirectories) {
        try {
            channel.cd(targetDirectories);
        } catch (SftpException e) {
            try {
                channel.mkdir(targetDirectories);
                channel.cd(targetDirectories);
            } catch (SftpException ex) {
                log.error("无法在sftp服务器上创建目录{}", targetDirectories);
                throw new BusinessException(ex, EnumBusinessError.SFTP_UPLOAD_FILE_FAILURE.getCode(), EnumBusinessError.SFTP_UPLOAD_FILE_FAILURE.getMsg());
            }
        }
    }

    /**
     * 测试sftp服务联通性
     *
     * @return boolean
     */
    public boolean testConnect() {
        if (!this.initialized.get()) {
            throw new BusinessException(EnumBusinessError.SFTP_NOT_INITIALIZED.getCode(), EnumBusinessError.SFTP_NOT_INITIALIZED.getMsg());
        }
        return session.isConnected() && channel.isConnected();
    }

    /**
     * 关闭sftp服务连接
     *
     * @return void
     */
    public void disconnect() {
        if (channel != null) {
            channel.disconnect();
            if (log.isDebugEnabled()) {
                log.debug("SSH Channel disconnected.channel={}", channel);
            }
        }
        if (session != null) {
            session.disconnect();
            if (log.isDebugEnabled()) {
                log.debug("SSH session disconnected.session={}", session);
            }
        }
    }
}
