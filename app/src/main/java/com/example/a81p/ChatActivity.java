package com.example.a81p;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private LinearLayout chatLayout;
    private EditText messageEditText;
    private Button sendButton;
    private String username;
    private Retrofit retrofit;
    private ChatBotService chatBotService;
    private List<Map<String, String>> chatHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        username = getIntent().getStringExtra("username");

        chatLayout = findViewById(R.id.chatLayout);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        // Initialize chat history
        chatHistory = new ArrayList<>();

        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/") // Correct base URL format
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create instance of ChatBotService
        chatBotService = retrofit.create(ChatBotService.class);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessageToChatBot(message);
                } else {
                    Toast.makeText(ChatActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessageToChatBot(String message) {
        // Add user message to chat layout and history
        addMessageToChatLayout(username, message);
        addMessageToChatHistory("User", message);

        // Create a ChatRequest object
        ChatRequest request = new ChatRequest(message, chatHistory);

        // Log the request payload for debugging
        Gson gson = new Gson();
        String requestJson = gson.toJson(request);
        Log.d(TAG, "Request JSON: " + requestJson);

        // Make the HTTP request
        Call<ChatResponse> call = chatBotService.sendMessage(request);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    ChatResponse chatResponse = response.body();
                    String llamaMessage = chatResponse.getMessage();

                    // Display chatbot response in chat layout and history
                    addMessageToChatLayout("Llama", llamaMessage);
                    addMessageToChatHistory("Llama", llamaMessage);
                } else {
                    // Log and handle unsuccessful response
                    Log.e(TAG, "Unsuccessful response: " + response.code() + " - " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    Toast.makeText(ChatActivity.this, "Failed to get response from chatbot", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                // Log and handle network errors or other failures
                Log.e(TAG, "Failure: " + t.getMessage(), t);
                Toast.makeText(ChatActivity.this, "Failed to communicate with chatbot server", Toast.LENGTH_SHORT).show();
            }
        });

        // Clear input field
        messageEditText.setText("");
    }

    private void addMessageToChatLayout(String sender, String message) {
        View messageView = getLayoutInflater().inflate(R.layout.item_message, null);
        TextView senderTextView = messageView.findViewById(R.id.senderTextView);
        TextView messageTextView = messageView.findViewById(R.id.messageTextView);

        senderTextView.setText(sender);
        messageTextView.setText(message);

        chatLayout.addView(messageView);
    }

    private void addMessageToChatHistory(String sender, String message) {
        Map<String, String> chatMessage = new HashMap<>();
        if (sender.equals("User")) {
            chatMessage.put("User", message);
            chatMessage.put("Llama", ""); // Ensure Llama key exists
        } else {
            chatMessage.put("User", "");
            chatMessage.put("Llama", message);
        }
        chatHistory.add(chatMessage);
    }
}
