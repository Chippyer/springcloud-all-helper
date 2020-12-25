package com.chippy.elasticjob.support.api;

import com.chippy.elasticjob.support.domain.JobInfo;
import org.apache.shardingsphere.elasticjob.infra.pojo.JobConfigurationPOJO;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.JobBriefInfo;

import java.util.List;

/**
 * 任务处理器
 * 定义任务的相关操作处理
 *
 * @author: chippy @datetime: 2020-11-11 18:08
 */
public interface TraceJobHandler {

    /**
     * 创建一个定时任务
     * <p>
     * 如果需要的话
     * 记录任务状态为{@link com.chippy.elasticjob.support.enums.JobStatusEnum}READY状态
     * 如果任务已存在则不进行创建，判断规则为 -> 任务{originalJobName}对应的任务状态为READY
     *
     * @author chippy
     */
    void createJob(JobInfo jobInfo);

    /**
     * 检查任务状态[准备中]的任务，内部调用{@link org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobOperateAPI}
     * 的disable()和remove();
     * <p>
     * 如果需要的话
     * ING、OVER、DISABLE不需要进行移除操作，因为他们已经不会再执行了
     * 并将任务状态设置为{JobStatusEnum.REMOVE}
     * 删除一个定时任务(包含disable动作)
     *
     * @author chippy
     */
    void removeJob(String originalJobName);

    /**
     * 更新一个定时任务信息
     * <p>
     * 如果需要的话
     * 如果任务不存在则不进行创建，判断规则为 -> 任务{originalJobName}对应的任务状态为READY
     * 存在则删除后重新创建
     * <p>
     * 一般来说调用更新操作之前就已经删除对应得任务了。
     *
     * @author chippy
     */
    void updateJob(JobInfo jobInfo);

    /**
     * 更新一个定时任务信息
     * <p>
     * 如果需要的话
     * 如果任务不存在则不进行创建，判断规则为 -> 任务{originalJobName}对应的任务状态为READY
     * 存在则删除后重新创建
     * <p>
     * 一般来说调用更新操作之前就已经删除对应得任务了。
     *
     * @author chippy
     */
    void updateJob(JobInfo jobInfo, boolean isCheckNull);

    /**
     * 获取原始任务名称对应的所有任务信息
     *
     * @author chippy
     */
    List<JobConfigurationPOJO> getJob(String originalJobName);

    /**
     * 获取原始任务名称对应的所有任务简明信息
     *
     * @author chippy
     */
    List<JobBriefInfo> getJobBriefInfo(String originalJobName);

}
