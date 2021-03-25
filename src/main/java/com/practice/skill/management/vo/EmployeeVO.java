package com.practice.skill.management.vo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;

public class EmployeeVO {
    
    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "EmployeeID")
    private String id;
    
    @DynamoDBAttribute(attributeName = "FirstName")
    private String firstName;
    
    @DynamoDBAttribute(attributeName = "LastName")
    private String lastName;
    
    @DynamoDBAttribute(attributeName = "DOB")
    private String dob;
    
    @DynamoDBAttribute(attributeName = "Skills")
    private String skills;
    
    @DynamoDBAttribute(attributeName = "Status")
    private String status;
    
    public String getId() {
        
        return id;
    }
    
    public void setId(String id) {
        
        this.id = id;
    }
    
    public String getFirstName() {
        
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        
        this.firstName = firstName;
    }
    
    public String getLastName() {
        
        return lastName;
    }
    
    public void setLastName(String lastName) {
        
        this.lastName = lastName;
    }
    
    public String getDob() {
        
        return dob;
    }
    
    public void setDob(String dob) {
        
        this.dob = dob;
    }
    
    public String getSkills() {
        
        return skills;
    }
    
    public void setSkills(String skills) {
        
        this.skills = skills;
    }
    
    public String getStatus() {
        
        return status;
    }
    
    public void setStatus(String status) {
        
        this.status = status;
    }
    
    @Override
    public String toString() {
        
        return "EmployeeVO [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", dob=" + dob + ", skills=" + skills + ", status=" + status + "]";
    }
    
}
