package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Category;

import java.util.List;

public interface CategoryDao {
    /**
     * 找到分类菜单
     * @return
     */
    List<Category> findAll();
}
