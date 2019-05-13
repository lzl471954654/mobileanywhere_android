package io.liuzhilin.mobileanywhere.util;



import java.math.BigDecimal;

import io.liuzhilin.mobileanywhere.bean.Point;

public class PointUtils {

    public static Point getPointByPointId(String pointId){
        String[] xy = pointId.split(":");
        if (xy.length < 2){
            Point point = new Point();
            point.setLatitude(0.0);
            point.setLongitude(0.0);
            return point;
        }
        String x = xy[0];
        String y = xy[1];
        Point point = new Point();
        point.setLongitude(new BigDecimal(x).doubleValue());
        point.setLatitude(new BigDecimal(y).doubleValue());
        point.setPointId(pointId);
        return point;
    }
}
