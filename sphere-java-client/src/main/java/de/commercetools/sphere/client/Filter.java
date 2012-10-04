package de.commercetools.sphere.client;

/** Represents a query constraint when fetching products, such as 'attributes.color == "green"'. */
public interface Filter {
    /** Sphere HTTP API query parameter that this filter will get turned into. Mostly for debugging purposes. */
    QueryParam createQueryParam();
}