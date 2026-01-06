package com.example.poochpalace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.ListCrudRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class PoochPalaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PoochPalaceApplication.class, args);
	}

	@Bean
	VectorStore vectorStore(EmbeddingModel embeddingModel) {
		return SimpleVectorStore.builder(embeddingModel).build();
	}

}

@Controller
@ResponseBody
class AssistantController {

    private final ChatClient ai;
    private final Map<String, PromptChatMemoryAdvisor> memory = new ConcurrentHashMap<>();
    private final VectorStore vectorStore;

    AssistantController(ChatClient.Builder ai,
                        DogRepository repository,
                        VectorStore vectorStore
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

        repository.findAll().forEach(dog -> {
            var dogument = new Document("id: %s, name: %s, description: %s".formatted(dog.getId(), dog.getName(), dog.getDescription()));
            vectorStore.add(List.of(dogument));
        });
        this.vectorStore = vectorStore;
    }


    @GetMapping("/{user}/assistant")
    String inquire (@PathVariable String user, @RequestParam String question) {
 
        var inMemoryChatMemoryRepository = new InMemoryChatMemoryRepository();
        var chatMemory = MessageWindowChatMemory
            .builder()
            .chatMemoryRepository(inMemoryChatMemoryRepository)
            .build();
        var advisor = PromptChatMemoryAdvisor
            .builder(chatMemory)
            .build();
        var advisorForUser = this.memory.computeIfAbsent(user, k -> advisor);

        return this.ai
                .prompt()
                .user(question)
                .advisors(advisorForUser)
                .advisors(new QuestionAnswerAdvisor(this.vectorStore))
                .call()
                .content();
    }
}

interface DogRepository extends ListCrudRepository<Dog, Integer> { }

@Entity
class Dog {
    @Id
    private int id;
    private String name;
    private String owner;
    private String description;

    public Dog() {
    }

    public Dog(int id, String name, String owner, String description) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}