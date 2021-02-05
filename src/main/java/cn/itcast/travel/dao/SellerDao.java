package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Seller;

public interface SellerDao {
    /**
     * 根据商家的id查询他的对象
     * @param id
     * @return
     */
    public Seller findById(int sid);
}
