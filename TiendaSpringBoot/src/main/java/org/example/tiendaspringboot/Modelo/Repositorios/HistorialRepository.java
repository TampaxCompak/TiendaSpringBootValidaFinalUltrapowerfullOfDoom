package org.example.tiendaspringboot.Modelo.Repositorios;

import org.example.tiendaspringboot.Modelo.DTOs.Historial;
import org.example.tiendaspringboot.Modelo.DTOs.Cliente;
import org.example.tiendaspringboot.Modelo.DTOs.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Integer> {
    Optional<Historial> findFirstByClienteAndProductoAndTipo(Cliente cliente, Producto producto, String tipo);
}
