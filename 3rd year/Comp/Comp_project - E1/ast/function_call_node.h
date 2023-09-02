#ifndef __MML_AST_FUNCTION_CALL_NODE_H__
#define __MML_AST_FUNCTION_CALL_NODE_H__

#include <cdk/ast/basic_node.h>

namespace mml
{

    /**
     * Class for describing function call node.
     */
    class function_call_node : public cdk::basic_node
    {
        bool _isInternalCall;
        std::string *_identifier;
        cdk::sequence_node *_params;

    public:
        inline function_call_node(int lineno, bool isInternalCall, std::string *identifier, cdk::sequence_node *params) : cdk::basic_node(lineno), _isInternalCall(isInternalCall), _identifier(identifier), _params(params)
        {
        }

    public:
        inline bool isInternalCall()
        {
            return _isInternalCall;
        }

        inline std::string *identifier()
        {
            return _identifier;
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
