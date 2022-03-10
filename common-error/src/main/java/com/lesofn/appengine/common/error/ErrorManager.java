package com.lesofn.appengine.common.error;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.lesofn.appengine.common.error.model.TreeNode;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author lishaofeng
 * @version 1.0 Created at: 2022-03-10 10:34
 */
public class ErrorManager {
    private static final BiMap<Integer, IErrorCode> GLOBAL_ERROR_CODE_MAP = HashBiMap.create();
    private static final Map<IErrorCode, IProjectModule> ERROR_PROJECT_MODULE_MAP = new ConcurrentHashMap<>();

    private static final Comparator<IProjectModule> PROJECT_MODULE_COMPARATOR = Comparator.comparingInt(IProjectModule::getProjectCode)
            .thenComparingInt(IProjectModule::getModuleCode);
    private static final Comparator<IErrorCode> ERROR_CODE_COMPARATOR = Comparator.comparingInt(IErrorCode::getNodeNum);

    public static void register(IProjectModule projectModule, IErrorCode errorCode) {
        Preconditions.checkNotNull(projectModule);
        Preconditions.checkArgument(projectModule.getProjectCode() >= 0);
        Preconditions.checkArgument(projectModule.getModuleCode() >= 0);
        Preconditions.checkArgument(errorCode.getNodeNum() >= 0);
        int code = genCode(projectModule, errorCode);
        Preconditions.checkArgument(!GLOBAL_ERROR_CODE_MAP.containsKey(code), "错误码重复:" + code);
        GLOBAL_ERROR_CODE_MAP.put(code, errorCode);
        ERROR_PROJECT_MODULE_MAP.put(errorCode, projectModule);
    }

    public static List<TreeNode> getAllErrorCodes() {
        return ERROR_PROJECT_MODULE_MAP.entrySet().stream()
                .sorted((it1, it2) -> ERROR_CODE_COMPARATOR.compare(it1.getKey(), it2.getKey()))
                .collect(Collectors.groupingBy(Map.Entry::getValue,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())))
                .entrySet()
                .stream()
                .sorted((it1, it2) -> PROJECT_MODULE_COMPARATOR.compare(it1.getKey(), it2.getKey()))
                .collect(Collectors.groupingBy(
                                e -> new TreeNode(e.getKey().getProjectCode(), e.getKey().getProjectName()),
                                Collectors.groupingBy(
                                        it -> new TreeNode(it.getKey().getModuleCode(), it.getKey().getModuleName()),
                                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                                )
                        )
                )
                .entrySet()
                .stream()
                .map(e -> {
                    TreeNode top = e.getKey();
                    List<TreeNode> middleNode = e.getValue()
                            .entrySet()
                            .stream()
                            .map(e1 -> {
                                TreeNode key = e1.getKey();
                                List<TreeNode> leftNode = e1.getValue().stream()
                                        .flatMap(Collection::stream)
                                        .map(errorCode -> new TreeNode(errorCode.getCode(), errorCode.getMsg()))
                                        .collect(Collectors.toList());
                                key.setNodes(leftNode);
                                return key;
                            })
                            .collect(Collectors.toList());
                    top.setNodes(middleNode);
                    return top;
                })
                .collect(Collectors.toList());
    }

    private static int genCode(IProjectModule projectModule, IErrorCode errorCode) {
        return projectModule.getProjectCode() * 10000 + projectModule.getModuleCode() * 100 + errorCode.getNodeNum();
    }

    static int genCode(IErrorCode errorCode) {
        return GLOBAL_ERROR_CODE_MAP.inverse().get(errorCode);
    }

    public static IProjectModule projectModule(IErrorCode errorCode) {
        return ERROR_PROJECT_MODULE_MAP.get(errorCode);
    }
}
