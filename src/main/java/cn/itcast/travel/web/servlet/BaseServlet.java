package cn.itcast.travel.web.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //System.out.println("BaseServlet的方法被执行了");
        //方法的分发操作
        //1.获取请求路径
        String uri = request.getRequestURI();
        System.out.println("请求的uri:"+uri);///travel/user
        //2.获取方法的名称
        String methodName = uri.substring(uri.lastIndexOf('/') + 1);//
        System.out.println("方法名:"+methodName);
        //3.获取方法对象[使用到了对象的代理]
        //谁调用的方法，代表谁
        System.out.println("方法对象:"+this);//cn.itcast.travel.web.servlet.UserServlet@4ec424bf
        try {
            //获取方法[注意一个事情，]
            Method method = this.getClass().getMethod(methodName,HttpServletRequest.class,HttpServletResponse.class);

            //如果使用的是一个受到保护的方法
//            Method method = this.getClass().getDeclaredMethod(methodName,HttpServletRequest.class,HttpServletResponse.class);
//            暴力强转
//            method.setAccessible(true);
            //执行方法
            method.invoke(this,request,response);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接将传入的对象序列化为json,并且写回客户端
     */
    public void writeValue(Object obj,HttpServletResponse response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(),obj);
    }
    /**
     * 将传入的对象序列化为json,放回String
     */
    public String writeValueAsString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}
