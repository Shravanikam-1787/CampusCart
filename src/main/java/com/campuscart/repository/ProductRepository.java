package com.campuscart.repository;

import com.campuscart.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for stationery products.
 * Pre-populated with standard college stationery items.
 */
@Repository
public class ProductRepository {

    private final Map<Long, Product> productMap = new ConcurrentHashMap<>();

    public ProductRepository() {
        initializeSampleProducts();
    }

    private void initializeSampleProducts() {
        addProduct(new Product(1L, "Spiral Notebook", "Notebooks", 
                "200 pages, ruled, high-quality white paper ideal for lecture notes.", 60.0, "notebook", true));
        addProduct(new Product(2L, "Blue Ball Pen", "Pens & Pencils", 
                "Smooth writing 0.7mm tip, smudge-proof blue ink.", 10.0, "pen-blue", true));
        addProduct(new Product(3L, "Black Gel Pen", "Pens & Pencils", 
                "Precision 0.5mm tip, quick-drying dark black ink for exams.", 10.0, "pen-black", true));
        addProduct(new Product(4L, "Engineering Pencil", "Pens & Pencils", 
                "0.7mm mechanical pencil with comfortable grip and dark graphite lead.", 5.0, "pencil", true));
        addProduct(new Product(5L, "Clear File Folder", "Folders & Organizers", 
                "Durable plastic file folder with button closure for assignments.", 35.0, "folder", true));
        addProduct(new Product(6L, "Scientific Calculator", "Electronics", 
                "240 functions, 2-line display, essential for engineering math and labs.", 450.0, "calculator", true));
        addProduct(new Product(7L, "Drawing Sheets (Pack of 10)", "Exam & Drafting", 
                "Standard A3 cartridge drawing sheets for engineering graphics.", 25.0, "sheets", true));
        addProduct(new Product(8L, "College Ring Binder Folder", "Folders & Organizers", 
                "Hardcover 2-ring binder file with index divider tabs.", 50.0, "ring-folder", true));
        addProduct(new Product(9L, "College ID Card Holder", "Accessories", 
                "Transparent card pouch with heavy-duty clip-on campus lanyard.", 30.0, "id-card", true));
        addProduct(new Product(10L, "Fluorescent Highlighter", "Pens & Pencils", 
                "Bright neon highlighter chisel tip for textbook revision.", 25.0, "highlighter", true));
    }

    public void addProduct(Product product) {
        productMap.put(product.getId(), product);
    }

    public List<Product> findAll() {
        return new ArrayList<>(productMap.values());
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(productMap.get(id));
    }

    public List<Product> findByCategory(String category) {
        return productMap.values().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }
}
