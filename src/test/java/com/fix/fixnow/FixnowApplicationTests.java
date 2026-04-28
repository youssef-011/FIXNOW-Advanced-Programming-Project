package com.fix.fixnow;

import com.fix.fixnow.model.Role;
import com.fix.fixnow.model.User;
import com.fix.fixnow.repository.UserRepo;
import com.fix.fixnow.security.SessionAuthConstants;
import com.fix.fixnow.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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

		mockMvc.perform(get("/admin/dashboard"))
				.andExpect(status().isUnauthorized());

		MockHttpSession adminSession = (MockHttpSession) mockMvc.perform(post("/register")
						.session(authenticatedSession(Role.ADMIN))
						.param("name", "Admin User")
						.param("email", "admin@example.com")
						.param("phone", "+201000000001")
						.param("password", "plain123")
						.param("role", "ADMIN"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/dashboard"))
				.andReturn()
				.getRequest()
				.getSession(false);

		assertNotNull(adminSession);

		mockMvc.perform(get("/admin/dashboard").session(adminSession))
				.andExpect(status().isOk());

		MockHttpSession customerSession = (MockHttpSession) mockMvc.perform(post("/register")
						.session(authenticatedSession(Role.CUSTOMER))
						.param("name", "Customer User")
						.param("email", "customer@example.com")
						.param("phone", "+201000000002")
						.param("password", "plain123")
						.param("role", "CUSTOMER"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/customer/dashboard"))
				.andReturn()
				.getRequest()
				.getSession(false);

		mockMvc.perform(get("/admin/dashboard").session(customerSession))
				.andExpect(status().isForbidden());
	}

	@Test
	void duplicateEmailIsRejected() throws Exception {
		userRepo.deleteAll();

		mockMvc.perform(post("/register")
						.session(authenticatedSession(Role.CUSTOMER))
						.param("name", "Repeated User")
						.param("email", "repeat@example.com")
						.param("phone", "+201000000003")
						.param("password", "plain123")
						.param("role", "CUSTOMER"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/customer/dashboard"));

		mockMvc.perform(post("/register")
						.session(authenticatedSession(Role.CUSTOMER))
						.param("name", "Repeated User")
						.param("email", "repeat@example.com")
						.param("phone", "+201000000003")
						.param("password", "plain123")
						.param("role", "CUSTOMER"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/register?error"));
	}

	@Test
	void invalidLoginRedirectsToError() throws Exception {
		userRepo.deleteAll();

		mockMvc.perform(post("/login")
						.session(authenticatedSession(Role.CUSTOMER))
						.param("email", "missing@example.com")
						.param("password", "wrong-password"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?error"));
	}

	private MockHttpSession authenticatedSession(Role role) {
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SessionAuthConstants.AUTH_EMAIL, role.name().toLowerCase() + "@test.local");
		session.setAttribute(SessionAuthConstants.AUTH_ROLE, role.name());
		return session;
	}

}
