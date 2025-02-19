package org.example.practicatienda3.Controlador.Servicios;

import org.example.practicatienda3.Modelo.DTOs.Cliente;
import org.example.practicatienda3.Modelo.DTOs.Historial;
import org.example.practicatienda3.Modelo.DTOs.HistorialDTO;
import org.example.practicatienda3.Modelo.DTOs.Producto;
import org.example.practicatienda3.Modelo.Repositorios.ClienteRepository;
import org.example.practicatienda3.Modelo.Repositorios.HistorialRepository;
import org.example.practicatienda3.Modelo.Repositorios.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class HistorialService {
    private final HistorialRepository historialRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public HistorialService(HistorialRepository historialRepository, ClienteRepository clienteRepository, ProductoRepository productoRepository) {
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.historialRepository = historialRepository;
    }

    @Cacheable("historial_cache")
    public List<Historial> findAll() {
        return historialRepository.findAll();
    }

    @Cacheable(value="historial_cache", key = "#id")
    public Optional<Historial> findById(Integer id) {
        return historialRepository.findById(id);
    }

    public Historial save(Historial historial) {
        return historialRepository.save(historial);
    }

    public void delete(Integer id) {
        historialRepository.deleteById(id);
    }

    public Historial findByClienteAndProducto(Cliente cliente, Producto producto) {
        return historialRepository.findFirstByClienteAndProductoAndTipo(cliente, producto, "COMPRA").orElse(null);
    }

    public ResponseEntity<String> realizarCompra(HistorialDTO historialDTO) {
        Cliente cliente = clienteRepository.findById(historialDTO.getClienteId()).orElse(null);
        Producto producto = productoRepository.findById(historialDTO.getProductoId()).orElse(null);

        if (cliente == null || producto == null) {
            return ResponseEntity.badRequest().body("Cliente o producto no encontrados");
        }
        if (historialDTO.getCantidad() > producto.getStock()) {
            return ResponseEntity.badRequest().body("Stock insuficiente para esta compra");
        }

        Historial historial = new Historial();
        historial.setCliente(cliente);
        historial.setProducto(producto);
        historial.setCantidad(historialDTO.getCantidad());
        historial.setTipo(historialDTO.getTipo());
        historial.setDescripcion(historialDTO.getDescripcion());
        historial.setFechaTransaccion(LocalDate.now());

        producto.setStock(producto.getStock() - historialDTO.getCantidad());
        productoRepository.save(producto);
        historialRepository.save(historial);

        return ResponseEntity.ok().body("Historial creado");
    }

    public ResponseEntity<String> realizarDevolucion(HistorialDTO historialDTO) {
        Cliente cliente = clienteRepository.findById(historialDTO.getClienteId()).orElse(null);
        Producto producto = productoRepository.findById(historialDTO.getProductoId()).orElse(null);
        if (cliente == null || producto == null) {
            return ResponseEntity.badRequest().body("Cliente o producto no encontrados");
        }

        Historial historialCompra = findByClienteAndProducto(cliente, producto);
        if (historialCompra == null) {
            return ResponseEntity.badRequest().body("No se encontró un historial de compra para este producto");
        }

        long diasDesdeCompra = ChronoUnit.DAYS.between(historialCompra.getFechaTransaccion(), LocalDate.now());
        if (diasDesdeCompra > 30) {
            return ResponseEntity.badRequest().body("No se pueden devolver productos que se compraron hace más de 30 días");
        }

        Historial historial = new Historial();
        historial.setCliente(cliente);
        historial.setProducto(producto);
        historial.setCantidad(historialDTO.getCantidad());
        historial.setTipo("DEVOLUCION");
        historial.setDescripcion(historialDTO.getDescripcion());
        historial.setFechaTransaccion(LocalDate.now());

        producto.setStock(producto.getStock() + historialDTO.getCantidad());
        productoRepository.save(producto);
        historialRepository.save(historial);

        return ResponseEntity.ok().body("Devolución registrada");
    }

    public ResponseEntity<String> actualizarHistorial(Integer id, HistorialDTO historialDTO) {
        Historial historial = historialRepository.findById(id).orElse(null);
        if (historial == null) {
            return ResponseEntity.badRequest().body("Historial no encontrado");
        }

        Producto producto = historial.getProducto();
        int diferencia = historialDTO.getCantidad() - historial.getCantidad();

        if ("COMPRA".equalsIgnoreCase(historialDTO.getTipo())) {
            if (producto.getStock() + diferencia < 0) {
                return ResponseEntity.badRequest().body("Stock insuficiente para esta compra");
            }
            producto.setStock(producto.getStock() - diferencia);
        } else if ("DEVOLUCION".equalsIgnoreCase(historial.getTipo())) {
            producto.setStock(producto.getStock() + diferencia);
        }

        productoRepository.save(producto);
        historial.setCantidad(historialDTO.getCantidad());
        if (historialDTO.getTipo() != null) historial.setTipo(historialDTO.getTipo());
        if (historialDTO.getDescripcion() != null) historial.setDescripcion(historialDTO.getDescripcion());

        historialRepository.save(historial);
        return ResponseEntity.ok().body("Historial actualizado");
    }
}
