package cn.edu.xmu.oomall.address.dao;

import cn.edu.xmu.oomall.address.mapper.AddressPoMapper;
import cn.edu.xmu.oomall.address.mapper.RegionPoMapper;
import cn.edu.xmu.oomall.address.model.bo.Region;
import cn.edu.xmu.oomall.address.model.po.RegionPo;
import cn.edu.xmu.oomall.address.model.po.RegionPoExample;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 地区Dao
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/24 23:30
 */
@Slf4j
@Repository
public class RegionDao implements InitializingBean {
    @Autowired
    AddressPoMapper addressMapper;

    @Autowired
    RegionPoMapper regionMapper;

    @Autowired
    RedisTemplate redisTemplate;

    static private String validRegion = "validRegion";

    /**
     * 初始化有效地区
     *
     * @author wwc
     * @date 2020/12/01 10:37
     * @version 1.0
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        RegionPoExample regionPoExample = new RegionPoExample();
        RegionPoExample.Criteria criteria = regionPoExample.createCriteria();
        criteria.andStateEqualTo((byte) 0);
        List<RegionPo> regionPoList = regionMapper.selectByExample(regionPoExample);
        // 先清空
        redisTemplate.delete(validRegion);
        Map<String, Region> allRegionMap = Maps.newHashMap();
        log.debug("load valid regionId" + regionPoList.size());
        for (RegionPo po : regionPoList) {
            allRegionMap.put(po.getId().toString(), new Region(po));
        }
        redisTemplate.opsForHash().putAll(validRegion, allRegionMap);
    }

    /**
     * 管理员在地区下新增子地区
     *
     * @param bo 地区bo
     * @author wwc
     * @date 2020/11/24 23:39
     * @version 1.0
     */
    public ReturnObject insertRegion(Region bo) {
        RegionPo po = bo.createPo();
        try {
            // 若添加的不为根地区则先查询是否该父地区有效
            if (po.getPid() != (byte) 0) {
                Region pBo = getSelectRegionById(po.getPid());

                // 若地区不存在
                if (pBo == null) {
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                }

                if(pBo.getState() == 1){
                    return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
                }


            }
            int ret = regionMapper.insertSelective(po);
            if (ret == 0) {
                // 插入失败
                log.debug("新增地区失败：" + po.getName());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增地区失败：%s", po.getName()));
            } else {
                // 插入成功
                log.debug("新增地区成功: " + po.getName());
                redisTemplate.opsForHash().put(validRegion, po.getId().toString(), new Region(po));
                return new ReturnObject<>(ResponseCode.OK, String.format("新增地区成功：%s", po.getName()));
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：%s", e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            log.error("其他错误：%s", e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    }

    /**
     * 管理员修改某个地区
     *
     * @param bo 地区bo
     * @author wwc
     * @date 2020/11/25 08:29
     * @version 1.0
     */
    public ReturnObject updateRegion(Region bo) {


        Region pBo = getSelectRegionById(bo.getId());

        // 若地区不存在
        if (pBo == null) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        if(pBo.getState() == 1){
            return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
        }

        RegionPo po = bo.createPo();
        RegionPoExample regionPoExample = new RegionPoExample();
        RegionPoExample.Criteria regionPoCriteria = regionPoExample.createCriteria();
        // 只能修改未被废弃的地区从redis删除，需要时加载
        redisTemplate.opsForHash().delete(validRegion, po.getId().toString());
        regionPoCriteria.andIdEqualTo(po.getId());
        regionPoCriteria.andStateEqualTo((byte) 0);
        try {
            int ret = regionMapper.updateByExampleSelective(po, regionPoExample);
            if (ret == 0) {
                // 修改失败
                log.debug("修改地区失败：" + po.getName());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("修改地区失败：%s", po.getName()));
            } else {
                // 修改成功
                log.debug("修改地区成功: " + po.getName());
                return new ReturnObject<>(ResponseCode.OK, String.format("修改地区成功：%s", po.getName()));
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
     * 管理员让某个地区无效
     *
     * @param id 地区id
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    public ReturnObject deleteRegion(Long id) {
        try {
            // 先查询该地区信息
            Region bo = getSelectRegionById(id);
            if (bo == null) {
                // 若该地区不存在
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                if(bo.getState() == 1){
                    // 若该地区被废弃
                    return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
                }
                // 该地区可以废弃
                log.debug("父地区可以废弃: " + id);
                // 查询该父地区的子地区,然后级联查询所有子地区的id
                // 最终要废弃的所有地区的id
                Set<Long> allDelete = Sets.newHashSet();
                // 存放当前查询的子地区的id,初始为该父地区id
                Set<Long> allSonId = Sets.newHashSet(id);
                // 当仍有子地区id表明没有查询到底
                while (!allSonId.isEmpty()) {
                    // 将sonId与allDelete去重复取交集
                    allDelete = Sets.union(allDelete, allSonId);
                    // 查询父地区id为当前sonId的地区的所有子地区
                    RegionPoExample sonPoExample = new RegionPoExample();
                    RegionPoExample.Criteria sonPoCriteria = sonPoExample.createCriteria();
                    List<Long> sonIdList = Lists.newArrayList(allSonId);
                    sonPoCriteria.andPidIn(sonIdList);
                    List<RegionPo> sonRegionPo = regionMapper.selectByExample(sonPoExample);
                    // 获得当前所查询获得的所有子地区的id
                    sonIdList = Lists.newArrayList();
                    for (RegionPo sonPo : sonRegionPo) {
                        sonIdList.add(sonPo.getId());
                    }
                    // 暂存入allSonId中
                    allSonId = Sets.newHashSet(sonIdList);
                }
                // 将allDelete中的地区全部废除
                RegionPoExample allSonPoExample = new RegionPoExample();
                RegionPoExample.Criteria allSonPoCriteria = allSonPoExample.createCriteria();
                allSonPoCriteria.andIdIn(Lists.newArrayList(allDelete));
                allSonPoCriteria.andStateEqualTo((byte) 0);
                // 更改状态和修改时间
                RegionPo po = new RegionPo();
                po.setState((byte) 1);
                po.setGmtModified(LocalDateTime.now());
                // 删除redis中的信息
                for (Long deleteId : allDelete) {
                    redisTemplate.opsForHash().delete(validRegion, deleteId.toString());
                }
                // 总共废弃的地区数
                int allNum = regionMapper.updateByExampleSelective(po, allSonPoExample);
                log.debug("废弃地区共: " + allNum);
                return new ReturnObject<>(ResponseCode.OK, String.format("废弃地区共：%s", allNum));
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
     * 查询某个地区的所有上级地区
     *
     * @param id 地区id
     * @author wwc
     * @date 2020/11/25 08:28
     * @version 1.0
     */
    public ReturnObject listSelectAllParentRegion(Long id) {
        try {

            RegionPo regionPo = regionMapper.selectByPrimaryKey(id);
            if(regionPo == null)
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            else if(regionPo.getState() == 1){
                // 地区已经被废弃
                return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
            }

            // 先查询该地区信息
            Region bo = getSelectRegionById(id);
            if (bo == null) {
                // 若该地区不存在
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {

                if(bo.getState() == 1){

                    // 地区已经被废弃
                    return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
                }

                // 递归查询父地区
                List<Region> allRegion = Lists.newArrayList();
                Long fatherId = bo.getPid();
                while (fatherId != null && fatherId != (byte) 0) {
                    bo = getSelectRegionById(fatherId);
                    // 该地区的父地区有效
                    if (bo != null) {
                        allRegion.add(bo);
                        fatherId = bo.getPid();
                    } else {
                        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("未查询到父地区: %s", bo.getPid()));
                    }
                }
                return new ReturnObject<>(Lists.reverse(allRegion));
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
     * 查询该地区是否已被废弃
     *
     * @param id 地区id
     * @author wwc
     * @date 2020/12、1 08:28
     * @version 1.0
     */
    public ReturnObject<Boolean> getSelectRegionVaild(Long id) {
        try {
            RegionPo po = regionMapper.selectByPrimaryKey(id);
            if (po != null && po.getState() == 0) {
                return new ReturnObject<>(true);
            } else {
                return new ReturnObject<>(false);
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
     * 根据id查询地区信息先查询redis
     *
     * @param id 地区id
     * @author wwc
     * @date 2020/12、1 08:28
     * @version 1.0
     */
    public Region getSelectRegionById(Long id) {
        try {
            // 先查redis
            Region region = (Region) redisTemplate.opsForHash().get(validRegion, id.toString());
            if (region != null) {
                return region;
            }
            // 否则检查数据库
            RegionPo po = regionMapper.selectByPrimaryKey(id);
            if (po == null) {
                // 若该地区不存在
                return null;
            }
            else {

                Region bo = new Region(po);

                // 该地区废弃
                if(bo.getState() == 1){
                    return bo;
                }

                // 若数据库存在该地区且未废弃，则加入redis
                redisTemplate.opsForHash().put(validRegion, bo.getId().toString(), bo);
                return bo;
            }
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return null;
        }
    }



    /**
     * 查询某个地区的所有上级地区，与订单模块接口
     *
     * @param id 地区id
     * @author yang8miao
     * @date 2020/12/18 13:28
     * @version 1.0
     */
    public ReturnObject<List<Long>> getParentListById(Long id) {
        try {


            // 先查询该地区信息
            Region bo = getSelectRegionById(id);
            if (bo == null) {
                // 若该地区不存在
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {

                if(bo.getState() == 1){

                    // 地区已经被废弃
                    return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
                }

                // 递归查询父地区
                List<Long> allRegion = Lists.newArrayList();
                Long fatherId = bo.getPid();
                while (fatherId != null && fatherId != (byte) 0) {
                    bo = getSelectRegionById(fatherId);

                    // 该地区的父地区有效
                    if (bo != null &&bo.getState() == 0) {
                        allRegion.add(bo.getId());
                        fatherId = bo.getPid();
                    } else {
                        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("未查询到父地区: %s", bo.getPid()));
                    }
                }
                return new ReturnObject<>(Lists.reverse(allRegion));
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
