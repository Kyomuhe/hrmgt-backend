package com.ceres.project.services;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.project.services.Employees.DepartmentService;
import com.ceres.project.services.Employees.EmployeesService;
import com.ceres.project.services.Employees.JobsService;
import com.ceres.project.services.Employees.LeavesService;
import com.ceres.project.services.auth.AuthService;
import com.ceres.project.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebActionsService {

    private final AuthService authService;
    private final EmployeesService employeesService;
    private final DepartmentService departmentService;
    private final JobsService jobsService;
    private final LeavesService leavesService;
    public OperationReturnObject processAction(String service, String action, JSONObject payload) {
        return switch (service) {
            case "Auth" -> authService.process(action, payload);
            case "EmployeesService" -> employeesService.process(action, payload);
            case "departmentService" -> departmentService.process(action, payload);
            case "jobService" -> jobsService.process(action, payload);
            case "leavesService" -> leavesService.process(action, payload);
            default -> {
                OperationReturnObject res = new OperationReturnObject();
                res.setReturnCodeAndReturnMessage(404, "UNKNOWN SERVICE");
                yield res;
            }
        };
    }
}
