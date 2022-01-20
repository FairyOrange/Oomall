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
 * created at 11/27/20 3:03 PM
 * @detail cn.edu.xmu.oomall.share.model.bo
 */
@Component("first")
public class FirstShareActivityType extends BaseShareActivityType {
    @Override
    public List<BeShare> calcRebate(ShareDao shareDao, Long price, Integer quantity, List<Pair<Long, Integer>> rebateList, List<BeShare> beShareList, Long orderId, RocketMQTemplate rocketMQTemplate) {
        BeShare beShare = beShareList.get(0);
        Long newNum = shareDao.increaseQuantity(beShare.getShareId(), quantity).getData();
        rocketMQTemplate.sendOneWay("quantity-topic", beShare.getShareId().toString());

        Long rank = newNum - quantity;
        Integer rebate = this.calculateRebate(rank, price, quantity.longValue(), rebateList);
        beShare.setRebate(rebate);
        beShare.setOrderId(orderId);
        List<BeShare> beShareRet = new ArrayList<>();
        beShareRet.add(beShare);

        for (int i = 1; i < beShareList.size(); i++) {
            BeShare b = beShareList.get(i);
            b.setOrderId(Long.valueOf(0));
            b.setRebate(Integer.valueOf(0));
            beShareRet.add(b);
        }

        return beShareRet;
    }

}
