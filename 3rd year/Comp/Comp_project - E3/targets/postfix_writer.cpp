#include <string>
#include <sstream>
#include "targets/type_checker.h"
#include "targets/postfix_writer.h"
#include ".auto/all_nodes.h" // all_nodes.h is automatically generated
#include "targets/symbol.h"
#include "targets/frame_size_calculator.h"

#define THROW(MSG)                   \
  {                                  \
    std::cerr << (MSG) << std::endl; \
    exit(1);                         \
  }

//---------------------------------------------------------------------------

void mml::postfix_writer::do_nil_node(cdk::nil_node *const node, int lvl)
{
  // EMPTY
}
void mml::postfix_writer::do_data_node(cdk::data_node *const node, int lvl)
{
  // EMPTY
}
void mml::postfix_writer::do_double_node(cdk::double_node *const node, int lvl)
{
  if (_function != nullptr)
  {
    _pf.DOUBLE(node->value()); // load number to the stack
  }
  else
  {
    _pf.SDOUBLE(node->value()); // double is on the DATA segment
  }
}
void mml::postfix_writer::do_not_node(cdk::not_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl + 2);
  _pf.INT(0);
  _pf.EQ();
}
void mml::postfix_writer::do_and_node(cdk::and_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  int lbl = ++_lbl;
  node->left()->accept(this, lvl + 2);
  _pf.DUP32();
  _pf.JZ(mklbl(lbl));
  node->right()->accept(this, lvl + 2);
  _pf.AND();
  _pf.ALIGN();
  _pf.LABEL(mklbl(lbl));
}
void mml::postfix_writer::do_or_node(cdk::or_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  int lbl = ++_lbl;
  node->left()->accept(this, lvl + 2);
  _pf.DUP32();
  _pf.JNZ(mklbl(lbl));
  node->right()->accept(this, lvl + 2);
  _pf.OR();
  _pf.ALIGN();
  _pf.LABEL(mklbl(lbl));
}
void mml::postfix_writer::do_declaration_node(mml::declaration_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;

  auto id = node->identifier();

  std::cout << "INITIAL OFFSET: " << _offset << std::endl;

  // type size?
  int offset = 0, typesize = node->type()->size(); // in bytes
  std::cout << "ARG: " << id << ", " << typesize << std::endl;

  if (_inFunctionArgs)
  {
    offset = _offset;
    _offset += typesize;
  }
  else if (_function != nullptr)
  {
    std::cout << "IN BODY" << std::endl;
    _offset -= typesize;
    offset = _offset;
  }
  else
  {
    std::cout << "GLOBAL" << std::endl;
    offset = 0; // global variable
  }

  std::cout << "OFFSET: " << id << ", " << offset << std::endl;

  auto symbol = new_symbol();
  if (symbol)
  {
    symbol->set_offset(offset);
    reset_new_symbol();
  }

  if (!_function)
  {

    if (node->isForeign())
    {
      _extern_functions.insert(node->identifier());
      symbol->set_allow_redeclaration(false);
      symbol->label(node->identifier());
      symbol->set_external(true);
      _externFunctionTransportSymbol = symbol;
      return;
    }
    if (node->isForward())
    {
      _extern_functions.insert(node->identifier());
      symbol->label(node->identifier());
      symbol->set_external(true);
      return;
    }


    int litlbl;

    if (node->expression() != nullptr && node->is_typed(cdk::TYPE_STRING))
    {
      _pf.RODATA();
      _pf.ALIGN();
      _pf.LABEL(mklbl(litlbl = ++_lbl));
      _pf.SSTRING(dynamic_cast<cdk::string_node *>(node->expression())->value());
    }

    if (node->expression() == nullptr)
    {
      _pf.BSS(); // uninitialized
    }
    else{
      if(node->is_typed(cdk::TYPE_FUNCTIONAL)){
        node->expression()->accept(this,lvl);
        if (symbol->is_external() && symbol->allow_redeclaration() && symbol->is_typed(cdk::TYPE_FUNCTIONAL))
        {
          symbol->set_allow_redeclaration(false);
          _extern_functions.erase(symbol->name());
        }
      }

      _pf.DATA(); // data
    }
      
    _pf.ALIGN();
    if (node->isPublic()){
      if(node->is_typed(cdk::TYPE_FUNCTIONAL)) 
        _pf.GLOBAL(id,_pf.FUNC());
      else
        _pf.GLOBAL(id, _pf.OBJ());
    }
      
    _pf.LABEL(id);

    if (node->expression() == nullptr)
    {
      _pf.SALLOC(typesize);
    }
    else
    {
      switch (node->type()->name())
      {
      case cdk::TYPE_DOUBLE:
        if (node->expression()->is_typed(cdk::TYPE_INT) || node->expression()->is_typed(cdk::TYPE_DOUBLE))
        {
          if (node->expression()->is_typed(cdk::TYPE_INT))
          {
            // int -> double
            cdk::integer_node *dclini = dynamic_cast<cdk::integer_node *>(node->expression());
            cdk::double_node ddi(dclini->lineno(), dclini->value());
            ddi.accept(this, lvl);
          }
          else
          {
            node->expression()->accept(this, lvl);
          }
        }
        else
        {
          THROW("[postfix_writer]: '" + id + "' has bad initializer for real value");
        }
        break;
      case cdk::TYPE_INT:
        node->expression()->accept(this, lvl);
        break;
      case cdk::TYPE_POINTER:
        node->expression()->accept(this, lvl);
        break;
      case cdk::TYPE_STRING:
        _pf.SADDR(mklbl(litlbl));
        node->expression()->accept(this, lvl);
        break;
      case cdk::TYPE_FUNCTIONAL:
        _pf.SADDR(_functionsLabels.top());
        symbol->label(_functionsLabels.top());
        _functionsLabels.pop();
        break;
      default:
        THROW("[postfix_writer]: '" + id + "' has unexpected initializer");
        break;
      }
    }
  }
  else
  {
    if (node->expression())
    {
      node->expression()->accept(this, lvl);
      _pf.LOCAL(symbol->offset());
      switch (node->type()->name())
      {
      case cdk::TYPE_INT:
        _pf.STINT();
        break;
      case cdk::TYPE_DOUBLE:
        if (node->expression()->is_typed(cdk::TYPE_INT))
          _pf.I2D();
        _pf.STDOUBLE();
        break;
      case cdk::TYPE_STRING:
        _pf.STINT();
        break;
      case cdk::TYPE_POINTER:
        _pf.STINT();
        break;
      default:
        THROW("[postfix_writer]: '" + id + "' has unexpected initializer");
        break;
      }
    }
    else
    {
      // THROW("[postfix_writer]: '" + id + "' has no initializer");
    }
  }
}
void mml::postfix_writer::do_stop_node(mml::stop_node *const node, int lvl)
{
  if (_whileEnd.size() != 0)
  {
    _pf.JMP(mklbl(_whileEnd.at(_whileEnd.size() - node->level()))); // jump to specified stop label
  }
  else
    THROW("[postfix_writer]: 'stop' outside 'while");
}
void mml::postfix_writer::do_next_node(mml::next_node *const node, int lvl)
{
  if (_whileCond.size() != 0)
  {
    _pf.JMP(mklbl(_whileCond.at(_whileCond.size() - node->level()))); // jump to next cycle
  }
  else
    THROW("[postfix_writer]: 'next' outside 'while'");
}
void mml::postfix_writer::do_return_node(mml::return_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;

  auto functionType = cdk::functional_type::cast(_function->type())->output(0)->name();

  if (functionType != cdk::TYPE_VOID)
  {
    node->expression()->accept(this, lvl + 2);

    switch (functionType)
    {
    case cdk::TYPE_INT:
      _pf.STFVAL32();
      break;
    case cdk::TYPE_DOUBLE:
      if (node->expression()->is_typed(cdk::TYPE_INT))
      {
        _pf.I2D(); // Cast int to double
      }
      _pf.STFVAL64();
      break;
    case cdk::TYPE_STRING:
      _pf.STFVAL32();
      break;
    case cdk::TYPE_POINTER:
      _pf.STFVAL32();
      break;
    default:
      THROW("[postfix_writer/return_node]: unknown return type");
    }
  }

  if (_function->name() == "_main")
    _program_node_return = true;

  _pf.LEAVE();
  _pf.RET();
}
void mml::postfix_writer::do_block_node(mml::block_node *const node, int lvl)
{
  _symtab.push();
  if (node->declarations())
    node->declarations()->accept(this, lvl + 2);
  if (node->instructions())
    node->instructions()->accept(this, lvl + 2);
  _symtab.pop();
}
void mml::postfix_writer::do_function_node(mml::function_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;

  // Save old function reference
  std::shared_ptr<mml::symbol> function_old = _function;
  std::cout << "[postfix_writer]: saved function reference: " << function_old << std::endl;

  _function = new_symbol();
  std::cout << "[postfix_writer]: new function context: " << _function << std::endl;

  auto name = mklbl(++_lbl);

  _function->label(name);

  _functionsLabels.push(name);

  _offset = 8;

  // declare args, and their respective scope
  _symtab.push();

  if (node->params()->size() != 0)
  {
    _inFunctionArgs = true;
    for (auto arg : node->params()->nodes())
    {
      arg->accept(this, lvl);
    }
    _inFunctionArgs = false;
  }

  _pf.TEXT();
  _pf.ALIGN();

  // Move this to declaration
  /* if (node->qualifier() == tPUBLIC)
  {
    _pf.GLOBAL(name, _pf.FUNC());
  } */

  _pf.LABEL(name);

  frame_size_calculator fsc(_compiler, _symtab, _function);
  node->accept(&fsc, lvl);

  _pf.ENTER(fsc.localsize());

  _offset = 0;
  node->block()->accept(this, lvl);

  _symtab.pop(); // arguments

  auto functionType = cdk::functional_type::cast(_function->type());

  // make sure that voids are returned from
  if (functionType->output(0)->name() == cdk::TYPE_VOID)
  {
    _pf.LEAVE();
    _pf.RET();
  }
  


  _function = function_old;
  std::cout << "[type_checker]: restored function context: " << _function << std::endl;
}
void mml::postfix_writer::do_function_call_node(mml::function_call_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;

  size_t argsSize = 0;
  cdk::rvalue_node *rvalue = dynamic_cast<cdk::rvalue_node *>(node->function());

  std::shared_ptr<cdk::functional_type> functionType;

  if (node->function() != nullptr)
    if (rvalue == nullptr){
      auto function_node = dynamic_cast<mml::function_node*>(node->function());
      functionType = cdk::functional_type::cast(function_node->type());
    }
    else {
      functionType = cdk::functional_type::cast(rvalue->type());
    }
  else
    functionType = cdk::functional_type::cast(_function->type());

  std::string end_assigment_label;
  if (node->params()->size() > 0)
  {
    auto refrence_hint_vector = function_reference_hints(); 
    for (int i = functionType->input_length() - 1; i >= 0; i--)
    {
      if (functionType->input(i)->name() == cdk::TYPE_FUNCTIONAL && !refrence_hint_vector->at(i)) 
        {
          end_assigment_label = mklbl(++_lbl);
          _pf.JMP(end_assigment_label);
        }
      node->param(i)->accept(this, lvl + 2);
      if (functionType->input(i)->name() == cdk::TYPE_DOUBLE && node->param(i)->is_typed(cdk::TYPE_INT))
      {
        _pf.I2D();
      }
      else if (functionType->input(i)->name() == cdk::TYPE_FUNCTIONAL && !_functionsLabels.empty()){
          _pf.LABEL(end_assigment_label);
          _pf.ADDR(_functionsLabels.top());
          _functionsLabels.pop();
          _pf.DUP32();
      }
      argsSize += functionType->input(i)->size();
    }
  }

  auto sym = _externFunctionTransportSymbol;
  if (sym != nullptr ){
    _pf.CALL(sym->name());
    _externFunctionTransportSymbol = nullptr;
  }
  else {
    if (node->function() != nullptr)
      node->function()->accept(this, lvl + 2);
    else
      _pf.ADDR(_function->label());

    _pf.BRANCH();
  }

  if (argsSize != 0)
  {
    _pf.TRASH(argsSize);
  }

  if (functionType->output(0)->name() == cdk::TYPE_DOUBLE)
  {
    _pf.LDFVAL64();
  }
  else
  {
    _pf.LDFVAL32();
  }
}

