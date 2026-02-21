package com.shop.controller;

import com.shop.common.Result;
import com.shop.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI智能客服控制器
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * AI对话
     */
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, String> params) {
        String message = params.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Result.error("消息不能为空");
        }
        String response = aiService.chat(message);
        return Result.success(response);
    }
}
