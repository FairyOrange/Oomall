package cn.edu.xmu.oomall.user.service;

import cn.edu.xmu.oomall.user.dao.UserDao;
import cn.edu.xmu.oomall.user.model.bo.User;
import cn.edu.xmu.oomall.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 用户Service
 *
 * @athor wwc
 * @date 2020/11/30 20:15
 * @version 1.0
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    RedisTemplate redisTemplate;

    // TODO 还未写到配置文件中
    /**
     * 是否可以重复登录
     */
    @Value("${privilegeservice.login.multiply:false}")
    private Boolean canMultiplyLogin;

    /**
     * 分布式锁的过期时间（秒）
     */
    @Value("${privilegeservice.lockerExpireTime:30}")
    private long lockerExpireTime;

    /**
     * jwt有效期
     */
    @Value("${privilegeservice.login.jwtExpire:3600}")
    private Integer jwtExpireTime;

    /**
     * 用户在Redis中的过期时间，而不是JWT的有效期
     */
    @Value("${privilegeservice.user.expiretime:600}")
    private long timeout;

    /**
     * 注册用户
     *
     * @author wwc
     * @date 2020/11/30 18:05
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject insertUser(User bo) {
        ReturnObject returnObject = userDao.insertUser(bo);
        return returnObject;
    }

    /**
     * 用户登录
     *
     * @author wwc
     * @date 2020/11/30 18:05
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject userLogin(String userName, String password) {
        ReturnObject returnObject = userDao.getSelectUserByName(userName);
        if (returnObject.getData() != null) {
            // 若查询成功则判断密码是否正确且是否被封禁
            User bo = (User)returnObject.getData();
            if (password.equals(bo.getPassword()) && bo.getState() != User.State.BANED) {
                String key = "upc_" + bo.getId();
                log.debug("login: key = "+ key);
                //创建新的token
                JwtHelper jwtHelper = new JwtHelper();
                // 普通用户的departID设置为-2
                String jwt = jwtHelper.createToken(bo.getId(), -2L, jwtExpireTime);
                // 用户模块仅判断是否登录，商城不需要校验权限及判断重复登录，仅作为判断该用户是否注销
                return new ReturnObject<>(jwt);
            } else {
                return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
            }
        } else {
            return returnObject;
        }
    }

    /**
     * 禁止持有特定令牌的用户登录
     * @param jwt JWT令牌
     */
    private void banJwt(String jwt){
        String[] banSetName = {"BanJwt_0", "BanJwt_1"};
        long bannIndex = 0;
        if (!redisTemplate.hasKey("banIndex")){
            redisTemplate.opsForValue().set("banIndex", Long.valueOf(0));
        } else {
            log.debug("banJwt: banIndex = " +redisTemplate.opsForValue().get("banIndex"));
            bannIndex = Long.parseLong(redisTemplate.opsForValue().get("banIndex").toString());
        }
        log.debug("banJwt: banIndex = " + bannIndex);
        String currentSetName = banSetName[(int) (bannIndex % banSetName.length)];
        log.debug("banJwt: currentSetName = " + currentSetName);
        if(!redisTemplate.hasKey(currentSetName)) {
            // 新建
            log.debug("banJwt: create ban set" + currentSetName);
            redisTemplate.opsForSet().add(currentSetName, jwt);
            redisTemplate.expire(currentSetName,jwtExpireTime * 2,TimeUnit.SECONDS);
        }else{
            //准备向其中添加元素
            if(redisTemplate.getExpire(currentSetName, TimeUnit.SECONDS) > jwtExpireTime) {
                // 有效期还长，直接加入
                log.debug("banJwt: add to exist ban set" + currentSetName);
                redisTemplate.opsForSet().add(currentSetName, jwt);
            } else {
                // 有效期不够JWT的过期时间，准备用第二集合，让第一个集合自然过期
                // 分步式加锁
                log.debug("banJwt: switch to next ban set" + currentSetName);
                long newBanIndex = bannIndex;
                while (newBanIndex == bannIndex &&
                        !redisTemplate.opsForValue().setIfAbsent("banIndexLocker","nouse", lockerExpireTime, TimeUnit.SECONDS)){
                    //如果BanIndex没被其他线程改变，且锁获取不到
                    try {
                        Thread.sleep(10);
                        //重新获得新的BanIndex
                        newBanIndex = (Long) redisTemplate.opsForValue().get("banIndex");
                    }catch (InterruptedException e){
                        log.error("banJwt: 锁等待被打断");
                    }
                    catch (IllegalArgumentException e){

                    }
                }
                if (newBanIndex == bannIndex) {
                    //切换ban set
                    bannIndex = redisTemplate.opsForValue().increment("banIndex");
                }else{
                    //已经被其他线程改变
                    bannIndex = newBanIndex;
                }

                currentSetName = banSetName[(int) (bannIndex % banSetName.length)];
                //启用之前，不管有没有，先删除一下，应该是没有，保险起见
                redisTemplate.delete(currentSetName);
                log.debug("banJwt: next ban set =" + currentSetName);
                redisTemplate.opsForSet().add(currentSetName, jwt);
                redisTemplate.expire(currentSetName,jwtExpireTime * 2,TimeUnit.SECONDS);
                // 解锁
                redisTemplate.delete("banIndexLocker");
            }
        }
    }

    /**
     * 用户注销
     *
     * @author wwc
     * @date 2020/11/30 19:48
     * @version 1.0
     */
    public ReturnObject userLogout(String jwt) {
        // 用户注销该token
        banJwt(jwt);
        return new ReturnObject<>(ResponseCode.OK);
    }

    /**
     * 用户修改密码
     *
     * @author wwc
     * @date 2020/11/30 19:48
     * @version 1.0
     */
    public ReturnObject updateUserPwd(String newPassword, String captcha) {
        ReturnObject returnObject = userDao.updateUserPwd(newPassword, captcha);
        return returnObject;
    }

    /**
     * 用户重置密码
     *
     * @author wwc
     * @date 2020/11/30 18:05
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateUserResetPwd(String userName, String email, String ip) {
        //防止重复请求验证码
        if (redisTemplate.hasKey("ip_"+ip)) {
            return new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN);
        } else {
            //1 min中内不能重复请求
            redisTemplate.opsForValue().set("ip_"+ip,ip);
            redisTemplate.expire("ip_" + ip, 60*1000, TimeUnit.MILLISECONDS);
        }
        ReturnObject returnObject = userDao.getSelectUserByName(userName);
        if (returnObject.getData() != null) {
            // 若查询成功则判断密码是否正确且是否重复登录
            User bo = (User)returnObject.getData();
            // 验证邮箱
            if (!email.equals(bo.getEmail())) {
                return new ReturnObject<>(ResponseCode.EMAIL_WRONG);
            }
            //随机生成验证码
            String captcha = RandomCaptcha.getRandomString(6);
            while (redisTemplate.hasKey(captcha)) {
                captcha = RandomCaptcha.getRandomString(6);
            }
            String id = bo.getId().toString();
            String key = "cp_" + captcha;
            // key:验证码,value:id存入redis
            redisTemplate.opsForValue().set(key, id);
            // 五分钟后过期
            redisTemplate.expire("cp_" + captcha, 5*60*1000, TimeUnit.MILLISECONDS);
            // TODO 添加邮件功能
//            //发送邮件(请在配置文件application.properties填写密钥)
//            SimpleMailMessage msg = new SimpleMailMessage();
//            msg.setSubject("【oomall】密码重置通知");
//            msg.setSentDate(new Date());
//            msg.setText("您的验证码是：" + captcha + "，5分钟内有效。");
//            msg.setFrom("925882085@qq.com");
//            msg.setTo(vo.getEmail());
//            try {
//                mailSender.send(msg);
//            } catch (MailException e) {
//                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
//            }
            return new ReturnObject<>(ResponseCode.OK);
        } else {
            return returnObject;
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
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getUserSelectInfo(Long userIdAudit) {
        ReturnObject returnObject = userDao.getUserSelectInfo(userIdAudit);
        return returnObject;
    }

    /**
     * 用户修改个人信息
     *
     * @param bo 当前用户信息
     * @author wwc
     * @date 2020/12/02 20:43
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateUserInfo(User bo) {
        ReturnObject returnObject = userDao.updateUserInfo(bo);
        return returnObject;
    }

    /**
     * 平台管理员获取所有用户
     *
     * @param bo 前端传递的参数
     * @param page 页数
     * @param pageSize 每页大小
     * @author wwc
     * @date 2020/12/02 20:43
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject listAdminSelectAllUser(User bo, Integer page, Integer pageSize) {
        ReturnObject returnObject = userDao.listAdminSelectAllUser(bo, page, pageSize);
        return returnObject;
    }

    /**
     * 管理员查看任意买家信息
     *
     * @param id 用户id
     * @author wwc
     * @date 2020/12/02 20:43
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getAdminSelectInfo(Long id) {
        ReturnObject returnObject = userDao.getUserSelectInfo(id);
        return returnObject;
    }

    /**
     * 平台管理员封禁买家
     *
     * @param id 用户id
     * @author wwc
     * @date 2020/12/02 20:43
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateUserToBan(Long id) {
        redisTemplate.opsForValue().set("banUser_" + id, id, jwtExpireTime, TimeUnit.SECONDS);
        ReturnObject returnObject = userDao.updateUserState(id, User.State.BANED);
        return returnObject;
    }

    /**
     * 平台管理员恢复买家
     *
     * @param id 用户id
     * @author wwc
     * @date 2020/12/02 20:43
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateUserToNormal(Long id) {
        redisTemplate.delete("banUser_" + id);
        ReturnObject returnObject = userDao.updateUserState(id, User.State.NORMAL);
        return returnObject;
    }
}
