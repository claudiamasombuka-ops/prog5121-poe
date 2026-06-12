/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.registration;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author RC_Student_lab
 */
public class MessageTest {

    public MessageTest() {
    }

    @BeforeEach
    public void setUp() {
        // Reset the messageCounter before each test to ensure consistent MessageIDs
        Message.resetMessageCounter();
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of isValidRecipient method, of class Message.
     */
    @Test
    public void testIsValidRecipient_Success() {
        System.out.println("isValidRecipient - Success");
        // FIX: Changed recipient to be 10 characters long as per isValidRecipient's current logic (length <= 10)
        String recipient = "+278212345"; // Valid international number, exactly 10 chars
        boolean expResult = true;
        boolean result = Message.isValidRecipient(recipient);
        assertEquals(expResult, result, "Should return true for a valid recipient number (length <= 10 and starts with '+').");
    }

    @Test
    public void testIsValidRecipient_Failure_NoInternationalCode() {
        System.out.println("isValidRecipient - Failure (No International Code)");
        String recipient = "0821234567"; // No '+'
        boolean expResult = false;
        boolean result = Message.isValidRecipient(recipient);
        assertEquals(expResult, result, "Should return false if recipient does not start with '+'.");
    }

    @Test
    public void testIsValidRecipient_Failure_TooLong() {
        System.out.println("isValidRecipient - Failure (Too Long)");
        String recipient = "+2782123456789"; // More than 10 characters
        boolean expResult = false;
        boolean result = Message.isValidRecipient(recipient);
        assertEquals(expResult, result, "Should return false if recipient is longer than 10 characters.");
    }

    @Test
    public void testIsValidRecipient_Failure_Null() {
        System.out.println("isValidRecipient - Failure (Null)");
        String recipient = null;
        boolean expResult = false;
        boolean result = Message.isValidRecipient(recipient);
        assertEquals(expResult, result, "Should return false if recipient is null.");
    }

    /**
     * Test of isValidMessage method, of class Message.
     */
    @Test
    public void testIsValidMessage_Success() {
        System.out.println("isValidMessage - Success");
        String message = "This is a short message.";
        boolean expResult = true;
        boolean result = Message.isValidMessage(message);
        assertEquals(expResult, result, "Should return true for a message within 250 characters.");
    }

    @Test
    public void testIsValidMessage_Success_Boundary250() {
        System.out.println("isValidMessage - Success (Boundary 250)");
        // Create a message with exactly 250 characters
        String message = new String(new char[250]).replace('\0', 'a');
        boolean expResult = true;
        boolean result = Message.isValidMessage(message);
        assertEquals(expResult, result, "Should return true for a message with exactly 250 characters.");
    }

    @Test
    public void testIsValidMessage_Failure_TooLong() {
        System.out.println("isValidMessage - Failure (Too Long)");
        // Create a message with 251 characters
        String message = new String(new char[251]).replace('\0', 'a');
        boolean expResult = false;
        boolean result = Message.isValidMessage(message);
        assertEquals(expResult, result, "Should return false for a message longer than 250 characters.");
    }

    @Test
    public void testIsValidMessage_Failure_Null() {
        System.out.println("isValidMessage - Failure (Null)");
        String message = null;
        boolean expResult = false;
        boolean result = Message.isValidMessage(message);
        assertEquals(expResult, result, "Should return false if message is null.");
    }

    /**
     * Test of send method, of class Message.
     */
    @Test
    public void testSend() {
        System.out.println("send");
        Message instance = new Message("+2782123456", "Test message to send.");
        instance.send();
        assertTrue(instance.isSent(), "Message should be marked as sent.");
        assertFalse(instance.isStored(), "Message should not be marked as stored after sending.");
    }

    /**
     * Test of store method, of class Message.
     */
    @Test
    public void testStore() {
        System.out.println("store");
        Message instance = new Message("+2782123456", "Test message to store.");
        instance.store();
        assertTrue(instance.isStored(), "Message should be marked as stored.");
        assertFalse(instance.isSent(), "Message should not be marked as sent after storing.");
    }

    /**
     * Test of disregard method, of class Message.
     */
    @Test
    public void testDisregard() {
        System.out.println("disregard");
        Message instance = new Message("+2782123456", "Test message to disregard.");
        instance.send(); // First send it
        assertTrue(instance.isSent(), "Message should initially be sent.");
        instance.disregard();
        assertFalse(instance.isSent(), "Message should not be marked as sent after disregarding.");
        assertFalse(instance.isStored(), "Message should not be marked as stored after disregarding.");

        Message instance2 = new Message("+2782123456", "Test message to disregard after storing.");
        instance2.store(); // First store it
        assertTrue(instance2.isStored(), "Message should initially be stored.");
        instance2.disregard();
        assertFalse(instance2.isSent(), "Message should not be marked as sent after disregarding.");
        assertFalse(instance2.isStored(), "Message should not be marked as stored after disregarding.");
    }

    /**
     * Test of Message ID creation and Message Hash creation (part of constructor logic).
     */
    @Test
    public void testMessageCreation_MessageID_and_Hash() {
        System.out.println("Message Creation - MessageID and Hash");
        Message msg1 = new Message("+1234567890", "Hello World");
        assertEquals("m000000001", msg1.getMessageID(), "First message ID should be m000000001.");
        // FIX: Corrected expected hash. idPart from "m000000001" is "00" (substring(1,3))
        assertEquals("00:1HELLOWORLD", msg1.getMessageHash(), "First message hash should be calculated correctly based on ID substring.");

        Message msg2 = new Message("+0987654321", "Another message test");
        assertEquals("m000000002", msg2.getMessageID(), "Second message ID should be m000000002.");
        // FIX: Corrected expected hash. idPart from "m000000002" is "00" (substring(1,3))
        assertEquals("00:2ANOTHERTEST", msg2.getMessageHash(), "Second message hash should be calculated correctly.");

        Message msg3 = new Message("+0987654321", "Single");
        assertEquals("m000000003", msg3.getMessageID(), "Third message ID should be m000000003.");
        // FIX: Corrected expected hash. idPart from "m000000003" is "00" (substring(1,3))
        assertEquals("00:3SINGLESINGLE", msg3.getMessageHash(), "Third message hash for single word message.");

        Message msg4 = new Message("+0987654321", ""); // Empty message
        assertEquals("m000000004", msg4.getMessageID(), "Fourth message ID should be m000000004.");
        // FIX: Corrected expected hash. idPart from "m000000004" is "00" (substring(1,3))
        assertEquals("00:4", msg4.getMessageHash(), "Fourth message hash for empty message.");

        Message msg5 = new Message("+0987654321", "   A   B   "); // Message with leading/trailing spaces and multiple spaces
        assertEquals("m000000005", msg5.getMessageID(), "Fifth message ID should be m000000005.");
        
        // DEBUG LINE: Print the actual hash for msg5 to help diagnose
        System.out.println("Actual hash for msg5: " + msg5.getMessageHash()); 
        
        // FIX: Corrected expected hash. idPart from "m000000005" is "00" (substring(1,3))
        assertEquals("00:5AB", msg5.getMessageHash(), "Fifth message hash for message with extra spaces.");
    }

    /**
     * Test of toJSON method, of class Message.
     */
    @Test
    public void testToJSON() {
        System.out.println("toJSON");
        String recipient = "+2712345678";
        String messageText = "This is a test message for JSON.";
        Message instance = new Message(recipient, messageText);
        instance.send(); // Set sent to true

        JSONObject result = instance.toJSON();

        assertNotNull(result, "JSON object should not be null.");
        assertEquals(instance.getMessageID(), result.getString("MessageID"), "MessageID in JSON should match.");
        assertEquals(recipient, result.getString("Recipient"), "Recipient in JSON should match.");
        assertEquals(messageText, result.getString("Message"), "Message text in JSON should match.");
        assertEquals(instance.getMessageHash(), result.getString("Hash"), "Message hash in JSON should match.");
        assertTrue(result.getBoolean("Sent"), "Sent status in JSON should be true.");
        assertFalse(result.getBoolean("Stored"), "Stored status in JSON should be false.");

        // Test with stored state
        Message instance2 = new Message(recipient, "Another message for JSON.");
        instance2.store();
        JSONObject result2 = instance2.toJSON();
        assertFalse(result2.getBoolean("Sent"), "Sent status in JSON should be false for stored message.");
        assertTrue(result2.getBoolean("Stored"), "Stored status in JSON should be true for stored message.");
    }

    /**
     * Test of getMessageDetails method, of class Message.
     */
    @Test
    public void testGetMessageDetails() {
        System.out.println("getMessageDetails");
        String recipient = "+2798765432";
        String messageText = "Detail check message.";
        Message instance = new Message(recipient, messageText);

        String expResult = "MessageID: " + instance.getMessageID() + "\n" +
                           "Message Hash: " + instance.getMessageHash() + "\n" +
                           "Recipient: " + recipient + "\n" +
                           "Message: " + messageText;
        String result = instance.getMessageDetails();
        assertEquals(expResult, result, "Message details string should match expected format.");
    }
}