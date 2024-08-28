package com.controlpago.controladores;

import com.controlpago.modelos.Grado;
import com.controlpago.servicios.interfaces.IGradoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
@RequestMapping("/grados")
public class GradoController {

    @Autowired
    private IGradoService gradoService;

    @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("search") Optional<Integer> search) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());

        List<Grado> allGrados = gradoService.obtenerTodos();
        model.addAttribute("allGrados", allGrados);

        Page<Grado> grados;

        if (search.isPresent()) {
            List<Grado> filteredGrados = gradoService.obtenerTodos().stream()
                    .filter(grado -> grado.getId().equals(search.get()))
                    .collect(Collectors.toList());

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), filteredGrados.size());

            grados = new PageImpl<>(filteredGrados.subList(start, end), pageable, filteredGrados.size());
        } else {
            grados = gradoService.buscarTodosPaginados(pageable);
        }

        model.addAttribute("grados", grados);

        int totalPage = grados.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPage)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("search", search.orElse(null));
        return "grado/index";
    }

    @GetMapping("/create")
    public String create(Grado pGrado){
        return "grado/create";
    }

    @PostMapping("/save")
    public String guardar(@ModelAttribute Grado pGrado, BindingResult result, Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("grado", pGrado);
            return "grado/create";
        }
        if (pGrado.getId() != null && pGrado.getId() > 0) {
            gradoService.crearOEditar(pGrado);
            attributes.addFlashAttribute("msg", "Grado actualizado exitosamente");
        } else {
            gradoService.crearOEditar(pGrado);
            attributes.addFlashAttribute("msg", "Grado creado exitosamente");
        }
        return "redirect:/grados";
    }


    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model){
        Grado grado = gradoService.buscarPorId(id).orElse(null);
        model.addAttribute("grado", grado);
        return "grado/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model){
        Grado grado = gradoService.buscarPorId(id).orElse(null);
        model.addAttribute("grado", grado);
        return "grado/edit";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, Model model){
        Grado grado = gradoService.buscarPorId(id).orElse(null);
        model.addAttribute("grado", grado);
        return "grado/delete";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes attributes){
        gradoService.eliminarPorId(id);
        attributes.addFlashAttribute("error", "Grado eliminado exitosamente");
        return "redirect:/grados";
    }
}
