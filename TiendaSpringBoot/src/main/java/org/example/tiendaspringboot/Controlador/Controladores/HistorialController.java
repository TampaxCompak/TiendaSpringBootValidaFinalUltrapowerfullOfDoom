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
    @GetMapping("/{id}")
    public Historial getHistorialById(@PathVariable int id) {
        Optional<Historial> historial = historialService.findById(id);
        return historial.orElse(null);
    }
    @PostMapping("/historial_compra")
    public ResponseEntity<String> compraHistorial(@Valid @RequestBody HistorialDTO historialDTO) {
        Cliente cliente = clienteService.findById(historialDTO.getClienteId()).orElse(null);
        Producto producto = productoService.findById(historialDTO.getProductoId()).orElse(null);

        if(cliente == null || producto == null) {
            return ResponseEntity.badRequest().body("Cliente o producto no encontrados");
        }
        if(historialDTO.getCantidad()>producto.getStock()){
            return ResponseEntity.badRequest().body("No hay stock suficiente");
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
        return ResponseEntity.ok().body("Compra creada");
    }
    @PostMapping("/historial_devolucion")
    public ResponseEntity<String> devolucionHistorial(@Valid @RequestBody HistorialDTO historialDTO) {
        Cliente cliente = clienteService.findById(historialDTO.getClienteId()).orElse(null);
        Producto producto = productoService.findById(historialDTO.getProductoId()).orElse(null);
        if(cliente == null || producto == null) {
            return ResponseEntity.badRequest().body("Cliente o producto no encontrados");
        }
        Historial historialCompra = historialService.findByClienteAndProducto(cliente, producto);
        if (historialCompra == null) {
            return ResponseEntity.badRequest().body("No se encontro el historial");
        }

        long diasDesdeCompra = ChronoUnit.DAYS.between(historialCompra.getFechaTransaccion(), LocalDate.now());
        if (diasDesdeCompra > 30){
            return ResponseEntity.badRequest().body("No se pueden realizar devoluciones pasados los 30 días");
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
        return ResponseEntity.ok().body("Devolución realizada con éxito");
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarHistorial(@PathVariable int id, @Valid @RequestBody HistorialUpdateDTO historialUpdateDTO) {
        Historial historial = historialService.findById(id).orElse(null);
        if (historial == null) {
            return ResponseEntity.badRequest().body("Historial no encontrado");
        }
        Producto producto = historial.getProducto();
        if (historialUpdateDTO.getCantidad() != null && !historialUpdateDTO.getCantidad().equals(producto.getStock())) {
            int diferencia = historialUpdateDTO.getCantidad() - historial.getCantidad();

            if ("COMPRA".equals(historialUpdateDTO.getTipo())) {
                if (producto.getStock() + diferencia < 0) {
                    return ResponseEntity.badRequest().body("Stock insuficiente");
                }
                producto.setStock(producto.getStock() + diferencia);
            } else if ("DEVOLUCION".equals(historial.getTipo())) {
                producto.setStock(producto.getStock() + diferencia);
            }
            productoService.save(producto);
            historial.setCantidad(historialUpdateDTO.getCantidad());
        }
        if (historialUpdateDTO.getDescripcion() != null) historial.setDescripcion(historialUpdateDTO.getDescripcion());
        historialService.save(historial);
        return ResponseEntity.ok().body("Historial actualizado");
    }
    @DeleteMapping
    public ResponseEntity<Void> eliminarHistorial(@PathVariable int id) {
        if (!historialService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        historialService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
