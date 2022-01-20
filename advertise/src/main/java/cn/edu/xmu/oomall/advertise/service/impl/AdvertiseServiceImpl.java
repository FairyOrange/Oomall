package cn.edu.xmu.oomall.advertise.service.impl;

import cn.edu.xmu.oomall.advertise.dao.AdvertiseDao;
import cn.edu.xmu.oomall.other.service.IAdvertiseService;
import cn.edu.xmu.oomall.util.ReturnObject;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 时间段接口实现类
 *
 * @author cxr
 * @date 2020/12/15
 * @version 1.0
 */
@DubboService // 注意这里的Serivce引用的是dubbo的包
public class AdvertiseServiceImpl implements IAdvertiseService {
    @Autowired
    private AdvertiseDao advertiseDao;

    /**
     * 获得所有秒杀/广告时间段id
     * @author cxr
     * @param segId 时段id
     * @return 时间段id列表
     */
    @Override
    public ReturnObject deleteTimeSegmentAdvertisements(Long segId) {
        return advertiseDao.deleteTimeSegmentAdvertisements(segId);
    };
}
