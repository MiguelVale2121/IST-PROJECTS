#ifndef __MML_AST_RETURN_NODE_H__
#define __MML_AST_RETURN_NODE_H__

#include <cdk/ast/basic_node.h>
#include <cdk/ast/expression_node.h>

namespace mml
{

    /**
     * Class for describing return node.
     */
    class return_node : public cdk::basic_node
    {
        cdk::expression_node *_expression;

    public:
        inline return_node(int lineno, cdk::expression_node *expression = nullptr) : cdk::basic_node(lineno), _expression(expression)
        {
        }

    public:
        inline cdk::expression_node *expression()
        {
            return _expression;
        }

        void accept(basic_ast_visitor *sp, int level)
        {
            sp->do_return_node(this, level);
        }
    };

} // mml

#endif
