package com.practice.skill.management;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.rules.ExternalResource;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

public class LocalDbCreationRule extends ExternalResource {
    
    private final static Logger LOGGER = LogManager.getLogger(LocalDbCreationRule.class);
    
    protected DynamoDBProxyServer server;
    
    public LocalDbCreationRule() {
        
    }
    
    @Override
    protected void before() throws Exception {
        
        String port = "8000";
        this.server = ServerRunner.createServerFromCommandLineArgs(new String[] {
                                                                                  "-inMemory", "-port", port
        });
        server.start();
        setupTables();
    }
    
    @Override
    protected void after() {
        
        this.stopUnchecked(server);
    }
    
    protected void stopUnchecked(DynamoDBProxyServer dynamoDbServer) {
        
        try {
            dynamoDbServer.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void setupTables() throws URISyntaxException, IOException, InterruptedException {
        
        LOGGER.info("setupTables()");
        DynamoDbClient dynamoDB = DynamoDbClient.builder().endpointOverride(new URI("http://localhost:8000")).region(Region.AP_SOUTH_1).build();
        
        LOGGER.info("Dynamo client created");
        createEmployeeTable(dynamoDB);
        LOGGER.info("setupTables() DONE");
    }
    
    private void createEmployeeTable(DynamoDbClient dynamoDB) throws URISyntaxException, IOException, InterruptedException {
        
        LOGGER.info("createing employee table()");
        
        List<KeySchemaElement> keySchema = Arrays.asList(KeySchemaElement.builder().keyType(KeyType.HASH).attributeName("EmployeeID").build());
        
        List<AttributeDefinition> attributes = Arrays.asList(AttributeDefinition.builder().attributeType(ScalarAttributeType.S).attributeName("EmployeeID").build());
        
        String tableName = "EmployeeDetails";
        
        CreateTableRequest createTableRequest = CreateTableRequest.builder().tableName(tableName).keySchema(keySchema).attributeDefinitions(attributes)
                                                                  .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(new Long(10)).writeCapacityUnits(new Long(10)).build())
                                                                  .build();
        
        CreateTableResponse createTableResponse = dynamoDB.createTable(createTableRequest);
        
        LOGGER.info("Table Created: {}", createTableResponse.tableDescription().tableName());
        
    }
    
}
