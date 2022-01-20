package cn.edu.xmu.oomall.other.service;

import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;

import java.util.List;

public interface IFootprintService {

    /**
     * 增加足迹
     */
    ReturnObject<ResponseCode> postFootprint(Long customerId, Long skuId);
}
