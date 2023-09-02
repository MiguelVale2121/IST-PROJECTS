#ifndef __MML_AST_INDEXING_PTR_NODE_H__
#define __MML_AST_INDEXING_PTR_NODE_H__

#include <cdk/ast/lvalue_node.h>

namespace mml {

  /**
   * Class for describing indexing pointer nodes.
   */
  class indexing_ptr_node: public cdk::lvalue_node {
    cdk::expression_node *_expression_ptr;
    cdk::expression_node *_index;

  public:
    indexing_ptr_node(int lineno, cdk::expression_node *expression_ptr, cdk::expression_node *index) :
        lvalue_node(lineno), _expression_ptr(expression_ptr), _index(index) {
    }

    inline cdk::expression_node *expression_ptr()
        {
            return _expression_ptr;
        }
    inline cdk::expression_node *index()
        {
            return _index;
        }


    void accept(basic_ast_visitor *sp, int level) {
      sp->do_indexing_ptr_node(this, level);
    }

  };

} // cdk

#endif
