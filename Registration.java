package com.mycompany.registration;

import javax.swing.JOptionPane;
import java.io.File; // Import File for checking existence

/**
 * Main application class that handles user registration, login,
 * and the primary message sending and reporting menu.
 */
public class Registration {
    public static void main(String[] args) {
        // Initialize Login and MessageStorage services
        Login login = new Login();
        MessageStorage storage = new MessageStorage();

        // Load messages from JSON file at application startup
        // This ensures that any messages saved from previous sessions are loaded.
        storage.loadMessagesFromJson("messages.json");

        // --- User Registration Process ---
        login.firstName = JOptionPane.showInputDialog("Enter First Name:");
        login.surname = JOptionPane.showInputDialog("Enter Last Name:");
        login.userName = JOptionPane.showInputDialog("Enter Username:");
        login.password = JOptionPane.showInputDialog("Enter Password:");
        login.cellPhone = JOptionPane.showInputDialog("Enter cellphone number:");
        JOptionPane.showMessageDialog(null, login.registerUser());

        // Loop to ensure username and password meet complexity requirements
        while (!login.checkUsername() || !login.checkPasswordComplexity()) {
            JOptionPane.showMessageDialog(null, "Registration failed. Please try again with a valid username and strong password!");
            login.userName = JOptionPane.showInputDialog("Enter Username:");
            login.password = JOptionPane.showInputDialog("Enter Password:");
            login.cellPhone = JOptionPane.showInputDialog("Enter cellphone number:");
            JOptionPane.showMessageDialog(null, login.registerUser());
        }

        // --- User Login Process ---
        login.enteredUserName = JOptionPane.showInputDialog("Enter Username:");
        login.enteredPassword = JOptionPane.showInputDialog("Enter Password:");
        login.enteredCell = JOptionPane.showInputDialog("Enter cellphone number:");
        JOptionPane.showMessageDialog(null, login.returnLoginStatus());

        // Loop to ensure successful login
        while (!login.loginUser()) {
            JOptionPane.showMessageDialog(null, "Login failed. Please try again.");
            login.enteredUserName = JOptionPane.showInputDialog("Enter Username:");
            login.enteredPassword = JOptionPane.showInputDialog("Enter Password:");
            login.enteredCell = JOptionPane.showInputDialog("Enter cellphone number:");
            JOptionPane.showMessageDialog(null, login.returnLoginStatus());
        }

        // --- Main Application Menu ---
        JOptionPane.showMessageDialog(null, "Welcome to QuickChat.");
        // Prompt for how many messages the user intends to send in THIS session
        int totalMessagesToSendInSession = Integer.parseInt(JOptionPane.showInputDialog("How many NEW messages would you like to send in this session?"));
        int messagesSentInCurrentSession = 0; // Tracks new messages sent/stored by the user in this session

        while (true) {
            String mainMenuChoice = JOptionPane.showInputDialog("""
                Please select an option:
                1) Send New Messages
                2) Show Recent Messages / Task Report
                3) Quit
            """);

            switch (mainMenuChoice) {
                case "1":
                    // Check if the session limit for new messages has been reached
                    if (messagesSentInCurrentSession >= totalMessagesToSendInSession) {
                        JOptionPane.showMessageDialog(null, "Message limit reached for this session. Please select another option or quit.");
                        break;
                    }

                    // Prompt for recipient
                    String recipient = JOptionPane.showInputDialog("Enter recipient number (include + country code, max 10 chars, e.g., +2783xxxxxxx):");
                    if (!Message.isValidRecipient(recipient)) {
                        JOptionPane.showMessageDialog(null, "Invalid recipient format. Must start with '+' and be max 10 characters.");
                        break;
                    }

                    // Prompt for message text
                    String messageText = JOptionPane.showInputDialog("Enter message (max 250 characters):");
                    if (!Message.isValidMessage(messageText)) {
                        JOptionPane.showMessageDialog(null, "Message too long (max 250 characters).");
                        break;
                    }

                    // Create a new Message object
                    Message newMessage = new Message(recipient, messageText);

                    // Prompt for message action (Send, Disregard, Store)
                    String messageActionChoice = JOptionPane.showInputDialog("""
                        Choose message option:
                        1) Send
                        2) Disregard
                        3) Store for later
                    """);

                    switch (messageActionChoice) {
                        case "1" -> { // Send message
                            newMessage.send();
                            storage.addMessage(newMessage); // Add to storage after status is set
                            messagesSentInCurrentSession++; // Increment session counter
                            JOptionPane.showMessageDialog(null, "Message sent!\n" + newMessage.getMessageDetails());
                        }
                        case "2" -> { // Disregard message
                            newMessage.disregard();
                            storage.addMessage(newMessage); // Add to storage even if disregarded, for tracking
                            JOptionPane.showMessageDialog(null, "Message disregarded. It will not be sent or stored.");
                        }
                        case "3" -> { // Store message for later
                            newMessage.store();
                            storage.addMessage(newMessage); // Add to storage after status is set
                            messagesSentInCurrentSession++; // Increment session counter
                            JOptionPane.showMessageDialog(null, "Message stored for later.\n" + newMessage.getMessageDetails());
                        }
                        default -> JOptionPane.showMessageDialog(null, "Invalid action choice for message. Message not processed.");
                    }
                    break;

                case "2": // Show Recent Messages / Task Report Menu (Part 3 functionalities)
                    String reportMenuChoice;
                    // Loop for the report menu until user chooses to exit
                    while (true) {
                        reportMenuChoice = JOptionPane.showInputDialog("""
                            Select a report option:
                            1) Display Sender and Recipient of All Sent Messages
                            2) Display Longest Sent Message
                            3) Search for Message ID
                            4) Search for Messages to a Specific Recipient
                            5) Delete a Message by Hash
                            6) Display Full Report of All Sent Messages
                            7) Back to Main Menu
                        """);

                        switch (reportMenuChoice) {
                            case "1" -> JOptionPane.showMessageDialog(null, storage.displaySentMessagesSummary());
                            case "2" -> {
                                Message longest = storage.getLongestSentMessage();
                                if (longest != null) {
                                    JOptionPane.showMessageDialog(null, "Longest Sent Message:\n" + longest.getMessageText());
                                } else {
                                    JOptionPane.showMessageDialog(null, "No sent messages found to determine the longest.");
                                }
                            }
                            case "3" -> {
                                String searchId = JOptionPane.showInputDialog("Enter Message ID to search (e.g., m000000001):");
                                Message foundMsg = storage.searchMessageById(searchId);
                                if (foundMsg != null) {
                                    JOptionPane.showMessageDialog(null, "Message Found:\n" +
                                            "Recipient: " + foundMsg.getRecipient() + "\n" +
                                            "Message: " + foundMsg.getMessageText());
                                } else {
                                    JOptionPane.showMessageDialog(null, "Message with ID '" + searchId + "' not found.");
                                }
                            }
                            case "4" -> {
                                String searchRecipient = JOptionPane.showInputDialog("Enter Recipient number to search (e.g., +2783xxxxxxx):");
                                String recipientMessages = storage.searchMessagesByRecipient(searchRecipient);
                                if (!recipientMessages.isEmpty()) {
                                    JOptionPane.showMessageDialog(null, "Messages for Recipient " + searchRecipient + ":\n" + recipientMessages);
                                } else {
                                    JOptionPane.showMessageDialog(null, "No sent or stored messages found for recipient '" + searchRecipient + "'.");
                                }
                            }
                            case "5" -> {
                                String hashToDelete = JOptionPane.showInputDialog("Enter Message Hash to delete (e.g., 01:1DIDYOUCAKE?):");
                                if (storage.deleteMessageByHash(hashToDelete)) {
                                    JOptionPane.showMessageDialog(null, "Message with hash '" + hashToDelete + "' successfully deleted.");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Message with hash '" + hashToDelete + "' not found or could not be deleted.");
                                }
                            }
                            case "6" -> JOptionPane.showMessageDialog(null, storage.generateReport());
                            case "7" -> {
                                // Exit the report menu loop and return to the main menu
                                break;
                            }
                            default -> JOptionPane.showMessageDialog(null, "Invalid report option. Please try again.");
                        }
                        // If the choice was '7', the inner loop breaks and control returns to the outer main menu loop.
                        // For any other valid choice, the loop continues, showing the report menu again.
                        if ("7".equals(reportMenuChoice)) {
                            break;
                        }
                    }
                    break; // Break from the outer switch to return to the main menu

                case "3": // Quit application
                    JOptionPane.showMessageDialog(null, "Exiting QuickChat. New messages processed in this session: " + messagesSentInCurrentSession);
                    // Save all current messages to JSON before application exits
                    storage.saveMessagesToJson("messages.json");
                    return; // Exit the main method, terminating the application

                default:
                    JOptionPane.showMessageDialog(null, "Invalid main menu option. Please try again.");
            }
        }
    }
}