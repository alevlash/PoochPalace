package com.example.poochpalace;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DogRepository dogRepository;

    public DataInitializer(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Clear any existing data
        dogRepository.deleteAll();

        // Create test dogs
        Dog dog1 = new Dog(1, "Max", "John Smith", "A friendly golden retriever who loves to play fetch and swim");
        Dog dog2 = new Dog(2, "Luna", "Sarah Johnson", "A beautiful husky with striking blue eyes, great with families");
        Dog dog3 = new Dog(3, "Charlie", "Mike Brown", "An energetic beagle puppy, very playful and affectionate");
        Dog dog4 = new Dog(4, "Bella", "Emily Davis", "A gentle German Shepherd, excellent at obedience training");
        Dog dog5 = new Dog(5, "Rocky", "James Wilson", "A strong boxer mix, loves outdoor activities and hiking");
        Dog dog6 = new Dog(6, "Daisy", "Lisa Anderson", "A sweet Cocker Spaniel, perfect companion for seniors");
        Dog dog7 = new Dog(7, "Cooper", "David Martinez", "A charming Labrador retriever, great therapy dog potential");
        Dog dog8 = new Dog(8, "Milo", "Jessica Taylor", "A small Dachshund mix, apartment-friendly and loyal");

        // Save all dogs to the database
        dogRepository.saveAll(Arrays.asList(dog1, dog2, dog3, dog4, dog5, dog6, dog7, dog8));

        System.out.println("✓ Database initialized with " + dogRepository.count() + " dogs");
    }
}
