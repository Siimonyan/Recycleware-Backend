package com.proyecto.daw.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "donaciones")
@Data
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_donante") 
    private Usuario donante;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_estado", nullable = false)
    private DonationState estado;

    @Column(name = "fecha_donacion", insertable = false, updatable = false)
    private LocalDateTime fechaDonacion;

    @Column(name = "cantidad_productos")
    private Integer cantidadProductos;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    private Float peso;

    @Column(name = "es_anonimo", columnDefinition = "boolean default false")
    private Boolean esAnonimo;
}