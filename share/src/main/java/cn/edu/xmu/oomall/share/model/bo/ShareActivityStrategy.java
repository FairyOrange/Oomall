package cn.edu.xmu.oomall.share.model.bo;

import cn.edu.xmu.oomall.share.dao.ShareDao;
import cn.edu.xmu.oomall.share.service.ShareActivityTypeFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.util.Pair;

import java.util.*;

/**
 * @author Fiber W.
 * created at 11/23/20 11:15 AM
 * @detail cn.edu.xmu.oomall.share.model.bo
 */
@Data
public class ShareActivityStrategy {
    private List<Pair<Long, Integer>> rebateList;

    public enum Type {
        FIRST(0, "first"),
        AVG(1, "avg");
        private static final Map<Integer, ShareActivityStrategy.Type> stateMap;

        static {
            // 由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (ShareActivityStrategy.Type enumState : values()) {
                stateMap.put(enumState.code, enumState);
            }
        }

        private int code;
        private String description;

        Type(int code, String description) {
            this.code = code;
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private Type firstOrAvg;
    private BaseShareActivityType shareActivityType;

    /**
     * 根据json生成分享活动规则
     * @param strategy 分享活动规则json
     * @author Fiber W.
     * created at 11/23/20 1:04 PM
     */
    public ShareActivityStrategy(String strategy) {
        JSONObject jsonObject = (JSONObject) JSON.parse(strategy);
        JSONArray jsonArray = jsonObject.getJSONArray("rule");
        this.firstOrAvg = Type.values()[jsonObject.getInteger("firstOrAvg")];
        rebateList = new ArrayList<>();

        for (Object object : jsonArray) {
            JSONObject obj = (JSONObject) object;
            Long num = obj.getLong("num");
            Integer rate = obj.getInteger("rate");
            rebateList.add(Pair.of(num, rate));
        }

        rebateList.sort(new Comparator<Pair<Long, Integer>>() {
            @Override
            public int compare(Pair<Long, Integer> o1, Pair<Long, Integer> o2) {
                if (o1.getFirst() > o2.getFirst()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

    }

    /**
     * 校验规则是否正确
     * @return boolean
     * @author Fiber W.
     * created at 12/2/20 11:42 AM
     */
    public boolean check() {
        for (int i = 0; i < this.rebateList.size() - 1; i++) {
            if (rebateList.get(i).getFirst() == rebateList.get(i + 1).getFirst()) {
                return false;
            }
        }
        return true;
    }

    public List<BeShare> calcRebate(ShareDao shareDao, Long price, Integer quantity, List<BeShare> beShareList, ShareActivityTypeFactory factory, Long orderId, RocketMQTemplate rocketMQTemplate) {
        String type = this.firstOrAvg.description;
        if (this.shareActivityType == null) {
            this.shareActivityType = factory.getActivityType(type);
        }
        List<BeShare> rebate = this.shareActivityType.calcRebate(shareDao, price, quantity, this.rebateList, beShareList, orderId, rocketMQTemplate);
        return rebate;
    }
}
