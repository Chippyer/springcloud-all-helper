package com.chippy.core.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页信息实体
 *
 * @author: chippy
 * @datetime 2020-12-25 10:23
 */
@Data
public class PageInfo<T> implements Serializable {

    /**
     * 当前页
     */
    private int pageNum;

    /**
     * 每页的数量
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 总记录数
     */
    private int total;

    /**
     * 分页数据
     */
    private List<T> list;

}
