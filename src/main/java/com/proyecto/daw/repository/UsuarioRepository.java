package com.proyecto.daw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.daw.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @Query(value = "SELECT * FROM usuarios WHERE UPPER(nombre) LIKE UPPER(CONCAT('%', :cadena, '%')) OR UPPER(razon_social) LIKE UPPER(CONCAT('%', :cadena, '%'))", nativeQuery = true)
    List<Usuario> findByNameContaining(@Param("cadena") String name);

    Usuario findByCorreo(String correo);

    long countByRol(com.proyecto.daw.model.Rol rol);

    long countByActivoTrue();

    long countByRolAndActivoTrue(com.proyecto.daw.model.Rol rol);
}
