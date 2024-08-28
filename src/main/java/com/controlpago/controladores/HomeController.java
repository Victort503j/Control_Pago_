package com.controlpago.controladores;

import com.controlpago.servicios.interfaces.IAlumnoService;
import com.controlpago.servicios.interfaces.IGradoService;
import com.controlpago.servicios.interfaces.IPagoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

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
        return "home/formLogin";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String && auth.getPrincipal().equals("anonymousUser"))) {
            // Redirige a la página principal si ya está autenticado
            return "redirect:/dashboard";
        }

        // Si no está autenticado, muestra la página de inicio de sesión
        //return "login";
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

        // Obtener el rango de fechas del mes actual
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        long alumnosQueHanPagado = pagoService.contarAlumnosQueHanPagado(inicioMes, finMes);
        long alumnosQueNoHanPagado = pagoService.contarAlumnosQueNoHanPagado(inicioMes, finMes);

        model.addAttribute("totalAlumnos", totalAlumnos);
        model.addAttribute("totalGrados", totalGrados);
        model.addAttribute("totalPagos", totalPagos);
        model.addAttribute("alumnosQueHanPagado", alumnosQueHanPagado);
        model.addAttribute("alumnosQueNoHanPagado", alumnosQueNoHanPagado);

        // Información sobre los alumnos que han pagado segun el grado

        List<Object[]> pagosPorGrado = pagoService.contarAlumnosQueHanPagadoPorGrado(inicioMes, finMes);
        List<Object[]> noPagosPorGrado = pagoService.contarAlumnosQueNoHanPagadoPorGrado(inicioMes, finMes);

        model.addAttribute("pagosPorGrado", pagosPorGrado);
        model.addAttribute("noPagosPorGrado", noPagosPorGrado);


        return "home/dashboard";
    }

}