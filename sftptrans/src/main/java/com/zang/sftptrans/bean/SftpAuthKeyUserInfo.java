package com.zang.sftptrans.bean;

import com.jcraft.jsch.UserInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SSH private key passphrase info
 *
 * @author xssdpgy
 * @version xssdpgy: SftpAuthKeyUserInfo.java,v1.0 2020/1/7 20:59 xssdpgy Exp $$
 * @since 1.0
 */
@Slf4j
@AllArgsConstructor
public class SftpAuthKeyUserInfo implements UserInfo {
    //SSH private key passphrase
    private String passphrase;

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean promptPassword(String s) {
        return false;
    }

    @Override
    public boolean promptPassphrase(String s) {
        return false;
    }

    @Override
    public boolean promptYesNo(String s) {
        return false;
    }

    @Override
    public void showMessage(String s) {
        log.info("SSH Message is: {}", s);
    }
}
