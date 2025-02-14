package org.example.tiendaspringboot.Modelo.Repositorios;

import org.example.tiendaspringboot.Modelo.DTOs.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

}
