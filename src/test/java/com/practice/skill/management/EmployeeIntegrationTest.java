package com.practice.skill.management;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

import wiremock.com.google.common.io.Resources;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(
                webEnvironment = WebEnvironment.RANDOM_PORT, 
                properties = { 
                        "spring.profiles.active=test" 
                        }
                )
@AutoConfigureMockMvc
public class EmployeeIntegrationTest {
    
    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(30000);
    
    @Rule
    public WireMockClassRule instanceRule = wireMockRule;
    
    @ClassRule
    public static LocalDbCreationRule dynamoDB = new LocalDbCreationRule();
    
    @Autowired
    private MockMvc mvc;
    
    @Before
    public void before() throws Exception {

    }
    
    @After
    public void clearStubs() {
        
        wireMockRule.resetAll();
    }

    
    @Test
    public void testBadRequest() throws Exception {

        
        mvc.perform(MockMvcRequestBuilders.post("/v1/employee/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(readResource("employee.json")))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        
    }
    
    
    private String readResource(final String fileName) throws IOException {

        return Resources.toString(Resources.getResource(fileName), Charset.defaultCharset());

    }
}
