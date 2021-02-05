package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Category;
import cn.itcast.travel.domain.User;

import java.util.List;

public interface UserDao {
    /**
     * 根据用户名查找数据用户
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * 保存激活用户信息
     * @param user
     */
    void save(User user);
    /**
     * 找到激活码
     * @param code
     * @return
     */
    User findByCode(String code);
    /**
     * 修改用户的激活状态
     */
    void updateStates(User user);

    /**
     * 找登录的用户名和密码
     * @param username
     * @param password
     * @return
     */
    User findByUsernameAndPassword(String username, String password);
}
