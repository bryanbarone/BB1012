package com.bryanbarone.cf.toolrental.repository;

import com.bryanbarone.cf.toolrental.model.Tool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ToolJDBCRepositoryTest {

    @Autowired
    private ToolJDBCRepository repository;


    @Test
    void findAllTools() {
        List<Tool> tools = repository.findAllTools();
        assertEquals(4, tools.size(), "Incorrect number of tools returned in all tools list");
    }

    // simple checks to ensure database tables were created and populated as expected
    @Test
    void findToolByCode_LADW() {
        // check Werner ladder data
        Tool tool = repository.findToolByCode("LADW");
        assertEquals("Ladder", tool.getType(), "Incorrect tool type for LADW");
        assertEquals("Werner", tool.getBrand(), "Incorrect tool brand for LADW");
        assertEquals(new BigDecimal("1.99"), tool.getCosts().getCharge(), "Incorrect cost for LADW");
    }

    @Test
    void findToolByCode_CHNS() {
        // check Stihl chainsaw data
        Tool tool = repository.findToolByCode("CHNS");
        assertEquals("Chainsaw", tool.getType(), "Incorrect tool type for CHNS");
        assertEquals("Stihl", tool.getBrand(), "Incorrect tool brand for CHNS");
        assertEquals(new BigDecimal("1.49"), tool.getCosts().getCharge(), "Incorrect cost for CHNS");
    }

    @Test
    void findToolByCode_JAKR() {
        // check Stihl chainsaw data
        Tool tool = repository.findToolByCode("JAKR");
        assertEquals("Jackhammer", tool.getType(), "Incorrect tool type for JAKR");
        assertEquals("Ridgid", tool.getBrand(), "Incorrect tool brand for JAKR");
        assertEquals(new BigDecimal("2.99"), tool.getCosts().getCharge(), "Incorrect cost for JAKR");
    }

    @Test
    void findToolByCode_Invalid() {
        // check invalid tool
        try {
            repository.findToolByCode("XXXX");
            Assertions.fail("Failure in retrieving invalid tool code");
        } catch (EmptyResultDataAccessException er) {
            // expected result
        }
    }
}