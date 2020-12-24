package com.chippy.core.common.utils;

import cn.hutool.core.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 集合相关便捷方法封装
 *
 * @author: chippy
 */
public class CollectionsUtils extends CollectionUtil {

    /**
     * 将传入数据按指定数量{len}切分为多个子集集合
     *
     * @author chippy
     */
    public static <T> List<List<T>> subListBySize(List<T> sourceList, int size) {
        if (ObjectsUtil.isEmpty(sourceList)) {
            return null;
        }

        List<List<T>> listGroup = new ArrayList<>();
        if (sourceList.size() < size) {
            listGroup.add(sourceList);
            return listGroup;
        }

        int listSize = sourceList.size();
        int toIndex = size;
        for (int i = 0; i < sourceList.size(); i += size) {
            if (i + size > listSize) {
                toIndex = listSize - i;
            }
            List<T> newList = sourceList.subList(i, i + toIndex);
            listGroup.add(newList);
        }
        return listGroup;
    }

    /**
     * 将传入数据按指定数量{batchNum}切分为指定数量的子集集合
     * 如果传入的元数据大小小于指定切分子集集合数量则直接返回一个子集集合数据
     *
     * @param sourceList 源数据集合
     * @param batchNum   转换后的个数
     * @author chippy
     */
    public static <T> List<List<T>> subListForBatchNum(List<T> sourceList, int batchNum) {
        if (ObjectsUtil.isEmpty(sourceList)) {
            throw new NullPointerException("argument to batch handler must not be null");
        }

        if (sourceList.size() <= batchNum) {
            return Collections.singletonList(sourceList);
        }

        List<List<T>> resultList = new ArrayList<>(batchNum);
        int remainder = sourceList.size() % batchNum;
        int divResult = sourceList.size() / batchNum;
        int maxIndex = 0;
        for (int i = 0; i < batchNum; i++) {
            int subIndex = i * divResult;
            int subedIndex = (i + 1) * divResult;
            resultList.add(sourceList.subList(subIndex, subedIndex));
            if (i == batchNum - 1) {
                maxIndex = subedIndex;
            }
        }

        if (remainder != 0) {
            resultList.add(sourceList.subList(maxIndex, maxIndex + remainder));
        }

        return resultList;
    }

}
