package com.shop.controller;

import com.shop.common.Result;
import com.shop.dto.ProductDTO;
import com.shop.entity.Product;
import com.shop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("商品控制器测试")
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product testProduct;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("iPhone 15");
        testProduct.setDescription("苹果手机");
        testProduct.setPrice(new BigDecimal("6999.00"));
        testProduct.setStock(100);
        testProduct.setCategoryId(1L);
        testProduct.setCategoryName("手机");
        testProduct.setStatus(1);
        testProduct.setSales(50);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("MacBook Pro");
        product2.setPrice(new BigDecimal("12999.00"));

        productList = Arrays.asList(testProduct, product2);
    }

    @Test
    @DisplayName("获取所有商品")
    void list() {
        when(productService.findAll()).thenReturn(productList);

        Result<List<Product>> result = productController.list();

        assertEquals(200, result.getCode());
        assertEquals(2, result.getData().size());
    }

    @Test
    @DisplayName("获取商品详情成功")
    void detail_Success() {
        when(productService.findById(1L)).thenReturn(testProduct);

        Result<Product> result = productController.detail(1L);

        assertEquals(200, result.getCode());
        assertEquals("iPhone 15", result.getData().getName());
    }

    @Test
    @DisplayName("获取商品详情失败 - 商品不存在")
    void detail_NotFound() {
        when(productService.findById(999L)).thenReturn(null);

        Result<Product> result = productController.detail(999L);

        assertEquals(500, result.getCode());
        assertEquals("商品不存在", result.getMessage());
    }

    @Test
    @DisplayName("根据分类获取商品")
    void listByCategory() {
        when(productService.findByCategoryId(1L)).thenReturn(Collections.singletonList(testProduct));

        Result<List<Product>> result = productController.listByCategory(1L);

        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().size());
    }

    @Test
    @DisplayName("搜索商品")
    void search() {
        when(productService.search("iPhone")).thenReturn(Collections.singletonList(testProduct));

        Result<List<Product>> result = productController.search("iPhone");

        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().size());
    }

    @Test
    @DisplayName("新增商品成功")
    void add_Success() {
        ProductDTO dto = new ProductDTO();
        dto.setName("新商品");
        dto.setPrice(new BigDecimal("999.00"));
        dto.setStock(50);
        dto.setCategoryId(1L);
        dto.setStatus(1);

        when(productService.add(any(Product.class))).thenReturn(true);

        Result<?> result = productController.add(dto);

        assertEquals(200, result.getCode());
    }

    @Test
    @DisplayName("新增商品失败")
    void add_Failure() {
        ProductDTO dto = new ProductDTO();
        dto.setName("新商品");
        dto.setPrice(new BigDecimal("999.00"));
        dto.setStock(50);
        dto.setCategoryId(1L);
        dto.setStatus(1);

        when(productService.add(any(Product.class))).thenReturn(false);

        Result<?> result = productController.add(dto);

        assertEquals(500, result.getCode());
        assertEquals("添加失败", result.getMessage());
    }

    @Test
    @DisplayName("更新商品成功")
    void update_Success() {
        ProductDTO dto = new ProductDTO();
        dto.setId(1L);
        dto.setName("更新商品");
        dto.setPrice(new BigDecimal("1999.00"));
        dto.setStock(100);
        dto.setCategoryId(1L);
        dto.setStatus(1);

        when(productService.update(any(Product.class))).thenReturn(true);

        Result<?> result = productController.update(dto);

        assertEquals(200, result.getCode());
    }

    @Test
    @DisplayName("更新商品失败 - ID为空")
    void update_NoId() {
        ProductDTO dto = new ProductDTO();
        dto.setName("更新商品");
        dto.setPrice(new BigDecimal("1999.00"));

        Result<?> result = productController.update(dto);

        assertEquals(500, result.getCode());
        assertEquals("商品ID不能为空", result.getMessage());
    }

    @Test
    @DisplayName("删除商品成功")
    void delete_Success() {
        when(productService.delete(1L)).thenReturn(true);

        Result<?> result = productController.delete(1L);

        assertEquals(200, result.getCode());
    }

    @Test
    @DisplayName("删除商品失败")
    void delete_Failure() {
        when(productService.delete(999L)).thenReturn(false);

        Result<?> result = productController.delete(999L);

        assertEquals(500, result.getCode());
        assertEquals("删除失败", result.getMessage());
    }

    @Test
    @DisplayName("获取热销商品")
    void hot() {
        Map<String, Object> hotProduct = new HashMap<>();
        hotProduct.put("id", 1L);
        hotProduct.put("name", "iPhone 15");
        hotProduct.put("salesCount", 50);

        when(productService.findHotProducts(10)).thenReturn(Collections.singletonList(hotProduct));

        Result<?> result = productController.hot(10);

        assertEquals(200, result.getCode());
    }
}
