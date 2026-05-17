package com.proyecto.daw.controller;

import com.proyecto.daw.model.*;
import com.proyecto.daw.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriasProductoService categoriasService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private DonationService donationService;

    @Autowired
    private ContactService contactService;

    // --- ESTADÍSTICAS Y MENSAJES ---

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = new HashMap<>();

        // Usuarios
        stats.put("totalUsuarios", usuarioService.count());
        stats.put("totalAdmins", usuarioService.countByRol("ADMIN"));
        stats.put("totalParticulares", usuarioService.countByRol("PARTICULAR"));
        stats.put("totalEmpresas", usuarioService.countByRol("EMPRESA"));

        // Productos
        stats.put("totalProductos", productoService.countByDisponibilidad("Disponible") + productoService.countByDisponibilidad("Reservado"));
        stats.put("productosDisponibles", productoService.countByDisponibilidad("Disponible"));
        stats.put("productosReservados", productoService.countByDisponibilidad("Reservado"));

        // Solicitudes
        stats.put("totalSolicitudes", requestService.count() - requestService.countByEstado("Entregada") - requestService.countByEstado("Denegada"));
        stats.put("solicitudesPendientes", requestService.countByEstado("Pendiente"));
        stats.put("solicitudesEnRevision", requestService.countByEstado("En Revisión"));
        stats.put("solicitudesAprobadas", requestService.countByEstado("Aprobada"));
        stats.put("solicitudesDenegadas", requestService.countByEstado("Denegada"));
        stats.put("solicitudesEntregadas", requestService.countByEstado("Entregada"));

        // Donaciones
        stats.put("totalDonaciones", donationService.count());
        stats.put("donacionesPendientes", donationService.countByEstado("Pendiente"));
        stats.put("donacionesEnRecogida", donationService.countByEstado("En Recogida"));
        stats.put("donacionesRecibidas", donationService.countByEstado("Recibido"));

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/mensajes")
    public ResponseEntity<List<Map<String, Object>>> getMessages() {
        List<Contact> contactos = contactService.obtenerTodos();
        List<Map<String, Object>> resultado = contactos.stream().map(c -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", c.getId());
            dto.put("nombre", c.getNombre());
            dto.put("correo", c.getCorreo());
            dto.put("mensaje", c.getMensaje());
            dto.put("fechaEnvio", c.getFechaEnvio() != null ? c.getFechaEnvio().toString() : null);
            dto.put("respuesta", c.getRespuesta());
            dto.put("leido", c.isLeido());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/mensajes/{id}")
    public ResponseEntity<?> respondToMessage(@PathVariable int id, @RequestBody Map<String, String> body) {
        Contact contact = contactService.findById(id);
        if (contact == null) return ResponseEntity.notFound().build();

        String respuesta = body.get("respuesta");
        contact.setRespuesta(respuesta);
        contact.setLeido(true);
        contact.setFechaRespuesta(java.time.LocalDateTime.now());
        
        contactService.save(contact);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Mensaje respondido con éxito");
        return ResponseEntity.ok(response);
    }

    // --- GESTIÓN DE USUARIOS ---

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> getUsers() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> updateUser(@PathVariable int id, @RequestBody Usuario usuario) {
        usuario.setId(id);
        Usuario updated = usuarioService.actualizarUsuario(usuario);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/usuarios/{id}/rol")
    public ResponseEntity<?> updateRole(@PathVariable int id, @RequestBody Map<String, String> body) {
        try {
            String roleStr = body.get("rol");
            System.out.println(">>> Intentando actualizar rol de usuario " + id + " a: " + roleStr);

            if (roleStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El campo 'rol' es requerido"));
            }

            Rol nuevoRol = Rol.valueOf(roleStr.toUpperCase());
            Usuario updated = usuarioService.actualizarRol(id, nuevoRol);

            if (updated != null) {
                System.out.println(">>> Rol actualizado con éxito para " + updated.getNombre());
                return ResponseEntity.ok(updated);
            } else {
                System.out.println(">>> No se encontró el usuario con ID: " + id);
                return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado con ID: " + id));
            }
        } catch (IllegalArgumentException e) {
            System.err.println(">>> Rol inválido: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Rol inválido: " + body.get("rol")));
        } catch (Exception e) {
            System.err.println(">>> ERROR al actualizar rol: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        try {
            // Seguridad: El admin no puede borrarse a sí mismo
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                org.springframework.security.core.userdetails.UserDetails userDetails = (org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal();
                Usuario currentAdmin = usuarioService.findByCorreo(userDetails.getUsername());
                if (currentAdmin != null && currentAdmin.getId().equals(id)) {
                    return ResponseEntity.status(403).body(java.util.Map.of("error", "No puedes desactivar tu propia cuenta de administrador."));
                }
            }

            Usuario usuario = usuarioService.findById(id);
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }

            // Alternar estado activo (si era null o true -> false, si era false -> true)
            boolean nuevoEstado = usuario.getActivo() != null && !usuario.getActivo();

            // Si el nuevo estado es inactivo (desactivar), aplicamos las validaciones de negocio
            if (!nuevoEstado) {
                // 1. Validar donaciones pendientes (id_estado = 1) o en recogida (id_estado = 2)
                List<Donation> donaciones = donationService.findByUsuarioId(id);
                for (Donation d : donaciones) {
                    if (d.getEstado() != null && (d.getEstado().getId() == 1 || d.getEstado().getId() == 2)) {
                        return ResponseEntity.status(409).body(java.util.Map.of("error", "No se puede desactivar: El usuario tiene donaciones pendientes de recogida."));
                    }
                }

                // 2. Validar solicitudes aprobadas (id_estado = 3)
                List<Request> solicitudes = requestService.obtenerSolicitudesPorUsuario(id);
                for (Request r : solicitudes) {
                    if (r.getState() != null && r.getState().getId() == 3) {
                        return ResponseEntity.status(409).body(java.util.Map.of("error", "No se puede desactivar: El usuario tiene solicitudes aprobadas pendientes de entrega."));
                    }
                }

                // 3. Denegar automáticamente solicitudes en revisión (id_estado = 2) o pendientes (id_estado = 1)
                for (Request r : solicitudes) {
                    if (r.getState() != null && (r.getState().getId() == 1 || r.getState().getId() == 2)) {
                        requestService.updateStatus(r.getId(), 4); // 4 = Denegada
                    }
                }
            }

            usuario.setActivo(nuevoEstado);
            usuarioService.actualizarUsuario(usuario);

            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Error interno al intentar cambiar el estado del usuario."));
        }
    }

    @Autowired
    private EstadosProductoService estadosProductoService;

    @Autowired
    private DisponibilidadProductoService disponibilidadProductoService;

    // --- GESTIÓN DE PRODUCTOS ---

    @GetMapping("/productos/estados")
    public ResponseEntity<List<com.proyecto.daw.model.EstadosProducto>> getProductStates() {
        return ResponseEntity.ok(estadosProductoService.findAll());
    }

    @GetMapping("/productos/disponibilidades")
    public ResponseEntity<List<com.proyecto.daw.model.DisponibilidadProducto>> getProductAvailabilities() {
        return ResponseEntity.ok(disponibilidadProductoService.findAll());
    }

    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> getProducts(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String estado) {
        
        if (categoria != null) {
            return ResponseEntity.ok(productoService.findByCategoriaString(categoria));
        }
        if (estado != null) {
            return ResponseEntity.ok(productoService.findByEstadoString(estado));
        }
        return ResponseEntity.ok(productoService.findAll());
    }

    @PostMapping("/productos")
    public ResponseEntity<Producto> createProduct(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.save(producto));
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> updateProduct(@PathVariable int id, @RequestBody Producto producto) {
        producto.setId(id);
        return ResponseEntity.ok(productoService.save(producto));
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        try {
            productoService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body(java.util.Map.of("error", "No se puede eliminar el producto porque tiene solicitudes (pedidos) asociadas."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Error interno al intentar eliminar el producto."));
        }
    }

    @PostMapping("/productos/{id}/imagen")
    public ResponseEntity<Map<String, String>> uploadProductImage(@PathVariable int id, @RequestParam("file") MultipartFile file) {
        try {
            System.out.println(">>> RECIBIENDO IMAGEN PARA PRODUCTO ID: " + id);
            Producto producto = productoService.findById(id);
            if (producto == null) {
                System.out.println(">>> PRODUCTO NO ENCONTRADO");
                return ResponseEntity.notFound().build();
            }

            String uploadDir = "uploads/productos/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println(">>> DIRECTORIO CREADO: " + uploadPath.toAbsolutePath());
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            System.out.println(">>> GUARDANDO EN: " + filePath.toAbsolutePath());
            
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            producto.setImagenUrl(fileName);
            productoService.save(producto);
            System.out.println(">>> IMAGEN GUARDADA CON ÉXITO: " + fileName);

            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            System.err.println(">>> ERROR DE IO AL SUBIR IMAGEN: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            System.err.println(">>> ERROR INESPERADO AL SUBIR IMAGEN: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- GESTIÓN DE CATEGORÍAS ---

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriasProducto>> getCategories() {
        return ResponseEntity.ok(categoriasService.findAll());
    }

    @PostMapping("/categorias")
    public ResponseEntity<?> createCategory(@RequestBody CategoriasProducto categoria) {
        try {
            return ResponseEntity.ok(categoriasService.save(categoria));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable int id, @RequestBody CategoriasProducto categoria) {
        try {
            return ResponseEntity.ok(categoriasService.update(id, categoria));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable int id) {
        try {
            categoriasService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    // --- GESTIÓN DE SOLICITUDES ---

    @GetMapping("/solicitudes")
    public ResponseEntity<List<Request>> getRequests(@RequestParam(required = false) String estado) {
        return ResponseEntity.ok(requestService.findAll());
    }

    @PutMapping("/solicitudes/{id}/estado")
    public ResponseEntity<Request> updateRequestStatus(@PathVariable int id, @RequestBody Map<String, Integer> body) {
        try {
            Request updated = requestService.updateStatus(id, body.get("idEstado"));
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/solicitudes/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable int id) {
        requestService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // --- GESTIÓN DE DONACIONES ---

    @GetMapping("/donaciones")
    public ResponseEntity<List<Donation>> getDonations(@RequestParam(required = false) String estado) {
        return ResponseEntity.ok(donationService.findAll());
    }

    @PutMapping("/donaciones/{id}/estado")
    public ResponseEntity<Donation> updateDonationStatus(@PathVariable int id, @RequestBody Map<String, Integer> body) {
        try {
            Donation updated = donationService.updateStatus(id, body.get("idEstado"));
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/donaciones/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable int id) {
        donationService.delete(id);
        return ResponseEntity.ok().build();
    }
}
