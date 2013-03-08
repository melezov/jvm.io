package io.jvm
package test

import org.scalatest._

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class QuoteFeatureSpec extends FeatureSpec with GivenWhenThen with MustMatchers {

  feature("jvm.io knows how to escape a character by duplication"){
    info("When quoting a character via duplication,")
    info("every instance of the said character must be doubled")
    info("and the quote character must be appended to both ends")

    import Quote.escape

    scenario("Escape (dup) is a stable function") {
      Given("a null input string")
      When("it is escaped via a character")
      Then("a null string must be returned")
      escape(null, 'q') must equal (null)
    }

    scenario("Escape (dup) handling of empty strings") {
      Given("an empty input string")
      When("it is escaped via a character")
      Then("a string with two quote characters must be returned")
      escape("", 'q') must equal ("qq")
    }

    scenario("Escape (dup) handling of a quote character") {
      Given("a string consisting of a single character")
      When("it is escaped via that character")
      Then("a string with four quote characters must be returned")
      escape("q", 'q') must equal ("q" * 4)
    }

    scenario("Escape (dup) handling of a non-quote character") {
      Given("a string consisting of a single character")
      When("it is escaped via a different character")
      Then("the original string must be surrounded by the quote characters")
      escape("a", 'q') must equal ('q' + "a" + 'q')
    }

    scenario("Escape (dup) handling of quote characters") {
      Given("a string consisting of multiple quote characters")
      When("it is escaped via that character")
      Then("the string must be duplicated")
      And("be surrounded by the quote characters")
      escape("qqq", 'q') must equal ('q' + ("qqq" * 2) + 'q')
    }

    scenario("Escape (dup) handling of non-quote characters") {
      Given("a string consisting of multiple non-quote characters")
      When("it is escaped via a non-present character")
      Then("the string must remain the same")
      And("be surrounded by the quote characters")
      escape("aaa", 'q') must equal ("q" + "aaa" + "q")
    }

    scenario("Escape (dup) handling of a sentence containing the quote characters") {
      Given("a string consisting of some quote characters")
      When("it is escaped via that character")
      Then("the quoted characters must be duplicated")
      And("the string must be surrounded by those characters")
      escape("It's 'OK'!", '\'') must equal ("'It''s ''OK''!'")
    }
  }

  feature("jvm.io knows how to escape a character via escapeing"){
    info("When quoting a character via escaping, every instance of the said")
    info("character and of its escape must be preceeded by the escape character")
    info("and the quote character must be appended to both ends")

    import Quote.escape

    scenario("Escape (esc) is a stable function") {
      Given("a null input string")
      When("it is escaped via a character")
      Then("a null string must be returned")
      escape(null, 'q', 'r') must equal (null)
    }

    scenario("Escape (esc) handling of empty strings") {
      Given("an empty input string")
      When("it is escaped via a character")
      Then("a string with two quote characters must be returned")
      escape("", 'q', 'r') must equal ("qq")
    }

    scenario("Escape (esc) handling of a quote character") {
      Given("a string consisting of a single character")
      When("that character is the quote character")
      Then("an escape charater must preceed the quote character in the string")
      escape("q", 'q', 'r') must equal ("qrqq")
    }

    scenario("Escape (esc) handling of an escape character") {
      Given("a string consisting of a single character")
      When("that character is the escape character")
      Then("an escape charater must be duplicated")
      escape("r", 'q', 'r') must equal ("qrrq")
    }

    scenario("Escape (esc) handling of a non-quote character") {
      Given("a string consisting of a single character")
      When("it is escaped via a different character")
      Then("the original string must be surrounded by the quote characters")
      escape("a", 'q', 'r') must equal ('q' + "a" + 'q')
    }

    scenario("Escape (esc) handling of quote characters") {
      Given("a string consisting of multiple quote characters")
      When("it is escaped via that character")
      Then("the string must be doubled in size")
      And("have interleaved escape characters for each quote")
      escape("qqq", 'q', 'r') must equal ('q' + ("rq" * "qqq".length) + 'q')
    }

    scenario("Escape (esc) handling of escape characters") {
      Given("a string consisting of multiple escape characters")
      When("it is escaped via a quote character")
      Then("the string must be duplicated")
      And("be surrounded by the quote characters")
      escape("rrr", 'q', 'r') must equal ('q' + ("rrr" * 2) + 'q')
    }

    scenario("Escape (esc) handling of non-quote characters") {
      Given("a string consisting of multiple non-quote, non-escape characters")
      When("it is escaped via a non-present character")
      And("the escape character is also not present")
      Then("the string must remain the same")
      And("be surrounded by the quote characters")
      escape("aaa", 'q', 'r') must equal ("q" + "aaa" + "q")
    }

    scenario("Escape handling of a sentence containing the quote and escape characters") {
      Given("a string consisting of some quote and escape characters")
      When("it is escaped via those characters")
      Then("the quoted characters must be escaped")
      And("the escape characters must be duplicated")
      And("the string must be surrounded by the quote characters")
      escape("""It's '\OK\'!""", '\'', '\\') must equal ("""'It\'s \'\\OK\\\'!'""")
    }
  }
}
