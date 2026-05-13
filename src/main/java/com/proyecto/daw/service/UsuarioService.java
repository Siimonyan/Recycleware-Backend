package com.proyecto.daw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.daw.model.Usuario;
import com.proyecto.daw.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;


    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(int id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Long count() {
        return usuarioRepository.count();
    }

    public Long countByRol(String rolStr) {
        try {
            com.proyecto.daw.model.Rol rol = com.proyecto.daw.model.Rol.valueOf(rolStr.toUpperCase());
            return usuarioRepository.countByRol(rol);
        } catch (Exception e) {
            return 0L;
        }
    }

    public List<Usuario> findByNameContaining(String name) {
        return usuarioRepository.findByNameContaining(name);
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public Usuario actualizarUsuario(Usuario datosNuevos) {
        Usuario usuarioExistente = usuarioRepository.findById((int)datosNuevos.getId()).orElse(null);
        if (usuarioExistente == null) return null;

        if (datosNuevos.getCorreo() != null) usuarioExistente.setCorreo(datosNuevos.getCorreo());
        usuarioExistente.setNombre(datosNuevos.getNombre());
        usuarioExistente.setTelefono(datosNuevos.getTelefono());
        usuarioExistente.setDireccion(datosNuevos.getDireccion());
        usuarioExistente.setLocalidad(datosNuevos.getLocalidad());
        usuarioExistente.setCodigoPostal(datosNuevos.getCodigoPostal());
        usuarioExistente.setNombreContacto(datosNuevos.getNombreContacto());
        usuarioExistente.setRazonSocial(datosNuevos.getRazonSocial());
        
        if (datosNuevos.getRol() != null) {
            usuarioExistente.setRol(datosNuevos.getRol());
        }

        System.out.println(">>> GUARDANDO CAMBIOS DE USUARIO...");
        Usuario saved = usuarioRepository.save(usuarioExistente);
        System.out.println(">>> CAMBIOS GUARDADOS CON ÉXITO.");
        return saved;
    }

    public void deleteById(int id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario actualizarRol(int id, com.proyecto.daw.model.Rol nuevoRol) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario != null) {
            usuario.setRol(nuevoRol);
            return usuarioRepository.save(usuario);
        }
        return null;
    }
}
