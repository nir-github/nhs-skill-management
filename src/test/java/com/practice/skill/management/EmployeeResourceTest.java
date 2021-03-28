package com.practice.skill.management;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.practice.skill.management.dto.EmployeeDTO;
import com.practice.skill.management.dto.EmployeeResponseDTO;
import com.practice.skill.management.dto.SkillLevel;
import com.practice.skill.management.repository.EmployeeRepository;
import com.practice.skill.management.vo.EmployeeVO;

import wiremock.com.google.common.io.Resources;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmployeeResourceTest {
    
    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(30000);
    
    @Rule
    public WireMockClassRule instanceRule = wireMockRule;
    
    @Autowired
    private MockMvc mvc;
    
    @Autowired
    private ObjectMapper mapper;
    
    @MockBean
    private EmployeeRepository repository;
    
    @After
    public void clearStubs() {
        
        wireMockRule.resetAll();
    }
    
    @Test
    public void createEmployeeTest() throws Exception {
        
        Mockito.doNothing().when(repository).save(Mockito.any())
        ;
        ResultActions s = mvc.perform(MockMvcRequestBuilders.post("/v1/employee/create").contentType(MediaType.APPLICATION_JSON)
                                                            .content("{\r\n" + "    \"firstName\": \"third\",\r\n" + "    \"lastName\": \"person\",\r\n" + "    \"dob\": \"03031973\",\r\n"
                                                                     + "    \"skills\": {\r\n" + "        \"Java\": \"PRACTITIONER\"\r\n" + "    }\r\n" + "}"))
                             .andExpect(MockMvcResultMatchers.status().isCreated());
        
        MvcResult s1 = s.andReturn();
        EmployeeResponseDTO resp = mapper.readValue(s1.getResponse().getContentAsString(), EmployeeResponseDTO.class);
        assertEquals(resp.getStatus(), "Success");
    }
    
    @Test
    public void skillNotPresentTest() throws Exception {
        
        mvc.perform(MockMvcRequestBuilders.post("/v1/employee/create").contentType(MediaType.APPLICATION_JSON).content(readResource("employee.json")))
           .andExpect(MockMvcResultMatchers.status().isNotFound())
           .andExpect(jsonPath("$.message").value("Skill not found in skill repository. Please provide right skill set."));
    }
    
    @Test
    public void retrieveActiveEmployeeTest() throws Exception {
        
        Mockito.doReturn(createEmployee("Active")).when(repository).getEmployee(Mockito.any());
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/v1/employee/retrieve/1234"))
           .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        EmployeeDTO responseDTO = mapper.readValue(result.andReturn().getResponse().getContentAsString(), EmployeeDTO.class);
        assertEquals(responseDTO.getFirstName(), "firstName");
    }
    
    @Test
    public void retrieveInactiveEmployeeTest() throws Exception {
        
        Mockito.doReturn(createEmployee("Inactive")).when(repository).getEmployee(Mockito.any());
        mvc.perform(MockMvcRequestBuilders.get("/v1/employee/retrieve/1234"))
           .andExpect(MockMvcResultMatchers.status().isNotFound())
           .andExpect(jsonPath("$.message").value("Employee with Id 1234 not found"));

        assertThatExceptionOfType(ResponseStatusException.class);
    }
    
    @Test
    public void deleteEmployeeTest() throws Exception {
        
        Mockito.doReturn(createEmployee("Active")).when(repository).getEmployee(Mockito.any());
        Mockito.doNothing().when(repository).save(Mockito.any())
        ;
        mvc.perform(MockMvcRequestBuilders.delete("/v1/employee/remove/1234"))
           .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    
    @Test
    public void getEmployeeBySkillsTest() throws Exception {
        
        Mockito.doReturn(getEmployeeList()).when(repository).getAllEmployees();
        mvc.perform(MockMvcRequestBuilders.get("/v1/employee/retrieve/Java/EXPERT"))
           .andExpect(MockMvcResultMatchers.status().isOk());
           //.andExpect(jsonPath("$.Java").value("EXPERT"));

    }
    
    private String readResource(final String fileName) throws IOException {
        
        return Resources.toString(Resources.getResource(fileName), Charset.defaultCharset());
    }
    
    private List<EmployeeDTO> getEmployeeList() {
        
        Map<String, String> skills = new HashMap<String, String>();
        skills.put("AWS", SkillLevel.PRACTITIONER.name());
        List<EmployeeDTO> empList = new ArrayList<EmployeeDTO>();
        empList.add(new EmployeeDTO("emp1", "last1", "11121980", skills));
        skills = new HashMap<String, String>();
        skills.put("Java", SkillLevel.EXPERT.name());
        empList.add(new EmployeeDTO("emp2", "last2", "01121981", skills));
        return empList;
    }
    
    private Optional<EmployeeVO> createEmployee(String status) {
        EmployeeVO vo = new EmployeeVO();
        vo.setFirstName("firstName");
        vo.setLastName("lastName");
        vo.setDob("12121980");
        vo.setId("1234");
        vo.setSkills("{\"Java\":\"EXPERT\",\".NET\":\"AWARENESS\",\"AWS\":\"PRACTITIONER\"}");
        vo.setStatus(status);
        return Optional.of(vo);
    }
}
