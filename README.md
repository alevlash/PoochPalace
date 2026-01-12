This is an implementation of https://www.infoq.com/articles/spring-ai-1-0/?utm_source=email&utm_medium=editorial&utm_campaign=SpecialNL&utm_content=11202025&forceSponsorshipId=772620d3-fb6d-4710-9a7b-d6234d3d92c1

Set your OpenAI API key: export OPENAI_API_KEY=<your OpenAI API key>

Build: ./mvnw clean install
Run: ./mvnw spring-boot:run

MCP server PoochPalaceScheduler is running on 8081 port

Links:
Check health: http://localhost:8080/actuator/health
Ask question: http://localhost:8080/jonathan/assistant?question="Hello, my name is John"
              ..."What is my name?"
              ..."What dogs do you have?"
              ..."Can I adopt Bella?"



