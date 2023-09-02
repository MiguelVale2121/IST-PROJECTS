#ifndef __MML_AST_FUNCTION_NODE_H__
#define __MML_AST_FUNCTION_NODE_H__

#include <cdk/ast/expression_node.h>
#include <cdk/ast/sequence_node.h>
#include "ast/block_node.h"

namespace mml
{

    /**
     * Class for describing function definition node.
     */
    class function_node : public cdk::expression_node
    {
        cdk::sequence_node *_params;
        mml::block_node *_block;

        public:
            inline function_node(int lineno, cdk::sequence_node *params, std::shared_ptr<cdk::basic_type> funType, mml::block_node *block) :
            cdk::expression_node(lineno), _params(params), _block(block) {
        type(funType);
        }

    public:
        inline cdk::sequence_node* params() {
        return _params;
        }
        inline cdk::typed_node* param(size_t ax) {
        return dynamic_cast<cdk::typed_node*>(_params->node(ax));
        }
        inline mml::block_node* block() {
        return _block;
        }

        void accept(basic_ast_visitor *sp, int level)
        {
            sp->do_function_node(this, level);
        }
    };

} // mml

#endif
