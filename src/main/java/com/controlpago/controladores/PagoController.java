package com.controlpago.controladores;

import com.controlpago.modelos.*;
import com.controlpago.servicios.implementaciones.StudentPaymentRecordService;
import com.controlpago.servicios.interfaces.*;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
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

    @Autowired
    private IMetodoPagoService metodoPagoService;

    @Autowired
    private IStudentPaymentRecordService studentPaymentRecordService;

    private final APIContext apiContext;

    public PagoController(APIContext apiContext) {
        this.apiContext = apiContext;
    }

    @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("id").descending());

        //Page<Pago> pagos = pagoService.buscarTodosPaginados(pageable);
        //model.addAttribute("pagos", pagos);

        Page<StudentPaymentRecord> studentPaymentRecords = studentPaymentRecordService.buscarTodosPaginados(pageable);
        model.addAttribute("payments",studentPaymentRecords);

        int totalPages = studentPaymentRecords.getTotalPages();
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

    @GetMapping("/studentPaymentRecord")
    public String createPaymentRecord(Model model){
        List<Alumno> alumnos = alumnoService.obtenerTodos();
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("studentPaymentRecord", new StudentPaymentRecord());
        return "pago/studentPaymentRecord";
    }

    @PostMapping("/savePaymentRecord")
    public String guardar(@ModelAttribute StudentPaymentRecord pStudentPaymentRecord, BindingResult result, Model model, RedirectAttributes attributes){
        Integer recordId = 0;
        if (result.hasErrors()) {
            return "rol/create";
        }
        if (pStudentPaymentRecord.getId() != null && pStudentPaymentRecord.getId() > 0) {
            studentPaymentRecordService.crearOEditar(pStudentPaymentRecord);
            attributes.addFlashAttribute("msg", "actualizado exitosamente");
        } else {
            pStudentPaymentRecord.setCreateAt(LocalDate.now());
            pStudentPaymentRecord.setUpdateAt(LocalDate.now());
            pStudentPaymentRecord.setStatus(true);
            pStudentPaymentRecord.setPaidAmount(BigDecimal.ZERO);
            pStudentPaymentRecord.setRemainingAmount(BigDecimal.ZERO);
            StudentPaymentRecord Record = studentPaymentRecordService.crearOEditar(pStudentPaymentRecord);
            recordId = Record.getId();
            attributes.addFlashAttribute("msg", "creado exitosamente");
        }
        String record = String.valueOf(recordId);
        return "redirect:/pagos/details/"+record;
    }


    @GetMapping("/create")
    @Transactional
    public String create(Model model, @RequestParam("alumnoId") Integer alumnoId, @RequestParam("payRecordId") Integer payRecordId){
        Alumno alumno = alumnoService.buscarPorId(alumnoId).orElse(null);
        StudentPaymentRecord studentPaymentRecord = studentPaymentRecordService.buscarPorId(payRecordId).orElse(null);
        List<MetodoPago>metodoPagos = metodoPagoService.obtenerTodos();
        model.addAttribute("alumno", alumno);
        model.addAttribute("studentPaymentRecord", studentPaymentRecord);
        model.addAttribute("metodoPagos", metodoPagos);
        model.addAttribute("pago", new Pago());
        return "pago/create";
    }

    @PostMapping("/save")
    public String guardar(@ModelAttribute Pago pPago,
                          BindingResult result,
                          Model model,
                          RedirectAttributes attributes) throws PayPalRESTException {
        if (result.hasErrors()) {
            model.addAttribute("pago", pPago);
            return "pago/create";
        }

        Long alumnoId = pPago.getAlumno().getId().longValue();
        Pago pagoExistente = pagoService.buscarPorAlumnoYMes(alumnoId, pPago.getFecha());
        Pago pagoSave = new Pago();
        Integer pagoId = 0;

        if (pagoExistente != null) {
            attributes.addFlashAttribute("error", "Este alumno ya ha realizado un pago en el mes seleccionado.");
            return "redirect:/pagos/create";
        }

        Integer metodoPagoId = pPago.getMetodoPago().getId();
        Optional<MetodoPago> metodoPago = metodoPagoService.buscarPorId(metodoPagoId);
        // Guardar el pago en la base de datos
        if (pPago.getMetodoPago().getId().equals(1)) {
            pPago.setMetodoPago(pPago.getMetodoPago());
            Pago pagosave=pagoService.crearOEditar(pPago);
            attributes.addFlashAttribute("success", "Pago en efectivo guardado exitosamente.");
            return "redirect:/pagos/details/"+pagosave.getStudentPaymentRecord().getId();
        } else if (pPago.getMetodoPago().getId().equals(2)) {
            pPago.setMetodoPago(pPago.getMetodoPago());
            pagoSave = pagoService.crearOEditar(pPago);
            pagoId = pagoSave.getId();
            // Lógica de PayPal
            Amount amount = new Amount();
            amount.setCurrency("USD");
            amount.setTotal(String.format(Locale.US, "%.2f", pPago.getCantidadPagar()));

            Transaction transaction = new Transaction();
            transaction.setDescription(pPago.getComentario());
            transaction.setAmount(amount);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl("http://localhost:8080/pagos/cancel");
            redirectUrls.setReturnUrl("http://localhost:8080/pagos/success?IdPago=" + pagoId);
            payment.setRedirectUrls(redirectUrls);

            Payment createdPayment = payment.create(apiContext);
            for (Links link : createdPayment.getLinks()) {
                if ("approval_url".equals(link.getRel())) {
                    return "redirect:" + link.getHref();
                }
            }
        }

        attributes.addFlashAttribute("error", "Ocurrió un error al procesar el pago.");
        return "redirect:/pagos/create";
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
        StudentPaymentRecord studentPaymentRecord = studentPaymentRecordService.buscarPorId(id).orElse(null);
        List<Pago> pagos = pagoService.buscarPagosPorStudentPaymnetRecord(studentPaymentRecord.getId());

        List<Alumno> alumnos = alumnoService.obtenerTodos();

        if (pagos == null) {
            return "redirect:/pagos";
        }

        BigDecimal totalPagado = pagos.stream()
                .map(Pago::getCantidadPagar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("pagos", pagos);
        model.addAttribute("alumno", alumnos);
        model.addAttribute("studentPaymentRecord", studentPaymentRecord);
//        model.addAttribute("pagosDelAlumno", pagosDelAlumno);
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

                return "redirect:/pagos/details/"+UpdatePago.getStudentPaymentRecord().getId();
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
