#ifndef __MML_AST_STOP_NODE_H__
#define __MML_AST_STOP_NODE_H__

#include <cdk/ast/basic_node.h>

namespace mml
{

    /**
     * Class for describing stop node.
     */
    class stop_node : public cdk::basic_node
    {
        cdk::integer_node *_argument;

    public:
        inline stop_node(int lineno, cdk::integer_node *argument) : cdk::basic_node(lineno), _argument(argument)
        {
        }

    public:
        inline cdk::integer_node *argument() {
            return _argument;
        }
        void accept(basic_ast_visitor *sp, int level)
        {
            sp->do_stop_node(this, level);
        }
    };

} // mml

#endif
