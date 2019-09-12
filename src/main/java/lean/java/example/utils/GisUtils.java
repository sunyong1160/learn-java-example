
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author weixin
 * 2019/6/28 10:30
 * 根据中心坐标，半径获取圆上点集
 */
public class GisUtils {

    /**
     * 反向转换程序中的迭代次数
     */
    private final int iterativeTimes = 10;
    /**
     * 反向转换程序中的迭代初始值
     */
    private final double iterativeValue = 0;
    /**
     * 椭球体长轴,千米
     */
    private double _A = 6378.137;
    /**
     * 椭球体短轴,千米
     */
    private double _B = 6356.752314;
    /**
     * 标准纬度,弧度
     */
    private double _B0 = 0;
    /**
     * 原点经度,弧度
     */
    private double _L0 = 0;
    /**
     * 默认点集点数量
     */
    private static final int _defaultNumPoints = 40;
    /**
     * 地球半径
     */
    //public static final double _R = 6371000.79;

    private GisUtils() {
    }

    /**
     * 获取实例
     * @author
     * @date  10:05
     */
    public static GisUtils getInstance(){
        return new GisUtils();
    }

    /**
     * 角度到弧度的转换
     * @author
     * @date
     */
    public double degreeToRad(double degree) {
        return Math.PI * degree / 180;
    }

    /**
     * 弧度到角度的转换
     * @author
     * @date
     */
    public double radToDegree(double rad) {
        return (180 * rad) / Math.PI;
    }

    /**
     * 设定_A与_B
     * @author
     * @date
     */
    public void setAB(double a, double b) {
        if (a <= 0 || b <= 0) {
            return;
        }
        _A = a;
        _B = b;
    }

    /**
     * 设定_B0
     * @author
     * @date
     */
    public void setLB0(double pmtL0, double pmtB0) {
        double l0 = degreeToRad(pmtL0);
        if (l0 < -Math.PI || l0 > Math.PI) {
            return;
        }
        _L0 = l0;

        double b0 = degreeToRad(pmtB0);
        if (b0 < -Math.PI / 2 || b0 > Math.PI / 2) {
            return;
        }
        _B0 = b0;
    }

    /**
     * 经纬度转XY坐标
     * pmtLB0: 参考点经纬度
     * pmtLB1: 要转换的经纬度
     * 返回值: 直角坐标，单位：公里
     * @author
     * @date
     */
    public Point lBToXY(Point pmtLB0, Point pmtLB1) {
        setLB0(pmtLB0.getLng(), pmtLB0.getLat());

        double B = degreeToRad(pmtLB1.getLat());
        double L = degreeToRad(pmtLB1.getLng());

        Point xy = new Point(0,0);
        //xy.setLat(0);
        //xy.setLng(0);

        double f/*扁率*/, e/*第一偏心率*/, e_/*第二偏心率*/, NB0/*卯酉圈曲率半径*/, K, dtemp;
        double E = Math.exp(1);
        if (L < -Math.PI || L > Math.PI || B < -Math.PI / 2 || B > Math.PI / 2) {
            return xy;
        }
        if (_A <= 0 || _B <= 0) {
            return xy;
        }
        f = (_A - _B) / _A;
        dtemp = 1 - (_B / _A) * (_B / _A);
        if (dtemp < 0) {
            return xy;
        }
        e = Math.sqrt(dtemp);
        dtemp = (_A / _B) * (_A / _B) - 1;
        if (dtemp < 0) {
            return xy;
        }
        e_ = Math.sqrt(dtemp);

        NB0 = ((_A * _A) / _B) / Math.sqrt(1 + e_ * e_ * Math.cos(_B0) * Math.cos(_B0));
        K = NB0 * Math.cos(_B0);
        xy.setLng(K * (L - _L0));
        xy.setLat(K * Math.log(Math.tan(Math.PI / 4 + (B) / 2) * Math.pow((1 - e * Math.sin(B)) / (1 + e * Math.sin(B)), e / 2)));
        double y0 = K * Math.log(Math.tan(Math.PI / 4 + (_B0) / 2) * Math.pow((1 - e * Math.sin(_B0)) / (1 + e * Math.sin(_B0)), e / 2));
        xy.setLat(xy.getLat() - y0);

        //正常的Y坐标系（向上）转程序的Y坐标系（向下）
        xy.setLat(-xy.getLat());

        return xy;
    }

