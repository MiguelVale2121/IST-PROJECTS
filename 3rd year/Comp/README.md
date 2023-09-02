# Compilers Project - MML Compiler

## First Delivery
The first delivery involves creating all the necessary AST node classes for the MML language. While CDK already provides some predefined nodes, they are not sufficient.

In addition to creating the classes, a skeleton of the visitors (xml_writer, postfix_writer, etc.) needs to be implemented. This code does not need to be functional but must compile.

No lexical, syntactic, or semantic analysis code needs to be implemented at this stage.

## Second Delivery
The second delivery focuses on implementing the parser and XML generator. The generated XML should be valid (suggestion: use the "xmllint" command to confirm) and should contain all the information present in the AST.

## Third Delivery
The third delivery involves implementing the generation of postfix code and, consequently, x86 assembly code (32 bits). Type checking should also be functional, propagating types when necessary and giving an error when types are incorrect.

## Comp_project

Grade: 17.72
