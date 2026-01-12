# PoochPalace Copilot Instructions

## Project Overview
PoochPalace is a Spring Boot + Spring AI application demonstrating RAG (Retrieval-Augmented Generation) with LLM integration for a dog adoption assistant. It uses OpenAI's GPT-4o-mini model, vector embeddings for semantic search, MCP (Model Context Protocol) client integration, and in-memory chat history per user.

**Reference**: [Spring AI 1.0 Implementation](https://www.infoq.com/articles/spring-ai-1-0/)

## Architecture Patterns

### Core Components (Single Monolithic File)
All business logic resides in [src/main/java/com/example/poochpalace/PoochPalaceApplication.java](src/main/java/com/example/poochpalace/PoochPalaceApplication.java):
- **AssistantController**: REST controller serving `/jonathan/assistant?question=` endpoint
- **Dog Entity**: JPA entity mapped to H2 database
- **DogRepository**: Spring Data CRUD interface for Dog persistence
- **Bean Definitions**: VectorStore, ChatClient configuration, McpSyncClient initialization

### Data Flow
1. **Initialization** ([DataInitializer.java](src/main/java/com/example/poochpalace/DataInitializer.java)): Populates H2 with 8 seed dogs on startup
2. **Vector Embedding**: Dogs are loaded into SimpleVectorStore during controller construction
3. **User Query**: Each request creates per-user chat memory and uses QuestionAnswerAdvisor for RAG
4. **MCP Integration**: Tool callbacks via MethodToolCallbackProvider delegate to external MCP server on `:8081`

## Build & Runtime Commands
```bash
export OPENAI_API_KEY=<your-key>        # Required environment variable
./mvnw clean install                    # Full Maven build
./mvnw spring-boot:run                  # Start dev server (port 8080)
./mvnw test                             # Run tests
```

**Health Check**: `GET http://localhost:8080/actuator/health`  
**Example Query**: `GET http://localhost:8080/jonathan/assistant?question=My%20name%20is%20John`

## Key Technologies & Configuration
- **Spring Boot 3.5.9-SNAPSHOT** with Java 21
- **Spring AI 1.1.2**: ChatClient, VectorStore (SimpleVectorStore), EmbeddingModel
- **OpenAI Integration**: Model `gpt-4o-mini` via `spring.ai.openai.*` properties
- **H2 Database**: In-memory, DDL mode `create-drop`, console at `/h2-console`
- **MCP Client**: HttpClientSseClientTransport connecting to `http://localhost:8081`
- **Chat Memory**: InMemoryChatMemoryRepository with MessageWindowChatMemory per user

**Config Location**: [src/main/resources/application.properties](src/main/resources/application.properties)

## Critical Patterns & Conventions

### RAG Implementation
- System prompt defines Pooch Palace adoption agency context (10 global locations)
- Dogs are embedded as Documents: `"id: <id>, name: <name>, description: <description>"`
- QuestionAnswerAdvisor automatically retrieves relevant dogs before sending to LLM

### Per-User State Management
- Chat memory stored in ConcurrentHashMap keyed by username from URL path (`/{user}/assistant`)
- Each new conversation gets fresh InMemoryChatMemoryRepository (not persisted across app restarts)

### MCP Tool Integration
- MethodToolCallbackProvider automatically exposes MCP server methods to ChatClient
- External MCP server must run on `:8081` before app startup

## Adding Features
1. **New Endpoints**: Add @GetMapping methods to AssistantController
2. **Database Changes**: Modify Dog entity (schema auto-migrates via `ddl-auto=create-drop`)
3. **Prompt Engineering**: Edit system prompt in AssistantController constructor
4. **RAG Adjustments**: Modify Document format or QuestionAnswerAdvisor configuration
5. **MCP Tools**: Add interfaces/methods and re-export from MCP server; no app code changes needed

## Testing
- Test file: [src/test/java/com/example/poochpalace/PoochPalaceApplicationTests.java](src/test/java/com/example/poochpalace/PoochPalaceApplicationTests.java)
- Spring Boot Test starter included; Mockito extensions available

## Common Issues
- **OpenAI API Key Missing**: Export `OPENAI_API_KEY` before running
- **MCP Connection Fails**: Ensure external MCP server is running on port 8081
- **Empty Dog Results**: DataInitializer runs on startup; verify H2 database isn't corrupted (check `/h2-console`)
