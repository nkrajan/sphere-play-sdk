package io.sphere.client.shop.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import io.sphere.client.model.LocalizedString;
import io.sphere.client.model.VersionedId;
import io.sphere.client.model.products.BackendCategory;
import io.sphere.internal.util.Ext;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

// This is a user friendly representation of a category,
// built from raw BackendCategory returned by the backend (the conversion is handled by CategoryTreeImpl).

/** Category of a {@link io.sphere.client.shop.model.Product} in the product catalog. */
public class Category {
    @Nonnull private String id;
    @JsonProperty("version") private int version;
    private LocalizedString name;
    private LocalizedString description;
    private String slug;
    private Category parent;
    private ImmutableList<Category> children = ImmutableList.<Category>of();
    private ImmutableList<Category> pathInTree = ImmutableList.<Category>of();

    public Category(VersionedId id, LocalizedString name, LocalizedString description) {
        this.id = id.getId();
        this.version = id.getVersion();
        this.name = name;
        this.slug = Ext.slugify(name.get(Locale.ENGLISH));   // no editable slug in the backend?
        this.description = description;
    }

    private static Category fromBackendCategory(BackendCategory c) {
        return new Category(c.getIdAndVersion(), c.getName(), c.getDescription());
    }

    /** The unique id. */
    @Nonnull public String getId() { return id; }

    /** The {@link #getId() id} plus version. */
    @Nonnull public VersionedId getIdAndVersion() { return VersionedId.create(id, version); }

    /** Name of this category. */
    public String getName() { return name.get(); }
    public String getName(Locale locale) { return name.get(locale); }

    /** URL-friendly slug of this category. */
    public String getSlug() { return slug; }

    /** Description of this category. */
    public String getDescription() { return description.get(); }
    /** Description of this category for a given locale. If that doesn't exist it will return a random one. If there
        are no translations it will return the empty string.*/
    public String getDescription(Locale locale) { return description.get(locale); }

    /** Parent category of this category. Null if this category is one of the roots. */
    public Category getParent() { return parent; }

    /** True if this is one of root categories. Equivalent to #getParent being null. */
    public boolean isRoot() { return getParent() == null; }

    /** Child categories of this category. */
    public List<Category> getChildren() { return children; }

    /** The path to this category in the category tree, starting with the root and ending with this category. */
    public List<Category> getPathInTree() { return pathInTree; }

    /** The depth at which this category is in the category tree. Root categories have level one. */
    public int getLevel() { return getPathInTree().size(); }

    // -----------------------------------------------------
    // Build tree
    // -----------------------------------------------------

    public static List<Category> buildTree(List<BackendCategory> categories) {
        if (categories == null || categories.size() == 0) {
            return new ArrayList<Category>();
        }
        List<BackendCategory> backendRoots = new ArrayList<BackendCategory>();
        Multimap<String, BackendCategory> categoriesByParent = HashMultimap.create();
        // partition categories into roots and non-roots
        for (BackendCategory c: categories) {
            if (c.getParent().isEmpty()) {
                backendRoots.add(c);
            } else {
                categoriesByParent.put(c.getParent().getId(), c);
            }
        }
        return buildTreeRecursive(null, backendRoots, new ArrayList<Category>(), categoriesByParent);
    }

    private static List<Category> buildTreeRecursive(
            Category parent,
            Collection<BackendCategory> backendChildren,
            List<Category> pathInTree,
            Multimap<String, BackendCategory> categoriesByParent)
    {
        List<Category> result = new ArrayList<Category>();
        for (BackendCategory child : backendChildren) {
            Category c = Category.fromBackendCategory(child);
            pathInTree.add(c);
            // We need some (private) mutability - it's hard to build truly immutable object graphs with circular references
            // http://stackoverflow.com/questions/7507965/instantiating-immutable-paired-objects
            c.children = ImmutableList.copyOf(buildTreeRecursive(c, categoriesByParent.get(c.getId()), pathInTree, categoriesByParent));
            c.pathInTree = ImmutableList.copyOf(pathInTree);
            pathInTree.remove(pathInTree.size() - 1);    // c.pathInTree ends with c itself
            c.parent = parent;
            result.add(c);
        }
        return result;
    }
}
