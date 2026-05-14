package com.proyecto.daw.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proyecto.daw.model.DisponibilidadProducto;
import com.proyecto.daw.repository.DisponibilidadProductoRepository;

@Service
public class DisponibilidadProductoService {

    @Autowired
    private DisponibilidadProductoRepository repository;

    public List<DisponibilidadProducto> findAll() {
        return repository.findAll();
    }

    public DisponibilidadProducto findById(int id) {
        return repository.findById(id).orElse(null);
    }
}
