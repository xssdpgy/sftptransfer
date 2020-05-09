package com.zang.sftptrans.sftp;

import com.zang.sftptrans.enums.EnumBusinessError;
import com.zang.sftptrans.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * sftp连接缓存池
 *
 * @author zangjf
 * @version zangjf: SftpClientPool.java,v1.0 2020/5/9 0:25 zangjf Exp $$
 * @since 1.0
 */
@Slf4j
public class SftpClientPool extends GenericObjectPool<SftpClient> {

    /**
     * 使用默认的配置构造池
     *
     * @param factory
     * @return
     */
    public SftpClientPool(PooledObjectFactory<SftpClient> factory) {
        super(factory);
    }

    /**
     * 使用自定义配置构造池
     *
     * @param factory
     * @param config
     * @return
     */
    public SftpClientPool(PooledObjectFactory<SftpClient> factory, GenericObjectPoolConfig config) {
        super(factory, config);
    }

    @Override
    public SftpClient borrowObject() {
        try {
            return super.borrowObject();
        } catch (Exception e) {
            log.error("从缓存池获取sftp client实例失败！原因：{}", e.getMessage());
            throw new BusinessException(EnumBusinessError.SFTP_GET_CLIENT_FAILURE.getCode(), EnumBusinessError.SFTP_GET_CLIENT_FAILURE.getMsg());
        }
    }

    @Override
    public void returnObject(SftpClient obj) {
        if (null != obj) {
            super.returnObject(obj);
        }
    }
}
