package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
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

@WebServlet("/loginUserServlet")
public class LoginUserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            UserService service = new UserServiceImpl();
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
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(),info);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }
}
