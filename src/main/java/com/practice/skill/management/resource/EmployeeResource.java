package com.practice.skill.management.resource;



import java.io.IOException;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.practice.skill.management.dto.EmployeeDTO;
import com.practice.skill.management.dto.EmployeeResponseDTO;
import com.practice.skill.management.service.EmployeeService;

@RestController
@RequestMapping(
        path = "/v1/employee",
        produces = {
                     MediaType.APPLICATION_JSON_VALUE
        })
public class EmployeeResource {
    
    private final EmployeeService employeeService;
    
    public EmployeeResource(EmployeeService employeeService) {
        
        this.employeeService = employeeService;
    }
    
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDTO create(@Valid @RequestBody EmployeeDTO employeeDTO) throws JsonProcessingException {
        
        return employeeService.createEmployee(employeeDTO);
    }
    
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponseDTO update(@PathVariable @NotNull String id, @Valid @RequestBody EmployeeDTO employeeDTO) throws JsonMappingException, JsonProcessingException {
        
        return employeeService.updateEmployee(id, employeeDTO);
        
    }
    
    @RequestMapping(value = "/retrieve/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDTO retrieve(@PathVariable @NotNull String id) throws IOException {
        
        return employeeService.getEmployeeDetails(id);
        
    }
    
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponseDTO remove(@PathVariable @NotNull String id) throws JsonMappingException, JsonProcessingException {
        
        return employeeService.deleteEmployee(id);
        
    }
    
    @RequestMapping(value = "/retrieve/{skill}/{level}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeDTO> getEmployeeForSkills(@PathVariable @NotNull String skill, @PathVariable @NotNull String level) throws JsonMappingException, JsonProcessingException {
        
        return employeeService.getEmployees(skill, level);
        
    }
}
