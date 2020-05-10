package com.zang.sftptrans.exception;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 异常抽象类封装
 *
 * @author xssdpgy
 * @version xssdpgy: AbstractExceptionWrapper.java,v1.0 2020/5/5 23:56 xssdpgy Exp $$
 * @since 1.0
 */
public abstract class AbstractExceptionWrapper extends RuntimeException {

    private static final long serialVersionUID = -7202420764788218870L;

    protected String errorCode;
    protected String errorMessage;
    protected Class<?> clazz;

    public AbstractExceptionWrapper() {
    }

    public AbstractExceptionWrapper(String message) {
        super(message);
        this.errorMessage = message;
    }

    public AbstractExceptionWrapper(String message, Throwable cause) {
        super(message, cause);
        this.errorMessage = message;
    }

    public AbstractExceptionWrapper(Throwable cause) {
        super(cause);
    }

    public AbstractExceptionWrapper(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorMessage = message;
    }

    protected static String format(String template, Object... values) {
        if (ArrayUtils.isEmpty(values)) {
            return template;
        } else {
            String replacedTemplate = template.replace("%", "%%");
            return StringUtils.isBlank(template) ? "" : String.format(replacedTemplate.replace("{}", "%s"), values);
        }
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }
}
