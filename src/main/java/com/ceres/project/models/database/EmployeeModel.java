package com.ceres.project.models.database;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Builder
@Table(name = "employees")
public class EmployeeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String maritalStatus;

    private String phone;

    private String address;

    private String nationality;

    private String officeLocation;

    private LocalDate joiningDate;

    private LocalDate birthDate;

    private String employmentType;
    private String gender;
    private Long department;

    private String designation;
    private String departmentName;

}
