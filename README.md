# Formular

[![Build Status](https://travis-ci.org/auktion-markt/formular.svg?branch=master)](https://travis-ci.org/auktion-markt/formular)

The goal of *Formular* is to provide an easy-to-use interface for creating, handling and rendering forms.

## Features

For a better understanding of what Formular provides, see `Concepts`-section.

 * Automatically create form mappings out of annotated models (manual or custom mappings are supported too)
 * Support for JPA relations (`@ManyToMany`, `@OneToOne`, `@OneToMany` and `@ManyToOne`)
 * Partial support include HTML5 validation tags based JSR-380 annotations (`@Min`, `@Max`,
   `@DecimalMin`, `@DecimalMax`)
 * Custom field types

## Concepts

*Formular* can be divided into three layers of functionality:

 * Mapping and specification representation
 * State representation
 * Rendering

**Mapping** is the static part of forms (the layout) can be done automatically using annotations (see
`de.auktionmarkt.formular.specification.annotation`). You are also able to do it manually by constructing
`de.auktionmarkt.formular.specification.FormSpecification` or by implementing 
`de.auktionmarkt.formular.specification.mapper.FormMapper`. It is recommended to do mapping in controller
constructors (when you need the specification just in the controller) or as a named bean (when you need the
specification for multiple classes).

**State representation** is the dynamic part of forms (the values and validation results). It will contains a string
representation of each field value, ready for embedding it in html directly. Obviously it requires a mapping.

By combining mapping and state representation you can let Formular do **Rendering** for you. Currently only
[Freemarker](https://freemarker.apache.org/) is supported. It provides a macro (like `spring.ftl` do) to you. Custom
form templates are supported by *themes*. More information will follow soon (but until then you can have a look at
`src/main/resources/formular/integration/freemarker`). The rendering requires a present mapping and a state
representation.

You can decide what layers you want to use. Each layer requires the layers above to be present. So you can use the
mapping only (e.g. for form definitions on REST APIs), use only state representation and mapping or use all three
components.

## ToDo

 * Better testing
 * More usage examples
 * Support for retrieving entities with entity graphs
 * Support for map attributes
 * Test for `DefaultFormStateApplicator`
