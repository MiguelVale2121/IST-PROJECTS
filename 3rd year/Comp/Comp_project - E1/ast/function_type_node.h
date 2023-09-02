#ifndef __MML_AST_FUNCTION_TYPE_NODE_H__
#define __MML_AST_FUNCTION_TYPE_NODE_H__

#include <cdk/ast/basic_node.h>

namespace mml
{

    /**
     * Class for describing function type declaration nodes.
     * type < ⟨ type ⟩ >
     */
    class function_type_node : public cdk::basic_node
    {
        cdk::basic_type *_return_type;
        cdk::sequence_node *_params;

    public:
        inline function_type_node(int lineno, cdk::basic_type *return_type, cdk::sequence_node *params) : cdk::basic_node(lineno), _return_type(return_type), _params(params)
        {
        }

    public:
        inline cdk::basic_type *return_type()
        {
            return _return_type;
        }

        inline cdk::sequence_node *params()
        {
            return _params;
        }

        void accept(basic_ast_visitor *sp, int level)
        {
            sp->do_function_type_node(this, level);
        }
    };

} // mml

#endif
