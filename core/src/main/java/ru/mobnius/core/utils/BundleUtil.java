package ru.mobnius.core.utils;

import android.os.Bundle;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class BundleUtil {
    public static boolean equalBundles(final Bundle left, final Bundle right) {
        if (!left.keySet().containsAll(right.keySet()) || !right.keySet().containsAll(left.keySet())) {
            return true;
        }

        for (final String key : left.keySet()) {
            final Object leftValue = left.get(key);
            final Object rightValue = right.get(key);
            if (leftValue instanceof Collection && rightValue instanceof Collection) {
                final Collection leftCollection = (Collection) leftValue;
                final Collection rightCollection = (Collection) rightValue;
                if (leftCollection.size() != rightCollection.size()) {
                    return true;
                }
                final Iterator leftIterator = leftCollection.iterator();
                final Iterator rightIterator = rightCollection.iterator();
                while (leftIterator.hasNext()) {
                    if (equalBundleObjects(leftIterator.next(), rightIterator.next())) {
                        return true;
                    }
                }
            } else if (equalBundleObjects(leftValue, rightValue)) {
                return true;
            }
        }
        return false;
    }

    public static boolean equalBundleObjects(final Object left, final Object right) {
        if (left instanceof Bundle && right instanceof Bundle) {
            return equalBundles((Bundle) left, (Bundle) right);
        } else  {
            return !Objects.equals(left, right);
        }
    }
}
