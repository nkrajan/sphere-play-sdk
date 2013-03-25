package io.sphere.client;

import io.sphere.internal.util.Util;
import io.sphere.client.model.Reference;
import io.sphere.client.shop.model.Catalog;
import net.jcip.annotations.Immutable;

/** Centralizes construction of backend HTTP API endpoints. */
@Immutable
public class ProjectEndpoints {
    private final String projectUrl;
    public final ProductEndpoints products    = new ProductEndpoints();
    public final CategoryEndpoints categories = new CategoryEndpoints();
    public final CustomerEndpoints customers  = new CustomerEndpoints();
    public final CartEndpoints carts          = new CartEndpoints();
    public final OrderEndpoints orders        = new OrderEndpoints();
    public final ReviewEndpoints reviews      = new ReviewEndpoints();
    public final CommentEndpoints comments    = new CommentEndpoints();
    public final InventoryEndpoints inventory = new InventoryEndpoints();

    public ProjectEndpoints(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public class ProductEndpoints {
        public String root()                     { return projectUrl + "/product-projections"; }
        public String byId(String id)            { return root() + "/" + id; }
        public String bySlug(String slug)        { return root() + "?where=" + Util.urlEncode("slug=\"" + slug + "\""); }
        public String search()                   { return root() + "/search"; }
    }

    public class CategoryEndpoints {
        public String root()              { return projectUrl + "/categories"; }
        public String category(String id) { return root() + "/" + id; }
    }

    public class OrderEndpoints {
        public String root()                { return projectUrl + "/orders"; }
        public String byId(String id)       { return root() + "/" + id; }
        public String updatePaymentState()  { return root() + "/payment-state"; }
        public String updateShipmentState() { return root() + "/shipment-state"; }

        public String queryByCustomerId(String customerId) {
            return root() + customerIdQuery(customerId);
        }
    }

    public class CartEndpoints {
        public String root()                        { return projectUrl + "/carts"; }
        public String byId(String id)               { return root() + "/" + id; }
        public String byCustomer(String customerId) { return root() + "/by-customer?customerId=" + customerId; }
        public String setShippingAddress()          { return root() + "/shipping-address"; }
        public String order()                       { return root() + "/order"; }
        public String loginWithAnonymousCart()      { return root() + "/login"; }
        private String lineItems()                  { return root() + "/line-items"; }
        public String addLineItem()                 { return lineItems(); }
        public String removeLineItem()              { return lineItems() + "/remove"; }
        public String updateLineItemQuantity()      { return lineItems() + "/quantity"; }
        public String increaseLineItemQuantity()    { return lineItems() + "/increase-quantity"; }
        public String decreaseLineItemQuantity()    { return lineItems() + "/decrease-quantity"; }
    }

    public class CustomerEndpoints {
        public String root()                        { return projectUrl + "/customers"; }
        public String signupWithCart()              { return root() + "/with-cart"; }
        public String byId(String id)               { return root() + "/" + id; }
        public String updateCustomer()              { return root() + "/update"; }
        public String changePassword()              { return root() + "/password"; }
        public String setDefaultShippingAddress()   { return root() + "/default-shipping-address"; }
        public String changeShippingAddress()       { return shippingAddresses() + "/change"; }
        public String removeShippingAddress()       { return shippingAddresses() + "/remove"; }
        public String createPasswordResetToken()    { return root() + "/password-token"; }
        public String resetPassword()               { return root() + "/password/reset"; }
        public String createEmailVerificationToken(){ return root() + "/email-token"; }
        public String confirmEmail()                { return root() + "/email/confirm"; }
        private String shippingAddresses()          { return root() + "/addresses"; }

        public String login(String email, String password) {
            return root() + "/authenticated?" + "email=" + Util.urlEncode(email) + "&password=" + Util.urlEncode(password);
        }
        public String byToken(String token) {
            return root() + "/by-token?token=" + Util.urlEncode(token);
        }
    }

    public class ReviewEndpoints {
        public String root()            { return projectUrl + "/reviews"; }
        public String byId(String id)   { return root() + "/" + id; }
        public String update()          { return root() + "/update"; }

        public String queryByCustomerId(String customerId) {
            return root() + customerIdQuery(customerId);
        }
        public String queryByProductId(String productId)   {
            return root() + productIdQuery(productId);
        }

        public String queryByCustomerIdProductId(String customerId, String productId) {
            return root() + "?where=" +
                    Util.urlEncode("customerId=\"" + customerId + "\" and productId=\"" + productId + "\"");
        }
    }

    public class CommentEndpoints {
        public String root()            { return projectUrl + "/comments"; }
        public String byId(String id)   { return root() + "/" + id; }
        public String update()          { return root() + "/update"; }

        public String queryByCustomerId(String customerId) {
            return root() + customerIdQuery(customerId);
        }
        public String queryByProductId(String productId)   {
            return root() + productIdQuery(productId);
        }
    }

    public class InventoryEndpoints {
        public String root()            { return projectUrl + "/inventory"; }
        public String byId(String id)   { return root() + "/" + id; }

        public String byVariantInCatalog(String productId, String variantId, Reference<Catalog> catalog) {
            String catalogQuery;
            if (catalog == null) { catalogQuery = "catalog IS NOT DEFINED"; }
            else { catalogQuery = referenceComparisonExpression("catalog", catalog); }
            return root() + "?where=" + Util.urlEncode(
                    "productId=\"" + productId + "\" and variantId=" + variantId + " and " + catalogQuery);
        }
    }

    // -----------------------
    // Helpers
    // -----------------------

    private String customerIdQuery(String customerId) {
        return "?where=" + Util.urlEncode("customerId=\"" + customerId + "\"");
    }

    private String productIdQuery(String productId) {
        return "?where=" + Util.urlEncode("productId=\"" + productId + "\"");
    }

    private String referenceComparisonExpression(String referenceFieldName, Reference reference) {
        return referenceFieldName + "(typeId=\"" + reference.getTypeId() + "\" and id=\"" + reference.getId() + "\")";
    }
}