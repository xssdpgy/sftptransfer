package com.zang.sftptrans.exception;

/**
 * 业务异常类
 *
 * @author zangjf
 * @version zangjf: BusinessException.java,v1.0 2020/5/5 23:56 zangjf Exp $$
 * @since 1.0
 */
public class BusinessException extends AbstractExceptionWrapper{

    private static final long serialVersionUID = 2399071367670550797L;

    public BusinessException(String errorCode, String errorMessage, String... params) {
        super(format(errorMessage, (Object[])params));
        this.errorCode = errorCode;
        this.errorMessage = format(errorMessage, (Object[])params);
    }

    public BusinessException(Throwable t, String errorCode, String errorMessage, String... params) {
        super(format(errorMessage, (Object[])params), t);
        this.errorCode = errorCode;
        this.errorMessage = format(errorMessage, (Object[])params);
    }

    public BusinessException(String errorCode, String errorMessage, Throwable t) {
        super(errorMessage, t);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BusinessException(String errorCode, String errorMessage, Class<?> clazz, Throwable t) {
        super(errorMessage, t);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.clazz = clazz;
    }

    public BusinessException(String errorCode, String errorMessage, Class<?> clazz, String... params) {
        super(format(errorMessage, (Object[])params));
        this.errorCode = errorCode;
        this.errorMessage = format(errorMessage, (Object[])params);
        this.clazz = clazz;
    }

    public BusinessException(Throwable t, String errorCode, String errorMessage, Class<?> clazz, String... params) {
        super(format(errorMessage, (Object[])params), t);
        this.errorCode = errorCode;
        this.errorMessage = format(errorMessage, (Object[])params);
        this.clazz = clazz;
    }
}
