package io.jvm
package test

import org.scalatest._

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JoinFeatureSpec extends FeatureSpec with GivenWhenThen with MustMatchers {
  feature("jvm.io.Join.escaping()"){
    def testBuildURI(uris: Array[String], separator: Character) =
      uris.map(_ replace(separator.toString, separator.toString * 2)).mkString(separator.toString)

    scenario("Escaping is a stable function") {
      Given("a null input string")
      When("it is escaped via a character")
      Then("a null string must be returned")
      Join.escaping(null, 'x') must equal (null)
    }

    scenario("Escaping an array contains null") {
      intercept[NullPointerException] {
        Join.escaping(Array(null, "fx"), 'x')
      }
    }

    scenario("Test 3 : array argument does not contain separator") {
      Join.escaping(Array("ab", "f"), 'x') must equal ("""abxf""")
    }

    scenario("Test 4 : array argument size = 0") {
      intercept[IllegalArgumentException] {
        Join.escaping(Array(), 'x')
      }
    }

    scenario("Test 5 : normal array argument") {
      Join.escaping(Array("axb", "fx"), 'x') must equal ("""axxbxfxx""")
    }

    scenario("Test 6 : array argument contains an empty string") {
      Join.escaping(Array("", "fx"), 'x') must equal ("""xfxx""")
    }

    scenario("Test 7 : Joining with a regular expression special character") {
      Given("a string consisting of some special characters")
      When("it is escaped via that character")
      Then("the special characters must be duplicated")
      Join.escaping(Array("a+b","c+d"), '+') must equal ("""a++b+c++d""")
    }
  }

  feature("jvm.io.buildSimpleUriList()") {
    def buildSimpleUriList = Join.escaping(_: Array[String], '/', '\'', ',');

    scenario("1 : null array argument") {
      buildSimpleUriList(null) must equal (null)
    }

    scenario("2 : empty array") {
      intercept[IllegalArgumentException] {
        buildSimpleUriList(Array())
      }
    }

    scenario("3 : array contains null") {
      intercept[NullPointerException] {
        buildSimpleUriList(Array("",null))
      }
    }

    scenario("4 : array contains empty string") {
        buildSimpleUriList(Array("","//","'")) must equal ("""'','/',''''""")
    }

    scenario("5 : simple build uri test without special chars") {
      buildSimpleUriList(Array("ab","cd")) must equal ("""'ab','cd'""")
    }

    scenario("6 : simple join uri test with both special chars") {
      buildSimpleUriList(Array("a'b","c//d")) must equal ("""'a''b','c/d'""")
    }

    scenario("7 : test a//b , a'//b , 'ab// , //ab', // ") {
      buildSimpleUriList(Array("a//b","a'//b","'ab//","//ab'","//")) must equal ("""'a/b','a''/b','''ab/','/ab''','/'""")
    }
  }

  feature("jvm.io.buildCompositeUriList()") {
    def buildCompositeUriList = Join.escaping(_: Array[String], '/', '\'', ',', '(', ')')

    scenario("1 : null array argument") {
      buildCompositeUriList(null) must equal (null)
    }

    scenario("2 : empty array") {
      intercept[IllegalArgumentException] {
        buildCompositeUriList(Array())
      }
    }

    scenario("3 : array contains null") {
      intercept[NullPointerException] {
        buildCompositeUriList(Array("",null))
      }
    }
    scenario("4.1 : array contains empty string") {
      buildCompositeUriList(Array("")) must equal ("""('')""")
    }

    scenario("4.2 : array contains ' character") {
      buildCompositeUriList(Array("a'b")) must equal ("""('a''b')""")
    }

    scenario("4.3 : array contains // characters") {
      buildCompositeUriList(Array("a//b")) must equal ("""('a/b')""")
    }

    scenario("4.4 : array contains / character") {
      buildCompositeUriList(Array("a/b")) must equal ("""('a','b')""")
    }

    scenario("5 : simple test without special chars") {
      buildCompositeUriList(Array("ab","cd")) must equal ("""('ab'),('cd')""")
    }

    scenario("6 : simple test with special chars") {
      buildCompositeUriList(Array("a'b","c//d","e/f")) must equal ("""('a''b'),('c/d'),('e','f')""")
    }

    scenario("7 : test / at both beginning and end, multiple // and /'/ ") {
      buildCompositeUriList(Array("/a//b//c/","/'/")) must equal ("""('','a/b/c',''),('','''','')""")
    }

    scenario("8 : more complex test (/////,/)") {
      buildCompositeUriList(Array("/////","/")) must equal ("""('//',''),('','')""")
    }

    scenario("9 : //a// and /a/ and 'a' ") {
      buildCompositeUriList(Array("//a//","/a/","'a'")) must equal ("""('/a/'),('','a',''),('''a''')""")
    }
  }
}
