package com.proyecto.daw.service;

import com.proyecto.daw.model.Contact;
import com.proyecto.daw.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public Contact guardarMensaje(Contact contacto) {
        return contactRepository.save(contacto);
    }

    public List<Contact> obtenerTodos() {
        return contactRepository.findAll();
    }

    public Contact findById(int id) {
        return contactRepository.findById(id).orElse(null);
    }

    public Contact save(Contact contact) {
        return contactRepository.save(contact);
    }
}