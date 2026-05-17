package com.proyecto.daw.service; // Ajustado a tu paquete

import com.proyecto.daw.model.Usuario;
import com.proyecto.daw.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository userRepository;

    public CustomUserDetailsService(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
   
        Usuario usuario = userRepository.findByCorreo(email);
        
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con el correo: " + email);
        }

        String rolActual = usuario.getRol().name(); 

        return User.withUsername(usuario.getCorreo())
                .password(usuario.getPassword()) 
                .authorities("ROLE_" + rolActual)
                .disabled(usuario.getActivo() != null && !usuario.getActivo())
                .build();
    }
}