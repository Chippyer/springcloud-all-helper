package com.chippy.common.filter;

import cn.hutool.json.JSONUtil;
import com.chippy.common.utils.ObjectsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

/**
 * 通用日志处理过滤器
 *
 * @author: chippy
 * @datetime 2020-11-08 15:53
 */
@Slf4j
public class RequestLoggingFilter extends AbstractRequestLoggingFilter implements RequestBodyAdvice {

    private static final String PROCESS_START_TIME_SUFFIX = ".PROCESS_START_TIME";

    private List<String> ignoreUrls = null;
    private boolean includeRequestBody;

    public boolean isIncludeRequestBody() {
        return includeRequestBody;
    }

    public void setIncludeRequestBody(boolean includeRequestBody) {
        this.includeRequestBody = includeRequestBody;
    }

    public void setIgnoreUrls(List<String> ignoreUrls) {
        if (this.ignoreUrls == null) {
            this.ignoreUrls = new LinkedList<>();
        }
        if (ignoreUrls != null) {
            this.ignoreUrls.addAll(ignoreUrls);
        }
    }

    public List<String> getIgnoreUrls() {
        return ignoreUrls;
    }

    public RequestLoggingFilter(boolean includeRequestBody, boolean includeQueryString, boolean includePayload,
                                boolean includeClient, boolean includeHeader) {
        super.setIncludeQueryString(includeQueryString);
        super.setIncludePayload(includePayload);
        super.setIncludeClientInfo(includeClient);
        super.setIncludeHeaders(includeHeader);
        this.includeRequestBody = includeRequestBody;
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        if (filterIgnoreUrl(request)) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug(getThreadId().concat(message).concat(calcRequestTimeIfNecessary(request)));
        }
    }

    private boolean filterIgnoreUrl(HttpServletRequest request) {
        List<String> ignoreUrls = this.getIgnoreUrls();
        if (ObjectsUtil.isNotEmpty(ignoreUrls)) {
            for (String ignoreUrl : ignoreUrls) {
                if (ignoreUrl.equals(request.getRequestURI())) {
                    if (log.isTraceEnabled()) {
                        log.trace("当前请求过滤请求监控: " + ignoreUrl);
                    }
                    request.setAttribute(ignoreUrl, true);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        String requestUri = request.getRequestURI();
        Object attribute = request.getAttribute(requestUri);
        if (ObjectsUtil.isNotEmpty(attribute) && Boolean.valueOf(String.valueOf(attribute)) == Boolean.TRUE) {
            request.removeAttribute(requestUri);
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug(getThreadId().concat(message).concat(calcRequestTimeIfNecessary(request)));
        }
    }

    private String calcRequestTimeIfNecessary(HttpServletRequest request) {
        long mills = 0;
        String requestTimeUniqueName = getRequestTimeUniqueName();
        Object processStartTime = request.getAttribute(requestTimeUniqueName);
        if (processStartTime == null) { //首次 放置值
            request.setAttribute(requestTimeUniqueName, Instant.now());
        } else { //请求结束的处理
            Instant start = (Instant) processStartTime;
            Instant now = Instant.now();
            mills = Duration.between(start, now).toMillis();
            request.removeAttribute(requestTimeUniqueName);
        }
        return mills == 0 ? "" : ("[耗时:" + mills + "ms] ");
    }

    private String getRequestTimeUniqueName() {
        return this.getClass().getName().concat(PROCESS_START_TIME_SUFFIX);
    }

    private String getThreadId() {
        return "[ThreadId:" + Thread.currentThread().getId() + "] ";
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        if (isIncludeRequestBody()) {
            return methodParameter.getParameterAnnotation(RequestBody.class) != null;
        }
        return false;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        Method method = parameter.getMethod();
        String requestBody = JSONUtil.toJsonStr(body);
        if (log.isDebugEnabled()) {
            log.debug("请求类: [" + parameter.getContainingClass().getSimpleName() + "], 请求方法名: [" + method.getName() + "]"
                    + "请求参数: [" + requestBody + "]");
        }
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                  Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

}
