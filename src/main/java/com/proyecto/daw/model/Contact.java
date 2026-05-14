package com.proyecto.daw.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes_contacto")
@Data
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String correo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler", "solicitudes", "donaciones"})
    private Usuario usuario;

    @Column(name = "fecha_envio", insertable = false, updatable = false)
    private LocalDateTime fechaEnvio;

    @Column(columnDefinition = "TEXT")
    private String respuesta;

    @Column(nullable = false)
    private boolean leido = false;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;
}