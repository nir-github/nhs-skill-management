package com.practice.skill.management.refdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.practice.skill.management.dto.SkillCode;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

@Component
public class SkillHelper {
    
    private final static Logger LOGGER = LogManager.getLogger(SkillHelper.class);
    
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
        LOGGER.debug("Updated skill list with {} skills", skillsList.size());
    }
    
    public boolean isSkillPresent(String skillName) {
        
        Optional<SkillCode> skill = skillsList.stream().filter(s -> s.getName().equalsIgnoreCase(skillName)).findFirst();
        return skill.isPresent();
    }

    public List<SkillCode> getAllSkills() {
        
        return skillsList;
    }
    
}
