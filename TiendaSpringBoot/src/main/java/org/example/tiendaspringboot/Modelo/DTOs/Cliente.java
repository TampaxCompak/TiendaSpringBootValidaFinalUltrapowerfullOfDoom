package org.example.tiendaspringboot.Modelo.DTOs;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras y espacios")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo puede contener letras y espacios")
    @Size(max = 50, message = "El apellido no puede tener más de 50 caracteres")
    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido;

    @NotBlank(message = "El nickname es obligatorio")
    @Size(max = 50, message = "El nickname no puede tener más de 50 caracteres")
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$",
            message = "La contraseña debe contener al menos una mayúscula, una minúscula y un número"
    )
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[69]\\d{8}$", message = "El teléfono debe tener 9 dígitos y empezar por 6 o 9")
    @Column(name = "telefono", length = 15)
    private String telefono;

    @NotBlank(message = "El domicilio es obligatorio")
    @Size(max = 100, message = "El domicilio no puede tener más de 100 caracteres")
    @Column(name = "domicilio", length = 100)
    private String domicilio;

    @JsonManagedReference
    @OneToMany(mappedBy = "cliente")
    private Set<org.example.tiendaspringboot.Modelo.DTOs.Historial> historials = new LinkedHashSet<>();

    public Cliente() {
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public Set<org.example.tiendaspringboot.Modelo.DTOs.Historial> getHistorials() {
        return historials;
    }

    public void setHistorials(Set<org.example.tiendaspringboot.Modelo.DTOs.Historial> historials) {
        this.historials = historials;
    }
}
