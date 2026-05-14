package com.proyecto.daw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.daw.model.Producto;
import com.proyecto.daw.model.Request;
import com.proyecto.daw.model.RequestState;
import com.proyecto.daw.model.Usuario;
import com.proyecto.daw.repository.RequestRepository;
import com.proyecto.daw.repository.RequestStateRepository;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestStateRepository requestStateRepository;

    @Autowired
    private UsuarioService UsuarioService;

    @Autowired
    private ProductoService productoService;

   
    public List<Request> findAll() {
        return requestRepository.findAll();
    }

 
    public Request crearSolicitud(int idSolicitante, int idProducto, String motivo) {
       
        Usuario solicitante = UsuarioService.findById(idSolicitante);
        Producto producto = productoService.findById(idProducto);

        
        RequestState estadoPendiente = requestStateRepository.findById(1).orElse(null);

        if (solicitante == null || producto == null || estadoPendiente == null) {
            throw new IllegalArgumentException("Error: No se ha encontrado el usuario, el producto o el estado.");
        }

        Request nuevaSolicitud = new Request();
        nuevaSolicitud.setApplicant(solicitante);
        nuevaSolicitud.setProduct(producto);
        nuevaSolicitud.setReason(motivo);
        nuevaSolicitud.setState(estadoPendiente);

        return requestRepository.save(nuevaSolicitud);
    }

    public List<Request> obtenerSolicitudesPorUsuario(int idSolicitante) {
        return requestRepository.findByApplicantId(idSolicitante);
    }

    public Request updateStatus(int idSolicitud, int idNuevoEstado) {
        
        Request solicitud = requestRepository.findById(idSolicitud).orElse(null);
        if (solicitud == null) {
            throw new IllegalArgumentException("Error: No se ha encontrado la solicitud con ID " + idSolicitud);
        }

        RequestState nuevoEstado = requestStateRepository.findById(idNuevoEstado).orElse(null);
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("Error: No se ha encontrado el estado con ID " + idNuevoEstado);
        }

        if (nuevoEstado.getName().equals("Aprobada") || nuevoEstado.getName().equals("Entregada")) {
            productoService.marcarProductoNoDisponibleById(solicitud.getProduct().getId());
        }

        solicitud.setState(nuevoEstado);



        return requestRepository.save(solicitud);
    }

    public long countEntregadas() {
        return requestRepository.countByStateName("Entregada");
    }

    public long count() {
        return requestRepository.count();
    }

    public void deleteById(int id) {
        requestRepository.deleteById(id);
    }

    public long countByEstado(String estadoNombre) {
        return requestRepository.countByStateName(estadoNombre);
    }
}