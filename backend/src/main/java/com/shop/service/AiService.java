package com.shop.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI智能客服服务类
 * 基于SpringAI实现
 */
@Service
public class AiService {

    @Autowired(required = false)
    private ChatClient.Builder chatClientBuilder;

    private static final String SYSTEM_PROMPT = 
        "你是一个专业的电商购物助手，名叫\"小智\"。你的职责是：\n" +
        "1. 帮助用户了解商品信息、推荐商品\n" +
        "2. 解答用户关于订单、支付、物流等问题\n" +
        "3. 处理用户的售后咨询\n" +
        "4. 提供友好、专业的服务\n\n" +
        "请用简洁、友好的语气回答用户问题。如果用户问的问题超出电商范围，请礼貌地引导用户回到购物相关话题。";

    /**
     * AI对话
     */
    public String chat(String userMessage) {
        if (chatClientBuilder == null) {
            return mockResponse(userMessage);
        }
        
        try {
            ChatClient chatClient = chatClientBuilder.build();
            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(SYSTEM_PROMPT));
            messages.add(new UserMessage(userMessage));
            
            Prompt prompt = new Prompt(messages);
            return chatClient.prompt(prompt).call().content();
        } catch (Exception e) {
            return mockResponse(userMessage);
        }
    }

    /**
     * 模拟AI响应(当AI服务不可用时)
     */
    private String mockResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("你好") || lowerMessage.contains("hi") || lowerMessage.contains("hello")) {
            return "您好！我是智能客服小智，很高兴为您服务！请问有什么可以帮助您的吗？";
        }
        if (lowerMessage.contains("商品") || lowerMessage.contains("产品")) {
            return "我们商城有丰富的商品供您选择，您可以通过搜索或浏览分类来查找心仪的商品。如果您有具体需求，可以告诉我，我来为您推荐！";
        }
        if (lowerMessage.contains("订单")) {
            return "关于订单问题，您可以在\"我的订单\"页面查看订单状态。如果有任何问题，请告诉我订单号，我来帮您查询。";
        }
        if (lowerMessage.contains("支付") || lowerMessage.contains("付款")) {
            return "我们支持多种支付方式，包括支付宝、微信支付等。如果支付遇到问题，请检查网络连接或联系客服处理。";
        }
        if (lowerMessage.contains("物流") || lowerMessage.contains("快递") || lowerMessage.contains("发货")) {
            return "订单发货后，您可以在订单详情页查看物流信息。一般情况下，商品会在1-3个工作日内发出。";
        }
        if (lowerMessage.contains("退款") || lowerMessage.contains("退货")) {
            return "如需退款退货，请在订单详情页申请售后服务。我们会在收到退货后3-5个工作日内处理退款。";
        }
        if (lowerMessage.contains("优惠") || lowerMessage.contains("折扣") || lowerMessage.contains("活动")) {
            return "我们经常会有优惠活动，您可以关注首页的活动专区。新用户还有专属优惠哦！";
        }
        
        return "感谢您的咨询！我是智能客服小智，可以帮您解答商品、订单、支付、物流等问题。请问您想了解什么呢？";
    }
}
