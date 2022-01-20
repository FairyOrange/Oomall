package cn.edu.xmu.oomall.user.dao;

import cn.edu.xmu.oomall.user.mapper.CustomerPoMapper;
import cn.edu.xmu.oomall.user.model.bo.SimpleUser;
import cn.edu.xmu.oomall.user.model.bo.User;
import cn.edu.xmu.oomall.user.model.po.CustomerPo;
import cn.edu.xmu.oomall.user.model.po.CustomerPoExample;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import cn.edu.xmu.oomall.util.bloom.BloomFilterHelper;
import cn.edu.xmu.oomall.util.bloom.RedisBloomFilter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户Dao
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/30 23:34
 */
@Slf4j
@Repository
public class UserDao implements InitializingBean {

    @Autowired
    CustomerPoMapper userMapper;

    @Autowired
    RedisTemplate redisTemplate;

    RedisBloomFilter bloomFilter;
    String[] fieldName;
    final String suffixName = "BloomFilter";

    /**
     * 通过该参数选择是否清空布隆过滤器
     */
    private boolean reinitialize = true;

    /**
     * 初始化布隆过滤器
     *
     * @throws Exception
     * @author wwc
     * @date 2020/11/30 23:34
     * @version 1.0
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        BloomFilterHelper bloomFilterHelper = new BloomFilterHelper<>(Funnels.stringFunnel(Charsets.UTF_8), 1000, 0.02);
        fieldName = new String[]{"email", "mobile", "userName"};
        bloomFilter = new RedisBloomFilter(redisTemplate, bloomFilterHelper);
        if (reinitialize) {
            for (int i = 0; i < fieldName.length; i++) {
                redisTemplate.delete(fieldName[i] + suffixName);
            }
        }
    }

    /**
     * @param po
     * @return ReturnObject 错误返回对象
     * @author wwc
     * @date 2020/11/30 23:34
     * @version 1.0
     */
    public ReturnObject checkBloomFilter(CustomerPo po) {
        if (bloomFilter.includeByBloomFilter("email" + suffixName, po.getEmail())) {
            return new ReturnObject<>(ResponseCode.EMAIL_REGISTERED);
        }
        if (bloomFilter.includeByBloomFilter("mobile" + suffixName, po.getMobile())) {
            return new ReturnObject<>(ResponseCode.MOBILE_REGISTERED);
        }
        if (bloomFilter.includeByBloomFilter("userName" + suffixName, po.getUserName())) {
            return new ReturnObject<>(ResponseCode.USER_NAME_REGISTERED);
        }
        return null;
    }

    /**
     * 由属性名及属性值设置相应布隆过滤器
     *
     * @param name 属性名
     * @param po   po对象
     * @author wwc
     * @date 2020/11/30 23:34
     * @version 1.0
     */
    public void setBloomFilterByName(String name, CustomerPo po) {
        try {
            Field field = CustomerPo.class.getDeclaredField(name);
            Method method = po.getClass().getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
            log.debug("add value " + method.invoke(po) + " to " + field.getName() + suffixName);
            bloomFilter.addByBloomFilter(field.getName() + suffixName, method.invoke(po));
        } catch (Exception ex) {
            log.error("Exception happened:" + ex.getMessage());
        }
    }

    /**
     * 检查用户名重复
     *
     * @param userName 需要检查的用户名
     * @return Boolean
     * @author wwc
     * @date 2020/11/30 23:34
     * @version 1.0
     */
    public Boolean isUserNameExist(String userName) {
        log.debug("is checking userName in user table");
        CustomerPoExample example = new CustomerPoExample();
        CustomerPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        List<CustomerPo> userPos = userMapper.selectByExample(example);
        return !userPos.isEmpty();
    }

    /**
     * 检查邮箱重复
     *
     * @param email
     * @return Boolean
     * @author wwc
     * @date 2020/11/30 23:34
     * @version 1.0
     */
    public Boolean isEmailExist(String email) {
        log.debug("is checking email in user table");
        CustomerPoExample example = new CustomerPoExample();
        CustomerPoExample.Criteria criteria = example.createCriteria();
        criteria.andEmailEqualTo(email);
        List<CustomerPo> userPos = userMapper.selectByExample(example);
        return !userPos.isEmpty();
    }

