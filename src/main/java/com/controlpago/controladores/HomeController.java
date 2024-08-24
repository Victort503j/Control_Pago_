package com.controlpago.controladores;

import com.controlpago.servicios.interfaces.IAlumnoService;
import com.controlpago.servicios.interfaces.IGradoService;
import com.controlpago.servicios.interfaces.IPagoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private IAlumnoService alumnoService;

    @Autowired
    private IGradoService gradoService;

    @Autowired
    private IPagoService pagoService;

    @GetMapping
    public String index() {
        return "home/index";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "home/formLogin";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, null, null);
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalAlumnos = alumnoService.contarTotalAlumnos();
        long totalGrados = gradoService.contarTotalGrados();
        long totalPagos = pagoService.contarTotalPagos();

        model.addAttribute("totalAlumnos", totalAlumnos);
        model.addAttribute("totalGrados", totalGrados);
        model.addAttribute("totalPagos", totalPagos);

        return "home/dashboard";
    }
}
