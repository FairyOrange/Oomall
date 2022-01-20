package cn.edu.xmu.oomall.share.service;

import cn.edu.xmu.oomall.share.model.bo.BaseShareActivityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fiber W.
 * created at 11/27/20 3:10 PM
 * @detail cn.edu.xmu.oomall.share.service
 */
@Service
public class ShareActivityTypeFactory {

    @Autowired
    Map<String, BaseShareActivityType> shareTypes = new ConcurrentHashMap<>(3);

    /**
     * 获取计算规则的对象
     * @param type 分享规则类型
     * @return ShareActivityType
     * @author Fiber W.
     * created at 11/27/20 3:13 PM
     */
    public BaseShareActivityType getActivityType(String type) {
        BaseShareActivityType shareActivityType = shareTypes.get(type);
        return shareActivityType;
    }
}
