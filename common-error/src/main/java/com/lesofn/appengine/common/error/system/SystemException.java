package com.lesofn.appengine.common.error.system;

import com.lesofn.appengine.common.error.ErrorInfo;
import com.lesofn.appengine.common.error.IErrorCode;
import com.lesofn.appengine.common.error.IProjectModule;
import com.lesofn.appengine.common.error.exception.BaseException;

/**
 * @author sofn
 * @version 1.0 Created at: 2022-03-09 16:41
 */
public class SystemException extends BaseException {

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }

    public SystemException(ErrorInfo errorInfo) {
        super(errorInfo);
    }

    public SystemException(IErrorCode errorCode) {
        super(errorCode);
    }

    public SystemException(IErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    @Override
    protected IProjectModule projectModule() {
        return SystemProjectModule.INSTANCE;
    }
}
