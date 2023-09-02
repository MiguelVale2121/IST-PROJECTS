#ifndef __MML_AST_MEMORY_ALLOC_NODE_H__
#define __MML_AST_MEMORY_ALLOC_NODE_H__

#include <cdk/ast/unary_operation_node.h>

namespace mml
{

  /**
   * Class for describing memory alloc nodes.
   */
  class memory_alloc_node : public cdk::expression_node
  {

    cdk::expression_node *_expression;

  public:
    inline memory_alloc_node(int lineno, cdk::expression_node *expression) : cdk::expression_node(lineno), _expression(expression)
    {
    }

  public:
    inline cdk::expression_node *expression()
    {
      return _expression;
    }

    void accept(basic_ast_visitor *sp, int level)
    {
      sp->do_memory_alloc_node(this, level);
    }
  };

} // mml

#endif
