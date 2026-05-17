package com.proyecto.daw.repository;

import com.proyecto.daw.model.Donation;
import com.proyecto.daw.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Integer> {

    List<Donation> findByDonanteId(Integer userId);

    @Query("SELECT d.donante.razonSocial, SUM(d.cantidadProductos) " +
            "FROM Donation d " +
            "WHERE d.donante.rol = 'EMPRESA' " +
            "AND d.donante.razonSocial IS NOT NULL " +
            "AND d.estado.id IN (3, 4) " +
            "AND (d.esAnonimo = false OR d.esAnonimo IS NULL) " +
            "GROUP BY d.donante.id, d.donante.razonSocial " +
            "ORDER BY SUM(d.cantidadProductos) DESC")
    List<Object[]> findRankingEmpresas();

    @Query("SELECT d FROM Donation d " +
           "WHERE d.estado.id = :estadoId " +
           "AND d.donante.rol = :rol " +
           "AND (d.esAnonimo = false OR d.esAnonimo IS NULL) " +
           "ORDER BY d.fechaDonacion DESC LIMIT 1")
    Donation findTopPublicDonation(Integer estadoId, Rol rol);

    long countByEstadoNombre(String nombre);
}