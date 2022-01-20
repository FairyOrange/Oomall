package cn.edu.xmu.oomall.time.dao;

import cn.edu.xmu.oomall.other.model.TimeDTO;
import cn.edu.xmu.oomall.time.mapper.TimeSegmentPoMapper;
import cn.edu.xmu.oomall.time.model.bo.Time;
import cn.edu.xmu.oomall.time.model.po.TimeSegmentPo;
import cn.edu.xmu.oomall.time.model.po.TimeSegmentPoExample;
import cn.edu.xmu.oomall.time.model.vo.TimeRetVo;
import cn.edu.xmu.oomall.util.Common;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 时间Dao
 *
 * @author wwc
 * @date 2020/11/24 23:30
 * @version 1.0
 */
@Slf4j
@Repository
public class TimeDao {

    @Autowired
    private TimeSegmentPoMapper timeMapper;

    /**
     * 平台管理员新增时间段
     *
     * @param bo 时间段bo
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    public ReturnObject insertTimesegment(Time bo) {
        TimeSegmentPo po = bo.createPo();
        TimeSegmentPoExample timePoExample = new TimeSegmentPoExample();
        TimeSegmentPoExample.Criteria timePoCriteria = timePoExample.createCriteria();
        timePoCriteria.andTypeEqualTo(bo.getType().getCode().byteValue());

        try {
            //所有时间段拎上来进行判断
            List<TimeSegmentPo> list = timeMapper.selectByExample(timePoExample);
            //寻找是否有重叠的时段
            LocalDateTime current = LocalDateTime.now();
            LocalDateTime begin = translateTime(current,bo.getBeginTime());
            LocalDateTime end = translateTime(current,bo.getEndTime());
            for(TimeSegmentPo timePo : list){
                LocalDateTime poBeginTime = translateTime(current,timePo.getBeginTime());
                LocalDateTime poEndTime = translateTime(current,timePo.getEndTime());
                //判断是否重叠
                if(!(!begin.isBefore(poEndTime)||!end.isAfter(poBeginTime))){
                    return new ReturnObject<>(ResponseCode.TIMESEG_CONFLICT);
                }
            }
            int ret = timeMapper.insertSelective(po);
            if (ret == 0) {
                // 插入失败
                log.debug("新增时间段失败：" + po.getId());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增时间段失败：%s", po.getId()));
            } else {
                // 插入成功
                log.debug("新增时间段: " + po.toString());
                // 通过反射获取插入时获得的记录
                bo.setId(po.getId());
                return new ReturnObject<>(bo);
            }

            //如果改了数据库的类型，就只需要用数据进行判断即可
//            timePoExample.createCriteria().andTypeEqualTo(po.getType())
//                    .andBeginTimeLessThan(po.getEndTime())
//                    .andEndTimeGreaterThan(po.getBeginTime());
//            timePoExample.or(timePoExample.createCriteria().andTypeEqualTo(po.getType())
//                    .andEndTimeGreaterThan(po.getBeginTime())
//                    .andBeginTimeLessThan(po.getEndTime()));
//            try {
//                long timeNum = timeMapper.countByExample(timePoExample);
//                if (timeNum > 0) {
//                    // 若有交叉的时间段
//                    return new ReturnObject<>(ResponseCode.TIMESEG_CONFLICT);
//                }
//            if (timeNum > 0) {
//                // 若有交叉的时间段
//                return new ReturnObject<>(ResponseCode.TIMESEG_CONFLICT);
//            }

        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    };

    /**
     * 平台管理员删除时间段
     *
     * @param bo 时间段bo
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    public ReturnObject deleteTimesegment(Time bo) {
        TimeSegmentPo segmentPo = timeMapper.selectByPrimaryKey(bo.getId());
        if(segmentPo == null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        TimeSegmentPo po = bo.createPo();
        TimeSegmentPoExample timePoExample = new TimeSegmentPoExample();
        TimeSegmentPoExample.Criteria timePoCriteria = timePoExample.createCriteria();
        timePoCriteria.andIdEqualTo(po.getId());
        timePoCriteria.andTypeEqualTo(po.getType());
        try {
            int ret = timeMapper.deleteByExample(timePoExample);
            if (ret == 0) {
                // 删除失败
                return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
            } else {
                // 删除成功
                log.debug("删除时间段: " + po.toString());
                return new ReturnObject<>(ResponseCode.OK);
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    };


    /**
     * 管理员获取时间段列表
     *
     * @param bo 时间段bo
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    public ReturnObject listSelectTimesegment(Time bo, Integer page, Integer pageSize) {
        TimeSegmentPo po = bo.createPo();
        TimeSegmentPoExample timePoExample = new TimeSegmentPoExample();
        TimeSegmentPoExample.Criteria timePoCriteria = timePoExample.createCriteria();
        timePoCriteria.andTypeEqualTo(po.getType());
        try {
            // 根据条件分页查询
            PageHelper.startPage(page, pageSize);
            List<TimeSegmentPo> timeSegmentPo = timeMapper.selectByExample(timePoExample);
            PageInfo<TimeSegmentPo> timeSegmentPoPage = new PageInfo<>(timeSegmentPo);
            List timeBoList = Lists.transform(timeSegmentPo, Time::new);
            PageInfo retObject = new PageInfo<>(timeBoList);
            retObject.setPages(timeSegmentPoPage.getPages());
            retObject.setPageNum(timeSegmentPoPage.getPageNum());
            retObject.setPageSize(timeSegmentPoPage.getPageSize());
            retObject.setTotal(timeSegmentPoPage.getTotal());
            return new ReturnObject<>(retObject);
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    };

    /**
     * 获取有效的（广告/秒杀）时间段id
     * @author cxr
     * @param type 时段类型
     * @return 时段id列表
     */
    public ReturnObject<List<Long>> listSelectAllTimeSegmentId(Byte type) {
        TimeSegmentPoExample timePoExample = new TimeSegmentPoExample();
        TimeSegmentPoExample.Criteria timePoCriteria = timePoExample.createCriteria();
        if (type != null) {
            timePoCriteria.andTypeEqualTo(type);
        }
        try {
            List<TimeSegmentPo> timeSegmentPo = timeMapper.selectByExample(timePoExample);
            List<Long> vaildTimeIdList = Lists.newArrayList();
            for (TimeSegmentPo po : timeSegmentPo) {
                vaildTimeIdList.add(po.getId());
            }
            return new ReturnObject<>(vaildTimeIdList);
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    };

    /**
     * @author cxr
     * 获得当前时段id
     */
    public ReturnObject<Long> getCurrentSegmentId(Byte type){
        TimeSegmentPoExample poExample = new TimeSegmentPoExample();
        TimeSegmentPoExample.Criteria criteria = poExample.createCriteria();
        LocalDateTime nowTime = LocalDateTime.now();
        criteria.andBeginTimeLessThan(nowTime).andEndTimeGreaterThan(nowTime);
        if (type != null) {
            criteria.andTypeEqualTo(type);
        }
        try {
            List<TimeSegmentPo> timeSegmentPo = timeMapper.selectByExample(poExample);
            if(timeSegmentPo.size()==1) {
                return new ReturnObject<>(timeSegmentPo.get(0).getId());
            }else{
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"未找到时段id");
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    };

    /**
     * 根据时段id获取相应时段
     * @author cxr
     * @param type 时段类型
     * @param id 时段id
     * @return returnObject
     */
    public ReturnObject<TimeDTO> getTimeSegmentId(Byte type, Long id){
        try {
            TimeSegmentPo po = timeMapper.selectByPrimaryKey(id);
            if(po == null || !po.getType().equals(type)){
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"未找到时段id：" + id);
            }
            else{
                return new ReturnObject<>(new TimeDTO(id, type, po.getBeginTime().toLocalTime(), po.getEndTime().toLocalTime()));
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }

    }

    /**
     * 由于比较时分秒，所以把所有时间的年月日都设成统一的时间
     * 但是由于是串行执行，可能会出现转换过程跨天了
     * 导致统一失败，故添加current记录统一的年月日进行转换
     * @author cxr
     * @param current 统一年月日标准的时间
     * @param time 要转换的时间
     * @return 统一后的时间
     */
    LocalDateTime translateTime(LocalDateTime current, LocalDateTime time){
        return LocalDateTime.of(current.getYear(),current.getMonth(),current.getDayOfMonth(),
                time.getHour(),time.getMinute(),time.getSecond());
    }
}