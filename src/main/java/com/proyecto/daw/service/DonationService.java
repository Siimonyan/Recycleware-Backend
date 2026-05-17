package com.proyecto.daw.service;

import com.proyecto.daw.exception.ResourceNotFoundException;
import com.proyecto.daw.model.Donation;
import com.proyecto.daw.model.Rol;
import com.proyecto.daw.model.Usuario;
import com.proyecto.daw.model.DonationState;
import com.proyecto.daw.repository.DonationRepository;
import com.proyecto.daw.repository.DonationStateRepository;
import com.proyecto.daw.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;
    @Autowired
    private UsuarioRepository UsuarioRepository;
    @Autowired
    private DonationStateRepository DonationStateRepository;

   
    public List<Donation> findAll() {
        return donationRepository.findAll();
    }

    
    public Donation findById(Integer id) {
        return donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donación no encontrada con ID: " + id));
    }

   
    public List<Donation> findByUsuarioId(Integer UsuarioId) {
        return donationRepository.findByDonanteId(UsuarioId);
    }

    public List<Object[]> obtenerRankingEmpresas() {
        return donationRepository.findRankingEmpresas();
    }

   
    @Transactional
    public Donation save(Donation donation) {
        
        donation.setId(null);

        if (donation.getDonante() != null && donation.getDonante().getId() != null) {
            Usuario usuario = UsuarioRepository.findById(donation.getDonante().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "El donante con ID " + donation.getDonante().getId() + " no existe."));
            donation.setDonante(usuario);
        } else {
            donation.setDonante(null);
        }

        int targetStateId = (donation.getDonante() != null) ? 1 : 3;
        
        DonationState state = DonationStateRepository.findById(targetStateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El estado de donación con ID " + targetStateId + " no existe."));
        
        donation.setEstado(state);

        return donationRepository.save(donation);
    }

    
    @Transactional
    public Donation updateStatus(Integer donationId, Integer newStateId) {
        
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donación no encontrada"));

        
        DonationState newState = DonationStateRepository.findById(newStateId)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado"));

        
        donation.setEstado(newState);
        return donationRepository.save(donation);
    }

    
    @Transactional
    public void delete(Integer id) {
        Donation donation = findById(id); 
        donationRepository.delete(donation);
    }

    
    public java.util.Map<String, Object> obtenerUltimaDonacionFormateada() {
        Donation ultima = donationRepository.findTopPublicDonation(3, Rol.EMPRESA);

        if (ultima == null) {
            return null;
        }

        java.util.Map<String, Object> respuesta = new java.util.HashMap<>();

        String nombreDonante = ultima.getDonante().getRazonSocial() != null ?
                ultima.getDonante().getRazonSocial() :
                ultima.getDonante().getNombre();

        respuesta.put("donante", nombreDonante);
        respuesta.put("cantidad", ultima.getCantidadProductos());
        respuesta.put("categoria", ultima.getDescripcion());

        return respuesta;
    }

    public long count() {
        return donationRepository.count();
    }

    public long countByEstado(String estadoNombre) {
        return donationRepository.countByEstadoNombre(estadoNombre);
    }
}