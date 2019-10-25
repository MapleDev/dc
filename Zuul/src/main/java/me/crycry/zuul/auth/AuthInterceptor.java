package me.crycry.zuul.auth;

import me.crycry.zuul.util.Constants;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        System.out.println(request.getRequestURI());
        if(Constants.passUri.get(request.getRequestURI())!=null) return true;
        if(session.getAttribute("user")==null){
            response.sendRedirect("/login.html");
            return false;
        }
        return true;
    }
}
