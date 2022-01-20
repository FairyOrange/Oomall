package cn.edu.xmu.oomall.address.service;

import cn.edu.xmu.oomall.address.dao.AddressDao;
import cn.edu.xmu.oomall.address.dao.RegionDao;
import cn.edu.xmu.oomall.address.model.bo.Address;
import cn.edu.xmu.oomall.address.model.bo.Region;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 地址Service
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/24 23:29
 */
@Slf4j
@Service
public class AddressService {
    @Autowired
    AddressDao addressDao;

    @Autowired
    RegionDao regionDao;

    /**
     * 管理员在地区下新增子地区
     *
     * @param bo 地区bo
     * @author wwc
     * @date 2020/11/24 23:39
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject insertRegion(Region bo) {
        ReturnObject returnObject = regionDao.insertRegion(bo);
        return returnObject;
    }

    /**
     * 管理员修改某个地区
     *
     * @param bo 地区bo
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateRegion(Region bo) {
        ReturnObject returnObject = regionDao.updateRegion(bo);
        return returnObject;
    }

    /**
     * 管理员让某个地区无效
     *
     * @param id 地区id
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteRegion(Long id) {
        ReturnObject returnObject = regionDao.deleteRegion(id);
        return returnObject;
    }

    /**
     * 查询某个地区的所有上级地区
     *
     * @param id 地区id
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject listSelectAllParentRegion(Long id) {
        ReturnObject returnObject = regionDao.listSelectAllParentRegion(id);
        return returnObject;
    }

    /**
     * 买家新增地址
     *
     * @param bo 地址bo
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject insertAddress(Address bo) {
        Region region = regionDao.getSelectRegionById(bo.getRegionId());
        if (region != null) {

            if(region.getState() == 1)
            {
                // 地区被废弃
                return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
            }
            // 若地区id有效则新建该地址
            ReturnObject returnObject = addressDao.insertAddress(bo);
            return returnObject;

        } else {
            return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
        }
    }

    /**
     * 买家修改自己的地址信息
     *
     * @param bo 地址bo
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateAddress(Address bo) {
        ReturnObject returnObject = addressDao.updateAddress(bo);
        return returnObject;
    }

    /**
     * 买家删除地址
     *
     * @param userIdAudit 当前用户id
     * @param id          售后单id
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteAddress(Long userIdAudit, Long id) {
        ReturnObject returnObject = addressDao.deleteAddress(userIdAudit, id);
        return returnObject;
    }

    /**
     * 买家查询所有已有的地址信息
     *
     * @param userIdAudit 当前用户id
     * @param page        页数
     * @param pageSize    每页大小
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject listUserSelectAddress(Long userIdAudit, Integer page, Integer pageSize) {
        ReturnObject returnObject = addressDao.listUserSelectAddress(userIdAudit, page, pageSize);
        return returnObject;
    }

    /**
     * 买家设置默认地址
     *
     * @param userIdAudit 当前用户id
     * @param id          id
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateAddressToDefault(Long userIdAudit, Long id) {
        ReturnObject returnObject = addressDao.updateAddressToDefault(userIdAudit, id);
        return returnObject;
    }

}
