package com.practice.skill.management.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.practice.skill.management.dto.EmployeeDTO;
import com.practice.skill.management.dto.EmployeeResponseDTO;
import com.practice.skill.management.dto.SkillCode;
import com.practice.skill.management.exception.EmployeeNotFoundException;
import com.practice.skill.management.exception.SkillNotFoundException;
import com.practice.skill.management.refdata.SkillHelper;
import com.practice.skill.management.repository.EmployeeRepository;
import com.practice.skill.management.vo.EmployeeVO;

@Service
public class EmployeeService {
    
    private final static Logger LOGGER = LogManager.getLogger(EmployeeService.class);
    
    private final EmployeeIdGenerator idGenerator;
    
    private final EmployeeRepository employeeRepository;
    
    private final SkillHelper skillHelper;
    
    private final ObjectMapper mapper;
    
    public EmployeeService(EmployeeIdGenerator idGenerator, EmployeeRepository employeeRepository, ObjectMapper mapper, SkillHelper skillHelper) {
        
        this.idGenerator = idGenerator;
        this.employeeRepository = employeeRepository;
        this.mapper = new ObjectMapper().registerModule(new Jdk8Module()).registerModule(new JavaTimeModule()).registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
                                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                        .setSerializationInclusion(Include.NON_NULL);
        ;
        this.skillHelper = skillHelper;
    }
    
    public EmployeeResponseDTO createEmployee(@Valid EmployeeDTO employeeDTO) throws JsonProcessingException {
        
        LOGGER.debug("Creating employee");
        if (allSkillsValid(employeeDTO.getSkills().keySet())) {
            String employeeId = idGenerator.generateEmployeeId();
            EmployeeVO employeeVO = new EmployeeVO();
            employeeVO.setId(employeeId);
            employeeVO.setFirstName(employeeDTO.getFirstName());
            employeeVO.setLastName(employeeDTO.getLastName());
            employeeVO.setDob(employeeDTO.getDob());
            employeeVO.setSkills(mapper.writeValueAsString(employeeDTO.getSkills()));
            employeeVO.setStatus("Active");
            employeeRepository.save(employeeVO);
            return new EmployeeResponseDTO(employeeId, "Success");
        } else {
            throw new SkillNotFoundException("Skill not found in skill repository. Please provide right skill set.");
        }
        
    }
    
    private boolean allSkillsValid(Set<String> skillNames) {
        
        return skillNames.stream().allMatch(skillHelper::isSkillPresent);
    }
    
    public EmployeeResponseDTO updateEmployee(@NotNull String id, @Valid EmployeeDTO employeeDTO) throws JsonMappingException, JsonProcessingException {
        
        if (!allSkillsValid(employeeDTO.getSkills().keySet())) {
            throw new SkillNotFoundException("Skill not found in skill repository. Please provide right skill set.");
        }
        
        Optional<EmployeeVO> employee = employeeRepository.getEmployee(id);
        if (employee.isPresent() && employee.get().getStatus().equalsIgnoreCase("Active")) {
            LOGGER.debug("Updating employee details for {}", id);
            EmployeeVO employeeVO = new EmployeeVO();
            employeeVO.setId(id);
            employeeVO.setFirstName(employeeDTO.getFirstName());
            employeeVO.setLastName(employeeDTO.getLastName());
            employeeVO.setSkills(mapper.writeValueAsString(employeeDTO.getSkills()));
            employeeVO.setStatus("Active");
            employeeRepository.save(employeeVO);
            
        } else {
            LOGGER.debug("Employee with id {} not found", id);
            throw new EmployeeNotFoundException("Employee with Id " + id + " not found");
        }
        
        return new EmployeeResponseDTO(id, "Update successful");
    }
    
    public EmployeeDTO getEmployeeDetails(String id) throws IOException {
        
        Optional<EmployeeVO> employeeVO = employeeRepository.getEmployee(id);
        
        if (employeeVO.isPresent() && employeeVO.get().getStatus().equalsIgnoreCase("Active")) {
            return new EmployeeDTO(employeeVO.get().getFirstName(), employeeVO.get().getLastName(), employeeVO.get().getDob(),
                                   (Map<String, String>) mapper.readValue(employeeVO.get().getSkills(), Map.class));
        } else {
            LOGGER.debug("Employee with id {} not found", id);
            throw new EmployeeNotFoundException("Employee with Id " + id + " not found");
        }
    }
    
    public void deleteEmployee(@NotNull String id) {
        
        Optional<EmployeeVO> employee = employeeRepository.getEmployee(id);
        if (employee.isPresent() && employee.get().getStatus().equalsIgnoreCase("Active")) {
            LOGGER.debug("Deleting employee with ID {}", id);
            EmployeeVO employeeVO = new EmployeeVO();
            employeeVO.setId(id);
            employeeVO.setStatus("Inactive");
            employeeRepository.save(employeeVO);
            
        } else {
            LOGGER.debug("Employee with id {} not found", id);
            throw new EmployeeNotFoundException("Employee with Id " + id + " not found");
        }
    }
    
    public List<EmployeeDTO> getEmployees(@NotNull String skill, @NotNull String level) {
        
        List<EmployeeDTO> employees = employeeRepository.getAllEmployees();
        
        if (!employees.isEmpty()) {
            LOGGER.debug("Found employees for given skills {} {}", skill, level);
            return employees.stream().filter(emp -> StringUtils.isNotBlank(emp.getSkills().get(skill)) && emp.getSkills().get(skill).equalsIgnoreCase(level)).collect(Collectors.toList());
        } else {
            LOGGER.debug("No employees were found for skill {} and level {}", skill, level);
            throw new EmployeeNotFoundException("No employee data found for given skills");
        }
    }

    public List<SkillCode> retrieveSkills() {
        
        LOGGER.debug("Retrieving skill list from repo");
        return skillHelper.getAllSkills();
    }
    
}
