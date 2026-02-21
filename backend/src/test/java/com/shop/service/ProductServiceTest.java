package com.shop.service;

import com.shop.entity.Product;
import com.shop.mapper.ProductMapper;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("商品服务单元测试")
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

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
    }

    @Test
    @DisplayName("查询所有商品")
    void findAll() {
        List<Product> products = Arrays.asList(testProduct, new Product());
        when(productMapper.findAll()).thenReturn(products);

        List<Product> result = productService.findAll();

        assertEquals(2, result.size());
        verify(productMapper).findAll();
    }

    @Test
    @DisplayName("根据ID查询商品")
    void findById() {
        when(productMapper.findById(1L)).thenReturn(testProduct);

        Product result = productService.findById(1L);

        assertNotNull(result);
        assertEquals("iPhone 15", result.getName());
    }

    @Test
    @DisplayName("根据ID查询商品 - 不存在")
    void findById_NotFound() {
        when(productMapper.findById(999L)).thenReturn(null);

        Product result = productService.findById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("根据分类ID查询商品")
    void findByCategoryId() {
        List<Product> products = Collections.singletonList(testProduct);
        when(productMapper.findByCategoryId(1L)).thenReturn(products);

        List<Product> result = productService.findByCategoryId(1L);

        assertEquals(1, result.size());
        assertEquals("iPhone 15", result.get(0).getName());
    }

    @Test
    @DisplayName("搜索商品")
    void search() {
        List<Product> products = Collections.singletonList(testProduct);
        when(productMapper.search("iPhone")).thenReturn(products);

        List<Product> result = productService.search("iPhone");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("新增商品成功")
    void add_Success() {
        when(productMapper.insert(any(Product.class))).thenReturn(1);

        boolean result = productService.add(testProduct);

        assertTrue(result);
        verify(productMapper).insert(testProduct);
    }

    @Test
    @DisplayName("新增商品失败")
    void add_Failure() {
        when(productMapper.insert(any(Product.class))).thenReturn(0);

        boolean result = productService.add(testProduct);

        assertFalse(result);
    }

    @Test
    @DisplayName("更新商品成功")
    void update_Success() {
        when(productMapper.update(any(Product.class))).thenReturn(1);

        boolean result = productService.update(testProduct);

        assertTrue(result);
    }

    @Test
    @DisplayName("删除商品成功")
    void delete_Success() {
        when(productMapper.deleteById(1L)).thenReturn(1);

        boolean result = productService.delete(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("统计商品数量")
    void count() {
        when(productMapper.count()).thenReturn(100);

        int result = productService.count();

        assertEquals(100, result);
    }

    @Test
    @DisplayName("查询热销商品")
    void findHotProducts() {
        Map<String, Object> product1 = new HashMap<>();
        product1.put("id", 1L);
        product1.put("name", "iPhone 15");
        product1.put("salesCount", 50);
        product1.put("salesAmount", new BigDecimal("349950.00"));

        List<Map<String, Object>> hotProducts = Collections.singletonList(product1);
        when(productMapper.findHotProducts(10)).thenReturn(hotProducts);

        List<Map<String, Object>> result = productService.findHotProducts(10);

        assertEquals(1, result.size());
        assertEquals("iPhone 15", result.get(0).get("name"));
    }

    // ==================== 边缘情况测试 ====================

    @Test
    @DisplayName("查询所有商品 - 空列表")
    void findAll_Empty() {
        when(productMapper.findAll()).thenReturn(Collections.emptyList());

        List<Product> result = productService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("根据分类ID查询商品 - 无商品")
    void findByCategoryId_Empty() {
        when(productMapper.findByCategoryId(999L)).thenReturn(Collections.emptyList());

        List<Product> result = productService.findByCategoryId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("搜索商品 - 空关键词")
    void search_EmptyKeyword() {
        when(productMapper.search("")).thenReturn(Collections.emptyList());

        List<Product> result = productService.search("");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("搜索商品 - 无结果")
    void search_NoResults() {
        when(productMapper.search("不存在的商品")).thenReturn(Collections.emptyList());

        List<Product> result = productService.search("不存在的商品");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("搜索商品 - 特殊字符")
    void search_SpecialCharacters() {
        when(productMapper.search("!@#$%")).thenReturn(Collections.emptyList());

        List<Product> result = productService.search("!@#$%");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("更新商品失败")
    void update_Failure() {
        when(productMapper.update(any(Product.class))).thenReturn(0);

        boolean result = productService.update(testProduct);

        assertFalse(result);
    }

    @Test
    @DisplayName("删除商品失败")
    void delete_Failure() {
        when(productMapper.deleteById(999L)).thenReturn(0);

        boolean result = productService.delete(999L);

        assertFalse(result);
    }

    @Test
    @DisplayName("统计商品数量 - 零")
    void count_Zero() {
        when(productMapper.count()).thenReturn(0);

        int result = productService.count();

        assertEquals(0, result);
    }

    @Test
    @DisplayName("查询热销商品 - 空列表")
    void findHotProducts_Empty() {
        when(productMapper.findHotProducts(10)).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = productService.findHotProducts(10);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("查询热销商品 - 零个")
    void findHotProducts_ZeroLimit() {
        when(productMapper.findHotProducts(0)).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = productService.findHotProducts(0);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("查询热销商品 - 负数")
    void findHotProducts_NegativeLimit() {
        when(productMapper.findHotProducts(-1)).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = productService.findHotProducts(-1);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("新增商品 - 价格为零")
    void add_ZeroPrice() {
        testProduct.setPrice(BigDecimal.ZERO);
        when(productMapper.insert(any(Product.class))).thenReturn(1);

        boolean result = productService.add(testProduct);

        assertTrue(result);
    }

    @Test
    @DisplayName("新增商品 - 库存为零")
    void add_ZeroStock() {
        testProduct.setStock(0);
        when(productMapper.insert(any(Product.class))).thenReturn(1);

        boolean result = productService.add(testProduct);

        assertTrue(result);
    }

    @Test
    @DisplayName("新增商品 - 负库存")
    void add_NegativeStock() {
        testProduct.setStock(-10);
        when(productMapper.insert(any(Product.class))).thenReturn(1);

        boolean result = productService.add(testProduct);

        assertTrue(result);
    }

    @Test
    @DisplayName("新增商品 - 空名称")
    void add_EmptyName() {
        testProduct.setName("");
        when(productMapper.insert(any(Product.class))).thenReturn(1);

        boolean result = productService.add(testProduct);

        assertTrue(result);
    }

    @Test
    @DisplayName("搜索商品 - Unicode关键词")
    void search_UnicodeKeyword() {
        when(productMapper.search("苹果手机")).thenReturn(Collections.singletonList(testProduct));

        List<Product> result = productService.search("苹果手机");

        assertEquals(1, result.size());
    }
}
