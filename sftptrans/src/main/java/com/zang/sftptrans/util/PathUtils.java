package com.zang.sftptrans.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 路径工具类
 *
 * @author zangjf
 * @version zangjf: PathUtils.java,v1.0 2020/5/6 0:33 zangjf Exp $$
 * @since 1.0
 */
@Slf4j
public class PathUtils {

    public static final String FORWARD_SLASH = "/";
    public static final String BACK_SLASH = "\\";
    public static final String DOUBLE_FORWARD_SLASH = "//";
    public static final String DOUBLE_BACK_SLASH = "\\\\";
    public static final String MCD = ".";

    public static final String WIDELY_ACCEPTED_PATH_SEPARATOR = FORWARD_SLASH;

    /**
     * 拼接父子路径
     *
     * @param parentPath
     * @param subPaths
     * @return java.lang.String
     */
    public static Path toPath (String parentPath, String... subPaths){
        return Paths.get (parentPath, subPaths);
    }

    public static String appendPath (String parentPath, String... subPaths) {
        return StringUtils.replace (toPath (parentPath, subPaths).toString (), BACK_SLASH, WIDELY_ACCEPTED_PATH_SEPARATOR);
    }

    /**
     * 转为当前目录的子目录
     *
     * @param relativePath
     * @return java.lang.String
     */
    public static String convertToSubdirectory (String relativePath){
        relativePath = StringUtils.replace (relativePath, BACK_SLASH, WIDELY_ACCEPTED_PATH_SEPARATOR);
        if (StringUtils.startsWith (relativePath, MCD)) {
            return relativePath;
        }
        if (StringUtils.startsWith (relativePath, WIDELY_ACCEPTED_PATH_SEPARATOR)) {
            return MCD + relativePath;
        }
        return MCD + WIDELY_ACCEPTED_PATH_SEPARATOR + relativePath;
    }

    /**
     * 检查目标路径是否可写
     *
     * @param localFilePath
     */
    public static void checkWritable (Path localFilePath) {
        if (Files.exists (localFilePath)) {
            if (!Files.isWritable (localFilePath)) {
                throw new IllegalStateException (localFilePath + " is not writable.");
            }
        } else {
            Path parent = localFilePath.getParent ();
            try {
                Files.createDirectories (parent);
            } catch (IOException e) {
                throw new IllegalStateException ("Failed to create parent directory " + parent + " .");
            }
        }
    }
    public static Path checkWritable (String localFilePath) {
        if (StringUtils.isBlank (localFilePath)) {
            throw new IllegalArgumentException ("The param 'targetOutPath' cannot be blank.");
        }
        Path target = Paths.get (localFilePath);
        PathUtils.checkWritable (target);
        return target;
    }

    /**
     * 检查本地文件是否存在
     *
     * @param localFilePath
     * @return boolean
     */
    public static boolean checkFileExists (Path localFilePath) {
        return Files.exists (localFilePath, LinkOption.NOFOLLOW_LINKS);
    }

    /**
     * 检查本地目录是否存在
     *
     * @param localFilePath
     * @return boolean
     */
    public static boolean checkDirExists (Path localFilePath) {
        return Files.exists (localFilePath, LinkOption.NOFOLLOW_LINKS) && Files.isDirectory (localFilePath);
    }

    /**
     * 检查本地文件是否是文件
     *
     * @param localFilePath
     * @return boolean
     */
    public static boolean checkIsFile (Path localFilePath) {
        return Files.isReadable (localFilePath) && !Files.isDirectory (localFilePath, LinkOption.NOFOLLOW_LINKS) && !Files.isSymbolicLink (localFilePath);
    }

    /**
     * @desc 检查目标路径是否可读
     *
     * @param localFilePath
     * @return java.nio.file.Path
     */
    public static void checkReadable (Path localFilePath) {
        if (!Files.isReadable (localFilePath)) {
            throw new IllegalStateException (localFilePath.toString () + " is not readable.");
        }
    }
    public static Path checkReadable (String localFilePath) {
        if (StringUtils.isBlank (localFilePath)) {
            throw new IllegalArgumentException ("The param 'localFilePath' cannot be blank.");
        }
        Path target = Paths.get (localFilePath);
        if (!Files.isReadable (target)) {
            throw new IllegalStateException (target.toString () + " is not readable.");
        }
        return target;
    }

    /**
     * @desc 删除本地文件
     *
     * @param localFilePath
     * @return boolean
     */
    public static boolean deleteLocalFile (Path localFilePath) {
        try {
            Files.delete (localFilePath);
        } catch (IOException e) {
            log.error ("删除本地文件操作失败", e);
            /*throw new HsjryBizException (e, EnumProLoanError.DELETE_LOCAL_FILE_ERROR.getCode (), EnumProLoanError.DELETE_LOCAL_FILE_ERROR.getMsg ());*/
            return false;
        }
        return true;
    }



    private PathUtils () {
        // Utility class
    }
}
