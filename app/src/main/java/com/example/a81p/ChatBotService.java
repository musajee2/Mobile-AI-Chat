package com.example.a81p;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatBotService {
    @POST("/chat") // Endpoint for sending messages to the chatbot
    Call<ChatResponse> sendMessage(@Body ChatRequest request);
}
