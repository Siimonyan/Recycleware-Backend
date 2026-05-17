package com.proyecto.daw.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.daw.model.Producto;
import com.proyecto.daw.repository.DisponibilidadProductoRepository;
import com.proyecto.daw.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DisponibilidadProductoRepository disponibilidadProductoRepository;


    

    public List<Producto> findAll() {
        return productoRepository.findSqlAll();
    }

    public Producto findById(int id) {
        return productoRepository.findSqlById(id);
    }

    public List<Producto> findByCategoriaString(String categoria) {
        List<Producto> all = productoRepository.findAll();
        List<Producto> filtered = new ArrayList<>();
        for (Producto producto : all) {
            if (producto.getCategoria().getNombre().equalsIgnoreCase(categoria)) {
                filtered.add(producto);
            }
        }
        return filtered;
    }

    public List<Producto> findByEstadoString(String estado) {
        List<Producto> all = productoRepository.findAll();
        List<Producto> filtered = new ArrayList<>();
        for (Producto producto : all) {
            if (producto.getEstado().getNombre().equalsIgnoreCase(estado)) {
                filtered.add(producto);
            }
        }
        return filtered;
    }

    public List<Producto> findAllDisponibles(){
        List<Producto> lista = new ArrayList<Producto>();
        for (Producto producto : productoRepository.findAll()) {
            if (producto.getDisponibilidad().getId() == 1) {
                lista.add(producto);
            }
        }       
        return lista;
    }

    public Long count() {
        return productoRepository.count();
    }

    public Long countByDisponibilidad(String nombreDisp) {
        return productoRepository.findAll().stream()
            .filter(p -> p.getDisponibilidad() != null && nombreDisp.equalsIgnoreCase(p.getDisponibilidad().getNombre()))
            .count();
    }

    public boolean marcarProductoNoDisponibleById(int id){
        Producto p = productoRepository.findSqlById(id);
        if (p == null) {
            return false;
        }
        p.setDisponibilidad(disponibilidadProductoRepository.findSqlById(2));
        productoRepository.save(p);
        return true;
    }

    public boolean marcarProductoEliminadoById(int id){
        Producto p = productoRepository.findSqlById(id);
        if (p == null) {
            return false;
        }
        p.setDisponibilidad(disponibilidadProductoRepository.findSqlById(3));
        productoRepository.save(p);
        return true;
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Autowired
    private com.proyecto.daw.repository.RequestRepository requestRepository;

    @Autowired
    private com.proyecto.daw.repository.DisponibilidadProductoRepository disponibilidadRepo;

    public void deleteById(int id) {
        Producto p = productoRepository.findSqlById(id);
        if (p == null) return;

        List<com.proyecto.daw.model.Request> requests = requestRepository.findByProductId(id);
        
        for (com.proyecto.daw.model.Request r : requests) {
            String stateName = r.getState() != null ? r.getState().getName() : "";
            if (!stateName.equalsIgnoreCase("Entregada") && !stateName.equalsIgnoreCase("Denegada")) {
                throw new org.springframework.dao.DataIntegrityViolationException("No se puede eliminar el producto porque tiene solicitudes activas (pendientes o aprobadas).");
            }
        }

        p.setDisponibilidad(disponibilidadRepo.findById(3).orElse(null));
        productoRepository.save(p);
    }
}
