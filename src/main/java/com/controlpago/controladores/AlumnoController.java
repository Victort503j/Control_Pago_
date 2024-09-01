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
            @RequestParam("nombre") Optional<String> nombre,
            @RequestParam("apellido") Optional<String> apellido,
            @RequestParam("grado") Optional<Integer> gradoId,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            Model model) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());

        Page<Alumno> alumnos = alumnoService.buscarPorCriterios(
                nombre.orElse(""), apellido.orElse(""), gradoId.orElse(null), pageable);
        model.addAttribute("alumnos", alumnos);

        int totalPage = alumnos.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pageNumber = IntStream.rangeClosed(1, totalPage)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumber);
        }
        List<Grado> grados = gradoService.obtenerTodos();
        model.addAttribute("grados", grados);
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
