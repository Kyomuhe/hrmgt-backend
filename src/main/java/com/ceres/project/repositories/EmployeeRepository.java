package com.ceres.project.repositories;

import com.ceres.project.models.database.EmployeeModel;
import com.ceres.project.models.jpa_helpers.repository.JetRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JetRepository<EmployeeModel, Long> {

}
