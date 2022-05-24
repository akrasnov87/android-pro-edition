package ru.mobnius.core.utils;

import android.graphics.PointF;
import android.view.MotionEvent;

public class TouchUtil {
    /**
     *
     * @param event обработчик прикосновения
     * @return расстояние между двумя точками прикосновения
     */
    public static float spaceCalculation(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public static void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
