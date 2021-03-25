package com.practice.skill.management.dto;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Valid
public class EmployeeDTO {
    
    @NotBlank(message = "firstName is mandatory")
    private final String firstName;
    
    @NotBlank(message = "lastName is mandatory")
    private final String lastName;
    
    @NotBlank(message = "dob is mandatory")
    private final String dob;
    
    @NotNull(message = "Skills are mandatory")
    private final Map<String, String> skills;
    
    public EmployeeDTO(String firstName, String lastName, String dob, Map<String, String> skills) {
        
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.skills = skills;
    }
    
    public String getFirstName() {
        
        return firstName;
    }
    
    public String getLastName() {
        
        return lastName;
    }
    
    public String getDob() {
        
        return dob;
    }
    
    public Map<String, String> getSkills() {
        
        return skills;
    }
    
    @Override
    public String toString() {
        
        return "EmployeeDTO [firstName=" + firstName + ", lastName=" + lastName + ", dob=" + dob + ", skills=" + skills + "]";
    }
    
}
