package cn.edu.xmu.oomall.share.service.mq;

import cn.edu.xmu.oomall.share.dao.ShareDao;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分享数量消费者
 * @author Fiber W.
 * created at 11/29/20 3:07 PM
 * @detail cn.edu.xmu.oomall.share.service.mq
 */
@Service
@Slf4j
@RocketMQMessageListener(topic = "quantity-topic", consumerGroup = "quantity-group")
public class ShareQuantityListener implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private ShareDao shareDao;

    @Override
    public void onMessage(String message) {
        Long id = Long.getLong(message);
        shareDao.setShareQuantity(id);
        log.debug("接收消息+quantity");
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        log.info("prepareStart: consumergroup =" + defaultMQPushConsumer.getConsumerGroup());
    }
}
