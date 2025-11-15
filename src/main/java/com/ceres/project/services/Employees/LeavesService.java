package com.ceres.project.services.Employees;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.project.models.database.Leaves;
import com.ceres.project.repositories.LeavesRepository;
import com.ceres.project.services.base.BaseWebActionsService;
import com.ceres.project.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeavesService extends BaseWebActionsService {
    private final LeavesRepository leavesRepository;
    OperationReturnObject res = new  OperationReturnObject();

    public OperationReturnObject leaveApplication(JSONObject request){
        try{
            Long employeeId = request.getLong("employeeId");
            if(employeeId == null){
                return createResponse("employeeId is required");
            }
            Leaves leaves = new Leaves();
            leaves.setLeaveReason(request.getString("leaveReason"));
            leaves.setLeaveType(request.getString("leaveType"));
            leaves.setStartDate(LocalDate.parse(request.getString("startDate")));
            leaves.setEndDate(LocalDate.parse(request.getString("endDate")));
            leaves.setPhoneNumber(request.getString("phoneNumber"));
            leaves.setEmergencyContact(request.getString("emergencyContact"));
            leaves.setName(request.getString("name"));
            leaves.setEmployeeId(employeeId);
            leaves.setDepartment(request.getString("department"));
            leaves.setStatus("pending");


            leavesRepository.save(leaves);
            res.setCodeAndMessageAndReturnObject(0, "leave created successfully", leaves);
            return res;


        }catch (Exception e){
            return createResponse(e.getMessage());
        }

    }

    public OperationReturnObject cancelLeaves(JSONObject request){
        try{
            Long leavesId = request.getLong("leavesId");
            if(leavesId == null){
                return createResponse("leavesId is null");
            }
            if(!leavesRepository.existsById(leavesId)){
                return createResponse("leavesId not found");
            }
            leavesRepository.deleteById(leavesId);
            res.setReturnCodeAndReturnMessage(0, "leaves cancelled successfully");
            return res;

        }catch (Exception e){
            return createResponse(e.getMessage());
        }
    }

    public OperationReturnObject displayEmployeeLeaves(JSONObject request){
        try{
            Long employeeId = request.getLong("id");
            if(employeeId == null){
                return createResponse("employeeId is null");
            }
            List<Leaves> leaves = leavesRepository.findByEmployeeId(employeeId);
            if(leaves == null){
                return createResponse("This employee has no leaves applied");
            }
            res.setCodeAndMessageAndReturnObject(0, "leaves found", leaves);
            return res;

        }catch (Exception e){
            return createResponse(e.getMessage());
        }
    }

    public OperationReturnObject displayAllLeaves(){
        try{
            List<Leaves> leaves = leavesRepository.findAll();
            res.setCodeAndMessageAndReturnObject(0,"leaves displayed", leaves);
            return res;

        }catch (Exception e){
            return createResponse("leavesId not found");
        }
    }

    public OperationReturnObject rejectLeave(JSONObject request){
        try{
            Long  leavesId = request.getLong("leavesId");
            String rejectionReason = request.getString("rejectionReason");
            if(leavesId == null){
                return createResponse("leavesId is null");
            }
            if(!leavesRepository.existsById(leavesId)){
                return createResponse("leavesId not found");
            }
            Leaves leaves = leavesRepository.findById(leavesId).get();
            leaves.setStatus("rejected");
            leaves.setRejectionReason(rejectionReason);
            leavesRepository.save(leaves);
            res.setCodeAndMessageAndReturnObject(0,"leaves updated successfully", leaves);
            return res;
        }catch (Exception e){
            return createResponse(e.getMessage());
        }
    }

    public OperationReturnObject approveLeave(JSONObject request){
        try{
            Long  leavesId = request.getLong("leavesId");
            if(leavesId == null){
                return createResponse("leavesId is null");
            }
            if(!leavesRepository.existsById(leavesId)){
                return createResponse("leavesId not found");
            }
            Leaves leaves = leavesRepository.findById(leavesId).get();
            leaves.setStatus("approved");
            leavesRepository.save(leaves);
            res.setCodeAndMessageAndReturnObject(0,"leaves updated successfully", leaves);
            return res;

        }catch (Exception e){
            return createResponse(e.getMessage());
        }
    }

    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) {
        return switch (action){

            case "leaveApplication" -> leaveApplication(request);
            case "cancelLeaves" -> cancelLeaves(request);
            case "displayEmployeeLeaves" -> displayEmployeeLeaves(request);
            case "displayAllLeaves" -> displayAllLeaves();
            case "reject" -> rejectLeave(request);
            case "approve" -> approveLeave(request);
            default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
        };
    }

}
