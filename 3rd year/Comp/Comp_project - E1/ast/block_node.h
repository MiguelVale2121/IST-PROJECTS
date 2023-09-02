#ifndef __MML_AST_BLOCK_NODE_H__
#define __MML_AST_BLOCK_NODE_H__

#include <cdk/ast/basic_node.h>
#include <cdk/ast/sequence_node.h>

namespace mml
{

    /**
     * Class for describing block node.
     */
    class block_node : public cdk::basic_node
    {

        cdk::sequence_node *_declarations;
        cdk::sequence_node *_instructions;

    public:
        inline block_node(int lineno, cdk::sequence_node *declarations, cdk::sequence_node *instructions) : cdk::basic_node(lineno), _declarations(declarations), _instructions(instructions)
        {
        }

    public:
        void accept(basic_ast_visitor *sp, int level)
        {
            sp->do_block_node(this, level);
        }
    };

} // mml

#endif
