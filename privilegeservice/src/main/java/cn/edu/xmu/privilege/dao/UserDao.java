package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.oomall.util.ReturnObject;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.util.*;
import cn.edu.xmu.privilege.mapper.RolePoMapper;
import cn.edu.xmu.oomall.util.encript.AES;
import cn.edu.xmu.oomall.util.ReturnObject;
import cn.edu.xmu.oomall.util.encript.SHA256;
import cn.edu.xmu.privilege.mapper.UserPoMapper;
import cn.edu.xmu.privilege.mapper.UserProxyPoMapper;
import cn.edu.xmu.privilege.mapper.UserRolePoMapper;
import cn.edu.xmu.privilege.model.bo.*;
import cn.edu.xmu.privilege.model.po.*;
import cn.edu.xmu.privilege.model.vo.*;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.edu.xmu.privilege.model.po.UserProxyPo;
import cn.edu.xmu.privilege.model.po.UserProxyPoExample;
import cn.edu.xmu.privilege.model.po.UserRolePo;
import cn.edu.xmu.privilege.model.po.UserRolePoExample;
import cn.edu.xmu.privilege.model.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Ming Qiu
 * @date Created in 2020/11/1 11:48
 * Modified in 2020/11/8 0:57
 **/
@Repository
public class UserDao{

    @Autowired
    private UserPoMapper userPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    // ?????????Redis??????????????????????????????JWT????????????
    @Value("${privilegeservice.user.expiretime}")
    private long timeout;


    @Autowired
    private UserRolePoMapper userRolePoMapper;

    @Autowired
    private UserProxyPoMapper userProxyPoMapper;

    @Autowired
    private UserPoMapper userMapper;

    @Autowired
    private RolePoMapper rolePoMapper;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private JavaMailSender mailSender;
    /**
     * @author yue hao
     * @param id ??????ID
     * @return ?????????????????????
     */

