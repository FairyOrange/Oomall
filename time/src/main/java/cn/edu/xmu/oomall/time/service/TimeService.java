package cn.edu.xmu.oomall.time.service;

import cn.edu.xmu.oomall.other.service.IAdvertiseService;
import cn.edu.xmu.oomall.time.dao.TimeDao;
import cn.edu.xmu.oomall.time.model.bo.Time;
import cn.edu.xmu.oomall.util.ReturnObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 时间Service
 *
 * @author wwc
 * @date 2020/11/24 23:29
 * @version 1.0
 */
@Slf4j
@Service
public class TimeService {

    @Autowired
    private TimeDao timeDao;

    /**
     * 平台管理员新增时间段
     *
     * @param bo 时间段bo
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject insertTimesegment(Time bo) {
        ReturnObject returnObject = timeDao.insertTimesegment(bo);
        return returnObject;
    };

    /**
     * 平台管理员删除时间段
     *
     * @param bo 时间段bo
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteTimesegment(Time bo) {
        ReturnObject returnObject = timeDao.deleteTimesegment(bo);
        return returnObject;
    };

    /**
     * 管理员获取时间段列表
     *
     * @param bo 时间段bo
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject listSelectTimesegment(Time bo, Integer page, Integer pageSize) {
        ReturnObject returnObject = timeDao.listSelectTimesegment(bo, page, pageSize);
        return returnObject;
    };
}
