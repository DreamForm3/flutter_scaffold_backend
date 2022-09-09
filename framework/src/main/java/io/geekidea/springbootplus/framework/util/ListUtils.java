package io.geekidea.springbootplus.framework.util;

import java.util.ArrayList;
import java.util.List;

/**
 * List 工具类
 */
public class ListUtils {

    /**
     * String List 转换城 Long list
     * @param strList
     * @return
     */
    public static List<Long> convertList(List<String> strList) {
        if (strList == null) {
            return null;
        }
        List<Long> list = new ArrayList<Long>();
        for (String str : strList) {
            list.add(Long.parseLong(str));
        }

        return list;
    }

    /**
     * 把一个 List 按照指定大小切成 N 份
     * @param list 要切分的 List
     * @param groupSize 一份多大
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int groupSize) {
        if (groupSize < 1) {
            throw new IllegalArgumentException("每组大小不能小于 1");
        }
        if (list == null) {
            return null;
        }
        int length = list.size();
        // 计算可以分成多少组
        int num = (length + groupSize - 1) / groupSize;
        List<List<T>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = Math.min((i + 1) * groupSize, length);
            newList.add(list.subList(fromIndex, toIndex));
        }
        return newList;
    }
}
