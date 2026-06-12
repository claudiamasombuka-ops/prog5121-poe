/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.registration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author RC_Student_lab
 */
public class MessageStorageTest {
    
    public MessageStorageTest() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of addMessage method, of class MessageStorage.
     */
    @Test
    public void testAddMessage() {
        System.out.println("addMessage");
        Message message = null;
        MessageStorage instance = new MessageStorage();
        instance.addMessage(message);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of saveMessagesToJson method, of class MessageStorage.
     */
    @Test
    public void testSaveMessagesToJson() {
        System.out.println("saveMessagesToJson");
        String filename = "";
        MessageStorage instance = new MessageStorage();
        instance.saveMessagesToJson(filename);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTotalMessages method, of class MessageStorage.
     */
    @Test
    public void testGetTotalMessages() {
        System.out.println("getTotalMessages");
        MessageStorage instance = new MessageStorage();
        int expResult = 0;
        int result = instance.getTotalMessages();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
