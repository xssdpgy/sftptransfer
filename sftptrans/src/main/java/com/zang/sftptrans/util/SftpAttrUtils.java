package com.zang.sftptrans.util;

import com.jcraft.jsch.SftpATTRS;
import lombok.extern.slf4j.Slf4j;

import javax.swing.text.StyledEditorKit;

/**
 * sftp文件属性工具
 *
 * @author xssdpgy
 * @version xssdpgy: SftpAttrUtils.java,v1.0 2020/5/7 23:29 xssdpgy Exp $$
 * @since 1.0
 */
@Slf4j
public class SftpAttrUtils {
    private static final long MOD_TIME_FACTOR = 1000L;

    /**
     * 获取sftp文件最后修改时间
     *
     * @param attrs
     * @return
     */
    public static long getLastModifiedTime(SftpATTRS attrs) {
        if (attrs == null || (attrs.getFlags() & SftpATTRS.SSH_FILEXFER_ATTR_ACMODTIME) == 0) {
            log.warn("unknown sftp file modtime.");
            return -1;
        }
        return attrs.getMTime() * MOD_TIME_FACTOR;
    }

    /**
     * 获取sftp文件大小
     *
     * @param attrs
     * @return
     */
    public static long getContentSize(SftpATTRS attrs) {
        if (attrs == null || (attrs.getFlags() & SftpATTRS.SSH_FILEXFER_ATTR_SIZE) == 0) {
            log.warn("unknown sftp file size.");
            return -1;
        }
        return attrs.getSize();
    }

    /**
     * 判断sftp文件类型是否是目录
     *
     * @param attrs
     * @return
     */
    public static Boolean isDirType(SftpATTRS attrs) {
        if ((attrs.getFlags() & SftpATTRS.SSH_FILEXFER_ATTR_PERMISSIONS) == 0) {
            log.warn("unknown sftp file permissions.");
            return null;
        }
        return attrs.isDir();
    }

    public SftpAttrUtils() {

    }
}
