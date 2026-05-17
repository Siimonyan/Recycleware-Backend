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
        return categoriasProductoRepository.findById(id).orElse(null);
    }

    public List<String> findAllNombreCategorias(){
        return categoriasProductoRepository.findAllNames();
    }

    public CategoriasProducto save(CategoriasProducto categoria) {
        if (categoria.getId() == null) {
            categoriasProductoRepository.findByNombre(categoria.getNombre()).ifPresent(c -> {
                throw new RuntimeException("Ya existe una categoría con el nombre: " + categoria.getNombre());
            });
        }
        return categoriasProductoRepository.save(categoria);
    }

    public CategoriasProducto update(int id, CategoriasProducto details) {
        CategoriasProducto existing = categoriasProductoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        
        categoriasProductoRepository.findByNombre(details.getNombre()).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new RuntimeException("Ya existe otra categoría con el nombre: " + details.getNombre());
            }
        });

        existing.setNombre(details.getNombre());
        return categoriasProductoRepository.save(existing);
    }

    public void deleteById(int id) {
        CategoriasProducto categoria = findById(id);
        if (categoria != null) {
            if (categoria.getProductos() != null && !categoria.getProductos().isEmpty()) {
                throw new RuntimeException("No se puede eliminar una categoría que tiene artículos asociados.");
            }
            categoriasProductoRepository.deleteById(id);
        }
    }
}