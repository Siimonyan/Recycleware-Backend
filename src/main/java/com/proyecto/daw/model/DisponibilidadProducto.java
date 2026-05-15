package com.proyecto.daw.model;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;




@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(exclude = "productos")
@EqualsAndHashCode(exclude = "productos")
@Entity
@Table(name = "disponibilidad_producto")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class DisponibilidadProducto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @NotBlank(message = "La disponibilidad necesita un nombre")
    @Size(min = 1, max = 45, message = "El tamaño máximo del nombre de un estado es de 45 carácteres")
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @OneToMany(mappedBy = "disponibilidad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("disponibilidad")
    private Set<Producto> productos;

}