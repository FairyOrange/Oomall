package cn.edu.xmu.oomall.share.service.mq;

import cn.edu.xmu.oomall.share.dao.BeShareDao;
import cn.edu.xmu.oomall.share.dao.ShareDao;
import cn.edu.xmu.oomall.share.model.bo.BeShare;
import cn.edu.xmu.oomall.util.JacksonUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.jdbc.ha.BestResponseTimeBalanceStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分享返点消费者
 * @author Fiber W.
 * created at 11/24/20 4:00 PM
 * @detail cn.edu.xmu.oomall.share.service.mq
 */
@Service
@Slf4j
@RocketMQMessageListener(topic = "rebate-topic", consumerGroup = "rebate-group")
public class ShareRebateListener implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private BeShareDao beShareDao;

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Override
    public void onMessage(String message) {

        JSONArray array = (JSONArray) JSON.parse(message);
        BeShare b = ((JSONObject) array.get(0)).toJavaObject(BeShare.class);
        Long userId = b.getCustomerId();
        Long spuId = b.getGoodsSkuId();
        String redisKey = "bs_lock" + userId + "_" + spuId;
        log.debug("接收消息+rebate");
        redisTemplate.delete(redisKey);

        for (Object object : array) {
            JSONObject obj = (JSONObject) object;
            BeShare beShare = obj.toJavaObject(BeShare.class);
            beShareDao.setBeShareRebate(beShare);
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        log.info("prepareStart: consumergroup =" + defaultMQPushConsumer.getConsumerGroup());
    }
}