void mml::postfix_writer::do_null_node(mml::null_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  if (_function != nullptr)
  {
    _pf.INT(0);
  }
  else
  {
    _pf.SINT(0);
  }
}

void mml::postfix_writer::do_identity_node(mml::identity_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl);
}

void mml::postfix_writer::do_indexing_ptr_node(mml::indexing_ptr_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->expression_ptr()->accept(this, lvl);
  node->index()->accept(this, lvl);
  auto reftype = cdk::reference_type::cast(node->expression_ptr()->type());
  if (reftype->referenced()->size() != 1)
  {
    _pf.INT(reftype->referenced()->size());
    _pf.MUL();
  }
  _pf.ADD(); // add pointer and index
}

void mml::postfix_writer::do_address_of_node(mml::address_of_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;

  // since the argument is an lvalue, it is already an address
  node->lvalue()->accept(this, lvl + 2);
}

void mml::postfix_writer::do_memory_alloc_node(mml::memory_alloc_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->expression()->accept(this, lvl);

  size_t elsize = cdk::reference_type::cast(node->type())->referenced()->size();
  if (elsize)
  {
    _pf.INT(elsize);
    _pf.MUL();
  }
  _pf.ALLOC();
  _pf.SP();
}

void mml::postfix_writer::do_sizeof_node(mml::sizeof_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  _pf.INT(node->expression()->type()->size());
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_sequence_node(cdk::sequence_node *const node, int lvl)
{
  for (size_t i = 0; i < node->size(); i++)
  {
    node->node(i)->accept(this, lvl);
  }
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_integer_node(cdk::integer_node *const node, int lvl)
{
  if (_function != nullptr)
  {
    _pf.INT(node->value()); // push an integer
  }
  else
  {
    _pf.SINT(node->value()); // integer literal is on the DATA segment
  }
}

void mml::postfix_writer::do_string_node(cdk::string_node *const node, int lvl)
{
  int lbl1;

  /* generate the string */
  _pf.RODATA();                    // strings are DATA readonly
  _pf.ALIGN();                     // make sure we are aligned
  _pf.LABEL(mklbl(lbl1 = ++_lbl)); // give the string a name
  _pf.SSTRING(node->value());      // output string characters

  /* leave the address on the stack */
  _pf.TEXT();            // return to the TEXT segment
  _pf.ADDR(mklbl(lbl1)); // the string to be printed
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_neg_node(cdk::neg_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl); // determine the value
  _pf.NEG();                           // 2-complement
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_add_node(cdk::add_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl + 2);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->left()->is_typed(cdk::TYPE_INT))
  {
    _pf.I2D();
  }
  else if (node->is_typed(cdk::TYPE_POINTER) && node->left()->is_typed(cdk::TYPE_INT))
  {
    auto reftype = cdk::reference_type::cast(node->type());
    _pf.INT(reftype->referenced()->size());
    _pf.MUL();
  }

  node->right()->accept(this, lvl + 2);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT))
  {
    _pf.I2D();
  }
  else if (node->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_INT))
  {
    auto reftype = cdk::reference_type::cast(node->type());
    _pf.INT(reftype->referenced()->size());
    _pf.MUL();
  }

  if (node->is_typed(cdk::TYPE_DOUBLE))
    _pf.DADD();
  else
    _pf.ADD();
}
void mml::postfix_writer::do_sub_node(cdk::sub_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;

  node->left()->accept(this, lvl);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->left()->is_typed(cdk::TYPE_INT))
  {
    _pf.I2D();
  }

  node->right()->accept(this, lvl);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT))
  {
    _pf.I2D();
  }
  else if (node->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_INT))
  {
    auto reftype = cdk::reference_type::cast(node->type());
    _pf.INT(reftype->referenced()->size());
    _pf.MUL();
  }

  if (node->is_typed(cdk::TYPE_DOUBLE))
  {
    _pf.DSUB();
  }
  else
  {
    _pf.SUB();
  }
}
void mml::postfix_writer::do_mul_node(cdk::mul_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->left()->is_typed(cdk::TYPE_INT))
  {
    _pf.I2D();
  }
  node->right()->accept(this, lvl);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT))
  {
    _pf.I2D();
  }

  if (node->is_typed(cdk::TYPE_INT))
  {
    _pf.MUL();
  }
  else
  {
    _pf.DMUL();
  }
}
void mml::postfix_writer::do_div_node(cdk::div_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->left()->is_typed(cdk::TYPE_INT))
  {
    _pf.I2D();
  }
  node->right()->accept(this, lvl);
  if (node->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT))
  {
    _pf.I2D();
  }

  if (node->is_typed(cdk::TYPE_INT))
  {
    _pf.DIV();
  }
  else
  {
    _pf.DDIV();
  }
}
void mml::postfix_writer::do_mod_node(cdk::mod_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.MOD();
}
void mml::postfix_writer::do_lt_node(cdk::lt_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl + 2);
  if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE))
    _pf.I2D();

  node->right()->accept(this, lvl + 2);
  if (node->right()->is_typed(cdk::TYPE_INT) && node->left()->is_typed(cdk::TYPE_DOUBLE))
    _pf.I2D();

  if (node->left()->is_typed(cdk::TYPE_DOUBLE) || node->right()->is_typed(cdk::TYPE_DOUBLE))
  {
    _pf.DCMP();
    _pf.INT(0);
  }
  _pf.LT();
}
void mml::postfix_writer::do_le_node(cdk::le_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.LE();
}
void mml::postfix_writer::do_ge_node(cdk::ge_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.GE();
}
void mml::postfix_writer::do_gt_node(cdk::gt_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl + 2);
  if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE))
    _pf.I2D();

  node->right()->accept(this, lvl + 2);
  if (node->right()->is_typed(cdk::TYPE_INT) && node->left()->is_typed(cdk::TYPE_DOUBLE))
    _pf.I2D();

  if (node->left()->is_typed(cdk::TYPE_DOUBLE) || node->right()->is_typed(cdk::TYPE_DOUBLE))
  {
    _pf.DCMP();
    _pf.INT(0);
  }
  _pf.GT();
}
void mml::postfix_writer::do_ne_node(cdk::ne_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl + 2);
  if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE))
    _pf.I2D();

  node->right()->accept(this, lvl + 2);
  if (node->right()->is_typed(cdk::TYPE_INT) && node->left()->is_typed(cdk::TYPE_DOUBLE))
    _pf.I2D();

  if (node->left()->is_typed(cdk::TYPE_DOUBLE) || node->right()->is_typed(cdk::TYPE_DOUBLE))
  {
    _pf.DCMP();
    _pf.INT(0);
  }

  _pf.NE();
}
void mml::postfix_writer::do_eq_node(cdk::eq_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl + 2);
  if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE))
    _pf.I2D();

  node->right()->accept(this, lvl + 2);
  if (node->right()->is_typed(cdk::TYPE_INT) && node->left()->is_typed(cdk::TYPE_DOUBLE))
    _pf.I2D();

  if (node->left()->is_typed(cdk::TYPE_DOUBLE) || node->right()->is_typed(cdk::TYPE_DOUBLE))
  {
    _pf.DCMP();
    _pf.INT(0);
  }
  _pf.EQ();
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_variable_node(cdk::variable_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;

  const std::string &id = node->name();
  auto symbol = _symtab.find(id);

  if (symbol->global())
  {
    _pf.ADDR(node->name());
  }
  else
  {
    _pf.LOCAL(symbol->offset());
  }
}

