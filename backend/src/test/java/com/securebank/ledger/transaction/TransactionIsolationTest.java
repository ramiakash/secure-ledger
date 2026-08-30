package com.securebank.ledger.transaction;

import com.securebank.ledger.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransactionIsolationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String loginAs(String username) throws Exception {
        String body = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"Password123!"}
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return body.split("\"accessToken\":\"")[1].split("\"")[0];
    }

    @Test
    void aUserCannotSeeAnotherUsersTransactions() throws Exception {
        String aliceToken = loginAs("alice");
        String bobToken = loginAs("bob");

        mockMvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 1250.75,
                                  "currency": "EUR",
                                  "description": "Alice private invoice",
                                  "counterpartyIban": "DE89370400440532013000"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/transactions")
                        .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        mockMvc.perform(get("/api/v1/transactions")
                        .header("Authorization", "Bearer " + bobToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void unauthenticatedRequestsAreRejected() throws Exception {
        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isUnauthorized());
    }
}