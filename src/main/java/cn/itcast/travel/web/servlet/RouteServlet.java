package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.FavoriteService;
import cn.itcast.travel.service.RouteService;
import cn.itcast.travel.service.impl.FavoriteServiceImpl;
import cn.itcast.travel.service.impl.RouteServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 分页查询
 */
@WebServlet("/route/*")
public class RouteServlet extends BaseServlet {
    private RouteService routeService = new RouteServiceImpl();
    private FavoriteService favoriteService = new FavoriteServiceImpl();
    /**
     * 页面的分页查询
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void pageQuery(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        //1.接收传输的参数参数
        String currentPageStr = request.getParameter("currentPage");
        String cidStr = request.getParameter("cid");
        String pageSizeStr = request.getParameter("pageSize");
        //接收rname
        String rname = request.getParameter("rname");
        //解决乱码问题
        if (rname != null){
            rname = new String(rname.getBytes("iso-8859-1"),"utf-8");
        }
        //2.处理参数
        int cid = 0;
        if (cidStr != null && cidStr.length() > 0 && !"null".equals(cidStr)){
            //定义类别的id
            cid = Integer.parseInt(cidStr);
        }
        int currentPage = 0;
        if (currentPageStr != null && currentPageStr.length() > 0){
            //定义当前的页码,不传递默认是第一页
             currentPage = Integer.parseInt(currentPageStr);
        }else {
            currentPage = 1;
        }
        int pageSize = 0;
        if (pageSizeStr != null && pageSizeStr.length() > 0){
            //定义每一页的查询条数,不传递默认是显示5条数据
            pageSize = Integer.parseInt(pageSizeStr);
        }else {
            pageSize = 5;
        }
        //3.调用service查询PageBean对象
        PageBean<Route> pb = routeService.pageQuery(cid, currentPage, pageSize,rname);

        //4.将PageBean对象序列化为json对象,返回
        writeValue(pb,response);
    }

    /**
     * 根据id查询一个旅游线路的详细信息
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //1.接收rid
        String rid = request.getParameter("rid");
        //2.调用service查询route对象
        Route route = routeService.findOne(rid);
        //3.转化为json对象写回客户端
        writeValue(route,response);
    }

    /**
     * 判断当前登录用户是否收藏过当前的线路
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void isFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //1.接收rid和uid
        String rid = request.getParameter("rid");
        User user = (User) request.getSession().getAttribute("user");
        int uid;
        if (user == null){
            //用户未登录
            uid = 0;
        }else {
            //用户登录了
            uid = user.getUid();
        }
        //2.调用FavoriteService查询是否收藏
        boolean flag = favoriteService.isFavorite(rid, uid);
        //3.写回客户端
        writeValue(flag,response);
    }

    /**
     * 添加收藏
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void addFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //1.获取rid
        String rid = request.getParameter("rid");
        //2.获取用户对象
        User user = (User) request.getSession().getAttribute("user");
        int uid;//用户ID
        if (user == null){
            //用户未登录
            return;
        }else {
            //用户登录成功
            uid = user.getUid();
        }
        //3.调用service添加收藏数据
        favoriteService.add(rid,uid);

    }
}
