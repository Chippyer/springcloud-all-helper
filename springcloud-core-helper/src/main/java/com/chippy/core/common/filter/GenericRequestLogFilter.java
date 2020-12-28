package com.chippy.core.common.filter;

import com.chippy.core.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

/**
 * 通用日志请求过滤器
 *
 * @author: chippy
 * @datetime 2020-12-28 14:53
 */
public class GenericRequestLogFilter extends RequestLoggingFilter {

    @Value("${oak.logging.ignore-urls}")
    private String ignoreUrlStr;

    public GenericRequestLogFilter(boolean includeRequestBody, boolean includeQueryString) {
        super(includeRequestBody, includeQueryString, false, false, false);
        List<String> ignoreUrls = null;
        if (ObjectsUtil.isNotEmpty(ignoreUrlStr)) {
            String[] ignoreUrlArr = ignoreUrlStr.split(",");
            ignoreUrls = Arrays.asList(ignoreUrlArr);
        }
        if (ObjectsUtil.isNotEmpty(ignoreUrls)) {
            super.setIgnoreUrls(ignoreUrls);
        }
    }

}
