package org.cboard.modules.services.role;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.cboard.modules.dao.JobDao;
import org.cboard.modules.services.AuthenticationService;
import org.cboard.modules.services.ServiceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by yfyuan on 2017/3/15.
 */
@Repository
@Aspect
public class JobRoleService {
    @Autowired
    private JobDao jobDao;

    @Autowired
    private AuthenticationService authenticationService;

    @Around("execution(* org.cboard.modules.services.job.JobService.update(..))")
    public Object update(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String json = (String) proceedingJoinPoint.getArgs()[1];
        JSONObject jsonObject = JSONObject.parseObject(json);
        String userid = authenticationService.getCurrentUser().getUserId();
        if (jobDao.checkJobRole(userid, jsonObject.getLong("id"), RolePermission.PATTERN_EDIT) > 0) {
            Object value = proceedingJoinPoint.proceed();
            return value;
        } else {
            return new ServiceStatus(ServiceStatus.Status.Fail, "No Permission");
        }
    }

    @Around("execution(* org.cboard.modules.services.job.JobService.delete(..))")
    public Object delete(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Long id = (Long) proceedingJoinPoint.getArgs()[1];
        String userid = authenticationService.getCurrentUser().getUserId();
        if (jobDao.checkJobRole(userid, id, RolePermission.PATTERN_DELETE) > 0) {
            Object value = proceedingJoinPoint.proceed();
            return value;
        } else {
            return new ServiceStatus(ServiceStatus.Status.Fail, "No Permission");
        }
    }
}
