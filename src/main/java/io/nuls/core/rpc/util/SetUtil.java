package io.nuls.core.rpc.util;

import java.util.HashSet;
import java.util.Set;

public class SetUtil {

    public static <E> Set<E> of(E... elements) {
        Set<E> set = new HashSet<>();
        for (E e : elements) {
            set.add(e);
        }
        return set;
    }
}
