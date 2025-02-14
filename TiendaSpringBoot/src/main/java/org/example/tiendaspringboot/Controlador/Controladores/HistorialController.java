package org.example.tiendaspringboot.Controlador.Controladores;

import jakarta.validation.Valid;
import org.example.tiendaspringboot.Controlador.Servicios.ClienteService;
import org.example.tiendaspringboot.Controlador.Servicios.HistorialService;
import org.example.tiendaspringboot.Controlador.Servicios.ProductoService;
import org.example.tiendaspringboot.Modelo.DTOs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/historial")
public class HistorialController {
    private final HistorialService historialService;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    @Autowired
    public HistorialController(HistorialService historialService, ClienteService clienteService, ProductoService productoService) {
        this.historialService = historialService;
        this.clienteService = clienteService;
        this.productoService = productoService;
    }

    @GetMapping
    public List<Historial> getHistorial() {
        return historialService.findAll();
    }

    @GetMapping("/historial/{id}")
    public Historial obtenerHistorialId(@PathVariable int id) {
        Optional<Historial> historial = historialService.findById(id);
        return historial.orElse(null);
    }

    @PostMapping
    public ResponseEntity<String> compraHistorial(@Valid @RequestBody HistorialDTO historialDTO) {
        Cliente cliente = clienteService.findById(historialDTO.getClienteId()).orElse(null);
        Producto producto = productoService.findById(historialDTO.getProductoId()).orElse(null);

        if (cliente == null || producto == null) {
            return ResponseEntity.badRequest().body("Error: Cliente o producto no encontrado.");
        }

        if (historialDTO.getCantidad() > producto.getStock()) {
            return ResponseEntity.badRequest().body("Error: Stock insuficiente. Cantidad disponible: " + producto.getStock());
        }

        Historial historial = new Historial();
        historial.setCliente(cliente);
        historial.setProducto(producto);
        historial.setCantidad(historialDTO.getCantidad());
        historial.setTipo(historialDTO.getTipo());
        historial.setDescripcion(historialDTO.getDescripcion());
        historial.setFechaTransaccion(LocalDate.now());

        producto.setStock(producto.getStock() - historialDTO.getCantidad());
        productoService.save(producto);

        historialService.save(historial);
        return ResponseEntity.status(HttpStatus.CREATED).body("Historial creado con éxito.");
    }

    @PostMapping("/historial_devolucion")
    public ResponseEntity<String> devolverHistorial(@Valid @RequestBody HistorialDTO historialDTO) {
        Cliente cliente = clienteService.findById(historialDTO.getClienteId()).orElse(null);
        Producto producto = productoService.findById(historialDTO.getProductoId()).orElse(null);

        if (cliente == null || producto == null) {
            return ResponseEntity.badRequest().body("Error: Cliente o producto no encontrado.");
        }

        Historial historialCompra = historialService.findByClienteAndProducto(cliente, producto);
        if (historialCompra == null) {
            return ResponseEntity.badRequest().body("Error: No se encontró un historial de compra para devolver.");
        }

        long diasDesdeCompra = ChronoUnit.DAYS.between(historialCompra.getFechaTransaccion(), LocalDate.now());
        if (diasDesdeCompra > 30) {
            return ResponseEntity.badRequest().body("Error: No se puede devolver un producto después de 30 días.");
        }

        Historial historial = new Historial();
        historial.setCliente(cliente);
        historial.setProducto(producto);
        historial.setCantidad(historialDTO.getCantidad());
        historial.setTipo("DEVOLUCIÓN");
        historial.setDescripcion(historialDTO.getDescripcion());
        historial.setFechaTransaccion(LocalDate.now());

        producto.setStock(producto.getStock() + historialDTO.getCantidad());
        productoService.save(producto);
        historialService.save(historial);

        return ResponseEntity.status(HttpStatus.CREATED).body("Devolución registrada con éxito.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarHistorial(
            @PathVariable Integer id,
            @Valid @RequestBody HistorialUpdateDTO historialDTO) {

        Historial historial = historialService.findById(id).orElse(null);
        if (historial == null) {
            return ResponseEntity.notFound().build();
        }

        Producto producto = historial.getProducto();

        if (historialDTO.getCantidad() != null && !historialDTO.getCantidad().equals(historial.getCantidad())) {
            int diferencia = historialDTO.getCantidad() - historial.getCantidad();

            if ("COMPRA".equalsIgnoreCase(historial.getTipo())) {
                if (producto.getStock() + diferencia < 0) {
                    return ResponseEntity.badRequest().body("Error: Stock insuficiente para modificar la cantidad.");
                }
                producto.setStock(producto.getStock() - diferencia);
            } else if ("DEVOLUCIÓN".equalsIgnoreCase(historial.getTipo())) {
                producto.setStock(producto.getStock() + diferencia);
            }

            productoService.save(producto);
            historial.setCantidad(historialDTO.getCantidad());
        }

        if (historialDTO.getTipo() != null) historial.setTipo(historialDTO.getTipo());
        if (historialDTO.getDescripcion() != null) historial.setDescripcion(historialDTO.getDescripcion());

        historialService.save(historial);
        return ResponseEntity.ok("Historial actualizado correctamente.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarHistorial(@PathVariable Integer id) {
        Historial historial = historialService.findById(id).orElse(null);

        if (historial == null) {
            return ResponseEntity.notFound().build();
        }

        historialService.delete(id);
        return ResponseEntity.ok("Historial eliminado correctamente.");
    }
}
