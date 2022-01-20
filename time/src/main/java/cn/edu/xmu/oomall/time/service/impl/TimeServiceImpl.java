package cn.edu.xmu.oomall.time.service.impl;

import cn.edu.xmu.oomall.other.model.TimeDTO;
import cn.edu.xmu.oomall.time.dao.TimeDao;
import cn.edu.xmu.oomall.other.service.ITimeService;
import cn.edu.xmu.oomall.util.ReturnObject;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 时间段接口实现类
 *
 * @author wwc
 * @date 2020/11/26 14:07
 * @version 1.0
 */
@DubboService // 注意这里的Serivce引用的是dubbo的包
public class TimeServiceImpl implements ITimeService {

    @Autowired
    private TimeDao timeDao;

    /**
     * 获得所有秒杀/广告时间段id
     * @author cxr
     * @param type (秒杀/广告)
     * @return 时间段id列表
     */
    @Override
    public ReturnObject<List<Long>> listSelectAllTimeSegmentId(Byte type) {
        return timeDao.listSelectAllTimeSegmentId(type);
    };

    /**
     * 判断秒杀/广告时段id是否存在
     * @author cxr
     * @param type 时段类型
     * @param id 时间段id
     * @return returnObject
     */
    @Override
    public ReturnObject<TimeDTO> getTimeSegmentId(Byte type, Long id){
        return timeDao.getTimeSegmentId(type, id);
    };

    /**
     * 获得当前(广告/秒杀)时段id
     * @author cxr
     * @return 当前时段id
     */
    @Override
    public ReturnObject<Long> getCurrentSegmentId(Byte type){
        return timeDao.getCurrentSegmentId(type);
    };
}
