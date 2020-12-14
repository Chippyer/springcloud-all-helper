package com.chippy.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * IP相关工具类
 *
 * @author: chippy
 */
@Slf4j
public class IpUtil {

    private static final String UN_KNOWN = "unknown";

    /**
     * 获取当前服务地址IP
     *
     * @return java.util.List<java.lang.String>
     * @author chippy
     */
    public static List<String> getCurrentServerIp() {
        try {
            List<String> ips = new ArrayList<>();
            ArrayList<NetworkInterface> list = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : list) {
                ArrayList<InetAddress> addressList = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress ip : addressList) {
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(':') == -1) {
                        ips.add(ip.getHostAddress());
                    }
                }
            }
            return ips;
        } catch (Exception e) {
            log.error("[getIp]获取本机IP异常:{}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public static String byHttpServletRequest(HttpServletRequest request) {
        String ip = null;

        try {
            ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }

            if (StringUtils.isEmpty(ip) || ip.length() == 0 || UN_KNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }

            if (StringUtils.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }

            if (StringUtils.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }

            if (StringUtils.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.error("IpUtil ERROR ", e);
        }

        return ip;
    }

}