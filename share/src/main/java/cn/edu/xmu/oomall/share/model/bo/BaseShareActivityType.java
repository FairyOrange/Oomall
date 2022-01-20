package cn.edu.xmu.oomall.share.model.bo;

import cn.edu.xmu.oomall.share.dao.ShareDao;
import io.swagger.models.auth.In;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.util.Pair;

import java.util.List;

/**
 * @author Fiber W.
 * created at 11/27/20 2:57 PM
 * @detail cn.edu.xmu.oomall.share.model.bo
 */
public abstract class BaseShareActivityType {
    public abstract List<BeShare> calcRebate(ShareDao shareDao, Long price, Integer quantity, List<Pair<Long, Integer>> rebateList, List<BeShare> beShareList, Long orderId, RocketMQTemplate rocketMQTemplate);


    /**
     * 计算返点
     * @param rank 用户的排位
     * @param price 商品价格
     * @param quantity 购买数量(单位：0.01件)
     * @return java.lang.Long 返点数
     * @author Fiber W.
     * created at 11/23/20 11:22 AM
     */
    protected Integer calculateRebate(Long rank, Long price, Long quantity, List<Pair<Long, Integer>> rebateList) {
        int stage = rebateList.size();
        for (int i = 0; i < rebateList.size(); i++) {
            if (rank >= rebateList.get(i).getFirst() - 1) {
                stage = i;
                break;
            }
        }
        if (stage == rebateList.size()) {
            return Integer.valueOf(0);
        }
        Integer rebate = Integer.valueOf(0);
        Long myRank = rank;
        Long num = quantity;

        for (; stage >= 0; stage--) {
            Integer rate = rebateList.get(stage).getSecond();
            if (stage > 0) {
                Long nextStage = rebateList.get(stage - 1).getFirst();
                Long calcNum = Math.min(nextStage - 1, myRank + num) - myRank;
                rebate += (int)((calcNum / 100.0) * price * rate * 0.01);
                num -= calcNum;
                myRank += calcNum;

                if (num == 0) {
                    break;
                }

            } else {
                rebate += (int)(num / 100.0 * price * rate * 0.01);
            }


        }
        return rebate;
    }
}
