package com.proyecto.daw.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/images")
@Controller
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*")
public class FileController {

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable int id) throws Exception {
        System.out.println(">>> PETICIÓN DE IMAGEN RECIBIDA: ID = " + id);
        String[] listaImagenes = {
                "static/images/resized_artecPc.jpg",
                "static/images/resized_Dell_Computer_Monitor.png",
                "static/images/resized_DellP2212Hb.jpg",
                "static/images/resized_LaptopBrokenScreen.jpg",
                "static/images/resized_LenovoG500.jpg",
                "static/images/resized_LogitechMouse.jpg",
                "static/images/resized_Old_Computer_Mouse.jpg",
                "static/images/resized_SapphireRadeonHD5570.jpg",
                "static/images/resizedIntelCorei7.jpg"
        };

        if (id < 0 || id >= listaImagenes.length) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Resource resource = new org.springframework.core.io.ClassPathResource(listaImagenes[id]);

        if (resource.exists()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}