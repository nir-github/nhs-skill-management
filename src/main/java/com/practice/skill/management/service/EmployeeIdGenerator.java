package com.practice.skill.management.service;

import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.practice.skill.management.Constants;

@Component
public class EmployeeIdGenerator {
    
    private final static Logger LOGGER = LogManager.getLogger(EmployeeIdGenerator.class);
 
    public String generateEmployeeId() {
        
        LOGGER.debug("Generating employee id");
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int dayOfTheYear = now.getDayOfYear();
        
        char yearDigit = Constants.DIGITS.charAt( ( year - 2000 ) % Constants.DIGITS.length());
        
        String dayDigits = "" + dayOfTheYear;
        while (dayDigits.length() < 3) {
            dayDigits = "0" + dayDigits;
        }
        
        int[] offset = new int[] {
                                   1, 5, 8, 3, 4, 2
        };
        int value = (int)(Math.random()*80000);
        String result = "";
        
        while (value > 0) {
            int digit = value % Constants.DIGITS.length();
            result = Constants.DIGITS.charAt( ( digit + offset[result.length()] ) % Constants.DIGITS.length()) + result;
            value = Math.round(value / Constants.DIGITS.length());
        }
        
        while (result.length() < 4) {
            result = Constants.DIGITS.charAt(offset[result.length()]) + result;
        }
        return Constants.EMP_ID_SEQUENCE + yearDigit + dayDigits + result;
    }
    
}
