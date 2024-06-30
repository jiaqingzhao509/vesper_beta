package com.example.chat.common;

import dev.langchain4j.model.input.Prompt;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author tycoding
 */
@Data
@Accessors(chain = true)
public class ChatReq {

    private String model;

    private String message;

    private Long userId;

    private String username;

    private String chatId;

    private String role;

    private Prompt prompt;

    private StreamEmitter emitter;
}
