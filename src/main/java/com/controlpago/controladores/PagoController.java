package com.controlpago.controladores;

import com.controlpago.modelos.Alumno;
import com.controlpago.modelos.Grado;
import com.controlpago.modelos.Pago;
import com.controlpago.servicios.interfaces.IAlumnoService;
import com.controlpago.servicios.interfaces.IGradoService;
import com.controlpago.servicios.interfaces.IPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/pagos")
public class PagoController {
    @Autowired
    private IPagoService pagoService;

    @Autowired
    private IAlumnoService alumnoService;

    @Autowired
    private IGradoService gradoService;

    @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());

        Page<Pago> pagos = pagoService.buscarTodosPaginados(pageable);
        model.addAttribute("pagos", pagos);

        List<Alumno> alumnos = alumnoService.obtenerTodos();
        model.addAttribute("alumnos", alumnos);

        List<Grado> grados = gradoService.obtenerTodos();
        model.addAttribute("grados", grados);

        int totalPage = pagos.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPage)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "pago/index";
    }

    @GetMapping("/search")
    public String search(@RequestParam("alumno") Optional<String> nombreCompleto,
                         @RequestParam("fecha") Optional<String> fecha,
                         @RequestParam("page") Optional<Integer> page,
                         @RequestParam("size") Optional<Integer> size,
                         Model model) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());

        Page<Pago> pagos = pagoService.buscarPagosPorNombreCompleto(
                nombreCompleto.orElse(null), pageable);

        model.addAttribute("pagos", pagos);
        List<Alumno> alumnos = alumnoService.obtenerTodos();
        model.addAttribute("alumnos", alumnos);

        int totalPage = pagos.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPage)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "pago/index";
    }



    @GetMapping("/create")
    public String create(Model model){
        List<Alumno> alumnos = alumnoService.obtenerTodos();
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("pago", new Pago());
        return "pago/create";
    }
    @PostMapping("/save")
    public String guardar(@ModelAttribute Pago pPago, BindingResult result, Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("pago", pPago);
            return "pago/create";
        }

        Long alumnoId = pPago.getAlumno().getId().longValue();
        Pago pagoExistente = pagoService.buscarPorAlumnoYMes(alumnoId, pPago.getFecha());
        if (pagoExistente != null) {
            result.rejectValue("fecha", "error.pago", "Este alumno ya ha realizado un pago en el mes seleccionado.");
            model.addAttribute("alumnos", alumnoService.obtenerTodos());
            return "pago/create";
        }

        pagoService.crearOEditar(pPago);
        attributes.addFlashAttribute("msg", "Pago guardado exitosamente");
        return "redirect:/pagos";
    }
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model){
        Pago pago = pagoService.buscarPorId(id).orElse(null);
        if (pago == null) {
            return "redirect:/pagos";
        }

        List<Pago> pagosDelAlumno = pagoService.buscarPagosPorAlumno(pago.getAlumno().getId().intValue());
        BigDecimal totalPagado = pagosDelAlumno.stream()
                .map(Pago::getCantidadPagar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("pago", pago);
        model.addAttribute("pagosDelAlumno", pagosDelAlumno);
        model.addAttribute("totalPagado", totalPagado);
        return "pago/details";
    }

}
