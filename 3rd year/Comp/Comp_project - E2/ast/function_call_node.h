#ifndef __MML_AST_FUNCTION_CALL_NODE_H__
#define __MML_AST_FUNCTION_CALL_NODE_H__

#include <cdk/ast/basic_node.h>
#include <cdk/ast/expression_node.h>
#include <cdk/ast/sequence_node.h>

namespace mml
{

    /**
     * Class for describing function call node.
     */
    class function_call_node : public cdk::expression_node
    {
        cdk::expression_node *_function;
        cdk::sequence_node *_params;

    public:
        inline function_call_node(int lineno, cdk::expression_node *function, cdk::sequence_node *params) : cdk::expression_node(lineno), _function(function), _params(params)
        {
        }

    public:
        inline cdk::expression_node *function()
        {
            return _function;
        }

        inline cdk::sequence_node *params()
        {
            return _params;
        }

        void accept(basic_ast_visitor *sp, int level)
        {
            sp->do_function_call_node(this, level);
        }
    };

} // mml

#endif
