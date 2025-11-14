package com.ceres.project.repositories;

import com.ceres.project.models.database.JobsModel;
import com.ceres.project.models.jpa_helpers.repository.JetRepository;

public interface JobRepository extends JetRepository<JobsModel, Long> {
}
