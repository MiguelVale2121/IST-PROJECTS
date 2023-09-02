#ifndef __MML_AST_READ_NODE_H__
#define __MML_AST_READ_NODE_H__

#include <cdk/ast/expression_node.h>

namespace mml
{

  /**
   * Class for describing read nodes.
   */
  class read_node : public cdk::basic_node
  {

    cdk::expression_node *_argument;

  public:
    inline read_node(int lineno, cdk::expression_node *argument) : cdk::basic_node(lineno), _argument(argument)
    {
    }

  public:
    inline cdk::expression_node *argument()
    {
      return _argument;
    }

    void accept(basic_ast_visitor *sp, int level)
    {
      sp->do_read_node(this, level);
    }
  };

} // mml

#endif
