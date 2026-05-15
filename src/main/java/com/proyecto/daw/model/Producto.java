package com.proyecto.daw.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@ToString(exclude = {"estado", "categoria"})       
@EqualsAndHashCode(exclude = {"estado", "categoria"}) 
@Entity
@Table(name = "productos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @NotBlank(message = "El producto necesita un nombre")
    @Size(min = 1, max = 100, message = "El tamaño máximo del nombre un producto es de 100 carácteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", referencedColumnName = "id")
    @JsonIgnoreProperties("productos")  
    private CategoriasProducto categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado", referencedColumnName = "id")
    @JsonIgnoreProperties("productos")  
    private EstadosProducto estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_disponibilidad", referencedColumnName = "id")
    @JsonIgnoreProperties("productos")  
    private DisponibilidadProducto disponibilidad;

    @NotBlank(message = "El producto necesita una descripcion")
    @Size(min = 1, max = 200, message = "El tamaño máximo de la descripción de un producto es de 200 carácteres")
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Size(min = 1, max = 500, message = "El tamaño máximo de la url de la imagen es de 500 carácteres")
    @Column(name = "imagen_url")
    private String imagenUrl;
}
