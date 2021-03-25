package com.practice.skill.management.exception;


public class SkillNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    private String message;
    
    public SkillNotFoundException(String message) {
        
        super();
        this.message = message;
    }
    
    public String getMessage() {
        
        return message;
    }
    
    public void setMessage(String message) {
        
        this.message = message;
    }
    
}
