package com.fix.fixnow;

import com.fix.fixnow.model.Role;
import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.model.Technician;
import com.fix.fixnow.model.User;
import com.fix.fixnow.repository.MessageRepo;
import com.fix.fixnow.repository.ReviewRepo;
import com.fix.fixnow.repository.ServiceRequestRepo;
import com.fix.fixnow.repository.TechnicianRepo;
import com.fix.fixnow.repository.UserRepo;
import com.fix.fixnow.security.SessionAuthConstants;
import com.fix.fixnow.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FixnowApplicationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TechnicianRepo technicianRepo;

    @Autowired
    private ServiceRequestRepo serviceRequestRepo;

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void cleanDatabase() {
        messageRepo.deleteAll();
        reviewRepo.deleteAll();
        serviceRequestRepo.deleteAll();
        technicianRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void registerHashesPasswordAndLoginAcceptsRawPassword() {
        User saved = createUser("Secure User", "secure.user@example.com", Role.CUSTOMER);

        assertNotEquals("plain123", saved.getPassword());
        assertTrue(saved.getPassword().startsWith("$2"));
        assertTrue(authService.login("secure.user@example.com", "plain123").isPresent());
        assertTrue(authService.login("secure.user@example.com", "wrong-password").isEmpty());
    }

    @Test
    void publicRegistrationRedirectsToLoginAndRejectsAdmin() throws Exception {
        mockMvc.perform(post("/register")
                        .param("name", "Public Customer")
                        .param("email", "public.customer@example.com")
                        .param("phone", "+201000000010")
                        .param("password", "plain123")
                        .param("role", "CUSTOMER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?success=registered"));

        assertTrue(userRepo.findByEmail("public.customer@example.com").isPresent());

        mockMvc.perform(post("/register")
                        .param("name", "Public Admin")
                        .param("email", "public.admin@example.com")
                        .param("phone", "+201000000011")
                        .param("password", "plain123")
                        .param("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register?error=role_not_allowed"));

        assertTrue(userRepo.findByEmail("public.admin@example.com").isEmpty());
    }

    @Test
    void registerPageUsesRoleFirstCustomerAndTechnicianForms() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Customer account")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Technician account")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("What is your profession?")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Short work description")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("value=\"ADMIN\""))));
    }

    @Test
    void technicianRegistrationCreatesTechnicianProfile() throws Exception {
        mockMvc.perform(post("/register")
                        .param("name", "New Technician")
                        .param("email", "new.tech@example.com")
                        .param("phone", "+201000000012")
                        .param("password", "plain123")
                        .param("role", "TECHNICIAN")
                        .param("skill", "AC Repair")
                        .param("description", "I repair split AC units and cooling problems."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?success=registered"));

        User techUser = userRepo.findByEmail("new.tech@example.com").orElseThrow();
        Technician technician = technicianRepo.findByUser_Id(techUser.getId()).orElseThrow();

        assertEquals("New Technician", technician.getName());
        assertEquals("AC Repair", technician.getSkill());
        assertEquals("I repair split AC units and cooling problems.", technician.getDescription());
        assertTrue(technician.isAvailable());
    }

    @Test
    void protectedPagesRedirectAndApiReturnsJsonAuthErrors() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/api/chat/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401));

        User admin = createUser("Admin User", "admin@example.com", Role.ADMIN);
        User customer = createUser("Customer User", "customer@example.com", Role.CUSTOMER);

        mockMvc.perform(get("/admin/dashboard").session(sessionFor(admin)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/admin/dashboard").session(sessionFor(customer)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void dashboardPagesRenderForEachRole() throws Exception {
        User admin = createUser("Admin User", "admin.dashboard@example.com", Role.ADMIN);
        User customer = createUser("Customer User", "customer.dashboard@example.com", Role.CUSTOMER);
        User technicianUser = createUser("Technician User", "tech.dashboard@example.com", Role.TECHNICIAN);

        mockMvc.perform(get("/admin/dashboard").session(sessionFor(admin)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/customer/dashboard").session(sessionFor(customer)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/technician/dashboard").session(sessionFor(technicianUser)))
                .andExpect(status().isOk());
    }

    @Test
    void customerCanCreateAndViewRequest() throws Exception {
        User customer = createUser("Request Customer", "request.customer@example.com", Role.CUSTOMER);
        MockHttpSession customerSession = sessionFor(customer);

        mockMvc.perform(post("/customer/request/new")
                        .session(customerSession)
                        .param("category", "PLUMBING")
                        .param("location", "Cairo")
                        .param("urgency", "URGENT")
                        .param("description", "Kitchen sink leak"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customer/dashboard?success=request_created"));

        List<ServiceRequest> requests = serviceRequestRepo.findByUser_Id(customer.getId());
        assertEquals(1, requests.size());
        assertEquals(ServiceRequest.PENDING, requests.get(0).getStatus());

        mockMvc.perform(get("/customer/request/{id}", requests.get(0).getId()).session(customerSession))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("PENDING -> ASSIGNED -> ACCEPTED -> COMPLETED")));
    }

    @Test
    void customerRequestAutoMatchesAvailableTechnicianBySkill() throws Exception {
        User customer = createUser("Auto Match Customer", "auto.match.customer@example.com", Role.CUSTOMER);
        User plumberUser = createUser("Auto Match Plumber", "auto.match.plumber@example.com", Role.TECHNICIAN);
        Technician plumber = technicianRepo.findByUser_Id(plumberUser.getId()).orElseThrow();
        plumber.setSkill("Plumbing");
        technicianRepo.save(plumber);

        mockMvc.perform(post("/customer/request/new")
                        .session(sessionFor(customer))
                        .param("category", "PLUMBING")
                        .param("location", "Cairo")
                        .param("urgency", "URGENT")
                        .param("description", "Kitchen sink leak"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customer/dashboard?success=request_created"));

        ServiceRequest request = serviceRequestRepo.findByUser_Id(customer.getId()).get(0);
        assertEquals(ServiceRequest.ASSIGNED, request.getStatus());
        assertEquals(plumber.getId(), request.getTechnician().getId());
        assertFalse(technicianRepo.findById(plumber.getId()).orElseThrow().isAvailable());
    }

    @Test
    void technicianAcceptsAndCompletesOnlyWhenAvailable() throws Exception {
        User customer = createUser("Tech Flow Customer", "tech.flow.customer@example.com", Role.CUSTOMER);
        User technicianUser = createUser("Tech Flow Worker", "tech.flow.worker@example.com", Role.TECHNICIAN);
        Technician technician = technicianRepo.findByUser_Id(technicianUser.getId()).orElseThrow();
        technician.setSkill("Plumbing");
        technicianRepo.save(technician);
        ServiceRequest firstRequest = createRequest(customer, null, ServiceRequest.PENDING);
        ServiceRequest secondRequest = createRequest(customer, null, ServiceRequest.PENDING);

        mockMvc.perform(post("/technician/request/{id}/accept", firstRequest.getId()).session(sessionFor(technicianUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/technician/dashboard?success=request_accepted"));

        ServiceRequest accepted = serviceRequestRepo.findById(firstRequest.getId()).orElseThrow();
        assertEquals(ServiceRequest.ACCEPTED, accepted.getStatus());
        assertEquals(technician.getId(), accepted.getTechnician().getId());
        assertFalse(technicianRepo.findById(technician.getId()).orElseThrow().isAvailable());

        mockMvc.perform(post("/technician/request/{id}/accept", secondRequest.getId()).session(sessionFor(technicianUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/technician/dashboard?error=accept_failed"));

        mockMvc.perform(post("/technician/request/{id}/complete", firstRequest.getId()).session(sessionFor(technicianUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/technician/dashboard?success=request_completed"));

        ServiceRequest completed = serviceRequestRepo.findById(firstRequest.getId()).orElseThrow();
        assertEquals(ServiceRequest.COMPLETED, completed.getStatus());
        assertTrue(technicianRepo.findById(technician.getId()).orElseThrow().isAvailable());
    }

    @Test
    void adminAssignsOnlyAvailableTechnicians() throws Exception {
        User admin = createUser("Dispatch Admin", "dispatch.admin@example.com", Role.ADMIN);
        User customer = createUser("Dispatch Customer", "dispatch.customer@example.com", Role.CUSTOMER);
        User technicianUser = createUser("Dispatch Technician", "dispatch.tech@example.com", Role.TECHNICIAN);
        Technician technician = technicianRepo.findByUser_Id(technicianUser.getId()).orElseThrow();
        technician.setSkill("Plumbing");
        technicianRepo.save(technician);
        ServiceRequest request = createRequest(customer, null, ServiceRequest.PENDING);

        technician.setAvailable(false);
        technicianRepo.save(technician);

        mockMvc.perform(post("/admin/request/{requestId}/assign/{technicianId}", request.getId(), technician.getId()).session(sessionFor(admin)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard?error=assign_failed"));

        assertNull(serviceRequestRepo.findById(request.getId()).orElseThrow().getTechnician());

        technician.setAvailable(true);
        technicianRepo.save(technician);

        mockMvc.perform(post("/admin/request/{requestId}/assign/{technicianId}", request.getId(), technician.getId()).session(sessionFor(admin)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard?success=technician_assigned"));

        ServiceRequest assigned = serviceRequestRepo.findById(request.getId()).orElseThrow();
        assertEquals(ServiceRequest.ASSIGNED, assigned.getStatus());
        assertEquals(technician.getId(), assigned.getTechnician().getId());
        assertFalse(technicianRepo.findById(technician.getId()).orElseThrow().isAvailable());
    }

    @Test
    void adminAndTechnicianCannotBypassSkillMatching() throws Exception {
        User admin = createUser("Mismatch Admin", "mismatch.admin@example.com", Role.ADMIN);
        User customer = createUser("Mismatch Customer", "mismatch.customer@example.com", Role.CUSTOMER);
        User technicianUser = createUser("Mismatch Electrician", "mismatch.tech@example.com", Role.TECHNICIAN);
        Technician technician = technicianRepo.findByUser_Id(technicianUser.getId()).orElseThrow();
        technician.setSkill("Electricity");
        technicianRepo.save(technician);
        ServiceRequest request = createRequest(customer, null, ServiceRequest.PENDING);

        mockMvc.perform(post("/admin/request/{requestId}/assign/{technicianId}", request.getId(), technician.getId()).session(sessionFor(admin)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard?error=assign_failed"));

        mockMvc.perform(post("/technician/request/{id}/accept", request.getId()).session(sessionFor(technicianUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/technician/dashboard?error=accept_failed"));

        ServiceRequest unchanged = serviceRequestRepo.findById(request.getId()).orElseThrow();
        assertEquals(ServiceRequest.PENDING, unchanged.getStatus());
        assertNull(unchanged.getTechnician());
    }

    @Test
    void technicianAvailabilityToggleIsBlockedByActiveJobs() throws Exception {
        User customer = createUser("Availability Customer", "availability.customer@example.com", Role.CUSTOMER);
        User technicianUser = createUser("Availability Tech", "availability.tech@example.com", Role.TECHNICIAN);
        Technician technician = technicianRepo.findByUser_Id(technicianUser.getId()).orElseThrow();
        technician.setSkill("Plumbing");
        technician.setAvailable(false);
        technicianRepo.save(technician);

        mockMvc.perform(post("/technician/availability")
                        .session(sessionFor(technicianUser))
                        .param("available", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/technician/dashboard?success=availability_updated"));

        assertTrue(technicianRepo.findById(technician.getId()).orElseThrow().isAvailable());

        ServiceRequest assigned = createRequest(customer, technicianRepo.findById(technician.getId()).orElseThrow(), ServiceRequest.ASSIGNED);
        technician = technicianRepo.findById(technician.getId()).orElseThrow();
        technician.setAvailable(false);
        technicianRepo.save(technician);

        mockMvc.perform(post("/technician/availability")
                        .session(sessionFor(technicianUser))
                        .param("available", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/technician/dashboard?error=availability_failed"));

        assertFalse(technicianRepo.findById(technician.getId()).orElseThrow().isAvailable());

        mockMvc.perform(post("/technician/request/{id}/accept", assigned.getId()).session(sessionFor(technicianUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/technician/dashboard?success=request_accepted"));

        mockMvc.perform(post("/technician/request/{id}/complete", assigned.getId()).session(sessionFor(technicianUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/technician/dashboard?success=request_completed"));

        assertTrue(technicianRepo.findById(technician.getId()).orElseThrow().isAvailable());
    }

    @Test
    void reviewRequiresCompletedRequestAndIsOnePerRequest() throws Exception {
        User customer = createUser("Review Customer", "review.customer@example.com", Role.CUSTOMER);
        User technicianUser = createUser("Review Technician", "review.tech@example.com", Role.TECHNICIAN);
        Technician technician = technicianRepo.findByUser_Id(technicianUser.getId()).orElseThrow();
        ServiceRequest request = createRequest(customer, technician, ServiceRequest.ACCEPTED);
        MockHttpSession customerSession = sessionFor(customer);

        mockMvc.perform(post("/customer/review/new")
                        .session(customerSession)
                        .param("requestId", request.getId().toString())
                        .param("rating", "5")
                        .param("comment", "Great work"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customer/dashboard?error=review_not_available"));

        assertEquals(0, reviewRepo.count());

        request.setStatus(ServiceRequest.COMPLETED);
        serviceRequestRepo.save(request);

        mockMvc.perform(get("/customer/review/new")
                        .session(customerSession)
                        .param("requestId", request.getId().toString()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/customer/review/new")
                        .session(customerSession)
                        .param("requestId", request.getId().toString())
                        .param("rating", "5")
                        .param("comment", "Great work"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customer/dashboard?success=review_submitted"));

        assertEquals(1, reviewRepo.count());
        assertEquals(5.0, technicianRepo.findById(technician.getId()).orElseThrow().getRating());

        mockMvc.perform(post("/customer/review/new")
                        .session(customerSession)
                        .param("requestId", request.getId().toString())
                        .param("rating", "4")
                        .param("comment", "Second review"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customer/dashboard?error=review_not_available"));

        assertEquals(1, reviewRepo.count());
    }

    @Test
    void chatUsesSessionSenderReturnsDtoAndBlocksNonParticipants() throws Exception {
        User customer = createUser("Chat Customer", "chat.customer@example.com", Role.CUSTOMER);
        User technicianUser = createUser("Chat Technician", "chat.tech@example.com", Role.TECHNICIAN);
        User outsider = createUser("Chat Outsider", "chat.outsider@example.com", Role.CUSTOMER);
        Technician technician = technicianRepo.findByUser_Id(technicianUser.getId()).orElseThrow();
        ServiceRequest request = createRequest(customer, technician, ServiceRequest.ASSIGNED);

        String payload = "{\"senderId\":" + outsider.getId() +
                ",\"receiverId\":" + technicianUser.getId() +
                ",\"requestId\":" + request.getId() +
                ",\"content\":\"Hello\"}";

        String responseBody = mockMvc.perform(post("/api/chat/send")
                        .session(sessionFor(customer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senderId").value(customer.getId()))
                .andExpect(jsonPath("$.receiverId").value(technicianUser.getId()))
                .andExpect(jsonPath("$.content").value("Hello"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertFalse(responseBody.contains("password"));
        assertFalse(responseBody.contains(customer.getPassword()));

        mockMvc.perform(get("/api/chat/{requestId}", request.getId()).session(sessionFor(outsider)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(403));

        mockMvc.perform(get("/api/chat/{requestId}", request.getId()).session(sessionFor(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello"))
                .andExpect(jsonPath("$[0].senderName").value("Chat Customer"));
    }

    private User createUser(String name, String email, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone("+201000000000");
        user.setPassword("plain123");
        user.setRole(role);
        return authService.register(user);
    }

    private ServiceRequest createRequest(User customer, Technician technician, String status) {
        ServiceRequest request = new ServiceRequest();
        request.setDescription("Test service request");
        request.setCategory("PLUMBING");
        request.setLocation("Cairo");
        request.setUrgency("NORMAL");
        request.setStatus(status);
        request.setUser(customer);
        request.setTechnician(technician);
        return serviceRequestRepo.save(request);
    }

    private MockHttpSession sessionFor(User user) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionAuthConstants.AUTH_USER_ID, user.getId());
        session.setAttribute(SessionAuthConstants.AUTH_EMAIL, user.getEmail());
        session.setAttribute(SessionAuthConstants.AUTH_ROLE, user.getRole().name());
        session.setAttribute(SessionAuthConstants.AUTH_NAME, user.getName());
        return session;
    }
}
