/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registration;

/**
 *
 * @author RC_Student_lab
 */
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONTokener;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MessageStorage {
    // Master list of all messages added to storage, regardless of status
    private final ArrayList<Message> allMessages = new ArrayList<>();
    // Specific lists for categorization
    private final ArrayList<Message> sentMessages = new ArrayList<>();
    private final ArrayList<Message> disregardedMessages = new ArrayList<>();
    private final ArrayList<Message> storedMessages = new ArrayList<>();
    // Lists to hold hashes and IDs for quick lookup (though iteration is also fine for small sets)
    private final ArrayList<String> messageHashes = new ArrayList<>();
    private ArrayList<String> messageIDs = new ArrayList<>();

    /**
     * Adds a message to storage and categorizes it based on its status.
     * @param message The Message object to add.
     */
    public void addMessage(Message message) {
        allMessages.add(message); // Add to master list
        if (message.isSent()) {
            sentMessages.add(message);
        } else if (message.isStored()) {
            storedMessages.add(message);
        } else if (message.isDisregarded()) { // Explicitly check disregarded status
            disregardedMessages.add(message);
        }
        messageHashes.add(message.getMessageHash());
        messageIDs.add(message.getMessageID());
    }

    /**
     * Saves all messages in 'allMessages' to a JSON file.
     * @param filename The name of the file to save to.
     */
    public void saveMessagesToJson(String filename) {
        JSONArray array = new JSONArray();
        for (Message m : allMessages) { // Iterate through all messages
            array.put(m.toJSON());
        }
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(array.toString(4)); // Pretty print with indent of 4 spaces
            System.out.println("Messages saved successfully to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving messages to JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads messages from a JSON file into storage, categorizing them.
     * Clears existing messages to prevent duplicates on load.
     * @param filename The name of the file to load from.
     */
    public void loadMessagesFromJson(String filename) {
        // Clear current lists before loading to avoid duplicates if loading multiple times
        allMessages.clear();
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();

        try {
            if (Files.exists(Paths.get(filename))) {
                String jsonText = new String(Files.readAllBytes(Paths.get(filename)));
                JSONArray array = new JSONArray(new JSONTokener(jsonText));
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    // Reconstruct Message object
                    Message m = new Message(obj.getString("Recipient"), obj.getString("Message"));
                    m.setMessageID(obj.getString("MessageID"));
                    m.setMessageHash(obj.getString("Hash"));

                    if (obj.getBoolean("Sent")) {
                        m.send();
                    } else if (obj.getBoolean("Stored")) {
                        m.store();
                    } else { // Assuming it was disregarded
                        m.disregard();
                    }
                    // Add to storage; the addMessage method will handle categorization
                    addMessage(m);
                }
                System.out.println("Messages loaded successfully from " + filename);
            } else {
                System.out.println("No existing messages.json file found. Starting fresh.");
            }
        } catch (IOException | JSONException e) {
            System.err.println("Error loading messages from JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns the total number of messages stored in the master list.
     * @return The total number of messages.
     */
    public int getTotalMessages() {
        return allMessages.size();
    }

    // --- Message Management and Report Functions ---

    /**
     * Displays the sender and recipient of all sent messages.
     * @return A formatted string of sender and recipient for all sent messages.
     */
    public String displaySentMessagesSummary() {
        if (sentMessages.isEmpty()) {
            return "No sent messages to display.";
        }
        StringBuilder sb = new StringBuilder("Sent Messages (Sender/Recipient):\n");
        for (Message m : sentMessages) {
            // Assuming "sender" is the current user logged in, which is not stored in Message.
            // For this task, "sender" refers to the context of the message being 'sent'.
            // Recipient is available.
            sb.append("- Recipient: ").append(m.getRecipient()).append(" (ID: ").append(m.getMessageID()).append(")\n");
        }
        return sb.toString();
    }

    /**
     * Finds and returns the longest sent message.
     * @return The Message object representing the longest sent message, or null if no sent messages.
     */
    public Message getLongestSentMessage() {
        if (sentMessages.isEmpty()) {
            return null;
        }
        Message longestMessage = sentMessages.get(0);
        for (int i = 1; i < sentMessages.size(); i++) {
            if (sentMessages.get(i).getMessageText().length() > longestMessage.getMessageText().length()) {
                longestMessage = sentMessages.get(i);
            }
        }
        return longestMessage;
    }

    /**
     * Searches for a message by its ID across all messages (sent, stored, disregarded).
     * @param messageId The ID of the message to search for.
     * @return The Message object if found, otherwise null.
     */
    public Message searchMessageById(String messageId) {
        for (Message m : allMessages) {
            if (m.getMessageID().equals(messageId)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Searches for all messages (sent or stored) sent to a particular recipient.
     * @param recipient The recipient number to search for.
     * @return A formatted string of messages sent/stored to the given recipient.
     */
    public String searchMessagesByRecipient(String recipient) {
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for (Message m : allMessages) {
            if ((m.isSent() || m.isStored()) && m.getRecipient().equals(recipient)) {
                sb.append("- Message ID: ").append(m.getMessageID())
                  .append(", Message: ").append(m.getMessageText()).append("\n");
                found = true;
            }
        }
        return found ? sb.toString() : "";
    }

    /**
     * Deletes a message using its message hash from all relevant lists.
     * @param messageHash The hash of the message to delete.
     * @return true if the message was found and deleted, false otherwise.
     */
    public boolean deleteMessageByHash(String messageHash) {
        Message messageToDelete = null;
        // Find the message in the master list first
        for (Message m : allMessages) {
            if (m.getMessageHash().equals(messageHash)) {
                messageToDelete = m;
                break;
            }
        }

        if (messageToDelete != null) {
            // Remove from all relevant lists
            allMessages.remove(messageToDelete);
            sentMessages.remove(messageToDelete);
            disregardedMessages.remove(messageToDelete);
            storedMessages.remove(messageToDelete);
            messageHashes.remove(messageToDelete.getMessageHash());
            messageIDs.remove(messageToDelete.getMessageID());
            return true;
        }
        return false;
    }

    /**
     * Generates a report listing full details of all sent messages.
     * @return A formatted string containing the report.
     */
    public String generateReport() {
        if (sentMessages.isEmpty()) {
            return "No sent messages to generate a report for.";
        }
        StringBuilder sb = new StringBuilder("--- Sent Messages Report ---\n");
        for (Message m : sentMessages) {
            sb.append("Message Hash: ").append(m.getMessageHash()).append("\n");
            sb.append("Recipient: ").append(m.getRecipient()).append("\n");
            sb.append("Message: ").append(m.getMessageText()).append("\n");
            sb.append("----------------------------\n");
        }
        return sb.toString();
    }

    // Getters for testing purposes (to access the internal lists)
    public ArrayList<Message> getAllMessages() {
        return allMessages;
    }

    public ArrayList<Message> getSentMessages() {
        return sentMessages;
    }

    public ArrayList<Message> getDisregardedMessages() {
        return disregardedMessages;
    }

    public ArrayList<Message> getStoredMessages() {
        return storedMessages;
    }

    public ArrayList<String> getMessageHashes() {
        return messageHashes;
    }

    public ArrayList<String> getMessageIDs() {
        return messageIDs;
    }
}

