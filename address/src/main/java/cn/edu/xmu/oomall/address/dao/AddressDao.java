package cn.edu.xmu.oomall.address.dao;

import cn.edu.xmu.oomall.address.mapper.AddressPoMapper;
import cn.edu.xmu.oomall.address.mapper.RegionPoMapper;
import cn.edu.xmu.oomall.address.model.bo.Address;
import cn.edu.xmu.oomall.address.model.bo.Region;
import cn.edu.xmu.oomall.address.model.po.AddressPo;
import cn.edu.xmu.oomall.address.model.po.AddressPoExample;
import cn.edu.xmu.oomall.address.model.po.RegionPo;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 地址Dao
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/24 23:30
 */
@Slf4j
@Repository
public class AddressDao {
    @Autowired
    private AddressPoMapper addressMapper;

    @Autowired
    private RegionPoMapper regionMapper;

    @Autowired
    private RegionDao regionDao;

    /**
     * 买家新增地址
     *
     * @param bo 地址bo
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    public ReturnObject insertAddress(Address bo) {
        AddressPo po = bo.createPo();
        AddressPoExample addressPoExample = new AddressPoExample();
        AddressPoExample.Criteria addressPoCriteria = addressPoExample.createCriteria();
        // 限制只能查询当前用户的地址
        addressPoCriteria.andCustomerIdEqualTo(bo.getCustomerId());
        try {
            Long addressNum = addressMapper.countByExample(addressPoExample);
            if (addressNum != null && addressNum.longValue() >= (long) 20) {
                // 若已有20个以上的地址
                return new ReturnObject<>(ResponseCode.ADDRESS_OUTLIMIT);
            } else {
                // 查询该地区码对应的地区是否有效
                Region region = regionDao.getSelectRegionById(po.getRegionId());
                if (region != null) {
                    // 若存在该地区且有效则插入
                    int ret = addressMapper.insertSelective(po);
                    if (ret == 0) {
                        // 插入失败
                        log.debug("新增地址失败：" + po.getId());
                        return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增地址失败：%s", po.getId()));
                    } else {
                        // 插入成功
                        log.debug("新增地址: " + po.toString());
                        // 通过反射获取插入时获得的记录
                        bo.setId(po.getId());
                        bo.setState(regionMapper.selectByPrimaryKey(po.getRegionId()).getState());
                        return new ReturnObject<>(bo);
                    }
                } else {
                    // 否则返回地区废弃
                    return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
                }
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
     * 买家修改地址
     *
     * @param bo 地址bo
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    public ReturnObject updateAddress(Address bo) {
        AddressPo po = bo.createPo();

        AddressPo my_po = addressMapper.selectByPrimaryKey(bo.getId());
        if(my_po == null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        if(!(my_po.getCustomerId().equals(bo.getCustomerId()))){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }

        try {
            // 查询该地区码对应的地区是否有效
            RegionPo regionPo = regionMapper.selectByPrimaryKey(po.getRegionId());
            if (regionPo != null && regionPo.getState() != (byte) 1) {
                // 从redis中删除该地址

                // 若存在该地区且有效则修改
                int ret = addressMapper.updateByPrimaryKeySelective(po);
                if (ret == 0) {
                    // 修改失败
                    log.debug("修改地址失败：" + po.getId());
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("修改地址失败：%s", po.getId()));
                } else {
                    // 修改成功
                    log.debug("修改地址: " + po.toString());
                    return new ReturnObject<>(ResponseCode.OK, String.format("修改地址成功：%s", po.getId()));
                }
            } else {
                // 否则返回地区废弃
                return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
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
     * 买家删除地址
     *
     * @param userIdAudit 当前用户id
     * @param id          售后单id
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    public ReturnObject deleteAddress(Long userIdAudit, Long id) {
        AddressPoExample addressPoExample = new AddressPoExample();
        AddressPoExample.Criteria addressPoCriteria = addressPoExample.createCriteria();
        // 只能删除当前用户的地址
        addressPoCriteria.andIdEqualTo(id);
        addressPoCriteria.andCustomerIdEqualTo(userIdAudit);
        try {
            // 从redis中删除该地址

            int ret = addressMapper.deleteByExample(addressPoExample);
            if (ret == 0) {
                // 删除失败
                log.debug("删除地址失败：" + id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("删除地址失败：%s", id));
            } else {
                // 删除成功
                log.debug("删除地址: " + id);
                return new ReturnObject<>(ResponseCode.OK, String.format("删除地址成功：%s", id));
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
     * 买家查询所有已有的地址信息
     *
     * @param userIdAudit 当前用户id
     * @param page        页数
     * @param pageSize    每页大小
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    public ReturnObject listUserSelectAddress(Long userIdAudit, Integer page, Integer pageSize) {
        AddressPoExample addressPoExample = new AddressPoExample();
        AddressPoExample.Criteria addressPoCriteria = addressPoExample.createCriteria();
        // 只能查询当前用户的地址簿
        addressPoCriteria.andCustomerIdEqualTo(userIdAudit);
        try {
            // 进行分页查询
            PageHelper.startPage(page, pageSize);
            List<AddressPo> addressPoList = addressMapper.selectByExample(addressPoExample);
            // 加载到redis中

            PageInfo<AddressPo> addressPoPage = new PageInfo<>(addressPoList);
            List<Address> addressList = Lists.newArrayList(Lists.transform(addressPoList, Address::new));
            // 分别获得state状态
            for (Address address : addressList) {
                address.setState(regionMapper.selectByPrimaryKey(address.getRegionId()).getState());
            }
            PageInfo retObject = new PageInfo<>(addressList);
            retObject.setPages(addressPoPage.getPages());
            retObject.setPageNum(addressPoPage.getPageNum());
            retObject.setPageSize(addressPoPage.getPageSize());
            retObject.setTotal(addressPoPage.getTotal());
            return new ReturnObject<>(retObject);
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    }

    /**
     * 买家设置默认地址
     *
     * @param userIdAudit 当前用户id
     * @param id          售后单id
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    public ReturnObject updateAddressToDefault(Long userIdAudit, Long id) {

        AddressPo addressPo = addressMapper.selectByPrimaryKey(id);
        if(addressPo == null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        if(!addressPo.getCustomerId().equals(userIdAudit)){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }

        // 只能修改本人的默认地址
        AddressPo po = new AddressPo();
        po.setCustomerId(userIdAudit);
        AddressPoExample addressPoExample = new AddressPoExample();
        AddressPoExample.Criteria addressPoCriteria = addressPoExample.createCriteria();
        addressPoCriteria.andBeDefaultEqualTo((byte) 0);
        addressPoCriteria.andCustomerIdEqualTo(userIdAudit);
        try {
            // 首先清空默认地址
            po.setBeDefault((byte) 1);
            addressMapper.updateByExampleSelective(po, addressPoExample);
            // 然后更新选中的地址为默认地址
            po.setId(id);
            po.setBeDefault((byte) 0);
            int ret = addressMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                // 设置默认失败
                log.debug("设置默认地址失败：" + id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("设置默认地址失败：%s", id));
            } else {
                // 设置默认成功
                log.debug("设置默认地址: " + id);
                return new ReturnObject<>(ResponseCode.OK, String.format("设置默认地址成功：%s", id));
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    }

}
