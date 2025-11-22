package br.com.fiap.api;

import com.google.genai.Client;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GeminiClientProducer {

    @Produces
    @Singleton
    // O Quarkus injeta a chave
    public Client createClient(@ConfigProperty(name = "GOOGLE_API_KEY") String apiKey) {

        // garantir que não haja aspas ou espaços.
        String cleanedApiKey = apiKey.replace("\"", "").trim();

        if (cleanedApiKey.isEmpty()) {
            throw new IllegalStateException("A propriedade GOOGLE_API_KEY não foi configurada corretamente ou está vazia.");
        }

        // padrão Builder para inicializar o cliente com a chave.
        return Client.builder()
                .apiKey(cleanedApiKey)
                .build();
    }
}