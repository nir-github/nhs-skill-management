package com.practice.skill.management.dto;

import com.univocity.parsers.annotations.Parsed;

public class SkillCode {
    
    @Parsed(field = "Skill")
    private String name;
    
    @Parsed(field = "Type")
    private String category;
    
    public String getName() {
        
        return name;
    }
    
    public void setName(String name) {
        
        this.name = name;
    }
    
    public String getCategory() {
        
        return category;
    }
    
    public void setCategory(String category) {
        
        this.category = category;
    }
    
}
