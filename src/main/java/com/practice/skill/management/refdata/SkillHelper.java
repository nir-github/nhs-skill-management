package com.practice.skill.management.refdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.practice.skill.management.dto.SkillCode;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

@Component
public class SkillHelper {
    
    private List<SkillCode> skillsList = new ArrayList<SkillCode>();
    
    @Value("classpath:refdata/skills.csv")
    private Resource resourceFile;
    
    @PostConstruct
    public void setup() throws IOException {
        
        // csv parser initialisation
        BeanListProcessor<SkillCode> rowProcessor = new BeanListProcessor<SkillCode>(SkillCode.class);
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.getFormat().setLineSeparator("\n");
        parserSettings.getFormat().setDelimiter('|');
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(parserSettings);
        
        parser.parse(resourceFile.getInputStream());
        
        for (SkillCode fileRow : rowProcessor.getBeans()) {
            skillsList.add(fileRow);
        }
        System.out.println(skillsList.size());
    }
    
    /*public SkillCode getABIDetailsFromAaCode(String abiTableNumber, String aaCode) {
        
        Optional<SkillCode> op = abiConvCodeslist.stream().filter(abiConv -> abiConv.getAaTableNumber().equalsIgnoreCase(abiTableNumber) && abiConv.getAaCode().equalsIgnoreCase(aaCode)).findFirst();
        
        if (op.isPresent()) {
            return op.get();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Abi code for aa table number " + abiTableNumber + " aa Code " + aaCode + " not found");
        
    }
    
    public SkillCode getABIDetailsFromSkillCode(String abiTableNumber, String SkillCode) {
        
        Optional<SkillCode> ops = abiConvCodeslist.stream().filter(abiConv -> abiConv.getAaTableNumber().equalsIgnoreCase(abiTableNumber) && abiConv.getSkillCode().equals(SkillCode)).findFirst();
        if (ops.isPresent()) {
            return ops.get();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ABI code for aa table number " + abiTableNumber + " abi Code " + SkillCode + " not found");
    }*/
    
}
