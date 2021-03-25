package com.practice.skill.management.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Test {
    public static void main(String[] args) throws JsonProcessingException {
        
        Map<String, String> skills = new HashMap<String, String>();
        
        skills.put("Java", SkillLevel.EXPERT.name());
        skills.put(".NET", SkillLevel.AWARENESS.name());
        skills.put("AWS", SkillLevel.PRACTITIONER.name());
        
        EmployeeDTO p = new EmployeeDTO("firstName", "lastName", "dob", skills);
        
        ObjectMapper m = new ObjectMapper();
        
        System.out.println(m.writeValueAsString(p));
    }
}
