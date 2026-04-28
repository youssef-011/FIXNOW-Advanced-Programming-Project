import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.security.SessionAuthConstants;
import com.fix.fixnow.service.TechnicianService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/technician")
public class TechnicianController {

    private final TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        List<ServiceRequest> availableRequests = technicianService.getAvailableRequests();
        model.addAttribute("availableRequests", availableRequests);
        model.addAttribute("availableRequestsCount", availableRequests.size());

        Object technicianName = session.getAttribute(SessionAuthConstants.AUTH_NAME);
        if (technicianName != null) {
            model.addAttribute("technicianName", technicianName);
        }

        return "technicianDashboard";
    }

    @PostMapping("/request/{id}/accept")
    public String acceptRequest(@PathVariable("id") Long requestId, HttpSession session) {
        Long technicianId = currentTechnicianId(session);
        if (technicianId == null) {
            return "redirect:/login";
        }

        technicianService.acceptRequest(requestId, technicianId);
        return "redirect:/technician/dashboard";
    }

    @PostMapping("/request/{id}/complete")
    public String completeRequest(@PathVariable("id") Long requestId, HttpSession session) {
        Long technicianId = currentTechnicianId(session);
        if (technicianId == null) {
            return "redirect:/login";
        }

        technicianService.completeRequest(requestId, technicianId);
        return "redirect:/technician/dashboard";
    }

    private Long currentTechnicianId(HttpSession session) {
        Object technicianId = session.getAttribute(SessionAuthConstants.AUTH_USER_ID);
        if (technicianId instanceof Long id) {
            return id;
        }
        if (technicianId instanceof Number number) {
            return number.longValue();
        }
        return null;
    }
}
