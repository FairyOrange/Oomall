package cn.edu.xmu.oomall.advertise.service;

import cn.edu.xmu.oomall.advertise.dao.AdvertiseDao;
import cn.edu.xmu.oomall.advertise.model.bo.AdvertiseBo;
import cn.edu.xmu.oomall.advertise.model.po.AdvertisementPo;
import cn.edu.xmu.oomall.other.model.TimeDTO;
import cn.edu.xmu.oomall.other.service.ITimeService;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import cn.edu.xmu.oomall.util.ImgHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 广告Service
 *
 * @author xiaoru chen
 * @version 1.0
 * @date 2020/11/25 19:02
 */
@Slf4j
@Service
public class AdvertiseService {

    // Dubbo远程调用注解
    @DubboReference(check = false)
    private ITimeService timeService;

    @Autowired
    private AdvertiseDao advertiseDao;

    @Value("${privilegeservice.imglocation}")
    private String imgLocation;

    @Value("${privilegeservice.dav.username}")
    private String davUserName;

    @Value("${privilegeservice.dav.password}")
    private String davPassword;

    @Value("${privilegeservice.dav.baseUrl}")
    private String baseUrl;

    @Value("${advertisement.limit-num}")
    public int ADVERTISEMENT_LIMIT_NUM;

    /**
     * 管理员设置默认广告
     *
     * @param id 广告id
     * @return
     * @author cxr
     */
    public ReturnObject putAdvertisementIdDefault(Long id) {
        ReturnObject returnObject = advertiseDao.putAdvertisementIdDefault(id);
        return returnObject;
    };

    /**
     * 管理员修改广告内容
     * 当修改成功之后，广告的状态变为审核态
     * 如果广告当前的状态为上架态，可能会改变队列
     * 如果队列存在该广告，删去
     *
     * @param bo
     * @return
     * @author cxr
     */
    public ReturnObject putAdvertisementId(AdvertiseBo bo) {
        //修改广告内容
        ReturnObject returnObject = advertiseDao.putAdvertise(bo);
        return returnObject;
    };

    /**
     * 根据id删除广告
     * 删除成功之后判断是否存在队列中，若存在，就删去
     *
     * @param id 广告id
     * @return retObject
     * @author cxr
     */
    public ReturnObject deleteAdvertisementId(Long id) {
        //根据id物理删除广告
        ReturnObject retObject = advertiseDao.deleteAdvertisementById(id);
        return retObject;
    };

    /**
     * 获得当前时段的所有广告
     *
     * @return 广告列表
     * @author cxr
     */
    public ReturnObject getAdvertisementCurrent() {
        //获取当前时段id(会出现不存在当前时段以及当前时段id的情况）
        ReturnObject ret = timeService.getCurrentSegmentId((byte) 0);
        ReturnObject returnObject;
        if(ret.getCode().equals(ResponseCode.OK)){
            Long curSegId = (Long) ret.getData();
            returnObject = advertiseDao.getAdvertisementCurrent(curSegId);
            return returnObject;
        }else{
            AdvertiseBo bo = advertiseDao.getDefaultAdvertisement();
            if(bo!=null){
                return new ReturnObject(bo);
            }
            return new ReturnObject();
        }
    };

    /**
     * 管理员上传广告图片
     * 这时候广告的状态变为审核态
     * 如果广告之前的状态为上架态，需要从时段广告队列中删除
     *
     * @param id            广告id
     * @param multipartFile 图片文件
     * @return returnobject
     * @author cxr
     */
    @Transactional
    public ReturnObject uploadImg(Long id, MultipartFile multipartFile) {
        ReturnObject returnObject = advertiseDao.getAdvertise(id);

        if (returnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST) {
            return returnObject;
        }
        AdvertiseBo bo = (AdvertiseBo) returnObject.getData();
        try {
            returnObject = ImgHelper.remoteSaveImg(multipartFile, 2, davUserName, davPassword, baseUrl);

            //文件上传错误
            if (returnObject.getCode() != ResponseCode.OK) {
                log.error(returnObject.getErrmsg());
                return returnObject;
            }
            String oldFilename = bo.getImageUrl();
            bo.setImageUrl(returnObject.getData().toString());
            bo.setGmtModified(LocalDateTime.now());
            ReturnObject updateReturnObject = advertiseDao.updateImagePath(bo);
            //数据库更新失败，需删除新增的图片
            if (updateReturnObject.getCode() == ResponseCode.INTERNAL_SERVER_ERR) {
                ImgHelper.deleteRemoteImg(returnObject.getData().toString(), davUserName, davPassword, baseUrl);
                return updateReturnObject;
            }

            //数据库更新成功需要删除旧图片，未设置则不删除
            if (oldFilename != null) {
                ImgHelper.deleteRemoteImg(oldFilename, davUserName, davPassword, baseUrl);
            }
        } catch (IOException e) {
            log.error("uploadImg: I/O Error:" + baseUrl);
            return new ReturnObject(ResponseCode.FILE_NO_WRITE_PERMISSION);
        }
        return returnObject;
    };

    /**
     * 管理员上架、下架以及审核广告
     * 管理员上架广告之后，有可能会在当前队列中增加该广告
     *
     * @param bo
     * @param needPreState
     * @return
     * @author cxr
     */
    public ReturnObject putAdvertisementIdState(AdvertiseBo bo, Byte needPreState) {
        //管理员上架广告
        ReturnObject returnObject = advertiseDao.putAdvertisementState(bo, needPreState);
        return returnObject;
    };

    /**
     * 根据时段id查找所有广告
     * 不涉及状态改变，无需改变时段广告队列
     * @param bo
     * @param page
     * @param pageSize
     * @return ReturnObject
     * @author cxr
     */
    public ReturnObject getAdvertisementBySegId(AdvertiseBo bo, Integer page, Integer pageSize) {
        //如果发过来的时段id不为0,需要判断该时段是否存在
        if (bo.getSegId() != 0L) {
            ReturnObject ret = timeService.getTimeSegmentId((byte) 0, bo.getSegId());
            if (ret.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)) {
                return ret;
            }
        }
        ReturnObject returnObject = advertiseDao.getAdvertiseBySegId(bo, page, pageSize);
        return returnObject;
    };

    /**
     * 在广告时段下新建广告
     * 由于此时广告的状态为审核态，不影响时段广告队列的变化
     * @param bo 可填写的广告信息
     * @return ReturnObject
     * @author cxr
     */
    public ReturnObject postTimesegmentsIdAdvertisement(AdvertiseBo bo) {
        //如果发过来的时段id不为0,需要判断该时段是否存在
        if (!bo.getSegId().equals(0L)) {
            ReturnObject ret = timeService.getTimeSegmentId((byte) 0, bo.getSegId());
            if (ret.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)) {
                return ret;
            }
        }
        ReturnObject returnObject = advertiseDao.postTimesegmentsIdAdvertisement(bo);
        return returnObject;
    };

    /**
     * 管理员在广告时段下增加广告
     * 由于改变了时段
     * 有可能改变当前时段广告队列
     *
     * @param bo
     * @return ReturnObject
     * @author cxr
     */
    public ReturnObject postTimesegmentsIdAdvertisementId(AdvertiseBo bo) {
        //如果发过来的时段id不为0,需要判断该时段是否存在
        if (!bo.getSegId().equals(0L)) {
            ReturnObject ret = timeService.getTimeSegmentId((byte) 0, bo.getSegId());
            if (ret.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)) {
                return ret;
            }
        }
        ReturnObject returnObject = advertiseDao.postTimesegmentsIdAdvertisementId(bo);
        return returnObject;
    };
}
