
package com.syfe.pfm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PersonalFinanceManagerApplicationTests {

    @Test
    void applicationContextLoads() {
        assertTrue(true);
    }

    @Test
    void applicationClassExists() {
        assertNotNull(PersonalFinanceManagerApplication.class);
    }
}
