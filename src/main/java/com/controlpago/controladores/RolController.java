package com.controlpago.controladores;

import com.controlpago.modelos.Grado;
import com.controlpago.modelos.Rol;
import com.controlpago.servicios.interfaces.IGradoService;
import com.controlpago.servicios.interfaces.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/roles")
public class RolController {
    @Autowired
    private IRolService rolService;

    @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<Rol> roles = rolService.buscarTodosPaginados(pageable);
        model.addAttribute("roles", roles);

        int totalPage = roles.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPage)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "rol/index";
    }

    @GetMapping("/create")
    public String create(Rol pRol){
        return "rol/create";
    }

    @PostMapping("/save")
    public String guardar(@ModelAttribute Rol pRol, BindingResult result, Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("grado", pRol);
            return "rol/create";
        }
        if (pRol.getId() != null && pRol.getId() > 0) {
            rolService.crearOEditar(pRol);
            attributes.addFlashAttribute("msg", "Rol actualizado exitosamente");
        } else {
            rolService.crearOEditar(pRol);
            attributes.addFlashAttribute("msg", "Rol creado exitosamente");
        }
        return "redirect:/roles";
    }


    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model){
        Rol rol = rolService.buscarPorId(id).orElse(null);
        model.addAttribute("rol", rol);
        return "rol/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model){
        Rol rol = rolService.buscarPorId(id).orElse(null);
        model.addAttribute("rol", rol);
        return "rol/edit";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, Model model){
        Rol rol = rolService.buscarPorId(id).orElse(null);
        model.addAttribute("rol", rol);
        return "rol/delete";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes attributes){
        rolService.elimanarPorId(id);
        attributes.addFlashAttribute("error", "Rol eliminado exitosamente");
        return "redirect:/roles";
    }
}
