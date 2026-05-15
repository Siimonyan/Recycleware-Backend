package com.proyecto.daw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.daw.model.CategoriasProducto;

@Repository
public interface CategoriasProductoRepository extends JpaRepository<CategoriasProducto, Integer> {

    @Query(value = "SELECT nombre FROM categorias_producto", nativeQuery = true)
    List<String> findAllNames();

    java.util.Optional<CategoriasProducto> findByNombre(String nombre);

}