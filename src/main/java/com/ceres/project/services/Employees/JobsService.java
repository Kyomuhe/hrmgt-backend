package com.ceres.project.services.Employees;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.project.models.database.JobsModel;
import com.ceres.project.repositories.JobRepository;
import com.ceres.project.services.base.BaseWebActionsService;
import com.ceres.project.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobsService extends BaseWebActionsService {

    private final JobRepository jobRepository;
    OperationReturnObject res = new OperationReturnObject();

    public OperationReturnObject createJob (JSONObject request){
//        can("ADMINISTRATOR","can ","administrator");
        
        try{
            JobsModel job = new JobsModel();

            job.setRole(request.getString("role"));
            job.setDepartment(request.getString("department"));
            job.setResponsibilities(request.getString("responsibilities"));
            job.setSalary(request.getString("salary"));
            job.setStatus("Active");
            job.setWorkLocation(request.getString("workLocation"));

            jobRepository.save(job);
            res.setCodeAndMessageAndReturnObject(0, "job created successfully", job);
            return res;

        }catch(Exception e){
            return createResponse(e.getMessage());
        }

    }

    public OperationReturnObject changeStatus(JSONObject request){
        try {

            Long jobId = request.getLong("jobId");

            JobsModel job = jobRepository.findById(jobId).orElse(null);

            job.setStatus(request.getString("status"));

            jobRepository.save(job);
            res.setCodeAndMessageAndReturnObject(0, "job status updated successfully", job);
            return res;

        }catch(Exception e){
            return createResponse(e.getMessage());
        }

    }

    public OperationReturnObject displayJobs(JSONObject request){
        try {
            List<JobsModel> jobs  =jobRepository.findAll();
            res.setCodeAndMessageAndReturnObject(0, "All jobs displayed successfully", jobs);
            return res;
        }catch(Exception e){
            return createResponse(e.getMessage());
        }
    }


    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) {
        return switch (action){

            case "createJob" -> createJob(request);
            case "changeStatus" -> changeStatus(request);
            case "displayJobs" -> displayJobs(request);
            default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
        };
    }

}
