package cn.edu.xmu.oomall.other.service;

import cn.edu.xmu.oomall.util.ReturnObject;

import java.util.List;

public interface IAddressService {

    /**
     * 查询该地区id是否被废弃
     */
    ReturnObject<Boolean> getValidRegionId(Long regionId);

    /**
     * 查询父地区ID，List类型
     */
    ReturnObject<List<Long>> getRegionId(Long regionId);

}
