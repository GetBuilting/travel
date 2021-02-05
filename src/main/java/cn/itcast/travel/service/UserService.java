package cn.itcast.travel.service;

import cn.itcast.travel.domain.Category;
import cn.itcast.travel.domain.User;

import java.util.List;

public interface UserService {
    /**
     * 判断数据库中有无user对象
     * @param user
     * @return
     */
    boolean register(User user);

    boolean active(String code);

    User login(User user);
}