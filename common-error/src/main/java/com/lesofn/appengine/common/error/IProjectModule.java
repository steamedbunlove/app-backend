package com.lesofn.appengine.common.error;

import com.google.common.base.Preconditions;
import com.lesofn.appengine.common.error.system.SystemProjectModule;

/**
 * 项目和模块的编码
 *
 * @author lishaofeng
 * @version 1.0 Created at: 2022-03-09 16:21
 */
public interface IProjectModule {

    /**
     * 项目编码
     */
    int getProjectCode();

    /**
     * 模块编码
     */
    int getModuleCode();

    /**
     * 项目名称
     */
    String getProjectName();

    /**
     * 模块名称
     */
    String getModuleName();

    static void check(IProjectModule required, IProjectModule input) {
        Preconditions.checkNotNull(required);
        if (input != SystemProjectModule.INSTANCE) {
            Preconditions.checkState(required == input,
                    "module not match, need: " + required.getClass().getSimpleName()
                            + " but input: " + input.getClass().getSimpleName());
        }
    }
}
