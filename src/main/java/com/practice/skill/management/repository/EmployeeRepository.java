package com.practice.skill.management.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.practice.skill.management.Constants;
import com.practice.skill.management.dto.EmployeeDTO;
import com.practice.skill.management.vo.EmployeeVO;

@Component
public class EmployeeRepository {
    
    private final static Logger LOGGER = LogManager.getLogger(EmployeeRepository.class);
    
    private final AmazonDynamoDB client;
    
    private final DynamoDB dynamoDB;
    
    private final DynamoDBMapper mapper;
    
    private static final String EMPLOYEE_TABLE_NAME = "EmployeeDetails";
    
    public EmployeeRepository() {
        
        client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).build();
        dynamoDB = new DynamoDB(client);
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder().withSaveBehavior(SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES).withTableNameOverride(new TableNameOverride(EMPLOYEE_TABLE_NAME))
                                                                .build();
        mapper = new DynamoDBMapper(client, mapperConfig);
    }
    
    @PostConstruct
    public void init() {
        
        if (!tableExists()) createEmployeeTable();
    }
    
    private boolean tableExists() {
        
        try {
            TableDescription tableDescription = dynamoDB.getTable(EMPLOYEE_TABLE_NAME).describe();
            LOGGER.debug("Table description: {}", tableDescription.getTableStatus());
            
            return true;
        } catch (com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException rnfe) {
            LOGGER.debug("Table does not exist");
        }
        return false;
        
    }
    
    private void createEmployeeTable() {
        
        try {
            
            List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions.add(new AttributeDefinition().withAttributeName("EmployeeID").withAttributeType("S"));
            
            List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            keySchema.add(new KeySchemaElement().withAttributeName("EmployeeID").withKeyType(KeyType.HASH)); // Partition
            // keySchema.add(new KeySchemaElement().withAttributeName("Skill").withKeyType(KeyType.RANGE)); // Partition
            // key
            
            CreateTableRequest request = new CreateTableRequest().withTableName(EMPLOYEE_TABLE_NAME).withKeySchema(keySchema).withAttributeDefinitions(attributeDefinitions)
                                                                 .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(6L));
            
            LOGGER.debug("Issuing CreateTable request for {}", EMPLOYEE_TABLE_NAME);
            Table table = dynamoDB.createTable(request);
            
            LOGGER.debug("Waiting for {} to be created...this may take a while...",EMPLOYEE_TABLE_NAME);
            table.waitForActive();
            
        } catch (Exception e) {
            LOGGER.error("CreateTable request failed for {} with error {}", EMPLOYEE_TABLE_NAME, e);
        }
        
    }
    
    public void save(@Valid EmployeeVO employeeVO) {
        
        mapper.save(employeeVO);
        LOGGER.debug("Saved employee with id {} to dynamoDB", employeeVO.getId());
        
    }
    
    public Optional<EmployeeVO> getEmployee(String id) {
        
        LOGGER.debug("Retrieving employee details from employee table");
        Map<String, AttributeValue> queryValues = new HashMap<String, AttributeValue>();
        queryValues.put(":EmployeeID", new AttributeValue().withS(id));
        
        DynamoDBQueryExpression<EmployeeVO> queryExpression = new DynamoDBQueryExpression<EmployeeVO>().withKeyConditionExpression("EmployeeID = :EmployeeID")
                                                                                                       .withExpressionAttributeValues(queryValues).withConsistentRead(Boolean.TRUE);
        
        List<EmployeeVO> employeeDetails = mapper.query(EmployeeVO.class, queryExpression);
        
        if (!employeeDetails.isEmpty()) return Optional.of(employeeDetails.get(0));
        else return Optional.empty();
        
    }
    
    public List<EmployeeDTO> getAllEmployees() {
        
        LOGGER.debug("Scanning employee table");
        HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
        ScanRequest scanRequest = new ScanRequest(EMPLOYEE_TABLE_NAME);// .withScanFilter(scanFilter);
        ScanResult scanResult = client.scan(scanRequest);
        return mapItemsToEmployee(scanResult.getItems());
    }
    
    private List<EmployeeDTO> mapItemsToEmployee(List<Map<String, AttributeValue>> items) {
        
        LOGGER.debug("Mapping employee to DTO");
        ObjectMapper mapper = new ObjectMapper();
        
        List<EmployeeDTO> employees = new ArrayList<EmployeeDTO>();
        
        items.stream().forEach(item -> {
            
            String id = null, firstName = null, lastName = null, dob = null;
            Map skills = null;
            
            AttributeValue attrValue = item.get(Constants.EMPLOYEE_ID);
            if (attrValue != null) {
                id = attrValue.getS();
            }
            attrValue = item.get(Constants.FIRSTNAME);
            if (attrValue != null) {
                firstName = attrValue.getS();
            }
            attrValue = item.get(Constants.LASTNAME);
            if (attrValue != null) {
                lastName = attrValue.getS();
            }
            attrValue = item.get(Constants.DOB);
            if (attrValue != null) {
                dob = attrValue.getS();
            }
            attrValue = item.get(Constants.SKILLS);
            if (attrValue != null) {
                Gson gson = new Gson();
                skills = gson.fromJson(attrValue.getS(), Map.class);
            }
            
            EmployeeDTO dto = new EmployeeDTO(firstName, lastName, dob, skills);
            employees.add(dto);
        });
        
        return employees;
    }
    
}