void mml::postfix_writer::do_rvalue_node(cdk::rvalue_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->lvalue()->accept(this, lvl);

  switch (node->type()->name())
  {
  case cdk::TYPE_DOUBLE:
    _pf.LDDOUBLE();
    break;

  default:
    _pf.LDINT();
    break;
  }
}

void mml::postfix_writer::do_assignment_node(cdk::assignment_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  std::string end_assigment_label;
  if (node->is_typed(cdk::TYPE_FUNCTIONAL)) 
  {
    end_assigment_label = mklbl(++_lbl);
    _pf.JMP(end_assigment_label);
  }
  node->rvalue()->accept(this, lvl); // determine the new value
  if (node->is_typed(cdk::TYPE_DOUBLE))
  {
    if (node->rvalue()->is_typed(cdk::TYPE_INT))
    {
      _pf.I2D();
    }
    _pf.DUP64();
  }
  else if (node->is_typed(cdk::TYPE_FUNCTIONAL))
  {
    _pf.LABEL(end_assigment_label);
    _pf.ADDR(_functionsLabels.top());
    _functionsLabels.pop();
    _pf.DUP32();
  }
  else
  {
    _pf.DUP32();
  }

  node->lvalue()->accept(this, lvl);
  if (node->is_typed(cdk::TYPE_DOUBLE))
  {
    _pf.STDOUBLE();
  }
  else
  {
    _pf.STINT();
  }
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_program_node(mml::program_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  _function = new_symbol();
  // Note that MML doesn't have functions. Thus, it doesn't need
  // a function node. However, it must start in the main function.
  // The ProgramNode (representing the whole program) doubles as a
  // main function node.

  // generate the main function (RTS mandates that its name be "_main")
  _pf.TEXT();
  _pf.ALIGN();
  _pf.GLOBAL("_main", _pf.FUNC());
  _pf.LABEL("_main");

  // Compute required space for local variables
  frame_size_calculator fsc(_compiler, _symtab, _function);
  node->accept(&fsc, lvl);
  _pf.ENTER(fsc.localsize());

  node->statements()->accept(this, lvl);

  if (!_program_node_return)
  {
    // end the main function
    _pf.INT(0);
    _pf.STFVAL32();
    _pf.LEAVE();
    _pf.RET();
  }

  // these are just a few library function imports

  for (std::string s : _extern_functions)
  {
    auto sym = _symtab.find(s);
    if (!sym || sym->allow_redeclaration() == false)
      _pf.EXTERN(s);
  }
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_evaluation_node(mml::evaluation_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl);
  _pf.TRASH(node->argument()->type()->size());
}

void mml::postfix_writer::do_print_node(mml::print_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;

  for (auto node : node->argument()->nodes())
  {
    auto expr = static_cast<cdk::expression_node *>(node);

    expr->accept(this, lvl);

    if (expr->is_typed(cdk::TYPE_FUNCTIONAL))
    {
      auto functionType = cdk::functional_type::cast(expr->type());
      expr->type(functionType->output(0));
    }

    if (expr->is_typed(cdk::TYPE_INT))
    {
      _extern_functions.insert("printi");
      _pf.CALL("printi");
      _pf.TRASH(4);
    }
    else if (expr->is_typed(cdk::TYPE_DOUBLE))
    {
      _extern_functions.insert("printd");
      _pf.CALL("printd");
      _pf.TRASH(8);
    }
    else if (expr->is_typed(cdk::TYPE_STRING))
    {
      _extern_functions.insert("prints");
      _pf.CALL("prints");
      _pf.TRASH(4);
    }
    else
    {
      THROW("[postfix_writer/write_node]: unkown type for print node");
    }
  }
  if (node->new_line())
  {
    _extern_functions.insert("println");
    _pf.CALL("println"); // print a newline
  }
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_read_node(mml::read_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  _extern_functions.insert("readi");
  _pf.CALL("readi");
  /* _pf.LDFVAL32();
  node->argument()->accept(this, lvl);
  _pf.STINT(); */
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_while_node(mml::while_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  _whileCond.push_back(++_lbl);
  _whileEnd.push_back(++_lbl);

  _pf.ALIGN();
  _pf.LABEL(mklbl(_whileCond.back()));
  node->condition()->accept(this, lvl);
  _pf.JZ(mklbl(_whileEnd.back()));

  node->block()->accept(this, lvl + 2);
  _pf.JMP(mklbl(_whileCond.back()));

  _pf.ALIGN();
  _pf.LABEL(mklbl(_whileEnd.back()));

  _whileEnd.pop_back();
  _whileCond.pop_back();
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_if_node(mml::if_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  int lbl1;
  node->condition()->accept(this, lvl);
  _pf.JZ(mklbl(lbl1 = ++_lbl));
  node->block()->accept(this, lvl + 2);
  _pf.LABEL(mklbl(lbl1));
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_if_else_node(mml::if_else_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  int lbl1, lbl2;
  node->condition()->accept(this, lvl);
  _pf.JZ(mklbl(lbl1 = ++_lbl));
  node->thenblock()->accept(this, lvl + 2);
  _pf.JMP(mklbl(lbl2 = ++_lbl));
  _pf.LABEL(mklbl(lbl1));
  node->elseblock()->accept(this, lvl + 2);
  _pf.LABEL(mklbl(lbl1 = lbl2));
}

//---------------------------------------------------------------------------
