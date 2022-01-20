package cn.edu.xmu.oomall.other.service;

import cn.edu.xmu.oomall.other.model.CustomerDTO;
import cn.edu.xmu.oomall.util.ReturnObject;

public interface ICustomerService {

    /**
     * 通过userId查找用户信息
     */
    ReturnObject<CustomerDTO> findCustomerByUserId(Long userId);

}