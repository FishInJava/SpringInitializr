package com.happyzombie.springinitializr.common.util;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * 套壳的目的是为了方便整体替换
 */
public class CollectionUtil {

    /**
     * 将一个list均分成n个list
     *
     * 每份n个
     */
    public static <T> List<List<T>> partition(List<T> source, int n) {
        return Lists.partition(source, n);
    }

    public static boolean isEmpty(final Collection<?> coll) {
        return CollectionUtils.isEmpty(coll);
    }

    public static boolean isNotEmpty(final Collection<?> coll) {
        return !CollectionUtils.isEmpty(coll);
    }

}