    /**
     * XY坐标转经纬度
     * pmtLB0: 参考点经纬度
     * pmtXY: 要转换的XY坐标，单位：公里
     * 返回值: 经纬度
     * @author
     * @date
     */
    public Point xYtoLB(Point pmtLB0, Point pmtXY) {
        setLB0(pmtLB0.getLng(), pmtLB0.getLat());

        double X = pmtXY.getLng();
        double Y = -pmtXY.getLat();//程序的Y坐标系（向下）转正常的Y坐标系（向上）

        double B = 0, L = 0;

        Point lb = new Point(0,0);
        //lb.setLat(0);
        //lb.setLng(0);

        double f/*扁率*/, e/*第一偏心率*/, e_/*第二偏心率*/, NB0/*卯酉圈曲率半径*/, K, dtemp;
        double E = Math.exp(1);

        if (_A <= 0 || _B <= 0) {
            return lb;
        }
        f = (_A - _B) / _A;
        dtemp = 1 - (_B / _A) * (_B / _A);
        if (dtemp < 0) {
            return lb;
        }
        e = Math.sqrt(dtemp);
        dtemp = (_A / _B) * (_A / _B) - 1;
        if (dtemp < 0) {
            return lb;
        }
        e_ = Math.sqrt(dtemp);
        NB0 = ((_A * _A) / _B) / Math.sqrt(1 + e_ * e_ * Math.cos(_B0) * Math.cos(_B0));
        K = NB0 * Math.cos(_B0);

        double y0 = K * Math.log(Math.tan(Math.PI / 4 + (_B0) / 2) * Math.pow((1 - e * Math.sin(_B0)) / (1 + e * Math.sin(_B0)), e / 2));
        Y = Y + y0;

        L = X / K + _L0;
        B = iterativeValue;

        for (int i = 0; i < iterativeTimes; i++) {
            B = Math.PI / 2 - 2 * Math.atan(Math.pow(E, (-Y / K)) * Math.pow(E, (e / 2) * Math.log((1 - e * Math.sin(B)) / (1 + e * Math.sin(B)))));
        }

        lb.setLng(radToDegree(L));
        lb.setLat(radToDegree(B));

        return lb;
    }


    /**
     * 获取圆形区域点集（返回区域点集数量默认40个）
     *
     * @author
     * @date  9:22
     * @param centerPointLat    中心点纬度
     * @param centerPointLng    中心点经度
     * @param radius            圆形区域半径（单位：千米）
     * @return
     */
    public List<double[]> getCircularAreaPoints(double centerPointLng,double centerPointLat,
                                                double radius){
        return getCircularAreaPoints(new Point(centerPointLng, centerPointLat),
                radius, _defaultNumPoints);
    }

    /**
     * 获取圆形区域点集
     *
     * @author
     * @date  9:22
     * @param centerPointLng    中心点经度
     * @param centerPointLat    中心点纬度
     * @param radius            圆形区域半径（单位：千米）
     * @param numPoints         返回圆区域点数量
     * @return
     */
    public List<double[]> getCircularAreaPoints(double centerPointLng,double centerPointLat,
                                                double radius, int numPoints){
        return getCircularAreaPoints(new Point(centerPointLng,centerPointLat),
                radius, numPoints);
    }

