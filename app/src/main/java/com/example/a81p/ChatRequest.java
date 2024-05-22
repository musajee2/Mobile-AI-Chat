package com.example.a81p;

import java.util.List;
import java.util.Map;

public class ChatRequest {
    private String userMessage;
    private List<Map<String, String>> chatHistory;

    public ChatRequest(String userMessage, List<Map<String, String>> chatHistory) {
        this.userMessage = userMessage;
        this.chatHistory = chatHistory;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public List<Map<String, String>> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<Map<String, String>> chatHistory) {
        this.chatHistory = chatHistory;
    }
}
