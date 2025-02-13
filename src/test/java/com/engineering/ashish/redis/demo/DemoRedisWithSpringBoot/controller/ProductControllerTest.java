package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.controller;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity.Product;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
//to test use commanan in terminal  mvn -Dtest=ProductControllerTest test
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void testGetProductById_Success() {
        // Arrange
        Product mockProduct = new Product(1L, "Test Product", "ok");
        when(productService.getProductById(1L)).thenReturn(Optional.of(mockProduct));

        // Act
        ResponseEntity<Product> response = productController.getProductById(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockProduct, response.getBody());
        verify(productService).getProductById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        // Arrange
        when(productService.getProductById(2L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Product> response = productController.getProductById(2L);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(productService).getProductById(2L);
    }

    @Test
    void testGetAllProducts() {
        // Arrange
        List<Product> mockProducts = Arrays.asList(
            new Product(1L, "Product A", "Good"),
            new Product(2L, "Product B", "Exicilent")
        );
        when(productService.getAllProducts()).thenReturn(mockProducts);

        // Act
        List<Product> response = productController.getAllProducts();

        // Assert
        assertEquals(2, response.size());
        verify(productService).getAllProducts();
    }

    @Test
    void testCreateProduct() {
        // Arrange
        Product newProduct = new Product(3L, "New Product", "Inferior");
        when(productService.saveProduct(any(Product.class))).thenReturn(newProduct);

        // Act
        Product response = productController.createProduct(newProduct);

        // Assert
        assertEquals(newProduct, response);
        verify(productService).saveProduct(newProduct);
    }

    @Test
    void testUpdateProduct_Success() {
        // Arrange
        Product existingProduct = new Product(4L, "Old Product", "usefull");
        Product updatedProduct = new Product(4L, "Updated Product", "Nice");
        
        when(productService.getProductById(4L)).thenReturn(Optional.of(existingProduct));
        when(productService.saveProduct(any(Product.class))).thenReturn(updatedProduct);

        // Act
        ResponseEntity<Product> response = productController.updateProduct(4L, updatedProduct);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedProduct, response.getBody());
        verify(productService).getProductById(4L);
        verify(productService).saveProduct(updatedProduct);
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Arrange
        Product updatedProduct = new Product(5L, "Updated Product", "Very Bad");
        when(productService.getProductById(5L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Product> response = productController.updateProduct(5L, updatedProduct);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(productService).getProductById(5L);
        verify(productService, never()).saveProduct(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        // Arrange
        Product existingProduct = new Product(6L, "Product to Delete", "Very Nice");
        when(productService.getProductById(6L)).thenReturn(Optional.of(existingProduct));
        doNothing().when(productService).deleteProduct(6L);

        // Act
        ResponseEntity<Void> response = productController.deleteProduct(6L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(productService).getProductById(6L);
        verify(productService).deleteProduct(6L);
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Arrange
        when(productService.getProductById(7L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Void> response = productController.deleteProduct(7L);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(productService).getProductById(7L);
        verify(productService, never()).deleteProduct(anyLong());
    }
}
