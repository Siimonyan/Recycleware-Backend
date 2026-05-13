package com.proyecto.daw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.daw.model.CategoriasProducto;
import com.proyecto.daw.repository.CategoriasProductoRepository;

@Service
public class CategoriasProductoService {

    @Autowired
    private CategoriasProductoRepository categoriasProductoRepository;


    

    public List<CategoriasProducto> findAll() {
        return categoriasProductoRepository.findAll();
    }

    public CategoriasProducto findById(int id) {
        return categoriasProductoRepository.findSqlById(id);
    }

    public List<String> findAllNombreCategorias(){
        return categoriasProductoRepository.findAllNames();
    }

    public CategoriasProducto save(CategoriasProducto categoria) {
        return categoriasProductoRepository.save(categoria);
    }

    public void deleteById(int id) {
        categoriasProductoRepository.deleteById(id);
    }
}