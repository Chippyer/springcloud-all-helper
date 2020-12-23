package com.chippy.elasticjob.support.api.db;

import com.chippy.common.utils.ObjectsUtil;
import com.chippy.elasticjob.support.domain.enums.JobStatusEnum;
import com.chippy.elasticjob.support.domain.mapper.JobInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.List;

/**
 * 任务信息服务接口MYSQL实现
 *
 * @author: chippy
 * @datetime 2020-12-09 13:12
 */
@Slf4j
public class MysqlJobInfoService implements IJobInfoService {

    private JobInfoMapper jobInfoMapper;

    public MysqlJobInfoService(JobInfoMapper jobInfoMapper) {
        this.jobInfoMapper = jobInfoMapper;
    }

    @Override
    public List<JobInfo> byOriginalJobName(String originalJobName) {
        if (ObjectsUtil.isEmpty(originalJobName)) {
            return null;
        }
        Example example = new Example(JobInfo.class);
        example.createCriteria().andEqualTo("originalJobName", originalJobName).andEqualTo("deleted", Boolean.FALSE);
        return jobInfoMapper.selectByExample(example);
    }

    @Override
    public JobInfo byOriginalJobNameIgnoreOverStatus(String originalJobName, JobStatusEnum jobStatusEnum) {
        if (ObjectsUtil.isEmpty(originalJobName) || ObjectsUtil.isEmpty(jobStatusEnum) || jobStatusEnum == JobStatusEnum.OVER) {
            return null;
        }
        Example example = new Example(JobInfo.class);
        example.createCriteria().andEqualTo("originalJobName", originalJobName).andEqualTo("status", jobStatusEnum.toString()).andEqualTo("deleted", Boolean.FALSE);
        return jobInfoMapper.selectOneByExample(example);
    }

    @Override
    public List<JobInfo> byOriginalJobName(String originalJobName, List<String> status) {
        if (ObjectsUtil.isEmpty(originalJobName)) {
            return Collections.emptyList();
        }
        Example example = new Example(JobInfo.class);
        final Example.Criteria criteria = example.createCriteria().andEqualTo("originalJobName", originalJobName)
            .andEqualTo("deleted", Boolean.FALSE);
        if (ObjectsUtil.isNotEmpty(status)) {
            criteria.andIn("status", status);
        }
        return jobInfoMapper.selectByExample(example);
    }

    @Override
    public JobInfo byJobName(String jobName) {
        if (ObjectsUtil.isEmpty(jobName)) {
            return null;
        }
        Example example = new Example(JobInfo.class);
        example.createCriteria().andEqualTo("jobName", jobName).andEqualTo("deleted", Boolean.FALSE);
        return jobInfoMapper.selectOneByExample(example);
    }

    @Override
    public JobInfo byJobName(String jobName, JobStatusEnum jobStatusEnum) {
        if (ObjectsUtil.isEmpty(jobName)) {
            return null;
        }
        final JobInfo jobInfo = this.byJobName(jobName);
        if (ObjectsUtil.isEmpty(jobStatusEnum)) {
            return jobInfo;
        }
        if (jobInfo.getStatus().equals(jobStatusEnum.toString())) {
            return jobInfo;
        }
        return null;
    }

    @Override
    public List<JobInfo> byStatus(JobStatusEnum jobStatusEnum) {
        if (ObjectsUtil.isEmpty(jobStatusEnum)) {
            return Collections.emptyList();
        }
        return this.byStatus(jobStatusEnum.toString());
    }

    @Override
    public List<JobInfo> byStatus(String status) {
        if (ObjectsUtil.isEmpty(status)) {
            return Collections.emptyList();
        }
        Example example = new Example(JobInfo.class);
        example.createCriteria().andEqualTo("status", status).andEqualTo("deleted", Boolean.FALSE);
        return jobInfoMapper.selectByExample(example);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByPrimaryKeySelective(JobInfo jobInfo) {
        if (ObjectsUtil.isEmpty(jobInfo)) {
            log.error("需要更新的任务信息不能为空");
            return false;
        }
        return jobInfoMapper.updateByPrimaryKeySelective(jobInfo) > 0;
    }

    @Override
    public void insertSelective(JobInfo jobInfo) {
        if (ObjectsUtil.isEmpty(jobInfo)) {
            log.error("查询任务信息不能为空");
            return;
        }
        jobInfoMapper.insertSelective(jobInfo);
    }
}