    public ReturnObject<List> findPrivsByUserId(Long id, Long did) {
        //getRoleIdByUserId????????????????????????
        User user = getUserById(id.longValue()).getData();
        if (user == null) {//?????????????????????????????????????????????
            logger.error("findPrivsByUserId: ??????????????????????????? userid=" + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Long departId = user.getDepartId();
        if(! departId.equals(did)) {
            logger.error("findPrivsByUserId: ??????id????????? userid=" + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        List<Long> roleIds = this.getRoleIdByUserId(id);
        List<Privilege> privileges = new ArrayList<>();
        for(Long roleId: roleIds) {
            List<Privilege> rolePriv = roleDao.findPrivsByRoleId(roleId);
            privileges.addAll(rolePriv);
        }
        return new ReturnObject<>(privileges);
    }


    /**
     * ????????????????????????
     *
     * @param userName
     * @return
     */
    public ReturnObject<User> getUserByName(String userName) {
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        List<UserPo> users = null;
        try {
            users = userPoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            StringBuilder message = new StringBuilder().append("getUserByName: ").append(e.getMessage());
            logger.error(message.toString());
        }

        if (null == users || users.isEmpty()) {
            return new ReturnObject<>();
        } else {
            User user = new User(users.get(0));
            if (!user.authetic()) {
                StringBuilder message = new StringBuilder().append("getUserByName: ").append("id= ")
                        .append(user.getId()).append(" username=").append(user.getUserName());
                logger.error(message.toString());
                return new ReturnObject<>(ResponseCode.RESOURCE_FALSIFY);
            } else {
                return new ReturnObject<>(user);
            }
        }
    }

    /**
     * @param userId ??????ID
     * @param IPAddr IP??????
     * @param date   ????????????
     * @return ??????????????????
     */
    public Boolean setLoginIPAndPosition(Long userId, String IPAddr, LocalDateTime date) {
        UserPo userPo = new UserPo();
        userPo.setId(userId);
        userPo.setLastLoginIp(IPAddr);
        userPo.setLastLoginTime(date);
        if (userPoMapper.updateByPrimaryKeySelective(userPo) == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ??????????????????
     * @param userid ??????id
     * @param roleid ??????id
     * @return ReturnObject<VoObject>
     * @author Xianwei Wang
     * */
    public ReturnObject<VoObject> revokeRole(Long userid, Long roleid){
        UserRolePoExample userRolePoExample = new UserRolePoExample();
        UserRolePoExample.Criteria criteria = userRolePoExample.createCriteria();
        criteria.andUserIdEqualTo(userid);
        criteria.andRoleIdEqualTo(roleid);

        User user = getUserById(userid.longValue()).getData();
        RolePo rolePo = rolePoMapper.selectByPrimaryKey(roleid);

        //??????id?????????id?????????
        if (user == null || rolePo == null) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        try {
            int state = userRolePoMapper.deleteByExample(userRolePoExample);
            if (state == 0){
                logger.warn("revokeRole: ????????????????????????");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }


        } catch (DataAccessException e) {
            // ???????????????
            logger.error("??????????????????" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("????????????????????????????????????%s", e.getMessage()));
        } catch (Exception e) {
            // ???????????????
            logger.error("???????????????" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("?????????????????????????????????%s", e.getMessage()));
        }

        //????????????
        clearUserPrivCache(userid);

        return new ReturnObject<>();
    }

    /**
     * ??????????????????
     * @param createid ?????????id
     * @param userid ??????id
     * @param roleid ??????id
     * @return ReturnObject<VoObject>
     * @author Xianwei Wang
     * */
    public ReturnObject<VoObject> assignRole(Long createid, Long userid, Long roleid){
        UserRolePo userRolePo = new UserRolePo();
        userRolePo.setUserId(userid);
        userRolePo.setRoleId(roleid);

        User user = getUserById(userid.longValue()).getData();
        User create = getUserById(createid.longValue()).getData();
        RolePo rolePo = rolePoMapper.selectByPrimaryKey(roleid);

        //??????id?????????id?????????
        if (user == null || create == null || rolePo == null) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        userRolePo.setCreatorId(createid);
        userRolePo.setGmtCreate(LocalDateTime.now());

        UserRole userRole = new UserRole(userRolePo, user, new Role(rolePo), create);
        userRolePo.setSignature(userRole.getCacuSignature());

        //??????????????????????????????????????????
        UserRolePoExample example = new UserRolePoExample();
        UserRolePoExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userid);
        criteria.andRoleIdEqualTo(roleid);

        //??????????????????????????????
        try {
            List<UserRolePo> userRolePoList = userRolePoMapper.selectByExample(example);
            if (userRolePoList.isEmpty()){
                userRolePoMapper.insert(userRolePo);
            } else {
                logger.warn("assignRole: ??????????????????????????? userid=" + userid + "roleid=" + roleid);
                return new ReturnObject<>(ResponseCode.USER_ROLE_REGISTERED);
            }
        } catch (DataAccessException e) {
            // ???????????????
            logger.error("??????????????????" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("????????????????????????????????????%s", e.getMessage()));
        } catch (Exception e) {
            // ???????????????
            logger.error("???????????????" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("?????????????????????????????????%s", e.getMessage()));
        }
        //????????????
        clearUserPrivCache(userid);

        return new ReturnObject<>(new UserRole(userRolePo, user, new Role(rolePo), create));

    }

    /**
     * ????????????id???????????????????????????????????????redis??????
     * @param userid ??????id
     * @author Xianwei Wang
     */
    private void clearUserPrivCache(Long userid){
        String key = "u_" + userid;
        redisTemplate.delete(key);

        UserProxyPoExample example = new UserProxyPoExample();
        UserProxyPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserBIdEqualTo(userid);
        List<UserProxyPo> userProxyPoList = userProxyPoMapper.selectByExample(example);

        LocalDateTime now = LocalDateTime.now();

        for (UserProxyPo po:
                userProxyPoList) {
            StringBuilder signature = Common.concatString("-", po.getUserAId().toString(),
                    po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), po.getValid().toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            UserProxyPo newPo = null;

            if (newSignature.equals(po.getSignature())) {
                if (now.isBefore(po.getEndDate()) && now.isAfter(po.getBeginDate())) {
                    //???????????????
                    String proxyKey = "up_" + po.getUserAId();
                    redisTemplate.delete(proxyKey);
                    logger.debug("clearUserPrivCache: userAId = " + po.getUserAId() + " userBId = " + po.getUserBId());
                } else {
                    //?????????????????????????????????????????????
                    newPo = newPo == null ? new UserProxyPo() : newPo;
                    newPo.setValid((byte) 0);
                    signature = Common.concatString("-", po.getUserAId().toString(),
                            po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), newPo.getValid().toString());
                    newSignature = SHA256.getSHA256(signature.toString());
                    newPo.setSignature(newSignature);
                }
            } else {
                logger.error("clearUserPrivCache: Wrong Signature(auth_user_proxy): id =" + po.getId());
            }

            if (null != newPo) {
                logger.debug("clearUserPrivCache: writing back.. po =" + newPo);
                userProxyPoMapper.updateByPrimaryKeySelective(newPo);
            }

        }
    }

    /**
     * ???????????????????????????
     * @param id ??????id
     * @return UserRole??????
     * @author Xianwei Wang
     * */
    public ReturnObject<List> getUserRoles(Long id){
        UserRolePoExample example = new UserRolePoExample();
        UserRolePoExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(id);
        List<UserRolePo> userRolePoList = userRolePoMapper.selectByExample(example);
        logger.info("getUserRoles: userId = "+ id + "roleNum = "+ userRolePoList.size());

        List<UserRole> retUserRoleList = new ArrayList<>(userRolePoList.size());

        if (retUserRoleList.isEmpty()) {
            User user = getUserById(id.longValue()).getData();
            if (user == null) {
                logger.error("getUserRoles: ??????????????????????????? userid=" + id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
        }

        for (UserRolePo po : userRolePoList) {
            User user = getUserById(po.getUserId().longValue()).getData();
            User creator = getUserById(po.getCreatorId().longValue()).getData();
            RolePo rolePo = rolePoMapper.selectByPrimaryKey(po.getRoleId());
            if (user == null) {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            if (creator == null) {
                logger.error("getUserRoles: ??????????????????????????? userid=" + po.getCreatorId());
            }
            if (rolePo == null) {
                logger.error("getUserRoles: ???????????????????????????:rolePo id=" + po.getRoleId());
                continue;
            }

            Role role = new Role(rolePo);
            UserRole userRole = new UserRole(po, user, role, creator);

            //????????????
            if (userRole.authetic()){
                retUserRoleList.add(userRole);
                logger.info("getRoleIdByUserId: userId = " + po.getUserId() + " roleId = " + po.getRoleId());
            } else {
                logger.error("getUserRoles: Wrong Signature(auth_user_role): id =" + po.getId());
            }
        }
        return new ReturnObject<>(retUserRoleList);
    }


    /**
     * @description ???????????????departid???????????????????????????
     * @param userid ??????id
     * @param departid ????????????departid
     * @return boolean
     * @author Xianwei Wang
     * created at 11/20/20 1:48 PM
     */
    public boolean checkUserDid(Long userid, Long departid) {
        UserPo userPo = userMapper.selectByPrimaryKey(userid);
        if (userPo == null) {
            return false;
        }
        if (userPo.getDepartId() != departid) {
            return false;
        }
        return true;
    }

    /**
     * @description ???????????????departid???????????????????????????
     * @param roleid ??????id
     * @param departid ????????????departid
     * @return boolean
     * @author Xianwei Wang
     * created at 11/20/20 1:51 PM
     */
    public boolean checkRoleDid(Long roleid, Long departid) {
        RolePo rolePo = rolePoMapper.selectByPrimaryKey(roleid);
        if (rolePo == null) {
            return false;
        }
        if (rolePo.getDepartId() != departid) {
            return false;
        }
        return true;
    }


    /**
     * ??????User??????????????????load???Redis
     *
     * @param id userID
     * @return void
     * <p>
     * createdBy: Ming Qiu 2020-11-02 11:44
     * modifiedBy: Ming Qiu 2020-11-03 12:31
     * ???????????????Roleid???????????????, ??????redis????????????
     * Ming Qiu 2020-11-07 8:00
     * ?????????????????????0???
     */
    private void loadSingleUserPriv(Long id) {
        List<Long> roleIds = this.getRoleIdByUserId(id);
        String key = "u_" + id;
        Set<String> roleKeys = new HashSet<>(roleIds.size());
        for (Long roleId : roleIds) {
            String roleKey = "r_" + roleId;
            roleKeys.add(roleKey);
            if (!redisTemplate.hasKey(roleKey)) {
                roleDao.loadRolePriv(roleId);
            }
            redisTemplate.opsForSet().unionAndStore(roleKeys, key);
        }
        redisTemplate.opsForSet().add(key, 0);
        long randTimeout = Common.addRandomTime(timeout);
        redisTemplate.expire(key, randTimeout, TimeUnit.SECONDS);
    }

    /**
     * ?????????????????????id
     *
     * @param id ??????id
     * @return ??????id??????
     * createdBy: Ming Qiu 2020/11/3 13:55
     */
    private List<Long> getRoleIdByUserId(Long id) {
        UserRolePoExample example = new UserRolePoExample();
        UserRolePoExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(id);
        List<UserRolePo> userRolePoList = userRolePoMapper.selectByExample(example);
        logger.debug("getRoleIdByUserId: userId = " + id + "roleNum = " + userRolePoList.size());
        List<Long> retIds = new ArrayList<>(userRolePoList.size());
        for (UserRolePo po : userRolePoList) {
            StringBuilder signature = Common.concatString("-",
                    po.getUserId().toString(), po.getRoleId().toString(), po.getCreatorId().toString());
            String newSignature = SHA256.getSHA256(signature.toString());


            if (newSignature.equals(po.getSignature())) {
                retIds.add(po.getRoleId());
                logger.debug("getRoleIdByUserId: userId = " + po.getUserId() + " roleId = " + po.getRoleId());
            } else {
                logger.error("getRoleIdByUserId: ????????????(auth_role_privilege): id =" + po.getId());
            }
        }
        return retIds;
    }

    /**
     * ??????User???????????????????????????????????????????????????????????????????????????load???Redis
     *
     * @param id userID
     * @return void
     * createdBy Ming Qiu 2020/11/1 11:48
     * modifiedBy Ming Qiu 2020/11/3 14:37
     */
    public void loadUserPriv(Long id, String jwt) {

        String key = "u_" + id;
        String aKey = "up_" + id;

        List<Long> proxyIds = this.getProxyIdsByUserId(id);
        List<String> proxyUserKey = new ArrayList<>(proxyIds.size());
        for (Long proxyId : proxyIds) {
            if (!redisTemplate.hasKey("u_" + proxyId)) {
                logger.debug("loadUserPriv: loading proxy user. proxId = " + proxyId);
                loadSingleUserPriv(proxyId);
            }
            proxyUserKey.add("u_" + proxyId);
        }
        if (!redisTemplate.hasKey(key)) {
            logger.debug("loadUserPriv: loading user. id = " + id);
            loadSingleUserPriv(id);
        }
        redisTemplate.opsForSet().unionAndStore(key, proxyUserKey, aKey);
        redisTemplate.opsForSet().add(aKey, jwt);
        long randTimeout = Common.addRandomTime(timeout);
        redisTemplate.expire(aKey, randTimeout, TimeUnit.SECONDS);
    }

    /**
     * ?????????????????????id??????
     *
     * @param id ??????id
     * @return ??????????????????id
     * createdBy Ming Qiu 14:37
     */
    private List<Long> getProxyIdsByUserId(Long id) {
        UserProxyPoExample example = new UserProxyPoExample();
        //??????????????????????????????????????????
        UserProxyPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserAIdEqualTo(id);
        criteria.andValidEqualTo((byte) 1);
        List<UserProxyPo> userProxyPos = userProxyPoMapper.selectByExample(example);
        List<Long> retIds = new ArrayList<>(userProxyPos.size());
        LocalDateTime now = LocalDateTime.now();
        for (UserProxyPo po : userProxyPos) {
            StringBuilder signature = Common.concatString("-", po.getUserAId().toString(),
                    po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), po.getValid().toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            UserProxyPo newPo = null;

            if (newSignature.equals(po.getSignature())) {
                if (now.isBefore(po.getEndDate()) && now.isAfter(po.getBeginDate())) {
                    //???????????????
                    retIds.add(po.getUserBId());
                    logger.debug("getProxyIdsByUserId: userAId = " + po.getUserAId() + " userBId = " + po.getUserBId());
                } else {
                    //?????????????????????????????????????????????
                    newPo = newPo == null ? new UserProxyPo() : newPo;
                    newPo.setValid((byte) 0);
                    signature = Common.concatString("-", po.getUserAId().toString(),
                            po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), newPo.getValid().toString());
                    newSignature = SHA256.getSHA256(signature.toString());
                    newPo.setSignature(newSignature);
                }
            } else {
                logger.error("getProxyIdsByUserId: Wrong Signature(auth_user_proxy): id =" + po.getId());
            }

            if (null != newPo) {
                logger.debug("getProxyIdsByUserId: writing back.. po =" + newPo);
                userProxyPoMapper.updateByPrimaryKeySelective(newPo);
            }
        }
        return retIds;
    }

    public void initialize() throws Exception {
        //?????????user
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        criteria.andSignatureIsNull();

        List<UserPo> userPos = userMapper.selectByExample(example);

        for (UserPo po : userPos) {
            UserPo newPo = new UserPo();
            newPo.setPassword(AES.encrypt(po.getPassword(), User.AESPASS));
            newPo.setEmail(AES.encrypt(po.getEmail(), User.AESPASS));
            newPo.setMobile(AES.encrypt(po.getMobile(), User.AESPASS));
            newPo.setName(AES.encrypt(po.getName(), User.AESPASS));
            newPo.setId(po.getId());

            StringBuilder signature = Common.concatString("-", po.getUserName(), newPo.getPassword(),
                    newPo.getMobile(), newPo.getEmail(), po.getOpenId(), po.getState().toString(), po.getDepartId().toString(),
                    po.getCreatorId().toString());
            newPo.setSignature(SHA256.getSHA256(signature.toString()));

            userMapper.updateByPrimaryKeySelective(newPo);
        }

        //?????????UserProxy
        UserProxyPoExample example1 = new UserProxyPoExample();
        UserProxyPoExample.Criteria criteria1 = example1.createCriteria();
        criteria1.andSignatureIsNull();
        List<UserProxyPo> userProxyPos = userProxyPoMapper.selectByExample(example1);

        for (UserProxyPo po : userProxyPos) {
            UserProxyPo newPo = new UserProxyPo();
            newPo.setId(po.getId());
            StringBuilder signature = Common.concatString("-", po.getUserAId().toString(),
                    po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), po.getValid().toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            newPo.setSignature(newSignature);
            userProxyPoMapper.updateByPrimaryKeySelective(newPo);
        }

        //?????????UserRole
        UserRolePoExample example3 = new UserRolePoExample();
        UserRolePoExample.Criteria criteria3 = example3.createCriteria();
        criteria3.andSignatureIsNull();
        List<UserRolePo> userRolePoList = userRolePoMapper.selectByExample(example3);
        for (UserRolePo po : userRolePoList) {
            StringBuilder signature = Common.concatString("-",
                    po.getUserId().toString(), po.getRoleId().toString(), po.getCreatorId().toString());
            String newSignature = SHA256.getSHA256(signature.toString());

            UserRolePo newPo = new UserRolePo();
            newPo.setId(po.getId());
            newPo.setSignature(newSignature);
            userRolePoMapper.updateByPrimaryKeySelective(newPo);
        }

    }

    /**
     * ????????????
     *
     * @param id userID
     * @return User
     * createdBy 3218 2020/11/4 15:48
     * modifiedBy 3218 2020/11/4 15:48
     */

    public ReturnObject<User> getUserById(Long id) {
        UserPo userPo = userMapper.selectByPrimaryKey(id);
        if (userPo == null) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        User user = new User(userPo);
        if (!user.authetic()) {
            StringBuilder message = new StringBuilder().append("getUserById: ").append(ResponseCode.RESOURCE_FALSIFY.getMessage()).append(" id = ")
                    .append(user.getId()).append(" username =").append(user.getUserName());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.RESOURCE_FALSIFY);
        }
        return new ReturnObject<>(user);
    }


    /**
     * ??????????????????
     *
     * @param user
     * @return User
     * createdBy 3218 2020/11/4 15:55
     * modifiedBy 3218 2020/11/4 15:55
     */
    public ReturnObject updateUserAvatar(User user) {
        ReturnObject returnObject = new ReturnObject();
        UserPo newUserPo = new UserPo();
        newUserPo.setId(user.getId());
        newUserPo.setAvatar(user.getAvatar());
        int ret = userMapper.updateByPrimaryKeySelective(newUserPo);
        if (ret == 0) {
            logger.debug("updateUserAvatar: update fail. user id: " + user.getId());
            returnObject = new ReturnObject(ResponseCode.FIELD_NOTVALID);
        } else {
            logger.debug("updateUserAvatar: update user success : " + user.toString());
            returnObject = new ReturnObject();
        }
        return returnObject;
    }

    /**
     * ID??????????????????
     * @author XQChen
     * @param id
     * @return ??????
     */
    public UserPo findUserById(Long Id) {
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(Id);

        logger.debug("findUserById: Id =" + Id);
        UserPo userPo = userPoMapper.selectByPrimaryKey(Id);

        return userPo;
    }

    /**
     * ID??????????????????
     * @author XQChen
     * @param id
     * @param did
     * @return ??????
     */
    public UserPo findUserByIdAndDid(Long Id, Long did) {
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(Id);
        criteria.andDepartIdEqualTo(did);

        logger.debug("findUserByIdAndDid: Id =" + Id + " did = " + did);
        UserPo userPo = userPoMapper.selectByPrimaryKey(Id);

        return userPo;
    }

    /**
     * ????????????????????????
     * @author XQChen
     * @return List<UserPo> ????????????
     */
    public PageInfo<UserPo> findAllUsers(String userNameAES, String mobileAES, Long did) {
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        criteria.andDepartIdEqualTo(did);
        if(!userNameAES.isBlank())
            criteria.andUserNameEqualTo(userNameAES);
        if(!mobileAES.isBlank())
            criteria.andMobileEqualTo(mobileAES);

        List<UserPo> users = userPoMapper.selectByExample(example);

        logger.debug("findUserById: retUsers = "+users);

        return new PageInfo<>(users);
    }

    /* auth009 */

    /**
     * ?????? id ??????????????????
     *
     * @param userVo ????????? User ??????
     * @return ???????????? ReturnObj
     * @author 19720182203919 ??????
     * Created at 2020/11/4 20:30
     * Modified by 19720182203919 ?????? at 2020/11/5 10:42
     */
    public ReturnObject<Object> modifyUserByVo(Long id, UserVo userVo) {
        // ???????????????????????????????????????
        UserPo orig = userMapper.selectByPrimaryKey(id);
        // ????????????????????????????????????
        if (orig == null || (orig.getState() != null && User.State.getTypeByCode(orig.getState().intValue()) == User.State.DELETE)) {
            logger.info("?????????????????????????????????id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        // ?????? User ?????????????????????
        User user = new User(orig);
        UserPo po = user.createUpdatePo(userVo);

        // ???????????????????????? (???????????????) ???????????????????????? false
        if (userVo.getEmail() != null && !userVo.getEmail().equals(user.getEmail())) {
            po.setEmailVerified((byte) 0);
        }
        if (userVo.getMobile() != null && !userVo.getMobile().equals(user.getMobile())) {
            po.setMobileVerified((byte) 0);
        }

        // ???????????????
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = userMapper.updateByPrimaryKeySelective(po);
        } catch (DataAccessException e) {
            // ???????????? Exception???????????????????????????????????????
            if (Objects.requireNonNull(e.getMessage()).contains("auth_user.auth_user_mobile_uindex")) {
                logger.info("???????????????" + userVo.getMobile());
                retObj = new ReturnObject<>(ResponseCode.MOBILE_REGISTERED);
            } else if (e.getMessage().contains("auth_user.auth_user_email_uindex")) {
                logger.info("???????????????" + userVo.getEmail());
                retObj = new ReturnObject<>(ResponseCode.EMAIL_REGISTERED);
            } else {
                // ???????????????????????????
                logger.error("??????????????????" + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                        String.format("????????????????????????????????????%s", e.getMessage()));
            }
            return retObj;
        } catch (Exception e) {
            // ?????? Exception ??????????????????
            logger.error("???????????????" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("?????????????????????????????????%s", e.getMessage()));
        }
        // ????????????????????????
        if (ret == 0) {
            logger.info("?????????????????????????????????id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("?????? id = " + id + " ??????????????????");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * (??????) ????????????
     *
     * @param id ?????? id
     * @return ???????????? ReturnObj
     * @author 19720182203919 ??????
     * Created at 2020/11/4 20:30
     * Modified by 19720182203919 ?????? at 2020/11/5 10:42
     */
    public ReturnObject<Object> physicallyDeleteUser(Long id) {
        ReturnObject<Object> retObj;
        int ret = userMapper.deleteByPrimaryKey(id);
        if (ret == 0) {
            logger.info("?????????????????????????????????id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("?????? id = " + id + " ??????????????????");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * ???????????????????????????????????? Po
     *
     * @param id    ?????? id
     * @param state ??????????????????
     * @return UserPo ??????
     * @author 19720182203919 ??????
     * Created at 2020/11/4 20:30
     * Modified by 19720182203919 ?????? at 2020/11/5 10:42
     */
    private UserPo createUserStateModPo(Long id, User.State state) {
        // ???????????????????????????????????????
        UserPo orig = userMapper.selectByPrimaryKey(id);
        // ?????????????????????????????????????????????
        if (orig == null || (orig.getState() != null && User.State.getTypeByCode(orig.getState().intValue()) == User.State.DELETE)) {
            return null;
        }

        // ?????? User ?????????????????????
        User user = new User(orig);
        user.setState(state);
        // ?????????????????? null ??? vo ?????????????????????????????????
        UserVo vo = new UserVo();

        return user.createUpdatePo(vo);
    }

    /**
     * ??????????????????
     *
     * @param id    ?????? id
     * @param state ????????????
     * @return ???????????? ReturnObj
     * @author 19720182203919 ??????
     * Created at 2020/11/4 20:30
     * Modified by 19720182203919 ?????? at 2020/11/5 10:42
     */
    public ReturnObject<Object> changeUserState(Long id, User.State state) {
        UserPo po = createUserStateModPo(id, state);
        if (po == null) {
            logger.info("?????????????????????????????????id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = userMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                logger.info("?????????????????????????????????id = " + id);
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                logger.info("?????? id = " + id + " ?????????????????? " + state.getDescription());
                retObj = new ReturnObject<>();
            }
        } catch (DataAccessException e) {
            // ???????????????
            logger.error("??????????????????" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("????????????????????????????????????%s", e.getMessage()));
        } catch (Exception e) {
            // ???????????????
            logger.error("???????????????" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("?????????????????????????????????%s", e.getMessage()));
        }
        return retObj;
    }

    /* auth009 ends */

    /* auth002 begin*/

    /**
     * auth002: ??????????????????
     * @param vo ??????????????????
     * @param ip ??????ip??????
     * @author 24320182203311 ??????
     * Created at 2020/11/11 19:32
     */
    public ReturnObject<Object> resetPassword(ResetPwdVo vo, String ip) {

        //???????????????????????????
        if(redisTemplate.hasKey("ip_"+ip))
            return new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN);
        else {
            //1 min????????????????????????
            redisTemplate.opsForValue().set("ip_"+ip,ip);
            redisTemplate.expire("ip_" + ip, 60*1000, TimeUnit.MILLISECONDS);
        }

        //????????????????????????
        UserPoExample userPoExample1 = new UserPoExample();
        UserPoExample.Criteria criteria = userPoExample1.createCriteria();
        criteria.andMobileEqualTo(AES.encrypt(vo.getMobile(),User.AESPASS));
        List<UserPo> userPo1 = null;
        try {
            userPo1 = userMapper.selectByExample(userPoExample1);
        }catch (Exception e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(userPo1.isEmpty())
            return new ReturnObject<>(ResponseCode.MOBILE_WRONG);
        else if(!userPo1.get(0).getEmail().equals(AES.encrypt(vo.getEmail(), User.AESPASS)))
            return new ReturnObject<>(ResponseCode.EMAIL_WRONG);


        //?????????????????????
        String captcha = RandomCaptcha.getRandomString(6);
        while(redisTemplate.hasKey(captcha))
            captcha = RandomCaptcha.getRandomString(6);

        String id = userPo1.get(0).getId().toString();
        String key = "cp_" + captcha;
        //key:?????????,value:id??????redis
        redisTemplate.opsForValue().set(key,id);
        //??????????????????
        redisTemplate.expire("cp_" + captcha, 5*60*1000, TimeUnit.MILLISECONDS);


//        //????????????(??????????????????application.properties????????????)
//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setSubject("???oomall?????????????????????");
//        msg.setSentDate(new Date());
//        msg.setText("?????????????????????" + captcha + "???5??????????????????");
//        msg.setFrom("925882085@qq.com");
//        msg.setTo(vo.getEmail());
//        try {
//            mailSender.send(msg);
//        } catch (MailException e) {
//            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
//        }

        return new ReturnObject<>(ResponseCode.OK);
    }

    /**
     * auth002: ??????????????????
     * @param modifyPwdVo ??????????????????
     * @return Object
     * @author 24320182203311 ??????
     * Created at 2020/11/11 19:32
     */
    public ReturnObject<Object> modifyPassword(ModifyPwdVo modifyPwdVo) {


        //?????????????????????id
        if(!redisTemplate.hasKey("cp_"+modifyPwdVo.getCaptcha()))
            return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
        String id= redisTemplate.opsForValue().get("cp_"+modifyPwdVo.getCaptcha()).toString();

        UserPo userpo = null;
        try {
            userpo = userPoMapper.selectByPrimaryKey(Long.parseLong(id));
        }catch (Exception e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,e.getMessage());
        }

        //???????????????????????????
        if(AES.decrypt(userpo.getPassword(), User.AESPASS).equals(modifyPwdVo.getNewPassword()))
            return new ReturnObject<>(ResponseCode.PASSWORD_SAME);

        //??????
        UserPo userPo = new UserPo();
        userPo.setPassword(AES.encrypt(modifyPwdVo.getNewPassword(),User.AESPASS));

        //???????????????
        try {
            userMapper.updateByPrimaryKeySelective(userPo);
        }catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,e.getMessage());
        }
        return new ReturnObject<>(ResponseCode.OK);
    }

    /* auth002 end*/


    /**
     * ?????????????????????role?????????user
     *
     * @param id ??????id
     * createdBy ?????? 24320182203277
     */
    public void clearUserByRoleId(Long id){
        UserRolePoExample example = new UserRolePoExample();
        UserRolePoExample.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(id);

        List<UserRolePo> userrolePos = userRolePoMapper.selectByExample(example);
        Long uid;
        for(UserRolePo e:userrolePos){
            uid = e.getUserId();
            clearUserPrivCache(uid);
        }
    }
    /**
     * ??????user
     *
     * createdBy Li Zihan 243201822032227
     */
    public ReturnObject addUser(NewUserPo po)
    {
        ReturnObject returnObject = null;
        UserPo userPo = new UserPo();
        userPo.setEmail(AES.encrypt(po.getEmail(), User.AESPASS));
        userPo.setMobile(AES.encrypt(po.getMobile(), User.AESPASS));
        userPo.setUserName(po.getUserName());
        userPo.setAvatar(po.getAvatar());
        userPo.setDepartId(po.getDepartId());
        userPo.setOpenId(po.getOpenId());
        userPo.setGmtCreate(LocalDateTime.now());
        try{
            returnObject = new ReturnObject<>(userPoMapper.insert(userPo));
            logger.debug("success insert User: " + userPo.getId());
        }
        catch (DataAccessException e)
        {
            if (Objects.requireNonNull(e.getMessage()).contains("auth_user.user_name_uindex")) {
                //??????????????????????????????
                logger.debug("insertUser: have same user name = " + userPo.getName());
                returnObject = new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("??????????????????" + userPo.getName()));
            } else {
                logger.debug("sql exception : " + e.getMessage());
                returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("??????????????????%s", e.getMessage()));
            }
        }

        catch (Exception e) {
            // ??????Exception??????
            logger.error("other exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("????????????????????????????????????%s", e.getMessage()));
        }
        return returnObject;
    }

    /**
     * ????????????: ????????????depart
     * @Param: userId departId
     * @Return:
     * @Author: Yifei Wang
     * @Date: 2020/12/8 11:35
     */
    public ReturnObject changeUserDepart(Long userId, Long departId){
        UserPo po = new UserPo();
        po.setId(userId);
        po.setDepartId(departId);
        try{
            logger.debug("Update User: " + userId);
            int ret=userPoMapper.updateByPrimaryKeySelective(po);
            if(ret == 0){
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
            }
            logger.debug("Success Update User: " + userId);
            return new ReturnObject<>(ResponseCode.OK);
        }catch (Exception e){
            logger.error("exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
    }
}

