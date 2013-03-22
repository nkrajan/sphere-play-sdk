package de.commercetools.internal;

import de.commercetools.internal.util.Log;
import de.commercetools.sphere.client.model.Reference;
import de.commercetools.sphere.client.model.products.BackendCategory;
import de.commercetools.sphere.client.model.products.BackendProduct;
import de.commercetools.sphere.client.shop.CategoryTree;
import de.commercetools.sphere.client.shop.model.*;

import java.util.ArrayList;
import java.util.List;

/** Converts products from the raw backend format into {@link de.commercetools.sphere.client.shop.model.Product}s.
 *  Products have references to categories resolved, as opposed to BackendProduct which hold raw References. */
public class ProductConversion {
    public static List<Product> fromBackendProducts(List<BackendProduct> rawProducts, CategoryTree categoryTree) {
        if (rawProducts == null) {
            return new ArrayList<Product>();
        }
        List<Product> result = new ArrayList<Product>(rawProducts.size());
        for (BackendProduct p : rawProducts) {
            result.add(fromBackendProduct(p, categoryTree));
        }
        return result;
    }

    public static Product fromBackendProduct(BackendProduct p, CategoryTree categoryTree) {
        List<Category> categories = new ArrayList<Category>(p.getCategories().size());
        for (Reference<BackendCategory> categoryReference : p.getCategories()) {
            Category resolved = categoryTree.getById(categoryReference.getId());
            if (resolved != null) {
                categories.add(resolved);
            } else {
                Log.warn(String.format("Product %s (%s) has an unknown category: %s", p.getId(), p.getName(), categoryReference.getId()));
            }
        }
        return new Product(
                p.getId(), p.getVersion(), p.getName(), p.getDescription(), p.getVendor(), p.getSlug(),
                p.getMetaTitle(), p.getMetaDescription(), p.getMetaKeywords(), p.getMasterVariant(),
                p.getVariants(), categories, p.getCatalogs(), p.getCatalog(), p.getReviewRating());
    }
}