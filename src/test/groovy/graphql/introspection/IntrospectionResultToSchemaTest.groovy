package graphql.introspection

import graphql.language.AstPrinter
import graphql.language.InterfaceTypeDefinition
import graphql.language.ObjectTypeDefinition
import groovy.json.JsonSlurper
import spock.lang.Specification

class IntrospectionResultToSchemaTest extends Specification {

    def introspectionResultToSchema = new IntrospectionResultToSchema()

    def "create object"() {
        def input = """ {
            "kind": "OBJECT",
            "name": "QueryType",
            "description": null,
            "fields": [
              {
                "name": "hero",
                "description": null,
                "args": [
                  {
                    "name": "episode",
                    "description": "comment about episode",
                    "type": {
                      "kind": "ENUM",
                      "name": "Episode",
                      "ofType": null
                    },
                    "defaultValue": null
                  },
                  {
                    "name": "foo",
                    "description": "",
                    "type": {
                        "kind": "SCALAR",
                        "name": "String",
                        "ofType": null
                    },
                    "defaultValue": "bar"
                  }
                ],
                "type": {
                  "kind": "INTERFACE",
                  "name": "Character",
                  "ofType": null
                },
                "isDeprecated": false,
                "deprecationReason": null
              }
            ],
            "inputFields": null,
            "interfaces": [],
            "enumValues": null,
            "possibleTypes": null
      }
      """
        def slurper = new JsonSlurper()
        def parsed = slurper.parseText(input)

        when:
        ObjectTypeDefinition objectTypeDefinition = introspectionResultToSchema.createObject(parsed)
        AstPrinter astPrinter = new AstPrinter()
        def result = astPrinter.printAst(objectTypeDefinition)

        then:
        result == """type QueryType {
  hero(episode: Episode, foo: String = \"bar\"): Character
}"""

    }

    def "create interface"() {
        def input = """ {
        "kind": "INTERFACE",
        "name": "Character",
        "description": "A character in the Star Wars Trilogy",
        "fields": [
          {
            "name": "id",
            "description": "The id of the character.",
            "args": [
            ],
            "type": {
              "kind": "NON_NULL",
              "name": null,
              "ofType": {
                "kind": "SCALAR",
                "name": "String",
                "ofType": null
              }
            },
            "isDeprecated": false,
            "deprecationReason": null
          },
          {
            "name": "name",
            "description": "The name of the character.",
            "args": [
            ],
            "type": {
              "kind": "SCALAR",
              "name": "String",
              "ofType": null
            },
            "isDeprecated": false,
            "deprecationReason": null
          },
          {
            "name": "friends",
            "description": "The friends of the character, or an empty list if they have none.",
            "args": [
            ],
            "type": {
              "kind": "LIST",
              "name": null,
              "ofType": {
                "kind": "INTERFACE",
                "name": "Character",
                "ofType": null
              }
            },
            "isDeprecated": false,
            "deprecationReason": null
          },
          {
            "name": "appearsIn",
            "description": "Which movies they appear in.",
            "args": [
            ],
            "type": {
              "kind": "LIST",
              "name": null,
              "ofType": {
                "kind": "ENUM",
                "name": "Episode",
                "ofType": null
              }
            },
            "isDeprecated": false,
            "deprecationReason": null
          }
        ],
        "inputFields": null,
        "interfaces": null,
        "enumValues": null,
        "possibleTypes": [
          {
            "kind": "OBJECT",
            "name": "Human",
            "ofType": null
          },
          {
            "kind": "OBJECT",
            "name": "Droid",
            "ofType": null
          }
        ]
      }
      """
        def slurper = new JsonSlurper()
        def parsed = slurper.parseText(input)

        when:
        InterfaceTypeDefinition interfaceTypeDefinition = introspectionResultToSchema.createInterface(parsed)
        AstPrinter astPrinter = new AstPrinter()
        def result = astPrinter.printAst(interfaceTypeDefinition)

        then:
        result == """interface Character {
  #The id of the character.
  id: String!
  #The name of the character.
  name: String
  #The friends of the character, or an empty list if they have none.
  friends: [Character]
  #Which movies they appear in.
  appearsIn: [Episode]
}"""

    }
}

