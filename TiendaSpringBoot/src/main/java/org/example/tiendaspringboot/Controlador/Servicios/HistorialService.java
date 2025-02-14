package org.example.tiendaspringboot.Controlador.Servicios;

import org.example.tiendaspringboot.Modelo.DTOs.Cliente;
import org.example.tiendaspringboot.Modelo.DTOs.Historial;
import org.example.tiendaspringboot.Modelo.DTOs.Producto;
import org.example.tiendaspringboot.Modelo.Repositorios.ClienteRepository;
import org.example.tiendaspringboot.Modelo.Repositorios.HistorialRepository;
import org.example.tiendaspringboot.Modelo.Repositorios.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

}
