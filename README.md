# nhs-skill-management
REST API for employee skill management

This project contains Rest API to create, update, delete and retrieve employee and their skills from backedn.

## High Level Design
[![skill-management-hld.png](https://i.postimg.cc/tT1499yS/skill-management-hld.png)](https://postimg.cc/62XX0J8n)

## Build and Run

After checkout to build the project use
```bash
gradle clean build
```
To run the project you should have AWS account with /users/.aws credential setup. Please refer [AWS Docs](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html "AWS Docs") to learn how to setup AWS on your machine


## Database

We are using AWS dynamoDB to store the employee details in below format

| EmployeeID | FirstName | LastName | DOBName | Skills | Status |
| ------ | ------ | ------ | ------ | ------ | ------ |
| 11111 | First | User | 12121980 | {"Java":"EXPERT",".NET":"AWARENESS","AWS":"PRACTITIONER"} | Active |
| 22222 | Second | User | 01091987 | {"Java":"AWARENESS",".NET":"EXPERT"} | Inactive |


## Rest API Details
### createEmployee (POST /v1/employee/create) - 
Creates employee record along with skills in dynamoDB. Request to this API looks like below,
```json
{
    "firstName": "third",
    "lastName": "person",
    "dob": "03031973",
    "skills": {
        "Java": "PRACTITIONER",
        ".NET": "AWARENESS"
    }
}
```
After successful creation it will return response with employeeID as below,
```json
{
    "employeeID": "NHS1084GJTQ",
    "status": "Success"
}
```
Before creating employee system also validate if skills given in request are present in our system. If any skill does not match with anything in system api will give error as below,
```json
{
    "timestamp": "2021-03-25T15:33:14.292+0000",
    "message": "Skill not found in skill repository. Please provide right skill set.",
    "details": "uri=/v1/employee/create/"
}
```


### Update Employee Details (POST /v1/employee/update/{employeeID}) - 
Updates employee record along with skills in dynamoDB for given id and status 'Active. If employee with given ID is not present it will give 404 with message " Employee not found". Request to this API looks like below,
```json
{
    "firstName": "john",
    "lastName": "walker",
    "dob": "12121980",
    "skills": {
        "Java": "EXPERT"
    }
}
```
After successful update it will return response with employeeID as below,
```json
{
    "employeeID": "NHS1084GJTQ",
    "status": "Success"
}
```
Before updating employee, system also validate if skills given in request are present in our system. If any skill does not match with anything in system api will give error as below,
```json
{
    "timestamp": "2021-03-25T15:33:14.292+0000",
    "message": "Skill not found in skill repository. Please provide right skill set.",
    "details": "uri=/v1/employee/update/NHS1084GJTQ"
}
```

### Get Employee Details (GET /v1/employee/retrieve/{employeeID}) - 
Fetch the details for employee with given EmployeeID and status 'Active'. If employee with given ID is not present it will give 404 with message " Employee not found".

After successful retrieval it will return response with employee details as below,
```json
{
    "firstName": "first",
    "lastName": "person",
    "dob": "01011987",
    "skills": {
        "Java": "EXPERT",
        ".NET": "AWARENESS"
    }
}
```

### Delete Employee (DELETE /v1/employee/remove/{employeeID}) - 
Updates the employee with given employeeID as Inactive. If employee with given ID is not present it will give 404 with message " Employee not found". Response to this request looks like below,

After successful retrieval it will return response with employee details as below,
```json
{
    "employeeID": "NHS1084F5HD",
    "status": "Delete successful"
}
```

### Get List of Employees for given skill and level (GET /v1/employee/retrive/Java/Expert) - 
Retirves all employees with given skill and skill level. This will fetch Active wmployees only. If no employees with given skill/level are found, it will give 404 with message "No employee data found for given skills". Response to this request looks like below,

After successful retrieval it will return response with employee details as below,
```json
[
    {
        "firstName": "first",
        "lastName": "person",
        "dob": "01011987",
        "skills": {
            "Java": "EXPERT",
            ".NET": "AWARENESS"
        }
    },
    {
        "firstName": "tst",
        "lastName": "walker",
        "dob": "12121980",
        "skills": {
            "Java": "EXPERT",
            ".NET": "AWARENESS",
            "AWS": "PRACTITIONER"
        }
    }
]
```

### Get List all the skills loaded in Project (GET /v1/employee/retrieveSkills) - 
When application first starts we load all skills from csv into memory.This api will fetch all skills present in application. Response to this request looks like below,

After successful retrieval it will return response with employee details as below,
```json
[
    {
        "name": "Drupal",
        "category": "Framework"
    },
    {
        "name": "Duckreek",
        "category": "Framework"
    },
    {
        "name": "Java",
        "category": "Backend"
    },
    {
        "name": "Informatica",
        "category": "Analytics"
    },
    {
        "name": "Kafka",
        "category": "Framework"
    },
    {
        "name": "Mainframe",
        "category": "Backend"
    },
    {
        "name": "Manual Testing",
        "category": "Testing"
    },
    {
        "name": "Mulesoft",
        "category": "Framework"
    },
    {
        "name": "Oracle",
        "category": "Database"
    },
    {
        "name": "SQL",
        "category": "Database"
    },
    {
        "name": "PHP",
        "category": "FE"
    },
    {
        "name": "PLSQL",
        "category": "Database"
    },
    {
        "name": "Python",
        "category": "Backend"
    },
    {
        "name": "Quality/Team Management",
        "category": "Operations"
    },
    {
        "name": "Salesforce",
        "category": "Framework"
    },
    {
        "name": "Scrum/BA",
        "category": "Operations"
    },
    {
        "name": "Tibco",
        "category": "Backend"
    },
    {
        "name": "Vmware",
        "category": "Framework"
    },
    {
        "name": "Product Owner",
        "category": "Operations"
    }
]
```
