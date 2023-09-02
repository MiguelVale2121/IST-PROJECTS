#ifndef __MML_TARGETS_POSTFIX_WRITER_H__
#define __MML_TARGETS_POSTFIX_WRITER_H__

#include "targets/basic_ast_visitor.h"
#include <vector>
#include <sstream>
#include <stack>
#include <set>
#include <cdk/emitters/basic_postfix_emitter.h>

namespace mml
{

  //!
  //! Traverse syntax tree and generate the corresponding assembly code.
  //!
  class postfix_writer : public basic_ast_visitor
  {
    cdk::symbol_table<mml::symbol> &_symtab;
    std::shared_ptr<mml::symbol> _function;
    bool _inFunctionArgs = false;
    std::set<std::string> _extern_functions;
    cdk::basic_postfix_emitter &_pf;
    int _lbl;
    int _offset; // current framepointer offset (0 means no vars defined)

    std::vector<int> _whileCond, _whileEnd;
    std::stack<std::string> _functionsLabels;
    std::shared_ptr<mml::symbol> _externFunctionTransportSymbol = nullptr;
    bool _program_node_return = false;

  public:
    postfix_writer(std::shared_ptr<cdk::compiler> compiler, cdk::symbol_table<mml::symbol> &symtab,
                   cdk::basic_postfix_emitter &pf) : basic_ast_visitor(compiler), _symtab(symtab), _pf(pf), _lbl(0), _offset(0)
    {
    }

  public:
    ~postfix_writer()
    {
      os().flush();
    }

  private:
    /** Method used to generate sequential labels. */
    inline std::string mklbl(int lbl)
    {
      std::ostringstream oss;
      if (lbl < 0)
        oss << ".L" << -lbl;
      else
        oss << "_L" << lbl;
      return oss.str();
    }

  public:
    // do not edit these lines
#define __IN_VISITOR_HEADER__
#include ".auto/visitor_decls.h" // automatically generated
#undef __IN_VISITOR_HEADER__
    // do not edit these lines: end
  };

} // mml

#endif
