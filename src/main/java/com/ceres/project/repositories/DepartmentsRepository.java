package com.ceres.project.repositories;

import com.ceres.project.models.database.Departments.DepartmentModel;
import com.ceres.project.models.jpa_helpers.repository.JetRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentsRepository extends JetRepository<DepartmentModel, Long> {
}
