package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.CategoryDao;
import cn.itcast.travel.dao.impl.CategoryDaoImpl;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.service.CategoryService;
import cn.itcast.travel.util.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryServiceImpl implements CategoryService {
    CategoryDao categoryDao = new CategoryDaoImpl();

    /**
     * 找到分类列表
     * @return
     */
    @Override
    public List<Category> findAll() {
        //1.为了优化数据的查询速率和减少数据库的负担，这里使用了redis数据库进行缓存查询
        //1.从redis中查询
        //1.1获取jedis客户端
        Jedis jedis = JedisUtil.getJedis();
        //1.2可使用sortedset排序查询
        //Set<String> categorys = jedis.zrange("category", 0, -1);
        //使用此方法查询出所有的数据cid+cname
        Set<Tuple> categorys = jedis.zrangeByScoreWithScores("category", 0, -1);
        List<Category> cs = null;
        //2.判断是否里面存的数据是空数据
        if (categorys == null || categorys.size() == 0){
            //3.如果是空，数据存入redis中
            //3.1如果为空，从数据库中查询
            cs = categoryDao.findAll();
            //3.2将集合数据存储到reids中的 category的key中
            for (int i = 0; i < cs.size(); i++) {
                //会自动排序
                jedis.zadd("category",cs.get(i).getCid(),cs.get(i).getCname());
            }
        }else {
            //4.如果不为空，将set的数据存入到list集合中
            cs = new ArrayList<Category>();
            for (Tuple tuple : categorys) {
                Category category = new Category();
                //获取节点信息,就是内容
                category.setCname(tuple.getElement());
                //获取cid,默认是字符串类型
                category.setCid((int)tuple.getScore());
                cs.add(category);
            }
            System.out.println(cs);
        }
        return cs;
    }
}
