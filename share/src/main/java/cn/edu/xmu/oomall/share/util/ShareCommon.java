package cn.edu.xmu.oomall.share.util;

import org.bouncycastle.asn1.cms.TimeStampAndCRL;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 工具类
 * @author Fiber W.
 * created at 11/23/20 6:53 PM
 * @detail cn.edu.xmu.oomall.share.util
 */
public class ShareCommon {

    private static final String timeRegex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";

    /**
     * 根据类型和id获取redisKey
     * @param clazz 类型
     * @param id
     * @return java.lang.String
     * @author Fiber W.
     * created at 11/23/20 6:57 PM
     */
    public static String getRedisKey(Class clazz, Long id) {
        String[] className = clazz.getName().split("\\.");
        return className[className.length - 1] + "_" + id;
    }

    /**
     * 根据spuid查询shopid外部调用接口
     * @param spuId 商品SPUID
     * @return java.lang.Long
     * @author Fiber W.
     * created at 11/24/20 10:34 AM
     */
    public static Long getShopIdBySpuId(Long spuId) {
        return Long.valueOf(1);
    }

    /**
     * 判断时间是否符合规范 null也符合规范
     * @param stringTime
     * @return boolean
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/5 下午6:58
    */
    public static boolean judgeTimeValid(String stringTime){
        if(null == stringTime){
            return true;
        }
        if(stringTime.matches(timeRegex)){
            return true;
        }
        return false;
    }

    /**
     * 将string类型转成LocalDateTime类型
     * @param timeString
     * @return java.time.LocalDateTime
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/5 下午7:10
    */
    public static LocalDateTime changeStringToLocalDateTime(String timeString){
        if(null != timeString){
            return Timestamp.valueOf(timeString).toLocalDateTime();
        }
        return null;
    }
}
