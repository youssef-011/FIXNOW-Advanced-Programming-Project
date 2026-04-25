package com.fix.fixnow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fix.fixnow.model.Role;
import com.fix.fixnow.model.User;
import com.fix.fixnow.repository.UserRepo;
import com.fix.fixnow.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FixnowApplicationTests {

	@Autowired
	private AuthService authService;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void registerHashesPasswordAndLoginAcceptsRawPassword() {
		userRepo.deleteAll();

		User user = new User();
		user.setName("Secure User");
		user.setEmail("secure.user@example.com");
		user.setPassword("plain123");
		user.setRole(Role.CUSTOMER);

		User saved = authService.register(user);

		assertNotEquals("plain123", saved.getPassword());
		assertTrue(saved.getPassword().startsWith("$2"));
		assertTrue(authService.login("secure.user@example.com", "plain123").isPresent());
		assertTrue(authService.login("secure.user@example.com", "wrong-password").isEmpty());
	}

	@Test
	void protectedEndpointRequiresSessionAndRole() throws Exception {
		userRepo.deleteAll();

		mockMvc.perform(get("/api/admin/requests"))
				.andExpect(status().isUnauthorized());

		String registerBody = objectMapper.writeValueAsString(Map.of(
				"name", "Admin User",
				"email", "admin@example.com",
				"password", "plain123",
				"role", "ADMIN"
		));

		MockHttpSession adminSession = (MockHttpSession) mockMvc.perform(post("/api/auth/register")
						.contentType("application/json")
						.content(registerBody))
				.andExpect(status().isCreated())
				.andReturn()
				.getRequest()
				.getSession(false);

		assertNotNull(adminSession);

		mockMvc.perform(get("/api/admin/requests").session(adminSession))
				.andExpect(status().isOk());

		String customerBody = objectMapper.writeValueAsString(Map.of(
				"name", "Customer User",
				"email", "customer@example.com",
				"password", "plain123",
				"role", "CUSTOMER"
		));

		MockHttpSession customerSession = (MockHttpSession) mockMvc.perform(post("/api/auth/register")
						.contentType("application/json")
						.content(customerBody))
				.andExpect(status().isCreated())
				.andReturn()
				.getRequest()
				.getSession(false);

		mockMvc.perform(get("/api/admin/requests").session(customerSession))
				.andExpect(status().isForbidden());
	}

	@Test
	void duplicateEmailIsRejected() throws Exception {
		userRepo.deleteAll();

		String body = objectMapper.writeValueAsString(Map.of(
				"name", "Repeated User",
				"email", "repeat@example.com",
				"password", "plain123",
				"role", "CUSTOMER"
		));

		mockMvc.perform(post("/api/auth/register")
						.contentType("application/json")
						.content(body))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/auth/register")
						.contentType("application/json")
						.content(body))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Email already exists"));
	}

}
