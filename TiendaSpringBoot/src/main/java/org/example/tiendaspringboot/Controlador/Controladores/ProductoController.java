package org.example.tiendaspringboot.Controlador.Controladores;

import jakarta.validation.Valid;
import org.example.tiendaspringboot.Controlador.Servicios.ProductoService;
import org.example.tiendaspringboot.Modelo.DTOs.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/producto")
public class ProductoController {
    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listar() {
        return productoService.findAll();
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<Producto> buscar(@PathVariable Integer id) {
        Optional<Producto> producto = productoService.findById(id);
        return producto.isPresent() ? ResponseEntity.ok(producto.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Producto> guardar(@Valid @RequestBody Producto producto) {
        Producto savedProducto = productoService.save(producto);
        return ResponseEntity.ok(savedProducto);
    }

    @PutMapping
    public ResponseEntity<Producto> actualizar(@Valid @RequestBody Producto producto) {
        if (!productoService.findById(producto.getId()).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        productoService.save(producto);
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!productoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        productoService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
