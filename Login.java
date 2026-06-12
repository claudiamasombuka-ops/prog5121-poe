/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registration;

/**
 *
 * @author RC_Student_lab
 */
public class Login {
    String userName;
    String password;
    String firstName = "Test";
    String surname = "User";
    String enteredUserName;
    String enteredPassword;
    String cellPhone;
    String enteredCell;

    public boolean checkUsername() {
        return userName != null && userName.length() <= 5 && userName.contains("_");
    }

    public boolean checkPasswordComplexity() {
        if (password == null || password.length() < 8) return false;

        boolean hasUpper = false, hasDigit = false, hasSpecial = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUpper = true;
            else if (Character.isDigit(ch)) hasDigit = true;
            else if (!Character.isLetterOrDigit(ch)) hasSpecial = true;
        }

        return hasUpper && hasDigit && hasSpecial;
    }

    public boolean checkCellPhone() {
        return cellPhone != null && cellPhone.matches("\\+\\d{1,3}\\d{10}");
    }

    public String registerUser() {
        StringBuilder response = new StringBuilder();

        if (!checkUsername()) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than 5 characters in length.";
        } else {
            response.append("Username successfully captured.\n");
        }

        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted, please ensure that the password contains at least 8 characters, a capital letter, a number, and a special character.";
        } else {
            response.append("Password successfully captured.\n");
        }

        if (checkCellPhone()) {
            response.append("Cell phone number successfully added.");
        } else {
            response.append("Cell phone number incorrectly formatted or does not contain international code.");
        }

        return response.toString();
    }

    public boolean loginUser() {
        return userName != null && password != null && cellPhone != null &&
               userName.equals(enteredUserName) &&
               password.equals(enteredPassword) &&
               cellPhone.equals(enteredCell);
    }

    public String returnLoginStatus() {
        if (loginUser()) {
            return "Successful login\nWelcome " + firstName + ", " + surname + ", it is great to see you.";
        } else {
            return "A failed login\nUsername or Password incorrect, please try again.";
        }
    }
}

