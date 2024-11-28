package com.controlpago.controladores;

import com.controlpago.modelos.Alumno;
import com.controlpago.modelos.Grado;
import com.controlpago.servicios.interfaces.IAlumnoService;
import com.controlpago.servicios.interfaces.IGradoService;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/alumnos")
public class AlumnoController {
    @Autowired
    private IAlumnoService alumnoService;
    @Autowired
    private IGradoService gradoService;
    @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("id").descending());

        Page<Alumno> alumnos = alumnoService.buscarTodosPaginados(pageable);
        model.addAttribute("alumnos", alumnos);

        int totalPages = alumnos.getTotalPages();
        if (totalPages > 0) {
            int startPage = Math.max(1, currentPage - 2);
            int endPage = Math.min(totalPages, currentPage + 2);

            List<Integer> pageNumber = IntStream.rangeClosed(startPage, endPage)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumber);
        }
        List<Grado> grados = gradoService.obtenerTodos();
        model.addAttribute("grados", grados);

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        return "alumno/index";
    }
    @GetMapping("/search")
    public String search(
            @RequestParam(value = "nombreCompleto", required = false) String nombreCompleto,
            @RequestParam(value = "grado", required = false) Integer gradoId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        // Ajustar el número de la página (páginas empiezan desde 0)
        int currentPage = page - 1;
        Pageable pageable = PageRequest.of(currentPage, size, Sort.by("id").descending());

        // Buscar alumnos según los criterios de búsqueda
        Page<Alumno> alumnos = alumnoService.buscarPorNombreCompletoYGrado(nombreCompleto, gradoId, pageable);
        model.addAttribute("alumnos", alumnos);

        // Cargar todos los alumnos para el <select>
        List<Alumno> todosLosAlumnos = alumnoService.obtenerTodos();
        model.addAttribute("todosLosAlumnos", todosLosAlumnos);

        // Calcular los números de páginas para mostrar en la paginación
        int totalPages = alumnos.getTotalPages();
        if (totalPages > 0) {
            int startPage = Math.max(1, currentPage - 2);
            int endPage = Math.min(totalPages, currentPage + 2);

            List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        // Agregar el número de página actual y el total de páginas
        model.addAttribute("currentPage", currentPage + 1);
        model.addAttribute("totalPages", totalPages);

        // Obtener todos los grados para mostrarlos en el filtro
        List<Grado> grados = gradoService.obtenerTodos();
        model.addAttribute("grados", grados);

        // Mantener los valores seleccionados
        model.addAttribute("selectedNombreCompleto", nombreCompleto);
        model.addAttribute("selectedGradoId", gradoId);

        return "alumno/index";
    }



    @GetMapping("/create")
    public String create(Model model) {
        List<Grado> grados = gradoService.obtenerTodos();
        model.addAttribute("alumno", new Alumno());
        model.addAttribute("grados", grados);
        return "alumno/create";
    }

    @PostMapping("/save")
    public String guardar(@ModelAttribute Alumno pAlumno, BindingResult result, Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            List<Grado> grados = gradoService.obtenerTodos();
            model.addAttribute("alumno", pAlumno);
            return "alumno/create";
        }
        if (pAlumno.getId() != null && pAlumno.getId() > 0) {
            alumnoService.crearOEditar(pAlumno);
            attributes.addFlashAttribute("msg", "Alumno actualizado exitosamente");
        } else {
            alumnoService.crearOEditar(pAlumno);
            attributes.addFlashAttribute("msg", "Alumno creado exitosamente");
        }
        return "redirect:/alumnos";
    }
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model){
        Alumno alumno = alumnoService.buscarPorId(id).orElse(null);
        model.addAttribute("alumno", alumno);
        return "alumno/details";
    }
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model){
        Alumno alumno = alumnoService.buscarPorId(id).orElse(null);
        List<Grado> grados = gradoService.obtenerTodos();
        model.addAttribute("grados", grados);
        model.addAttribute("alumno", alumno);
        return "alumno/edit";
    }
    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, Model model){
        Alumno alumno = alumnoService.buscarPorId(id).orElse(null);
        model.addAttribute("alumno", alumno);
        return "alumno/delete";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes attributes){
        alumnoService.eliminarPorId(id);
        attributes.addFlashAttribute("msg", "Alumno eliminado exitosamente");
        return "redirect:/alumnos";
    }
}
