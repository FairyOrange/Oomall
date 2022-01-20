package cn.edu.xmu.oomall.address.service.impl;

import cn.edu.xmu.oomall.address.dao.RegionDao;
import cn.edu.xmu.oomall.address.model.bo.Region;
import cn.edu.xmu.oomall.other.service.IAddressService;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 地址接口实现类
 *
 * @author wwc
 * @version 1.0
 * @date 2020/12/1 14:07
 */
@DubboService // 注意这里的Serivce引用的是dubbo的包
public class AddressServiceImpl implements IAddressService {

    @Autowired
    private RegionDao regionDao;

    /**
     * 查询该地区id是否被废弃
     *
     * @author wwc
     * @date 2020/12/01 09:12
     * @version 1.0
     */
    @Override
    public ReturnObject<Boolean> getValidRegionId(Long regionId) {
        ReturnObject ret = regionDao.getSelectRegionVaild(regionId);
        return ret;
    }


    /**
     * 查询父地区ID，List类型
     */
    @Override
    public ReturnObject<List<Long>> getRegionId(Long regionId) {
        ReturnObject<List<Long>> ret = regionDao.getParentListById(regionId);
        return ret;
    }

}
