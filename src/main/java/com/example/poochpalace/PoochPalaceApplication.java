package com.example.poochpalace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.ListCrudRepository;
import jakarta.persistence.Id;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class PoochPalaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PoochPalaceApplication.class, args);
	}

}

@Controller
@ResponseBody
class AssistantController {

    private final ChatClient ai;
    private final Map<String, PromptChatMemoryAdvisor> memory = new ConcurrentHashMap<>();

    AssistantController(ChatClient.Builder ai
    ) {
        var system = """
                You are an AI powered assistant to help people adopt a dog from the adoption
                agency named Pooch Palace with locations in Antwerp, Seoul, Tokyo, Singapore, Paris,
                Mumbai, New Delhi, Barcelona, San Francisco, and London. Information about the dogs 
                available will be presented below. If there is no information, then return a polite response 
                suggesting we don't have any dogs available.
                """;
        this.ai = ai
                .defaultSystem(system)
                .build();
    }


    @GetMapping("/{user}/assistant")
    String inquire (@PathVariable String user, @RequestParam String question) {
/* 
        var inMemoryChatMemoryRepository = new InMemoryChatMemoryRepository();
        var chatMemory = MessageWindowChatMemory
            .builder()
            .chatMemoryRepository(inMemoryChatMemoryRepository)
            .build();
        var advisor = PromptChatMemoryAdvisor
            .builder(chatMemory)
            .build();
        var advisorForUser = this.memory.computeIfAbsent(user, k -> advisor);
*/
        return this.ai
                .prompt()
                .user(question)
//                .advisors(advisorForUser)
                .call()
                .content();
    }
}