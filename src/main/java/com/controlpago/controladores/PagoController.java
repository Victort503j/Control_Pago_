package com.controlpago.controladores;

import com.controlpago.modelos.Alumno;
import com.controlpago.modelos.Grado;
import com.controlpago.modelos.Pago;
import com.controlpago.servicios.interfaces.IAlumnoService;
import com.controlpago.servicios.interfaces.IGradoService;
import com.controlpago.servicios.interfaces.IPagoService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Converter;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.*;
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

    private final APIContext apiContext;

    public PagoController(APIContext apiContext) {
        this.apiContext = apiContext;
    }

    @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("id").descending());

        Page<Pago> pagos = pagoService.buscarTodosPaginados(pageable);
        model.addAttribute("pagos", pagos);

        int totalPages = pagos.getTotalPages();
        if (totalPages > 0) {
            int startPage = Math.max(1, currentPage - 2);
            int endPage = Math.min(totalPages, currentPage + 2);

            List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        List<Alumno> alumnos = alumnoService.obtenerTodos();
        model.addAttribute("alumnos", alumnos);

        List<Grado> grados = gradoService.obtenerTodos();
        model.addAttribute("grados", grados);

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);

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

        Page<Pago> pagos = Page.empty();  // Inicializamos la variable de pagos
        LocalDate fechaParsed = null;
        String errorMessage = null;

        try {
            // Validación de la fecha, si está presente
            if (fecha.isPresent() && !fecha.get().isEmpty()) {
                fechaParsed = LocalDate.parse(fecha.get());
            }

            // Realizar la búsqueda de pagos, con filtros opcionales
            pagos = pagoService.buscarPagosPorNombreCompletoYFecha(
                    nombreCompleto.orElse(null), fechaParsed, pageable);

            if (pagos.isEmpty()) {
                errorMessage = "No se encontraron pagos para los criterios de búsqueda proporcionados.";
            }

        } catch (DateTimeParseException e) {
            errorMessage = "Formato de fecha inválido. Por favor, ingrese una fecha válida.";
        } catch (Exception e) {
            errorMessage = "Ocurrió un error al realizar la búsqueda. Por favor, intente nuevamente.";
        }

        // Añadir los pagos, alumnos y posibles mensajes de error al modelo
        model.addAttribute("pagos", pagos);
        List<Alumno> alumnos = alumnoService.obtenerTodos();
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("errorMessage", errorMessage);

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
    @Transactional
    public String create(Model model){
        List<Alumno> alumnos = alumnoService.obtenerTodos();
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("pago", new Pago());
        return "pago/create";
    }

    @PostMapping("/save")
    public String guardar(@ModelAttribute Pago pPago, BindingResult result, Model model, RedirectAttributes attributes) throws PayPalRESTException {
        if (result.hasErrors()) {
            model.addAttribute("pago", pPago);
            return "pago/create";
        }

        Long alumnoId = pPago.getAlumno().getId().longValue();
        Pago pagoExistente = pagoService.buscarPorAlumnoYMes(alumnoId, pPago.getFecha());

        if (pagoExistente != null) {
            attributes.addFlashAttribute("error", "Este alumno ya ha realizado un pago en el mes seleccionado.");
            return "redirect:/pagos/create";
        }

        Pago pagoGuardado = pagoService.crearOEditar(pPago);
        Integer idPago = pagoGuardado.getId();
        //Pago IdPago = pagoService.buscarPorId(pagoGuardado.getId()).orElse(null);

        //*************************Apartado para paypal**********************
        // Crear monto
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.format(Locale.forLanguageTag("USD"), "%.2f", pPago.getCantidadPagar())); // Monto fijo o calculado dinámicamente

        // Crear transacción
        Transaction transaction = new Transaction();
        transaction.setDescription(pPago.getComentario());
        transaction.setAmount(amount);

        // Configurar el pago
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        // Redirección de URLs
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:8080/success"); // Cambiar por tu ruta
        redirectUrls.setReturnUrl("http://localhost:8080/pagos/success?IdPago=" + idPago);// Cambiar por tu ruta
        payment.setRedirectUrls(redirectUrls);

        // Crear el pago en PayPal
        Payment createdPayment = payment.create(apiContext);

        // Obtener el enlace de aprobación
        String approvalUrl = null;
        for (Links link : createdPayment.getLinks()) {
            if (link.getRel().equals("approval_url")) {
                return "redirect:" + link.getHref();

            }
        }

