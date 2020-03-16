package com.queue.publisher.filters;

import com.queue.publisher.utils.HeaderMapRequestWrapper;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String transactionId;
        HttpServletRequest request = (HttpServletRequest) req;
        HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);
        HttpServletResponse httpServletResponse = (HttpServletResponse) res;

        if (requestWrapper.getHeader("x-request-id") == null) {
            transactionId = UUID.randomUUID().toString();
        } else {
            transactionId = requestWrapper.getHeader("x-request-id");
        }
        requestWrapper.addHeader("x-request-id", transactionId);
        httpServletResponse.addHeader("x-request-id", transactionId);

        chain.doFilter(requestWrapper, httpServletResponse);
    }

    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig arg0) throws ServletException {}
}
