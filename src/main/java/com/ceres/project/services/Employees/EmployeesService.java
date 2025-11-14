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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class EmployeesService  extends BaseWebActionsService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentsRepository departmentRepository;

    OperationReturnObject res = new  OperationReturnObject();

    public OperationReturnObject addEmployee(JSONObject request) {
        try{
            String firstName = request.getString("firstName");
            String lastName = request.getString("lastName");
            String email = request.getString("email");
            String maritalStatus = request.getString("maritalStatus");
            String phoneNumber = request.getString("phoneNumber");
            String address = request.getString("address");
            String nationality = request.getString("nationality");
            String gender = request.getString("gender");
            LocalDate birthDate = LocalDate.parse(request.getString("birthDate"));
            String employmentType = request.getString("employmentType");
            Long departmentId = request.getLong("departmentId");
            String departmentName = request.getString("departmentName");
            String officeLocation = request.getString("officeLocation");
            LocalDate joiningDate = LocalDate.parse(request.getString("joiningDate"));
            String designation = request.getString("designation");

            if(firstName == null || firstName.isEmpty()){
                return createResponse("First name must be provided");
            }
            if(email == null || email.isEmpty()){
                return createResponse("Email must  be provided");
            }
            Optional<DepartmentModel> department =departmentRepository.findById(departmentId);
            if(department.isEmpty()){
                return createResponse("Department not found");
            }
            DepartmentModel actualDepartment = department.get();
            List <Long> employees =JSON.parseArray(actualDepartment.getEmployeeIds()).toJavaList(Long.class);

            EmployeeModel employeeModel = new EmployeeModel();
            employeeModel.setFirstName(firstName);
            employeeModel.setLastName(lastName);
            employeeModel.setEmail(email);
            employeeModel.setMaritalStatus(maritalStatus);
            employeeModel.setPhone(phoneNumber);
            employeeModel.setAddress(address);
            employeeModel.setNationality(nationality);
            employeeModel.setGender(gender);
            employeeModel.setBirthDate(birthDate);
            employeeModel.setEmploymentType(employmentType);
            employeeModel.setDepartment(departmentId);
            employeeModel.setDepartmentName(departmentName);
            employeeModel.setOfficeLocation(officeLocation);
            employeeModel.setJoiningDate(joiningDate);
            employeeModel.setDesignation(designation);

            EmployeeModel savedEmployee = employeeRepository.save(employeeModel);
            employees.add(savedEmployee.getId());
            actualDepartment.setEmployeeIds(JSON.toJSON(employees).toString());
            actualDepartment.setEmployeeCount(actualDepartment.getEmployeeCount()+1);
            departmentRepository.save(actualDepartment);

            res.setReturnCodeAndReturnMessage(0, "added employee successfully");
            res.setReturnObject(employeeModel);
            return res;

        }catch(Exception e){
            e.printStackTrace();
            e.getMessage();
            return createResponse(e.getMessage());

        }

    }

    public OperationReturnObject displayAllEmployees() {
        try{
            List<EmployeeModel> employees = employeeRepository.findAll();
            res.setReturnCodeAndReturnMessage(0, "displayed all employees successfully");
            res.setReturnObject(employees);
            return res;

        }catch(Exception e){
            return createResponse(e.getMessage());
        }
    }

    public OperationReturnObject displayEmployee(JSONObject request) {
        try{
            Long employeeId = request.getLong("id");
            EmployeeModel employee = employeeRepository.findById(employeeId).orElse(null);
            res.setReturnCodeAndReturnMessage(0, "displayed employee successfully");
            res.setReturnObject(employee);
            return res;
        }catch(Exception e){
            return createResponse(e.getMessage());
        }
    }

    public OperationReturnObject deleteEmployee(JSONObject request) {
        try{
            long employeeId = request.getLong("employeeId");

            EmployeeModel employee = employeeRepository.findById(employeeId).orElse(null);
            employeeRepository.delete(employee);

            res.setReturnCodeAndReturnMessage(0,"deleted employee successfully");
            return res;
        }catch(Exception e){
            e.printStackTrace();
            e.getMessage();
            return createResponse(e.getMessage());
        }
    }

    public OperationReturnObject updateEmployee(JSONObject request) {
        try{
            long employeeId = request.getLong("employeeId");
            EmployeeModel employee = employeeRepository.findById(employeeId).orElse(null);

            employee.setFirstName(request.getString("firstName"));
            employee.setLastName(request.getString("lastName"));
            employee.setEmail(request.getString("email"));
//            employee.setMaritalStatus(request.getString("maritalStatus"));
            employee.setPhone(request.getString("phone"));
            employee.setAddress(request.getString("address"));
            employee.setNationality(request.getString("nationality"));
//            employee.setGender(request.getString("gender"));
            employee.setBirthDate(LocalDate.parse(request.getString("birthDate")));
            employee.setEmploymentType(request.getString("employmentType"));
            employee.setDepartmentName(request.getString("departmentName"));
            employee.setOfficeLocation(request.getString("officeLocation"));
            employee.setJoiningDate(LocalDate.parse(request.getString("joiningDate")));
            employee.setDesignation(request.getString("designation"));

            employeeRepository.save(employee);

            res.setReturnCodeAndReturnMessage(0,"updated employee successfully");
            res.setReturnObject(employee);
            return res;

        }catch(Exception e){
            e.printStackTrace();

            return createResponse(e.getMessage());
        }

    }

    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) {
        return switch (action){
            case "addEmployee" -> addEmployee(request);
            case "displayAllEmployees" -> displayAllEmployees();
            case "displayEmployee" -> displayEmployee(request);
            case "deleteEmployee" -> deleteEmployee(request);
            case "updateEmployee" -> updateEmployee(request);
            default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
        };
    }
}
