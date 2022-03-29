/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.kalsym.userservice.filters;

import org.springframework.stereotype.Component;
 
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
 

/**
 *
 * @author taufik
 */
@Component
public class AddResponseHeaderFilter implements Filter {
 
    @Override
    public void doFilter(ServletRequest request,
                        ServletResponse response,
                        FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
 
        //add any http header globally
        //res.addHeader("Access-Control-Allow-Origin", "*");
        
        chain.doFilter(req, res);
    }
}
