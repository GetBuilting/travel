package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.util.MailUtils;
import cn.itcast.travel.util.UuidUtil;

import java.util.List;

public class UserServiceImpl implements UserService {
    private UserDao userDao = new UserDaoImpl();

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public boolean register(User user) {
        //1.根据用户名查找对象
        User u = userDao.findByUsername(user.getUsername());
        //判断用户名是否为空
        if (u != null){
            //用户名存在，注册失败
            return false;
        }
        //2.保存用户信息
        //2.1设置激活码，唯一字符
        user.setCode(UuidUtil.getUuid());
        //设置激活状态[开始注册初始化N]
        user.setStatus("N");
        userDao.save(user);
        //3.激活邮件发送，邮件正文
        String content = "<a href='http://localhost:9090/travel/user/activeUser?code="+user.getCode()+"'>点击激活【黑马旅游网】</a>";
        MailUtils.sendMail(user.getEmail(),content,"激活邮件");
        return true;
    }

    /**
     * 激活用户设置状态码
     * @param code
     * @return
     */
    @Override
    public boolean active(String code) {
        //1.根据激活码查询对象
        User user = userDao.findByCode(code);
        if (user != null){
            //2.调用dao修改激活状态的方法
            userDao.updateStates(user);
            return true;
        }else {
            return false;
        }
    }

    /**
     * 用户登录使用
     * @param user
     * @return
     */
    @Override
    public User login(User user) {
        return userDao.findByUsernameAndPassword(user.getUsername(),user.getPassword());

    }
}
