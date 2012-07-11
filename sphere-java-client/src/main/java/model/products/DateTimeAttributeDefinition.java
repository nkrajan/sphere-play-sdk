package de.commercetools.sphere.client.model.products;

public class DateTimeAttributeDefinition extends AttributeDefinition {

    public DateTimeAttributeDefinition(String name, boolean isRequired, boolean isVariant) {
        super(name, isRequired, isVariant);
    }

    // for the JSON deserializer
    private DateTimeAttributeDefinition() { }
}