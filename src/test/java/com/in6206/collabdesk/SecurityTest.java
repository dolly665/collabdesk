package com.in6206.collabdesk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Checks the URL / role rules in SecurityConfig and the JSON error format of the API. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityTest {

    @Autowired MockMvc mvc;

    @Test
    void anonymousBrowserIsSentToLogin() throws Exception {
        // browsers send Accept: text/html -> form login entry point -> redirect
        mvc.perform(get("/creator/dashboard").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void anonymousApiClientGets401() throws Exception {
        // API clients (Postman, scripts) get 401 + WWW-Authenticate instead of an HTML redirect
        mvc.perform(get("/api/deals"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists("WWW-Authenticate"));
    }

    @Test
    @WithUserDetails("creator_ken")
    void creatorCannotOpenAdminPages() throws Exception {
        mvc.perform(get("/admin/dashboard")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("creator_ken")
    void creatorSeesOwnDashboard() throws Exception {
        mvc.perform(get("/creator/dashboard")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("brand_bubbletea")
    void brandCannotCounterOffer() throws Exception {
        mvc.perform(post("/api/deals/1/counter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 999}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("creator_ken")
    void illegalPublishReturns409WithMessage() throws Exception {
        String dealId = "3";   // Ken's deal whose script is still in review (see DataSeeder)
        mvc.perform(post("/api/deals/" + dealId + "/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"https://youtube.com/x\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }
}
