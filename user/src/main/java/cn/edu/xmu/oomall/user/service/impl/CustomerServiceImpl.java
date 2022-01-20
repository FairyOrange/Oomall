package cn.edu.xmu.oomall.user.service.impl;

import cn.edu.xmu.oomall.other.model.CustomerDTO;
import cn.edu.xmu.oomall.other.service.ICustomerService;
import cn.edu.xmu.oomall.user.dao.UserDao;
import cn.edu.xmu.oomall.user.model.bo.User;
import cn.edu.xmu.oomall.user.model.po.CustomerPo;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private UserDao userDao;

    @Override
    public ReturnObject<CustomerDTO> findCustomerByUserId(Long userId) {
        ReturnObject ret = userDao.getUserSelectInfo(userId);
        if (ret.getCode() == ResponseCode.OK) {
            User bo = (User) ret.getData();
            return new ReturnObject<>(new CustomerDTO(bo.getUserName(), bo.getRealName()));
        } else {
         return ret;
        }
    }
}