    /**
     * 检查电话重复
     *
     * @param mobile 电话号码
     * @return Boolean
     * @author wwc
     * @date 2020/11/30 23:34
     * @version 1.0
     */
    public Boolean isMobileExist(String mobile) {
        log.debug("is checking mobile in user table");
        CustomerPoExample example = new CustomerPoExample();
        CustomerPoExample.Criteria criteria = example.createCriteria();
        criteria.andMobileEqualTo(mobile);
        List<CustomerPo> userPos = userMapper.selectByExample(example);
        return !userPos.isEmpty();
    }

    /**
     * 检查重复后插入
     *
     * @author wwc
     * @date 2020/11/30 18:15
     * @version 1.0
     */
    public ReturnObject insertUser(User bo) {
        CustomerPo po = bo.createPo();
        // 首先检查布隆过滤器是否有该用户
        ReturnObject ret = checkBloomFilter(po);
        if (ret != null) {
            log.debug("布隆过滤器过滤成功");
            return ret;
        }
        // 若布隆过滤器中没有该用户则查询是否为误差并设置对应过滤器
        // TODO 现在使用了三条查询语句之后可以考虑优化
        if (isEmailExist(po.getEmail())) {
            setBloomFilterByName("email", po);
            return new ReturnObject<>(ResponseCode.EMAIL_REGISTERED);
        }
        if (isMobileExist(po.getMobile())) {
            setBloomFilterByName("mobile", po);
            return new ReturnObject<>(ResponseCode.MOBILE_REGISTERED);
        }
        if (isUserNameExist(po.getUserName())) {
            setBloomFilterByName("userName", po);
            return new ReturnObject<>(ResponseCode.USER_NAME_REGISTERED);
        }
        // 然后尝试增加新用户
        try {
            int insertNum = userMapper.insertSelective(po);
            if (insertNum == 0) {
                // 新建用户失败
                log.debug("新建用户失败：" + po.getUserName());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新建用户失败：%s", po.getUserName()));
            } else {
                bo.setId(po.getId());
                return new ReturnObject<>(bo);
            }
        } catch (DuplicateKeyException e) {
            log.debug("唯一键值冲突");
            // 设置布隆过滤器
            String info = e.getMessage();
            if (info.contains("user_name_uindex")) {
                setBloomFilterByName("userName", po);
                return new ReturnObject<>(ResponseCode.USER_NAME_REGISTERED);
            }
            if (info.contains("email_uindex")) {
                setBloomFilterByName("email", po);
                return new ReturnObject<>(ResponseCode.EMAIL_REGISTERED);
            }
            if (info.contains("mobile_uindex")) {
                setBloomFilterByName("mobile", po);
                return new ReturnObject<>(ResponseCode.MOBILE_REGISTERED);
            }
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    }

    /**
     * 根据用户名查询用户信息
     *
     * @author wwc
     * @date 2020/11/30 18:15
     * @version 1.0
     */
    public ReturnObject getSelectUserByName(String userName) {
        CustomerPoExample example = new CustomerPoExample();
        CustomerPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        try {
            List<CustomerPo> userPoList = userMapper.selectByExample(example);
            if (userPoList.isEmpty() || userPoList == null) {
                // 查询用户失败
                log.debug("没有该用户：" + userName);
                return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
            } else {
                CustomerPo userPo = userPoList.get(0);
                if (userPo.getState() == null || userPo.getState() != User.State.BANED.getCode().byteValue()) {
                    // 查询用户成功
                    User bo = new User(userPo);
                    return new ReturnObject<>(bo);
                } else {
                    return new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN);
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
     * 用户修改密码
     *
     * @author wwc
     * @date 2020/11/30 18:15
     * @version 1.0
     */
    public ReturnObject updateUserPwd(String newPassword, String captcha) {
        try {
            //通过验证码取出id
            if (!redisTemplate.hasKey("cp_" + captcha)) {
                // 若验证码过期或无该验证码对应的ID
                // TODO 无验证码错误
                return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
            }
            Long id = Long.parseLong(redisTemplate.opsForValue().get("cp_" + captcha).toString());
            // 用后即删除
            redisTemplate.delete("cp_" + captcha);
            CustomerPo po = userMapper.selectByPrimaryKey(id);
            if (newPassword.equals(po.getPassword())) {
                // 新密码与原密码相同
                return new ReturnObject<>(ResponseCode.PASSWORD_SAME);
            } else {
                po = new CustomerPo();
                po.setId(id);
                po.setPassword(newPassword);
                po.setGmtModified(LocalDateTime.now());
                userMapper.updateByPrimaryKeySelective(po);
                return new ReturnObject<>(ResponseCode.OK);
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
     * 用户查询个人信息
     *
     * @param userIdAudit 当前用户id
     * @author wwc
     * @date 2020/12/02 20:43
     * @version 1.0
     */
    public ReturnObject getUserSelectInfo(Long userIdAudit) {
        try {
            CustomerPo po = userMapper.selectByPrimaryKey(userIdAudit);
            return new ReturnObject<>(new User(po));
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    }

    /**
     * 用户修改个人信息
     *
     * @param bo 修改的用户信息
     * @author wwc
     * @date 2020/12/02 20:43
     * @version 1.0
     */
    public ReturnObject updateUserInfo(User bo) {
        CustomerPo po = bo.createPo();
        try {
            int updateNum = userMapper.updateByPrimaryKeySelective(po);
            if (updateNum == 0) {
                // 修改用户失败
                log.debug("修改用户失败：" + po.getUserName());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("修改用户失败：%s", po.getUserName()));
            } else {
                return new ReturnObject<>(ResponseCode.OK);
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
     * 平台管理员获取所有用户
     *
     * @param bo 修改的用户信息
     * @author wwc
     * @date 2020/12/02 20:43
     * @version 1.0
     */
    public ReturnObject listAdminSelectAllUser(User bo, Integer page, Integer pageSize) {
        CustomerPoExample customerPoExample = new CustomerPoExample();
        CustomerPoExample.Criteria criteria = customerPoExample.createCriteria();
        if (bo.getUserName() != null) {
            criteria.andUserNameEqualTo(bo.getUserName());
        }
        if (bo.getEmail() != null) {
            criteria.andEmailEqualTo(bo.getEmail());
        }
        if (bo.getMobile() != null) {
            criteria.andMobileEqualTo(bo.getMobile());
        }
        try {
            // 根据条件分页查询
            PageHelper.startPage(page, pageSize);
            List<CustomerPo> userPoList = userMapper.selectByExample(customerPoExample);
            PageInfo poPage = new PageInfo<>(userPoList);
            List userBoList = Lists.transform(userPoList, SimpleUser::new);
            PageInfo retObject = new PageInfo<>(userBoList);
            retObject.setPages(poPage.getPages());
            retObject.setPageNum(poPage.getPageNum());
            retObject.setPageSize(poPage.getPageSize());
            retObject.setTotal(poPage.getTotal());
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
     * 更新用户状态
     *
     * @param id    要修改的用户id
     * @param state 修改成的状态
     * @author wwc
     * @date 2020/12/02 20:43
     * @version 1.0
     */
    public ReturnObject updateUserState(Long id, User.State state) {
        CustomerPo po = new CustomerPo();
        po.setId(id);
        po.setState(state.getCode().byteValue());
        po.setGmtModified(LocalDateTime.now());
        try {
            int updateNum = userMapper.updateByPrimaryKeySelective(po);
            if (updateNum == 0) {
                // 修改状态失败
                log.debug("修改状态失败：" + po.getId());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("修改状态失败：%s", po.getId()));
            } else {
                return new ReturnObject<>(ResponseCode.OK);
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
