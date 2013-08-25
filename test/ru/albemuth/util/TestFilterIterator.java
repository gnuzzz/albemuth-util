package ru.albemuth.util;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestFilterIterator extends TestCase {

    public void test() {
        try {
            List<Product> products = new ArrayList<Product>();
            for (int i = 1; i <= 100; i++) {
                products.add(new Product(i, i));
            }

            List<Product> filteredProducts = new ArrayList<Product>();
            for (Iterator<Product> it = new FilterIterator<Product>(products.iterator()) {
                protected boolean accept(Product product) {
                    return product.getPrice() > 50;
                }
            }; it.hasNext(); ) {
                filteredProducts.add(it.next());
            }
            assertEquals(50, filteredProducts.size());
            for (Product product: filteredProducts) {
                assertTrue(product.getPrice() > 50);
            }

            filteredProducts = new ArrayList<Product>();
            for (Iterator<Product> it = new FilterIterator<Product>(products.iterator()) {
                protected boolean accept(Product product) {
                    return product.getPrice() > 30;
                }
            }; it.hasNext(); ) {
                filteredProducts.add(it.next());
            }
            assertEquals(70, filteredProducts.size());
            for (Product product: filteredProducts) {
                assertTrue(product.getPrice() > 30);
            }

            filteredProducts = new ArrayList<Product>();
            for (Iterator<Product> it = new FilterIterator<Product>(products.iterator()) {
                protected boolean accept(Product product) {
                    return product.getPrice() > 70;
                }
            }; it.hasNext(); ) {
                filteredProducts.add(it.next());
            }
            assertEquals(30, filteredProducts.size());
            for (Product product: filteredProducts) {
                assertTrue(product.getPrice() > 70);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    class Product {

        private int id;
        private int price;

        Product(int id, int price) {
            this.id = id;
            this.price = price;
        }

        public int getId() {
            return id;
        }

        public int getPrice() {
            return price;
        }

    }

}
