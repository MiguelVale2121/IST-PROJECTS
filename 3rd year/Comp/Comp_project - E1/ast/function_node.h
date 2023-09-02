#ifndef __MML_AST_FUNCTION_NODE_H__
#define __MML_AST_FUNCTION_NODE_H__

#include <cdk/ast/basic_node.h>
#include <cdk/ast/expression_node.h>
#include <cdk/ast/sequence_node.h>

namespace mml
{

    /**
     * Class for describing function definition node.
     * ( ) -> type block
     * ( params ) -> type block
     */
    class function_node : public cdk::expression_node
    {
        cdk::sequence_node *_params;
        cdk::basic_type *_return_type;
        mml::block_node *_block;

    public:
        inline function_node(int lineno, cdk::sequence_node *params, cdk::basic_type *return_type, mml::block_node *block) : cdk::expression_node(lineno), _params(params), _return_type(return_type), _block(block)
        {
        }

    public:
        inline cdk::sequence_node *params()
        {
            return _params;
        }

        inline cdk::basic_type *return_type()
        {
            return _return_type;
        }

        inline mml::block_node *block()
        {
            return _block;
        }

        void accept(basic_ast_visitor *sp, int level)
        {
            sp->do_function_node(this, level);
        }
    };

} // mml

#endif
