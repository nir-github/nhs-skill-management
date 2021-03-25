package com.practice.skill.management.dto;

public class EmployeeResponseDTO {
    
    private final String employeeID;
    
    private final String status;
    
    public EmployeeResponseDTO(String employeeID, String status) {
        
        this.employeeID = employeeID;
        this.status = status;
    }
    
    public String getEmployeeID() {
        
        return employeeID;
    }
    
    public String getStatus() {
        
        return status;
    }
    
    @Override
    public String toString() {
        
        return "EmployeeResponseDTO [employeeID=" + employeeID + ", status=" + status + "]";
    }
    
}