//        if (approvalUrl != null) {
//
//            // Capturar el pago una vez que ha sido aprobado (puedes hacer esto sin redirigir al usuario)
//            String paymentId = createdPayment.getId();  // Obtén el ID del pago
//            Payment paymentDetails = Payment.get(apiContext, paymentId);
//            Payment executedPayment = executePayment(paymentId, paymentDetails.getPayer().getFundingOptionId());
//
//            Pago UpdatePago = new Pago();
//            UpdatePago.setId(pPagoSave.getId());
//            UpdatePago.setTransactionId(executedPayment.getId());
//            UpdatePago.setAmountPaypal(new BigDecimal(executedPayment.getTransactions().get(0).getAmount().getTotal()));
//            UpdatePago.setCurrency(executedPayment.getTransactions().get(0).getAmount().getCurrency());
//            UpdatePago.setPaymentMethodId(executedPayment.getPayer().getPaymentMethod());
//            UpdatePago.setPaymentStatus(executedPayment.getState());
//            UpdatePago.setDatePaypal(new Date(executedPayment.getCreateTime()));
//            UpdatePago.setOrderId(paymentId);
//            UpdatePago.setDetails(executedPayment.getTransactions().get(0).getDescription());
//
//
//            if (executedPayment != null && executedPayment.getState().equals("approved")) {
//                // El pago se ha completado exitosamente, realiza acciones adicionales
//                attributes.addFlashAttribute("msg", "Pago realizado exitosamente.");
//            } else {
//                // Hubo un error al completar el pago
//                attributes.addFlashAttribute("error", "Error al completar el pago.");
//            }
//        }

        return "http://localhost:8080/success?alumnoId="+idPago;
    }

    // Metodo para ejecutar el pago y capturarlo
//    private Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
//        Payment payment = new Payment();
//        payment.setId(paymentId);
//
//        PaymentExecution paymentExecution = new PaymentExecution();
//        paymentExecution.setPayerId(payerId);
//
//        // Ejecutar el pago para capturarlo
//        return payment.execute(apiContext, paymentExecution);
//    }

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

    @GetMapping("/success")
    public String success(@RequestParam("paymentId") String paymentId,
                          @RequestParam("PayerID") String payerId,
                          @RequestParam("IdPago") Integer IdPago,
                          Model model) {
        try {
            var ok = false;
            if (ok = true){
                Payment payment = Payment.get(apiContext, paymentId);

                PaymentExecution paymentExecution = new PaymentExecution();
                paymentExecution.setPayerId(payerId);

                Payment executedPayment = payment.execute(apiContext, paymentExecution);
                Pago UpdatePago = pagoService.buscarPorId(IdPago).orElse(null);
                UpdatePago.setTransactionId(executedPayment.getId());
                UpdatePago.setAmountPaypal(new BigDecimal(executedPayment.getTransactions().get(0).getAmount().getTotal()));
                UpdatePago.setCurrency(executedPayment.getTransactions().get(0).getAmount().getCurrency());
                UpdatePago.setPaymentMethodId(executedPayment.getPayer().getPaymentMethod());
                UpdatePago.setPaymentStatus(executedPayment.getState());

                String localDate = executedPayment.getCreateTime(); // Valor que recibes
                String dateOnlyString = localDate.split("T")[0];
                LocalDate date = LocalDate.parse(dateOnlyString);

                // Asignar la fecha a la entidad
                UpdatePago.setDatePaypal(date);
                UpdatePago.setOrderId(paymentId);
                UpdatePago.setDetails(executedPayment.getTransactions().get(0).getDescription());
                pagoService.crearOEditar(UpdatePago);


                if (executedPayment != null && executedPayment.getState().equals("COMPLETED")) {
                    // El pago se ha completado exitosamente, realiza acciones adicionales
                    model.addAttribute("msg", "Pago realizado exitosamente.");
                } else {
                    // Hubo un error al completar el pago
                    model.addAttribute("error", "Error al completar el pago.");
                }

                model.addAttribute("message", "Pago completado exitosamente!");
                model.addAttribute("payment", executedPayment);

                return "redirect:/pagos";
            }
            return "redirect:/pagos";
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al completar el pago: " + e.getMessage());
            return "http://localhost:8080/pagos";
        }
    }

    @GetMapping("/cancel")
    public String cancel(Model model) {
        model.addAttribute("message", "El pago fue cancelado.");
        return "http://localhost:8080/pagos/create";
    }

}
