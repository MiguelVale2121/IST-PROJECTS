#ifndef __MML_AST_PRINT_NODE_H__
#define __MML_AST_PRINT_NODE_H__

#include <cdk/ast/expression_node.h>

namespace mml
{

  /**
   * Class for describing print nodes.
   */
  class print_node : public cdk::basic_node
  {
    bool _new_line;
    cdk::expression_node *_argument;

  public:
    inline print_node(int lineno, bool new_line, cdk::expression_node *argument) : cdk::basic_node(lineno), _new_line(new_line), _argument(argument)
    {
    }

  public:
    inline bool new_line()
    {
      return _new_line;
    }

    inline cdk::expression_node *argument()
    {
      return _argument;
    }

    void accept(basic_ast_visitor *sp, int level)
    {
      sp->do_print_node(this, level);
    }
  };

} // mml

#endif
