package com.chippy.common.response;

/**
 * 服务间通讯返回结果结构接口
 *
 * @author: chippy
 * @datetime 2020/12/12 10:41
 */
public interface Result<T> {

    /**
     * 成功的Code编码值
     *
     * @return int
     * @author chippy
     */
    int definitionSuccessCode();

    /**
     * 通讯返回编码值
     *
     * @return int
     * @author chippy
     */
    int getCode();

    /**
     * 设置通讯返回结果内容
     *
     * @param data 通讯返回结果
     * @author chippy
     */
    void setData(T data);

    /**
     * 通讯返回结果内容
     *
     * @return <T>
     * @author chippy
     */
    T getData();

    /**
     * 通讯返回错误信息
     *
     * @return java.lang.String
     * @author chippy
     */
    String getErrorMsg();

}
