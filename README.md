# Sphere Play SDK

[![Build Status](https://travis-ci.org/commercetools/sphere-play-sdk.png)](https://travis-ci.org/commercetools/sphere-play-sdk)

[SPHERE.IO](http://sphere.io) is the first Platform-as-a-Service solution for eCommerce.

The Sphere Play SDK comprises of two Java libraries designed for building shop applications on top of SPHERE.IO:

* standalone Java client for accessing Sphere HTTP APIs
* SDK for building online shops using the [Play Framework](http://www.playframework.com/)

## Building
This project is built using `sbt`. If you want to execute the tests run
```
sbt test
```

## Getting started

To use the Play SDK in your Play application, add the following to your build file:

````scala
resolvers += "sphere" at "http://public-repo.ci.cloud.commercetools.de/content/repositories/releases"

libraryDependencies += "io.sphere" %% "sphere-play-sdk" % "0.33" withSources()
````

To get started quickly, check out our [tutorial](http://sphere.io/dev/Play_SDK.html), which includes creation of a fully functional sample shop.

If you want to use just the Java client, the Maven dependency is:

````xml
<dependency>
  <groupId>io.sphere</groupId>
  <artifactId>sphere-java-client</artifactId>
  <version>0.33</version>
</dependency>
````

Until the artifacts are released to Maven Central, please use our public repo:
`http://public-repo.ci.cloud.commercetools.de/content/repositories/releases`

## License

The Sphere Play SDK is released under the
[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html). 

## Dependencies

This project uses the following open-source libraries:

- [AsyncHttpClient](https://github.com/AsyncHttpClient/async-http-client)
- [Guava](https://code.google.com/p/guava-libraries/)
- [Joda time](http://joda-time.sourceforge.net/)
- [Jackson](http://jackson.codehaus.org/)
- [Apache Commons Codec](http://commons.apache.org/proper/commons-codec/)
- [nv-i18n](https://github.com/TakahikoKawasaki/nv-i18n)

These libaries are all also released under the Apache 2.0 License.

## Design

### Data objects

All objects returned by the fetch, query and search APIs are pure data objects that have no HTTP connection to the backend.

This means you can't do the following:

```java
Review review = sphere.reviews.byId(id).fetch().orNull();
review.update(...);
```

Instead, all the modifications have to go through services. For example:

```java
Review review = sphere.reviews.byId(id).fetch().orNull();
Review updated = sphere.reviews.update(review.getIdAndVersion(), ...);
```

or

```java
Cart cart = sphere.currentCart().fetch();  // currentCart is a service
Cart updated = sphere.currentCart().addLineItem(...);
```

### Null values

All getters in the data model that are guaranteed to never return null are annotated using `javax.annotation.Nonnull`.
For example:

````java
/** The sum of prices of all line items. */
@Nonnull public Money getTotalPrice()
````

**Important:** Any getters that don't have a `Nonnull` annotation can return null!

#### Collections

Collections (lists, sets) returned by the SDK are never null. For extra clarity, all collection fields are also annotated
using `Nonnull`:

````java
/** Categories this product is in. */
@Nonnull public List<Category> getCategories()
````

#### Strings

The `Nonnull` annotation is also used for Strings that are guaranteed to never be empty, such as:

````java
/** Id of the customer who wrote the product review. */
@Nonnull public String getCustomerId()
````

When a string field is not set, we prefer returning an empty string over null. Nevertheless, you should always check for
empty strings using Guava's `Strings.isNullOrEmpty(s)`.


