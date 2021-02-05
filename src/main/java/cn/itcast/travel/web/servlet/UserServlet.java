package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    /**
     * 邮箱激活的
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private UserService service = new UserServiceImpl();
    public void activeUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        if (code != null){
            //调用service完成激活
            boolean flag = service.active(code);
            //判断标记
            String msg = null;
            if (flag){
                //激活成功
                msg = "激活成功，请<a href='login.html'>登录</a>";
            }else {
                //激活失败
                msg = "激活失败，请联系管理员";
            }
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
        }
    }

    /**
     * 退出登录
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void exit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.销毁session
        request.getSession().invalidate();
        //2.跳转重定向操作
        response.sendRedirect(request.getContextPath()+"/login.html");
    }

    /**
     * 登录成功后index页面名字显示
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void findUserOne(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
        Object user = request.getSession().getAttribute("user");
        //将user写回客户端
//        ObjectMapper mapper = new ObjectMapper();
//        response.setContentType("application/json;charset=utf-8");
//        mapper.writeValue(response.getOutputStream(),user);
        writeValue(user,response);
    }

    /**
     * 登录提示
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void login(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        //定义存错错误信息
        ResultInfo info = new ResultInfo();
        //1.获取验证码
        String checkcode_server = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
        request.getSession().removeAttribute("CHECKCODE_SERVER");
        String check = request.getParameter("check");
        if (!check.equalsIgnoreCase(checkcode_server)){
            info.setFlag(false);
            info.setErrorMsg("验证码有误");
        }else {
            //2.获取用户名和密码数据
            Map<String, String[]> map = request.getParameterMap();
            //2.1封装user对象
            User user = new User();
            try {
                BeanUtils.populate(user,map);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            //3.调用service来查询用户名和密码
            User u = service.login(user);
            //4.判断用户对象是否为null
            if (u==null){
                //用户名和密码错误
                info.setFlag(false);
                info.setErrorMsg("用户名或密码错误");
            }
            if (u!=null&&!"Y".equals(u.getStatus())){
                //用户还未激活
                info.setFlag(false);
                info.setErrorMsg("用户未激活,请先去激活");
            }
            if (u!=null&&"Y".equals(u.getStatus())){
                //用户成功登录
                info.setFlag(true);
                request.getSession().setAttribute("user",u);
            }
        }
        //5.进行数据的响应
//        ObjectMapper mapper = new ObjectMapper();
//        response.setContentType("application/json;charset=utf-8");
//        mapper.writeValue(response.getOutputStream(),info);
        writeValue(info,response);
    }

    /**
     * 注册校验
     * @param request
     * @param response
     * @throws IOException
     */
    public void register(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //0.验证码的判断
        //0.1获取验证码参数
        String check = request.getParameter("check");
        //0.2获取随机生成的验证码
        String checkCode = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
        request.getSession().removeAttribute("CHECKCODE_SERVER");
        if (!checkCode.equalsIgnoreCase(check)||checkCode==null){
            //验证码错误的情况
            ResultInfo info = new ResultInfo();
            //注册失败
            info.setFlag(false);
            info.setErrorMsg("验证码错误");
            //转化为json对象
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(info);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(json);
            return;
        }

        //1.获取数据map
        Map<String,String[]> map = request.getParameterMap();
        //2.创建user对象
        User user = new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.调用service完成注册的操作
        boolean flag = service.register(user);
        ResultInfo info = new ResultInfo();
        //4.响应结果
        if (flag){
            //注册成功
            info.setFlag(true);
        }else{
            //注册失败
            info.setFlag(false);
            info.setErrorMsg("注册失败,用户名存在了");
        }
        //5.序列化为JSON对象
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        //6.写回客户端
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);
    }
}
