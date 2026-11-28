package cl.duoc.ferreteria.services;

import cl.duoc.ferreteria.models.Producto;
import cl.duoc.ferreteria.repositories.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional
    public List<Producto> importarProductos(List<Producto> productos) {
        if (productos.size() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Máximo 500 productos por importación");
        }
        for (Producto producto : productos) {
            if (!productoRepository.existsById(producto.getId())) {
                productoRepository.save(producto);
            }
        }
        return productoRepository.findAll();
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarProducto(String id) {
        return productoRepository.findById(id);
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public boolean eliminarProducto(String id) {
        if (!productoRepository.existsById(id)) {
            return false;
        }

        productoRepository.deleteById(id);
        return true;
    }
}