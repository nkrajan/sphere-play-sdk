package de.commercetools.sphere.client
package shop

import de.commercetools.internal.{CustomerCommands, CommandBase, CommandRequestBuilderImpl, RequestBuilderImpl}
import de.commercetools.sphere.client.shop.model.{Name, CustomerUpdate, Address, Customer}
import de.commercetools.internal.CustomerCommands.{AddShippingAddress, ChangeEmail, ChangeName}
import de.commercetools.sphere.client.util.CommandRequestBuilder

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers

class CustomersSpec extends WordSpec with MustMatchers {

  val customerId = "764c4d25-5d04-4999-8a73-0cf8570f7601"
  val customerJson =
    """{
        "type":"Consumer",
        "id":"%s",
        "version":0,
        "email":"em@ail.com",
        "firstName":"hans",
        "lastName":"wurst",
        "password":"p75aPGdoBK62KSHuWcoWrw==$LMnb/9st6JhKFS0gBMx/zOBV3MVY+cbC2qBFR7aeutg=",
        "middleName":"the horrible",
        "title":"sir",
        "shippingAddresses":[]}""".format(customerId)

  val customerShopClient = Mocks.mockShopClient(customerJson)
  val testAddress = new Address("Alexanderplatz")

  // downcast to be able to test some request properties which are not public for shop developers
  private def asImpl(reqBuilder: RequestBuilder[Customer]) = reqBuilder.asInstanceOf[RequestBuilderImpl[Customer]]
  private def asImpl(reqBuilder: CommandRequestBuilder[Customer]) = reqBuilder.asInstanceOf[CommandRequestBuilderImpl[Customer]]

  private def checkIdAndVersion(cmd: CommandBase): Unit = {
    cmd.getId() must be (customerId)
    cmd.getVersion() must be (1)
  }

  "Get all customers" in {
    val shopClient = Mocks.mockShopClient("{}")
    shopClient.customers.all().fetch.getCount must be(0)
  }

  "Get customer byId" in {
    val reqBuilder = customerShopClient.customers.byId(customerId)
    asImpl(reqBuilder).getRawUrl must be ("/consumers/" + customerId)
    val customer = reqBuilder.fetch()
    customer.getId() must be(customerId)
  }

  "Create customer" in {
    val reqBuilder = asImpl(customerShopClient.customers.signup("em@ail.com", "secret", "hans", "wurst", "don", "sir"))
    reqBuilder.getRawUrl must be("/consumers")
    val cmd = reqBuilder.getCommand.asInstanceOf[CustomerCommands.CreateCustomer]
    cmd.getEmail must be ("em@ail.com")
    cmd.getPassword must be ("secret")
    cmd.getFirstName must be ("hans")
    cmd.getLastName must be ("wurst")
    cmd.getMiddleName must be ("don")
    cmd.getTitle must be ("sir")
    val customer: Customer = reqBuilder.execute()
    customer.getId() must be(customerId)
  }

  "Login" in {
    val reqBuilder = asImpl(customerShopClient.customers.login("em@ail.com", "secret"))
    reqBuilder.getRawUrl must be("/consumers/authenticated?email=em@ail.com&password=secret")
    val customer: Customer = reqBuilder.fetch()
    customer.getId() must be(customerId)
  }

  "Change password" in {
    val reqBuilder = asImpl(customerShopClient.customers.changePassword(customerId, 1, "old", "new"))
    reqBuilder.getRawUrl must be("/consumers/password")
    val cmd = reqBuilder.getCommand.asInstanceOf[CustomerCommands.ChangePassword]
    checkIdAndVersion(cmd)
    cmd.getCurrentPassword must be ("old")
    cmd.getNewPassword must be ("new")
    val customer: Customer = reqBuilder.execute()
    customer.getId() must be(customerId)
  }

  "Change shipping address" in {
    val reqBuilder = asImpl(customerShopClient.customers.changeShippingAddress(customerId, 1, 0, testAddress))
    reqBuilder.getRawUrl must be("/consumers/shipping-addresses/change")
    val cmd = reqBuilder.getCommand.asInstanceOf[CustomerCommands.ChangeShippingAddress]
    checkIdAndVersion(cmd)
    cmd.getAddress.getFullAddress must be (testAddress.getFullAddress)
    cmd.getAddressIndex must be (0)
    val customer: Customer = reqBuilder.execute()
    customer.getId() must be(customerId)
  }

  "Remove shipping address" in {
    val reqBuilder = asImpl(customerShopClient.customers.removeShippingAddress(customerId, 1, 0))
    reqBuilder.getRawUrl must be("/consumers/shipping-addresses/remove")
    val cmd = reqBuilder.getCommand.asInstanceOf[CustomerCommands.RemoveShippingAddress]
    checkIdAndVersion(cmd)
    cmd.getAddressIndex must be (0)
    val customer: Customer = reqBuilder.execute()
    customer.getId() must be(customerId)
  }

  "Set default shipping address" in {
    val reqBuilder = asImpl(customerShopClient.customers.setDefaultShippingAddress(customerId, 1, 0))
    reqBuilder.getRawUrl must be("/consumers/shipping-addresses/default")
    val cmd = reqBuilder.getCommand.asInstanceOf[CustomerCommands.SetDefaultShippingAddress]
    checkIdAndVersion(cmd)
    cmd.getAddressIndex must be (0)
    val customer: Customer = reqBuilder.execute()
    customer.getId() must be(customerId)
  }

  "Update" in {
    val update = new CustomerUpdate();
    update.setEmail("new@mail.com")
    update.setName(new Name("updatedFirst", "updatedLast"))
    update.addShippingAddress(new Address("Alex"))
    update.addShippingAddress(new Address("Zoo"))
    val reqBuilder = asImpl(customerShopClient.customers.updateCustomer(customerId, 1, update))
    reqBuilder.getRawUrl must be("/consumers/update")
    val cmd = reqBuilder.getCommand.asInstanceOf[CustomerCommands.UpdateCustomer]
    checkIdAndVersion(cmd)
    val actions = scala.collection.JavaConversions.asScalaBuffer((cmd.getActions)).toList
    actions.length must be (4)
    actions.collect({ case a: ChangeName => a})
      .count(cn => cn.getFirstName == "updatedFirst" && cn.getLastName == "updatedLast") must be (1)
    actions.collect({ case a: ChangeEmail => a}).count(_.getEmail == "new@mail.com") must be (1)
    actions.collect({ case a: AddShippingAddress => a}).count(_.getAddress.getFullAddress == "Alex") must be (1)
    actions.collect({ case a: AddShippingAddress => a}).count(_.getAddress.getFullAddress == "Zoo") must be (1)
    val customer: Customer = reqBuilder.execute()
    customer.getId() must be(customerId)
  }

}