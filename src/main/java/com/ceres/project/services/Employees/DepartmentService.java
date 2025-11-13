package com.ceres.project.services.Employees;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ceres.project.models.database.Departments.DepartmentModel;
import com.ceres.project.models.database.EmployeeModel;
import com.ceres.project.repositories.DepartmentsRepository;
import com.ceres.project.repositories.EmployeeRepository;
import com.ceres.project.services.base.BaseWebActionsService;
import com.ceres.project.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DepartmentService extends BaseWebActionsService {
OperationReturnObject res = new  OperationReturnObject();

private final DepartmentsRepository departmentsRepository;
private final EmployeeRepository employeeRepository;

    public OperationReturnObject createDepartment(JSONObject request) {
        try {
            List <Long> employees = new ArrayList();

            String name =request.getString("name");

            DepartmentModel departmentModel = DepartmentModel.builder()
                    .name(name)
                    .employeeCount(0)
                    .employeeIds(JSON.toJSON(employees).toString())
                    .build();
            DepartmentModel savedDepartment = departmentsRepository.save(departmentModel);
            res.setCodeAndMessageAndReturnObject(0, "department", savedDepartment );
            return res;

        }catch (Exception e){
            return createResponse(e.getMessage());
        }
    }

    public OperationReturnObject diplayDepartments() {
        try{
            List<DepartmentModel> departments = departmentsRepository.findAll();
            res.setCodeAndMessageAndReturnObject(0, "departments", departments);
            return res;
        }catch (Exception e){
            return createResponse(e.getMessage());
        }
    }


    public OperationReturnObject displayEmployeesByDepartment(JSONObject request){
        try{
            Long departmentId = request.getLong("departmentId");
            Optional<DepartmentModel> department = departmentsRepository.findById(departmentId);

            if (!department.isPresent()){
                res.setCodeAndMessageAndReturnObject(1, "Department not found", null);
                return res;
            }

            DepartmentModel departmentModel = department.get();
            String employeeIds = departmentModel.getEmployeeIds();
            String deptName = departmentModel.getName();

            if (employeeIds == null || employeeIds.trim().isEmpty()) {
                res.setCodeAndMessageAndReturnObject(0, "No employees in this department", new ArrayList<>());
                return res;
            }

            employeeIds = employeeIds.replaceAll("[\\[\\]\\s]", "");

            List<EmployeeModel> employees = new ArrayList<>();

            for (String employeeId : employeeIds.split(",")) {
                Optional<EmployeeModel> employee = employeeRepository.findById(Long.parseLong(employeeId.trim()));

                if (employee.isPresent()) {
                    employees.add(employee.get());
                }
            }
            Map<String, Object> response = new HashMap<>();
            response.put("employees", employees);
            response.put("departmentName", deptName);

            res.setCodeAndMessageAndReturnObject(0, "Employees retrieved successfully", response);
            return res;

        } catch(Exception e) {
            return createResponse("Error: " + e.getMessage());
        }
    }

    public OperationReturnObject displayDepartmentsWithEmployees() {
        try {
            List<DepartmentModel> departments = departmentsRepository.findAll();
            List<Map<String, Object>> departmentsWithEmployees = new ArrayList<>();

            for (DepartmentModel department : departments) {
                Map<String, Object> departmentData = new HashMap<>();
                departmentData.put("department", department);

                String employeeIds = department.getEmployeeIds();
                List<EmployeeModel> employees = new ArrayList<>();

                if (employeeIds != null && !employeeIds.trim().isEmpty()) {

                    employeeIds = employeeIds.replaceAll("[\\[\\]\\s]", "");

                    if (!employeeIds.trim().isEmpty()) {
                        for (String employeeId : employeeIds.split(",")) {
                                    Optional<EmployeeModel> employee = employeeRepository.findById(Long.parseLong(employeeId.trim()));
                                    if (employee.isPresent()) {
                                        employees.add(employee.get());

                            }
                        }
                    }
                }

                departmentData.put("employees", employees);
                departmentsWithEmployees.add(departmentData);
            }

            res.setCodeAndMessageAndReturnObject(0, "Departments with employees retrieved successfully", departmentsWithEmployees);
            return res;

        } catch (Exception e) {
            return createResponse("Error: " + e.getMessage());
        }
    }

    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) {
        return switch (action){
            case "addDepartment" -> createDepartment(request);
            case "diplayDepartment" -> diplayDepartments();
            case "displayEmployeeByDept" -> displayEmployeesByDepartment(request);
            case "displayDepartmentsWithEmployees" -> displayDepartmentsWithEmployees();
            default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
        };
    }
}
