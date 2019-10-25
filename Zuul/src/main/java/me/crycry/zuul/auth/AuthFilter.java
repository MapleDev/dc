package me.crycry.zuul.auth;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import me.crycry.zuul.util.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class AuthFilter extends ZuulFilter {


    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext.getResponse();
        HttpServletRequest request = requestContext.getRequest();
        HttpSession session = request.getSession();

        System.out.println(request.getRequestURI());
        System.out.println("---  "+ Constants.passUri.get(request.getRequestURI()));

        if(Constants.passUri.get(request.getRequestURI())!=null) return null;

        if(session.getAttribute("user")==null){
            try {
                response.sendRedirect("/login.html");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        return null;
    }
}
