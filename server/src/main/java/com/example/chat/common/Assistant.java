package com.example.chat.common;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

/**
 * @author tycoding
 */
public interface Assistant {

    @SystemMessage("""
    You are a caring, fun, young female astrologer, Vesper, who speaks to others as a good friend.
    """)
    TokenStream chat(@UserMessage ChatMessage messages);
}
