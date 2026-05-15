package com.proyecto.daw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.proyecto.daw.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

   

    @Query(value = "SELECT * FROM productos WHERE id_disponibilidad != 3", nativeQuery = true)
    List<Producto> findSqlAll();

    @Query(value = "SELECT * FROM productos WHERE id = :id", nativeQuery = true)
    Producto findSqlById(@Param("id") int id);

}
