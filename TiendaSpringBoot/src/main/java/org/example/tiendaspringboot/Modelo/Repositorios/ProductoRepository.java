package org.example.tiendaspringboot.Modelo.Repositorios;

import org.example.tiendaspringboot.Modelo.DTOs.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

}
