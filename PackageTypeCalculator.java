

/**
 * @author : zly
 * @date : 2024/9/22
 */
/*
* 通过BigDecimal包装类确认精度防止精度丢失
* */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class PackageTypeCalculator {

    // 转换系数
    private static final BigDecimal CM_TO_INCH = new BigDecimal("2.54");
    private static final BigDecimal KG_TO_LB = new BigDecimal("0.454");
    private static final BigDecimal VOLUME_WEIGHT_DIVISOR = new BigDecimal("250");

    // 类型定义阈值
    private static final BigDecimal MAX_WEIGHT_FOR_OUTSPACE = new BigDecimal("150");
    private static final BigDecimal MAX_LENGTH_FOR_OUTSPACE = new BigDecimal("108");
    private static final BigDecimal MAX_GIRTH_FOR_OUTSPACE = new BigDecimal("165");

    private static final BigDecimal MAX_GIRTH_FOR_OVERSIZE = new BigDecimal("165");
    private static final BigDecimal MIN_GIRTH_FOR_OVERSIZE = new BigDecimal("130");
    private static final BigDecimal MIN_LENGTH_FOR_OVERSIZE = new BigDecimal("96");
    private static final BigDecimal MAX_LENGTH_FOR_OVERSIZE = new BigDecimal("108");

    private static final BigDecimal MAX_WEIGHT_FOR_AHS = new BigDecimal("150");
    private static final BigDecimal MIN_WEIGHT_FOR_AHS = new BigDecimal("50");
    private static final BigDecimal MIN_LENGTH_FOR_AHS = new BigDecimal("48");
    private static final BigDecimal MAX_LENGTH_FOR_AHS = new BigDecimal("108");
    private static final BigDecimal MIN_WIDTH_FOR_AHS = new BigDecimal("30");
    private static final BigDecimal MIN_GIRTH_FOR_AHS = new BigDecimal("105");

    public static void main(String[] args) {
        // 示例输入
        List<String> result1 = calculatePackageType(new BigDecimal("68"), new BigDecimal("70"), new BigDecimal("60"), new BigDecimal("23"));
        List<String> result2 = calculatePackageType(new BigDecimal("114.50"), new BigDecimal("42"), new BigDecimal("26"), new BigDecimal("47.5"));
        List<String> result3 = calculatePackageType(new BigDecimal("162"), new BigDecimal("60"), new BigDecimal("11"), new BigDecimal("14"));
        List<String> result4 = calculatePackageType(new BigDecimal("113"), new BigDecimal("64"), new BigDecimal("42.5"), new BigDecimal("35.85"));
        List<String> result5 = calculatePackageType(new BigDecimal("114.5"), new BigDecimal("17"), new BigDecimal("51.5"), new BigDecimal("16.5"));

        System.out.println(result1);  // 输出: [AHS-WEIGHT, AHS-SIZE]
        System.out.println(result2);  // 输出: [AHS-WEIGHT]
        System.out.println(result3);  // 输出: [AHS-SIZE]
        System.out.println(result4);  // 输出: [OVERSIZE]
        System.out.println(result5);  // 输出: []
    }

    // 计算包裹类型
    public static List<String> calculatePackageType(BigDecimal length, BigDecimal width, BigDecimal height, BigDecimal weightKg) {
        List<String> packageTypes = new ArrayList<>();

        // 转换成英寸和磅
        BigDecimal lengthInch = length.divide(CM_TO_INCH, 2, RoundingMode.UP);
        BigDecimal widthInch = width.divide(CM_TO_INCH, 2, RoundingMode.UP);
        BigDecimal heightInch = height.divide(CM_TO_INCH, 2, RoundingMode.UP);
        BigDecimal weightLb = weightKg.divide(KG_TO_LB, 2, RoundingMode.UP);

        // 计算围长
        BigDecimal girth = lengthInch.add(widthInch.add(heightInch).multiply(new BigDecimal("2")));

        // 计算体积重
        BigDecimal volumeWeight = lengthInch.multiply(widthInch).multiply(heightInch).divide(VOLUME_WEIGHT_DIVISOR, 2, RoundingMode.UP);

        // 实重为产品重量和体积重之间的最大值
        BigDecimal realWeight = weightLb.max(volumeWeight).setScale(0, RoundingMode.UP);

        // 判断OUT_SPACE
        if (realWeight.compareTo(MAX_WEIGHT_FOR_OUTSPACE) > 0 ||
                lengthInch.compareTo(MAX_LENGTH_FOR_OUTSPACE) > 0 ||
                girth.compareTo(MAX_GIRTH_FOR_OUTSPACE) > 0) {
            packageTypes.add("OUT_SPACE");
            return packageTypes;
        }

        // 判断OVERSIZE
        if ((girth.compareTo(MIN_GIRTH_FOR_OVERSIZE) > 0 && girth.compareTo(MAX_GIRTH_FOR_OVERSIZE) <= 0) ||
                (lengthInch.compareTo(MIN_LENGTH_FOR_OVERSIZE) >= 0 && lengthInch.compareTo(MAX_LENGTH_FOR_OVERSIZE) < 0)) {
            packageTypes.add("OVERSIZE");
            return packageTypes;
        }

        // 判断AHS
        if (realWeight.compareTo(MIN_WEIGHT_FOR_AHS) > 0 && realWeight.compareTo(MAX_WEIGHT_FOR_AHS) <= 0) {
            packageTypes.add("AHS-WEIGHT");
        }
        if (girth.compareTo(MIN_GIRTH_FOR_AHS) > 0 ||
                (lengthInch.compareTo(MIN_LENGTH_FOR_AHS) >= 0 && lengthInch.compareTo(MAX_LENGTH_FOR_AHS) < 0) ||
                widthInch.compareTo(MIN_WIDTH_FOR_AHS) >= 0) {
            packageTypes.add("AHS-SIZE");
        }

        return packageTypes;
    }
}


