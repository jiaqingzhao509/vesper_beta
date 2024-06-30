package com.example.chat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.example.auth.config.AuthUtil;
import com.example.chat.common.*;
import com.example.common.constant.CommonConstant;
import com.example.common.props.AuthProps;
import com.example.common.utils.R;
import com.example.modules.entity.SysMessage;
import com.example.modules.entity.SysUser;
import com.example.modules.service.SysMessageService;
import com.example.modules.service.SysUserService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.AiServices;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;

/**
 * @author tycoding
 */

@AllArgsConstructor
@RestController
@RequestMapping("/api/chat")



   

public class ChatEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(ChatEndpoint.class);

    private final PersistentChatMemoryStore chatMemoryStore;
    private final SysMessageService messageService;
    private final SysUserService userService;
    private final AuthProps props;

    public static String getCurrentDate() {
    return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    @PostMapping
    public Object chat(@RequestBody ChatReq req) {
        SysUser userInfo = AuthUtil.getUserInfo();
        if (userInfo == null || userInfo.getChatLimit() <= 0) {
            throw new RuntimeException("The number of conversations has exceeded three. Please upgrade the plan");
        }
        if (StrUtil.isBlank(userInfo.getLat()) || StrUtil.isBlank(userInfo.getLng()) || StrUtil.isBlank(userInfo.getChart())) {
            throw new RuntimeException("Please complete your birthday date and location Settings first");
        }
        userInfo.setChatLimit(userInfo.getChatLimit() - 1);
        StpUtil.getSession().set(CommonConstant.AUTH_USER_INFO_KEY, userInfo);
        userService.updateById(new SysUser().setId(userInfo.getId()).setChatLimit(userInfo.getChatLimit()));

        StreamEmitter emitter = new StreamEmitter();
        req.setEmitter(emitter);
        req.setUserId(AuthUtil.getUserId());
        req.setUsername(AuthUtil.getUsername());
        //req.setPrompt(new PromptTemplate(ChatConst.prompt).apply(Map.of("question", req.getMessage(), "charts", userInfo.getChart(),"history", chatMemoryStore.getMessages(req.getUserId())), "date", getCurrentDate()));
        req.setPrompt(new PromptTemplate(ChatConst.prompt).apply(Map.of(
        "question", req.getMessage(),
        "charts", userInfo.getChart(),
        "history", chatMemoryStore.getMessages(req.getUserId()),
        "date", getCurrentDate()
        )));

        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                .apiKey(props.getApiKey())
                .modelName(props.getModelName())
                .timeout(Duration.ofSeconds(600))
                .build();
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(userInfo.getId())
                .maxMessages(10)
                //.chatMemoryStore(chatMemoryStore)
                .build();
        
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        Assistant assistant = AiServices.builder(Assistant.class)
                .streamingChatLanguageModel(model)
                .chatMemory(chatMemory)
                .chatMemoryProvider(chatMemoryProvider)
                .build();

        long startTime = System.currentTimeMillis();
        StringBuilder text = new StringBuilder();
     
        // save user message
        req.setRole("user");
        saveMessage(req);
        chatMemoryStore.addMessage(req.getUserId(), new UserMessage(req.getMessage()));
        logger.info("Request messages: {}", req.getUserId());
        assistant.chat(req.getPrompt().toUserMessage())
                .onNext(e -> {
                    text.append(e);
                    emitter.send(new ChatRes(e));
                })
                .onComplete(e -> {
                    TokenUsage tokenUsage = e.tokenUsage();
                    emitter.send(new ChatRes(tokenUsage.totalTokenCount(), startTime));
                    emitter.complete();
                    // save message
                    req.setMessage(text.toString());
                    req.setRole("assistant");
                    saveMessage(req);
                    chatMemoryStore.addMessage(req.getUserId(), new AiMessage(text.toString()));
                    
                })
                .onError((e) -> {
                    emitter.error(e.getMessage());
                    throw new RuntimeException(e.getMessage());
                })
                .start();
        return emitter.get();
    }

    private void saveMessage(ChatReq req) {
        SysMessage message = new SysMessage();
        BeanUtils.copyProperties(req, message);
        messageService.addMessage(message);
    }

    @PostMapping("/auto-suggestion")
    public R autoSuggestion(@RequestBody ChatReq req) {
        req.setUserId(AuthUtil.getUserId());
        //logger.info("Request messages: {}",  req.getUserId());
        logger.info("Request messages: {}", req.getUserId());

        StreamEmitter emitter = new StreamEmitter();
        req.setEmitter(emitter);
        //logger.info("Request messages: {}", chatMemoryStore.getMessages(toString(req.getUserId())));
        req.setPrompt(new PromptTemplate(ChatConst.QUESTION_PROMPT).apply(Map.of("history", chatMemoryStore.getMessages(req.getUserId()))));

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(props.getApiKey())
                .modelName(props.getModelName())
                .responseFormat("json_object")
                .timeout(Duration.ofSeconds(600))
                .build();
        Response<AiMessage> generate = chatModel.generate(req.getPrompt().toUserMessage());
        ChatResJson res = JSON.parseObject(generate.content().text(), ChatResJson.class);
        //return R.ok(res.getSimilar_questions());
        return R.ok(res.getFollow_up_questions());
    }

    @GetMapping("/init-suggestion")
    public R getSuggestion() {
        return R.ok(chatMemoryStore.getSuggestion());
    }
}