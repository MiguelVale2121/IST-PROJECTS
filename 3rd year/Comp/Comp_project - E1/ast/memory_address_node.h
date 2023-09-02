#ifndef __MML_AST_MEMORY_ADDRESS_NODE_H__
#define __MML_AST_MEMORY_ADDRESS_NODE_H__

#include <cdk/ast/unary_operation_node.h>

namespace mml {

  /**
   * Class for describing memory address nodes.
   */
  class memory_address_node: public cdk::unary_operation_node {


  public:
    inline memory_address_node(int lineno, cdk::expression_node *arg) :
        cdk::unary_operation_node(lineno, arg) {
    }

  public:

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_memory_address_node(this, level);
    }

  };

} // mml

#endif