    /**
     * 获取圆形区域点集
     *
     * @author
     * @date  9:13
     * @param centerPoint   圆心点（平面坐标转经纬度参考点）
     * @param radius        圆形区域半径（单位：千米）
     * @param numPoints     返回圆区域点数量
     * @return
     */
    public List<double[]> getCircularAreaPoints(Point centerPoint, double radius, int numPoints) {
        List<double[]> points =  Lists.newArrayList();
        double phase = 2 * Math.PI / numPoints;
        Point tmpPoint = null;
        for (int i = 0; i < numPoints; i++) {
            /**
             * 计算坐标点
             */
            double dx = (radius * Math.cos(i * phase));
            double dy = (radius * Math.sin(i * phase));

            /**
             * 转换成经纬度
             */
            tmpPoint = xYtoLB(new Point(centerPoint.getLat(),centerPoint.getLng()),
                    new Point(dx, dy));
            points.add(new double[]{roundDown6(tmpPoint.getLng()),
                    roundDown6(tmpPoint.getLat())});
        }
        return points;
    }

    /**
     * 获取圆形区域点集（地图点集）
     *
     * @author
     * @date  9:13
     * @param centerPoint   圆心点（平面坐标转经纬度参考点）
     * @param radius        圆形区域半径（单位：千米）
     * @param numPoints     返回圆区域点数量
     * @return
     */
    public List<Point> getCircularAreaMapPoints(Point centerPoint, double radius, int numPoints) {
        List<Point> points = Lists.newArrayList();
        double phase = 2 * Math.PI / numPoints;
        Point tmpPoint = null;
        for (int i = 0; i < numPoints; i++) {
            /**
             * 计算坐标点
             */
            double dx = (radius * Math.cos(i * phase));
            double dy = (radius * Math.sin(i * phase));

            /**
             * 转换成经纬度
             */
            tmpPoint = xYtoLB(new Point(centerPoint.getLng(),centerPoint.getLat()),
                    new Point(dx, dy));
            points.add(new Point(roundDown6(tmpPoint.getLng()),
                    roundDown6(tmpPoint.getLat())));
        }
        return points;
    }

    /**
     * 获取圆形区域点集（返回区域点集数量默认40个）（地图点集）
     *
     * @author
     * @date  9:22
     * @param centerPointLat    中心点纬度
     * @param centerPointLng    中心点经度
     * @param radius            圆形区域半径（单位：千米）
     * @return
     */
    public String getCircularAreaMapPoints(double centerPointLng,double centerPointLat,
                                           double radius){
        List<Point> points = getCircularAreaMapPoints(new Point(centerPointLng, centerPointLat),
                radius, _defaultNumPoints);
        return GsonUtil.to(points);
    }

    /**
     * 获取圆形区域点集（返回区域点集数量默认40个）（地图点集）
     *
     * @author
     * @date  9:22
     * @param centerPointLat    中心点纬度
     * @param centerPointLng    中心点经度
     * @param radius            圆形区域半径（单位：千米）
     * @return
     */
    public String getCircularAreaArrayPoints(double centerPointLng,double centerPointLat,
                                             double radius){
        List<double[]> points = getCircularAreaMapPoints(new Point(centerPointLng, centerPointLat),
                radius, _defaultNumPoints).stream().map(Point::getPoint).collect(toList());
        return points.stream().map(line -> GsonUtil.to(line)).collect(Collectors.joining(","));
    }

    /**
     * 保留小数点后6位，不进位
     * @author
     * @date  10:14
     */
    public double roundDown6(double value) {
        return Double.valueOf(new BigDecimal(value).setScale(6,
                BigDecimal.ROUND_DOWN).doubleValue());
    }

    /**
     * 点
     */
    class Point {

        /**
         * 经纬度
         */
        private double[] point;
        /**
         * 经度
         */
        private double lng;
        /**
         * 纬度
         */
        private double lat;

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public Point(double lng, double lat) {
            this.lng = lng;
            this.lat = lat;
            this.point = new double[]{lng,lat};
        }

        public double[] getPoint() {
            return point;
        }

        public void setPoint(double[] point) {
            this.point = point;
        }
    }

    public static void main(String[] args) {
        //116.402544,39.959857
        long startTime = System.currentTimeMillis();//获取开始时间
        String tmp = GisUtils.getInstance().getCircularAreaArrayPoints(116.405994,39.916485, 1);
        System.out.println(tmp);
        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间

    }
}
