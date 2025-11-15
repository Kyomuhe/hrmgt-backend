package com.ceres.project.repositories;

import com.ceres.project.models.database.Leaves;
import com.ceres.project.models.jpa_helpers.repository.JetRepository;

import java.util.List;

public interface LeavesRepository extends JetRepository<Leaves,  Long> {
    List<Leaves> findByEmployeeId(Long id);
}
