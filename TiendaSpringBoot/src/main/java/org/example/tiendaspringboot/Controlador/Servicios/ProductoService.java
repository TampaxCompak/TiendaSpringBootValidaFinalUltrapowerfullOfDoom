package org.example.tiendaspringboot.Controlador.Servicios;

import org.example.tiendaspringboot.Modelo.DTOs.Producto;
import org.example.tiendaspringboot.Modelo.Repositorios.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Cacheable("cache_producto")
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Cacheable(value = "cache_producto", key = "#id")
    public Optional<Producto> findById(Integer id) {
        return productoRepository.findById(id);
    }
    @Transactional
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    public void delete(Integer id) {
        productoRepository.deleteById(id);
    }
}
