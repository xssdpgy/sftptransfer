package com.zang.sftptrans.file;

import com.jcraft.jsch.SftpProgressMonitor;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * sftp文件上传下载进度监控
 *
 * @author xssdpgy
 * @version xssdpgy: FileProgressMonitor.java,v1.0 2020/5/8 17:47 xssdpgy Exp $$
 * @since 1.0
 */
@Slf4j
public class FileProgressMonitor implements SftpProgressMonitor {

    /**
     * 百分比格式化器
     */
    private static final DecimalFormat df = new DecimalFormat("##.00%");
    /**
     * 打印日志时间间隔
     */
    private static final long LOGGING_INTERVAL = TimeUnit.SECONDS.toNanos(1);
    /**
     * 记录已传输的数据总大小
     */
    private long transmitted;
    /**
     * 记录文件总大小
     */
    private long total;
    /**
     * 记录传输开始时间
     */
    private long startTime;
    /**
     * 记录上一次打印日志时间
     */
    private long preLogTime;

    /**
     * 传输开始
     *
     * @param op
     * @param src
     * @param dest
     * @param max
     */
    @Override
    public void init(int op, String src, String dest, long max) {
        this.total = max;
        log.info("Start transmitting...");
        preLogTime = startTime = System.nanoTime();
    }

    /**
     * 传输中
     *
     * @param count
     * @return
     */
    @Override
    public boolean count(long count) {
        transmitted += count;
        if (total == 0) {
            log.info("Transmitting report: total = 0, transmitted={}", transmitted);
        } else if (transmitted == 0) {
            log.info("Transmitting report: total={}, transmitted = 0", total);
            preLogTime = System.nanoTime();
        } else if (System.nanoTime() - preLogTime > LOGGING_INTERVAL && transmitted < total) {
            log.info("Transmitting report: {}%", df.format(transmitted * 1.0 / total));
            preLogTime = System.nanoTime();
        }
        return true;
    }

    /**
     * 传输结束
     */
    @Override
    public void end() {
        log.info("Transmission completion time: {}ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
    }
}
