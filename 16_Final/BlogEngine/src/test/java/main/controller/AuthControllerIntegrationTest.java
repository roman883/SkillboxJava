package main.controller;

import main.TestUtils;
import main.api.request.RegisterRequest;
import main.model.entities.CaptchaCode;
import main.services.interfaces.CaptchaRepositoryService;
import main.services.interfaces.UserRepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepositoryService userRepoService;
    @Autowired
    private CaptchaRepositoryService captchaRepoService;
    @Autowired
    private ApiAuthController apiAuthController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    public void testLogout() {
//        // TODO: Как протестировать? нужна сессия и чтобы она была запомнена в userRepository
//
//    }

    @Test
    public void testRegister_allIsOk() throws Exception {
        // Prepare
        captchaRepoService.generateCaptcha();
        CaptchaCode captchaCode = captchaRepoService.getAllCaptchas().stream().findFirst().get();
        RegisterRequest request = new RegisterRequest("someExample" + (int) (Math.random() * 100000) + "@mail.ru",
                "password", captchaCode.getCode(), captchaCode.getSecretCode(), "");
        // Execute
        mockMvc.perform(
                post("/api/auth/register").contentType(TestUtils.APPLICATION_JSON_UTF8).
                        content(TestUtils.convertObjectToJsonBytes(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}