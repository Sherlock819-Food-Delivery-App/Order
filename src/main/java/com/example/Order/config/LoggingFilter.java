package com.example.Order.config;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Proceed with the request
        chain.doFilter(request, response);

        // Log the response headers
        logger.debug("Response Headers:");
        httpResponse.getHeaderNames().forEach(header ->
                logger.debug("{}: {}", header, httpResponse.getHeader(header))
        );
    }
}