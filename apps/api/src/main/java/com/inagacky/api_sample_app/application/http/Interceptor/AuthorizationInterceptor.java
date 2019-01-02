package com.inagacky.api_sample_app.application.http.Interceptor;

import com.inagacky.api_sample_app.configure.annotation.NonAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 *
 * 認可のインターセプタ
 *
 */
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    public enum ErrorCode {
        INVALID_TOKEN("invalid_token")
        ;
        private final String errorCode;

        ErrorCode(final String errorCode) {
            this.errorCode = errorCode;
        }

        public String getValue() {
            return this.errorCode;
        }

    }

    /**
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        return authorize(request, response, handler);
    }

    /**
     *
     * @param request
     * @param response
     * @param handler
     *
     * @return
     */
    private boolean authorize(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }

        if (preAuthorize(request, response, handler)) {
            return true;
        }

        // TODO: Please Authorize Actions

        return true;
    }

    private boolean preAuthorize(HttpServletRequest request, HttpServletResponse response, Object handler) {

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        NonAuth nonCustomerAuthAnnotation = AnnotationUtils.findAnnotation(method, NonAuth.class);
        if (nonCustomerAuthAnnotation != null) {
            log.info(String.format("Exclude NonAuth Check URI[%s]", request.getRequestURI()));

            return true;
        }

        return false;
    }

    /**
     *
     * @param response
     * @param responseErrorCode
     */
    private void setErrorResponse(HttpServletResponse response, ErrorCode responseErrorCode) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // TODO: Please Error Action
    }
}