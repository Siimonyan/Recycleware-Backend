package com.proyecto.daw.config;

import com.proyecto.daw.model.Contact;
import com.proyecto.daw.repository.ContactRepository;
import com.proyecto.daw.repository.UsuarioRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final ContactRepository contactRepository;
    private final UsuarioRepository usuarioRepository;

    public DataInitializer(ContactRepository contactRepository, UsuarioRepository usuarioRepository) {
        this.contactRepository = contactRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initMensajes() {
        if (contactRepository.count() == 0) {
            System.out.println(">>> Insertando mensajes de contacto de prueba...");

            insertar("Luis Part", "luis.part@particular.com",
                    "Buenas tardes, queria preguntar cuanto tiempo tarda en procesarse una solicitud de producto una vez aprobada. Gracias.");

            insertar("Bela Emp", "bela.emp@empresa.com",
                    "Nos gustaria realizar una donacion de 15 monitores de 24 pulgadas en buen estado que ya no utilizamos. Como procedemos?");

            insertar("Aaron Part", "aaron.part@particular.com",
                    "He intentado iniciar sesion varias veces en la plataforma y no me deja acceder. Podeis ayudarme a recuperar mi cuenta?");

            insertar("Ricardo Emp", "ricardo.emp@empresa.com",
                    "Somos una empresa interesada en colaborar regularmente con donaciones de material informatico reacondicionado. Quien es el contacto adecuado?");

            insertar("Gines Emp", "Gines.emp@empresa.com",
                    "Tenemos 30 teclados y ratones que ya no utilizamos. Los aceptais aunque algunos tengan pequenos desperfectos esteticos?");

            System.out.println(">>> Mensajes de contacto insertados correctamente.");
        } else {
            System.out.println(">>> La tabla mensajes_contacto ya tiene " + contactRepository.count() + " registros. Omitiendo inicializacion.");
        }
    }

    private void insertar(String nombre, String correo, String mensaje) {
        Contact c = new Contact();
        c.setNombre(nombre);
        c.setCorreo(correo);
        c.setMensaje(mensaje);
        contactRepository.save(c);
    }
}
