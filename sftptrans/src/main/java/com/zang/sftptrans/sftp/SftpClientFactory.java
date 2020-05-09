package com.zang.sftptrans.sftp;

import com.zang.sftptrans.bean.SftpServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * sftp客户端对象工厂
 *
 * @author zangjf
 * @version zangjf: SftpClientFactory.java,v1.0 2020/5/9 23:25 zangjf Exp $$
 * @since 1.0
 */
@Slf4j
public class SftpClientFactory extends BasePooledObjectFactory<SftpClient>{

    private SftpServerProperties server;

    public SftpClientFactory(SftpServerProperties properties) {
        this.server = properties;
    }

    @Override
    public SftpClient create(){
        return new SftpClient(server);
    }

    @Override
    public PooledObject<SftpClient> wrap(SftpClient sftpClient) {
        return new DefaultPooledObject<>(sftpClient);
    }

    @Override
    public boolean validateObject(PooledObject<SftpClient> p) {
        SftpClient client = p.getObject();
        return client.testConnect();
    }

    @Override
    public void destroyObject(PooledObject<SftpClient> p) {
        p.getObject().disconnect();
    }
}
