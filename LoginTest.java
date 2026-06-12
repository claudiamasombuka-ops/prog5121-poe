/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.registration;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginTest {

    private Login login;

    @BeforeEach
    void setUp() {
        login = new Login();
        login.firstName = "Test";
        login.surname = "User";
    }

    void setValidDefaults() {
        login.userName = "kyl_1";
        login.password = "Ch&&sec@ke99!";
        login.cellPhone = "+27838968976";
    }

    @Test
    void testLoginStatus_Success() {
        setValidDefaults();
        login.enteredUserName = "kyl_1";
        login.enteredPassword = "Ch&&sec@ke99!";
        login.enteredCell = "+27838968976";

        String expected = "Successful login\nWelcome Test, User, it is great to see you.";
        assertEquals(expected, login.returnLoginStatus());
    }

    @Test
    void testLoginStatus_Failure() {
        setValidDefaults();
        login.enteredUserName = "wrong";
        login.enteredPassword = "Ch&&sec@ke99!";
        login.enteredCell = "+27838968976";

        String expected = "A failed login\nUsername or Password incorrect, please try again.";
        assertEquals(expected, login.returnLoginStatus());
    }

    @Test
    void testUsernameValid() {
        login.userName = "abc_d";
        assertTrue(login.checkUsername());
    }

    @Test
    void testUsernameInvalid_NoUnderscore() {
        login.userName = "abcde";
        assertFalse(login.checkUsername());
    }

    @Test
    void testUsernameInvalid_TooLong() {
        login.userName = "abcdef";
        assertFalse(login.checkUsername());
    }

    @Test
    void testPasswordValidComplexity() {
        login.password = "Abc@1234";
        assertTrue(login.checkPasswordComplexity());
    }

    @Test
    void testPasswordInvalid_MissingUpper() {
        login.password = "abc@1234";
        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    void testPasswordInvalid_MissingDigit() {
        login.password = "Abc@defg";
        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    void testPasswordInvalid_MissingSpecial() {
        login.password = "Abc12345";
        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    void testCellPhoneValid() {
        login.cellPhone = "+27838968976";
        assertTrue(login.checkCellPhone());
    }

    @Test
    void testCellPhoneInvalid_MissingPlus() {
        login.cellPhone = "27838968976";
        assertFalse(login.checkCellPhone());
    }

    @Test
    void testRegisterUser_AllValid() {
        setValidDefaults();
        String result = login.registerUser();
        assertTrue(result.contains("Username successfully captured"));
        assertTrue(result.contains("Password successfully captured"));
        assertTrue(result.contains("Cell phone number successfully added"));
    }

    @Test
    void testRegisterUser_InvalidUsername() {
        login.userName = "kyle";
        login.password = "Ch&&sec@ke99!";
        login.cellPhone = "+27838968976";
        String result = login.registerUser();
        assertEquals("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than 5 characters in length.", result);
    }

    @Test
    void testRegisterUser_InvalidPassword() {
        login.userName = "kyl_1";
        login.password = "password";
        login.cellPhone = "+27838968976";
        String result = login.registerUser();
        assertEquals("Password is not correctly formatted, please ensure that the password contains at least 8 characters, a capital letter, a number, and a special character.", result);
    }

    @Test
    void testRegisterUser_InvalidPhone() {
        login.userName = "kyl_1";
        login.password = "Ch&&sec@ke99!";
        login.cellPhone = "0831234567";
        String result = login.registerUser();
        assertTrue(result.contains("Cell phone number incorrectly formatted"));
    }

    @Test
    void testLoginFailsDueToWrongCellPhone() {
        setValidDefaults();
        login.enteredUserName = "kyl_1";
        login.enteredPassword = "Ch&&sec@ke99!";
        login.enteredCell = "+27831111111"; // Mismatch
        assertFalse(login.loginUser());
    }
}