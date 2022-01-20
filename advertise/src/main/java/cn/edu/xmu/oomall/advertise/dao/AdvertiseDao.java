package cn.edu.xmu.oomall.advertise.dao;

import cn.edu.xmu.oomall.advertise.mapper.AdvertisementPoMapper;
import cn.edu.xmu.oomall.advertise.model.bo.AdvertiseBo;
import cn.edu.xmu.oomall.advertise.model.po.AdvertisementPo;
import cn.edu.xmu.oomall.advertise.model.po.AdvertisementPoExample;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Repository
public class AdvertiseDao {

    @Autowired
    private AdvertisementPoMapper advertisementPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(AdvertiseDao.class);

    /**
     * 管理员设置默认广告
     *
     * @param id 广告id
     * @return ReturnObject
     * @author cxr
     */
    public ReturnObject putAdvertisementIdDefault(Long id) {
        try {
            AdvertisementPo po = advertisementPoMapper.selectByPrimaryKey(id);
            if (po == null) {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("操作的资源id不存在"));
            } else {
                Byte def = po.getBeDefault();
                if (def.byteValue() == 0) {
                    //把之前的默认广告设为非默认
                    AdvertisementPoExample poExample = new AdvertisementPoExample();
                    AdvertisementPoExample.Criteria criteria = poExample.createCriteria();
                    criteria.andBeDefaultEqualTo((byte)1);
                    AdvertisementPo po1 = new AdvertisementPo();
                    po1.setBeDefault((byte)0);
                    advertisementPoMapper.updateByExampleSelective(po1,poExample);
                    //如果当前广告为非默认广告，则改为默认广告
                    po.setBeDefault((byte) 1);
                } else {
                    //如果当前广告为默认广告，则改为非默认广告
                    po.setBeDefault((byte) 0);
                }
                po.setGmtModified(LocalDateTime.now());
                int ret = advertisementPoMapper.updateByPrimaryKey(po);
                if (ret == 0) {
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("操作的资源id不存在"));
                } else {
                    return new ReturnObject<>();
                }
            }
        } catch (DataAccessException e) {
            log.error("insert DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    };

    /**
     * 管理员修改广告信息
     *
     * @param bo
     * @return Returnobject
     * @author cxr
     */
    public ReturnObject putAdvertise(AdvertiseBo bo) {
        try {
            //修改了信息之后，需要将广告的状态改为审核态
            bo.setState(AdvertiseBo.State.AUDIT);
            AdvertisementPo advertisementPo = bo.creatPo();
            AdvertisementPoExample advertisementPoExample = new AdvertisementPoExample();
            AdvertisementPoExample.Criteria criteria = advertisementPoExample.createCriteria();
            criteria.andIdEqualTo(bo.getId());
            int ret = advertisementPoMapper.updateByExampleSelective(advertisementPo,advertisementPoExample);
            if (ret == 0) {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("操作的广告不存在"));
            } else {
                return new ReturnObject<>(ResponseCode.OK, String.format("修改广告成功: " + bo.getId()));
            }
        } catch (DataAccessException e) {
            log.error("insert DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误: %s", e.getMessage()));
        }
    };

    /**
     * 根据id删除广告
     *
     * @param id
     * @return ReturnObject
     * @author cxr
     */
    public ReturnObject deleteAdvertisementById(Long id) {
        try {
            int ret = advertisementPoMapper.deleteByPrimaryKey(id);
            if (ret == 0) {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                logger.info("广告id = " + id + " 已被永久删除");
                return new ReturnObject<>(ResponseCode.OK);
            }
        } catch (DataAccessException e) {
            log.error("insert DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            //其他Exception错误
            log.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    };

    /**
     * 获取当前有效时段广告列表
     * 需要满足的条件
     * 时段id对应当前时段
     * 广告必须为上架态
     * 广告的日期必须覆盖当天日期或者repeat字段为true
     *
     * @param segId 时段id
     * @return
     */
    public ReturnObject getAdvertisementCurrent(Long segId) {
        //设置查询条件
        AdvertisementPoExample poExample = new AdvertisementPoExample();

        AdvertisementPoExample.Criteria criteria = poExample.createCriteria();
        if(segId.equals(0L)){
            criteria.andBeDefaultEqualTo((byte)1);
        }else {
            criteria.andSegIdEqualTo(segId);
        }
        criteria.andStateEqualTo(AdvertiseBo.State.ONSHELF.getCode().byteValue());
        //广告的日期必须覆盖当天日期
        criteria.andBeginDateLessThanOrEqualTo(LocalDate.now());
        criteria.andEndDateGreaterThanOrEqualTo(LocalDate.now());
        poExample.or(criteria);

        AdvertisementPoExample.Criteria criteria1 = poExample.createCriteria();
        if(segId.equals(0L)){
            criteria1.andBeDefaultEqualTo((byte)1);
        }else {
            criteria1.andSegIdEqualTo(segId);
        }
        criteria1.andStateEqualTo(AdvertiseBo.State.ONSHELF.getCode().byteValue());
        //repeat字段为true
        criteria.andRepeatsEqualTo((byte) 1);
        poExample.or(criteria1);

        try {
            List<AdvertisementPo> list = advertisementPoMapper.selectByExample(poExample);
            return new ReturnObject(list);
        } catch (DataAccessException e) {
            log.error("insert DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    };

    /**
     * 获取某一时段的广告列表
     *
     * @param id 时段id
     * @return 广告列表
     * @author cxr
     */
    public ReturnObject getAdvertisementsBySegId(Long id) {
        AdvertisementPoExample poExample = new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria = poExample.createCriteria();
        criteria.andSegIdEqualTo(id);
        criteria.andStateEqualTo(AdvertiseBo.State.ONSHELF.getCode().byteValue());

        criteria.andBeginDateGreaterThanOrEqualTo(LocalDate.now());
        criteria.andEndDateLessThanOrEqualTo(LocalDate.now());

        try {
            List<AdvertisementPo> advertisementPoList = advertisementPoMapper.selectByExample(poExample);
            List<AdvertiseBo> advertiseBoList = advertisementPoList.stream().map(AdvertiseBo::new).collect(Collectors.toList());
            return new ReturnObject<>(advertiseBoList);
        } catch (DataAccessException e) {
            log.error("insert DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    };

    /**
     * 更新广告图片
     *
     * @param bo 广告信息
     * @return ReturnObject
     * @author cxr
     */
    public ReturnObject updateImagePath(AdvertiseBo bo) {
        AdvertisementPo po = new AdvertisementPo();
        po.setId(bo.getId());
        po.setImageUrl(bo.getImageUrl());
        po.setGmtModified(LocalDateTime.now());
        po.setState(AdvertiseBo.State.AUDIT.getCode().byteValue());
        try {
            int ret = advertisementPoMapper.updateByPrimaryKey(po);
            if (ret == 0) {
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
            } else {
                bo.setState(AdvertiseBo.State.AUDIT);
                return new ReturnObject<>(ResponseCode.OK, String.format("广告上传图片成功"));
            }
        } catch (DataAccessException e) {
            log.error("insert DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    };

    /**
     * 修改广告状态
     *
     * @param bo 广告信息
     * @param needPreState 所需前提状态
     * @return ReturnObject
     * @author cxr
     */
    public ReturnObject putAdvertisementState(AdvertiseBo bo, Byte needPreState) {
        //先判断广告是否存在
        AdvertisementPo advertisementPo = advertisementPoMapper.selectByPrimaryKey(bo.getId());
        if (advertisementPo == null) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        AdvertisementPo po = bo.creatPo();
        AdvertisementPoExample advertisementPoExample = new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria = advertisementPoExample.createCriteria();
        criteria.andIdEqualTo(bo.getId());
        //修改的前提是满足所需前置状态（审核-》下架、下架-》上架、上架-》下架）
        criteria.andStateEqualTo(needPreState);
        try {
            int ret = advertisementPoMapper.updateByExampleSelective(po, advertisementPoExample);
            if (ret == 0) {
                return new ReturnObject<>(ResponseCode.ADVERTISEMENT_STATENOTALLOW);
            } else {
                return new ReturnObject<>(ResponseCode.OK);
            }
        } catch (DataAccessException e) {
            log.error("insert DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            log.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    };

    /**
     * 根据时段id查找指定开始日期和结束日期的广告
     *
     * @param bo
     * @param page
     * @param pageSize
     * @return ReturnObject
     * @author cxr
     */
    public ReturnObject getAdvertiseBySegId(AdvertiseBo bo, Integer page, Integer pageSize) {
        AdvertisementPoExample poExample = new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria = poExample.createCriteria();
        criteria.andSegIdEqualTo(bo.getSegId());
        //如果日期不为空，就需要作为限制条件
        if(bo.getBeginDate()!=null){
            criteria.andBeginDateGreaterThanOrEqualTo(bo.getBeginDate());
        }
        if(bo.getEndDate() != null) {
            criteria.andEndDateLessThanOrEqualTo(bo.getEndDate());
        }
        try {
            PageHelper.startPage(page, pageSize,true,true,null);
            List<AdvertisementPo> advertisePoList = advertisementPoMapper.selectByExample(poExample);

            PageInfo<AdvertisementPo> advertisePoPage = new PageInfo<>(advertisePoList);
            List aftersaleBoList = advertisePoPage.getList().stream().map(AdvertiseBo::new).collect(Collectors.toList());

            PageInfo retObject = new PageInfo<>(aftersaleBoList);
            retObject.setPages(advertisePoPage.getPages());
            retObject.setPageNum(advertisePoPage.getPageNum());
            retObject.setPageSize(advertisePoPage.getPageSize());
            retObject.setTotal(advertisePoPage.getTotal());

            return new ReturnObject<>(retObject);
        } catch (DataAccessException e) {
            log.error("insert DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            log.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    };

    /**
     * 在广告时段下新建广告
     *
     * @param bo 新建广告的信息
     * @return ReturnObject
     * @author cxr
     */
    public ReturnObject postTimesegmentsIdAdvertisement(AdvertiseBo bo) {
        AdvertisementPo po = bo.creatPo();
        ReturnObject returnObject = null;
        try {
            //插入广告
            int ret = advertisementPoMapper.insert(po);
            if (ret == 0) {
                // 插入失败
                log.debug("新增广告失败：" + po.getId());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增广告失败：%s", po.getId()));
            } else {
                // 插入成功
                log.debug("insert = " + po.toString());
                // 通过反射获取插入时获得的记录
                bo.setId(po.getId());
                returnObject = new ReturnObject<>(bo);
            }
        } catch (DataAccessException e) {
            log.error("insert DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            log.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
        return returnObject;
    }

    /**
     * 在广告时段下新增广告
     *
     * @param bo 广告的信息
     * @return ReturnObject
     * @author cxr
     */
    public ReturnObject postTimesegmentsIdAdvertisementId(AdvertiseBo bo) {
        try {
            AdvertisementPo po = advertisementPoMapper.selectByPrimaryKey(bo.getId());
            if(po == null){
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("操作的广告不存在"));
            }
            AdvertisementPo advertisementPo = bo.creatPo();
            AdvertisementPoExample poExample = new AdvertisementPoExample();
            AdvertisementPoExample.Criteria criteria = poExample.createCriteria();
            criteria.andIdEqualTo(bo.getId());
            int ret = advertisementPoMapper.updateByExampleSelective(advertisementPo,poExample);
            if (ret == 0) {
                log.error("该广告不存在：" + bo.getId());
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("操作的广告不存在"));
            } else {
                po.setSegId(bo.getSegId());
                AdvertiseBo advertiseBo = new AdvertiseBo(po);
                return new ReturnObject(advertiseBo);
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他严重错误：%s", e.getMessage()));
        }
    };

    /**
     * 根据广告id获得广告
     * @param id 广告id
     * @return
     */
    public ReturnObject getAdvertise(Long id) {
        try {
            AdvertisementPo po = advertisementPoMapper.selectByPrimaryKey(id);
            if (po == null) {
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "该广告找不到：" + id);
            } else {
                return new ReturnObject(new AdvertiseBo(po));
            }
        } catch (DataAccessException e) {
            log.error("数据库错误" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误：%s" + e.getMessage());
        } catch (Exception e) {
            log.error("其他错误" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "其他严重错误：%s" + e.getMessage());
        }

    };

    /**
     * 获取默认广告
     * @author cxr
     * @return 默认广告
     */
    public AdvertiseBo getDefaultAdvertisement() {
        AdvertisementPoExample poExample = new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria = poExample.createCriteria();
        criteria.andBeDefaultEqualTo((byte) 1);
        try {
            List<AdvertisementPo> list = advertisementPoMapper.selectByExample(poExample);
            if(list!=null && list.size()!=0){
                AdvertiseBo bo = new AdvertiseBo(list.get(0));
                if(bo.getState().getCode().equals(AdvertiseBo.State.ONSHELF)&&(bo.getRepeats().equals(true)||(
                        !bo.getBeginDate().isAfter(LocalDate.now())&&!bo.getEndDate().isBefore(LocalDate.now())))){
                    return bo;
                }
            }
            return null;
        } catch (DataAccessException e) {
            log.error("数据库错误" + e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("其他错误" + e.getMessage());
            return null;
        }

    };

    /**
     * 将某一广告时段下的所有广告的时段置为0
     * @author cxr
     * @param segId 时段id
     * @return
     */
    public ReturnObject deleteTimeSegmentAdvertisements(Long segId){
        AdvertisementPoExample poExample = new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria = poExample.createCriteria();
        criteria.andSegIdEqualTo(segId);
        AdvertisementPo po = new AdvertisementPo();
        po.setSegId(0L);
        try{
            advertisementPoMapper.updateByExampleSelective(po,poExample);
            return new ReturnObject();
        } catch (DataAccessException e) {
            log.error("数据库错误" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误：%s" + e.getMessage());
        } catch (Exception e) {
            log.error("其他错误" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "其他严重错误：%s" + e.getMessage());
        }
    }
}
