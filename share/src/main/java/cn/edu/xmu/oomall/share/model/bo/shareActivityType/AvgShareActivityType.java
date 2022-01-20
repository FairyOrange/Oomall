package cn.edu.xmu.oomall.share.model.bo.shareActivityType;

import cn.edu.xmu.oomall.share.dao.ShareDao;
import cn.edu.xmu.oomall.share.model.bo.BaseShareActivityType;
import cn.edu.xmu.oomall.share.model.bo.BeShare;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fiber W.
 * created at 11/28/20 6:37 PM
 * @detail cn.edu.xmu.oomall.share.model.bo
 */
@Component("avg")
public class AvgShareActivityType extends BaseShareActivityType {
    @Override
    public List<BeShare> calcRebate(ShareDao shareDao, Long price, Integer quantity, List<Pair<Long, Integer>> rebateList, List<BeShare> beShareList, Long orderId, RocketMQTemplate rocketMQTemplate) {
        int size = beShareList.size();
        if (size > 100) {
            size = 100;
        }
        Integer quantityAvg = quantity / size;
        List<BeShare> beShareRet = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            BeShare beShare = beShareList.get(i);
            Long newNum = shareDao.increaseQuantity(beShare.getShareId(), quantityAvg).getData();
            rocketMQTemplate.sendOneWay("quantity-topic", beShare.getShareId().toString());

            Long rank = newNum - quantityAvg;
            Integer rebate = this.calculateRebate(rank, price, quantityAvg.longValue(), rebateList);
            beShare.setRebate(rebate);
            beShare.setOrderId(orderId);
            beShareRet.add(beShare);
        }
        for (int i = 101; i < beShareList.size(); i++) {
            BeShare beShare = beShareList.get(i);
            beShare.setOrderId(Long.valueOf(0));
            beShare.setRebate(Integer.valueOf(0));
            beShareRet.add(beShare);
        }
        return beShareRet;
    }
}
