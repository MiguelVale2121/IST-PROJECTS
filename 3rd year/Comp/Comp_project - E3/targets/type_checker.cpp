#include <string>
#include <set>
#include "targets/type_checker.h"
#include ".auto/all_nodes.h" // automatically generated
#include <cdk/types/primitive_type.h>

#define ASSERT_UNSPEC                                                 \
  {                                                                   \
    if (node->type() != nullptr && !node->is_typed(cdk::TYPE_UNSPEC)) \
      return;                                                         \
  }

#define THROW(MSG)                   \
  {                                  \
    std::cerr << (MSG) << std::endl; \
    exit(1);                         \
  }

static bool is_ID(cdk::typed_node *const node)
{
  return node->is_typed(cdk::TYPE_INT) || node->is_typed(cdk::TYPE_DOUBLE);
}

class TypeCompatOptions
{
public:
  TypeCompatOptions(bool id = true, bool di = true, bool unspec = false, bool ptrassign = true, bool generalizeptr = false, bool voidptrint = true, bool intvoidptr = true)
      : acceptID(id), acceptDI(di), acceptUnspec(unspec), ptrAssignment(ptrassign), generalizePtr(generalizeptr), acceptVoidPtrInt(voidptrint), acceptIntVoidPtr(intvoidptr) {}
  bool acceptID;         // accept (TYPE_INT, TYPE_DOUBLE), return TYPE_DOUBLE
  bool acceptDI;         // accept (TYPE_DOUBLE, TYPE_INT), return TYPE_DOUBLE
  bool acceptUnspec;     // accept TYPE_UNSPEC in one or more arguments, return the one that isn't TYPE_UNSPEC if it exists
  bool ptrAssignment;    // (ptr<auto>, ptr<X>) returns ptr<auto> and (ptr<X>, ptr<auto>) returns ptr<X> (allow conversion to/from void ptr)
  bool generalizePtr;    // (ptr<X>, ptr<Y>) returns ptr<auto>
  bool acceptVoidPtrInt; // (ptr<auto>, int) returns int
  bool acceptIntVoidPtr; // (int, ptr<auto>) returns int
};

const TypeCompatOptions DEFAULT_TYPE_COMPAT = TypeCompatOptions();
const TypeCompatOptions GENERALIZE_TYPE_COMPAT = TypeCompatOptions(true, true, true, true, true, true, true);
const TypeCompatOptions ASSIGNMENT_TYPE_COMPAT = TypeCompatOptions(false, true, false, true, false, false, true);
const TypeCompatOptions INITIALIZER_TYPE_COMPAT = TypeCompatOptions(false, true, true, true, false, false, true);
const TypeCompatOptions DECL_TYPE_COMPAT = TypeCompatOptions(false, false, true, false, false, false, false);

static std::shared_ptr<cdk::basic_type> compatible_types(std::shared_ptr<cdk::basic_type> a, std::shared_ptr<cdk::basic_type> b, TypeCompatOptions opts);

static std::shared_ptr<cdk::basic_type> is_void_ptr(std::shared_ptr<cdk::reference_type> t)
{
  if (t->referenced()->name() == cdk::TYPE_POINTER)
  {
    return is_void_ptr(cdk::reference_type::cast(t->referenced()));
  }
  else if (t->referenced()->name() == cdk::TYPE_VOID)
  {
    return t;
  }
  else
  {
    return nullptr;
  }
}

static std::shared_ptr<cdk::basic_type> is_void_ptr(std::shared_ptr<cdk::basic_type> t)
{
  if (t->name() == cdk::TYPE_POINTER)
  {
    return is_void_ptr(cdk::reference_type::cast(t));
  }
  else
  {
    return nullptr;
  }
}

static std::shared_ptr<cdk::basic_type> compatible_types_ptr(std::shared_ptr<cdk::reference_type> a, std::shared_ptr<cdk::reference_type> b, TypeCompatOptions opts)
{
  if (auto aa = is_void_ptr(a); aa && (opts.ptrAssignment || opts.generalizePtr))
  {
    return aa;
  }
  else if (auto bb = is_void_ptr(b); bb && (opts.ptrAssignment || opts.generalizePtr))
  {
    if (opts.ptrAssignment)
    {
      return a;
    }
    else if (opts.generalizePtr)
    {
      return bb;
    }
    else
    {
      THROW("[type_checker]: Compatibility error\n");
    }
  }
  else
  {
    // ptr<int> != ptr<double> always
    opts.acceptID = false;
    opts.acceptDI = false;
    // and ptr<ptr<auto>> ? ptr<int> is treated separately
    opts.acceptVoidPtrInt = false;
    opts.acceptIntVoidPtr = false;

    auto referenced = compatible_types(a->referenced(), b->referenced(), opts);
    if (referenced == nullptr)
    {
      if (opts.generalizePtr)
      {
        referenced = cdk::primitive_type::create(1, cdk::TYPE_VOID);
      }
      else
      {
        return nullptr;
      }
    }

    return cdk::reference_type::create(4, referenced);
  }
}

static std::shared_ptr<cdk::basic_type> compatible_types(std::shared_ptr<cdk::basic_type> a, std::shared_ptr<cdk::basic_type> b, TypeCompatOptions opts = DEFAULT_TYPE_COMPAT)
{
  if (opts.acceptUnspec && a->name() == cdk::TYPE_UNSPEC)
  {
    return b;
  }
  else if (opts.acceptUnspec && b->name() == cdk::TYPE_UNSPEC)
  {
    return a;
  }
  else if (opts.acceptDI && a->name() == cdk::TYPE_DOUBLE && b->name() == cdk::TYPE_INT)
  {
    return a; // convert to double
  }
  else if (opts.acceptID && a->name() == cdk::TYPE_INT && b->name() == cdk::TYPE_DOUBLE)
  {
    return b; // convert to double
  }
  else if (opts.acceptIntVoidPtr && a->name() == cdk::TYPE_INT && is_void_ptr(b))
  {
    return a; // convert to int
  }
  else if (opts.acceptVoidPtrInt && is_void_ptr(a) && b->name() == cdk::TYPE_INT)
  {
    return b; // convert to int
  }
  else if (a->name() != b->name())
  { // structs with different sizes may be alright, pointers are all 4 bytes
    return nullptr;
  }

  if (a->name() == cdk::TYPE_POINTER)
  {
    return compatible_types_ptr(cdk::reference_type::cast(a), cdk::reference_type::cast(b), opts);
  }
  else if (a->size() == b->size())
  {
    return a;
  }
  else
  {
    return nullptr;
  }
}

//---------------------------------------------------------------------------

void mml::type_checker::do_sequence_node(cdk::sequence_node *const node, int lvl)
{
  for (auto el : node->nodes())
  {
    el->accept(this, lvl + 2);
  }
}

//---------------------------------------------------------------------------

void mml::type_checker::do_nil_node(cdk::nil_node *const node, int lvl)
{
  // EMPTY
}
void mml::type_checker::do_data_node(cdk::data_node *const node, int lvl)
{
  // EMPTY
}
void mml::type_checker::do_double_node(cdk::double_node *const node, int lvl)
{
  ASSERT_UNSPEC;
  node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
}

void mml::type_checker::processLogicExpression(cdk::binary_operation_node *const node, int lvl)
{
  ASSERT_UNSPEC;
  node->left()->accept(this, lvl + 2);

  if (!node->left()->is_typed(cdk::TYPE_INT))
  {
    THROW("[type_check]: invalid type for 1st arg in logical expression");
  }

  node->right()->accept(this, lvl + 2);

  if (!node->right()->is_typed(cdk::TYPE_INT))
  {
    THROW("[type_check]: invalid type for 2nd arg in logical expression");
  }

  node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
}

void mml::type_checker::do_and_node(cdk::and_node *const node, int lvl)
{
  processLogicExpression(node, lvl);
}
void mml::type_checker::do_or_node(cdk::or_node *const node, int lvl)
{
  processLogicExpression(node, lvl);
}

void mml::type_checker::do_declaration_node(mml::declaration_node *const node, int lvl)
{

  // TODO: Qualifiers!
  if (node->expression() && node->isForward())
    THROW("[type_checker]: forward variables cannot be initialized ");

  auto typeHint = node->type();
  std::shared_ptr<cdk::basic_type> inferedType = typeHint;

  if (node->expression() != nullptr)
  {
    TypeCompatOptions opt = INITIALIZER_TYPE_COMPAT;
    if (node->isAuto())
    {
      opt = GENERALIZE_TYPE_COMPAT;
    }

    node->expression()->accept(this, lvl + 2);
    inferedType = compatible_types(typeHint, node->expression()->type(), opt);

    if (inferedType == nullptr)
    {
      THROW("[type_checker]: type mismatch at expression declaration");
    }
  }

  std::string id = node->identifier();
  auto current_symbol = _symtab.find_local(id);

  auto new_symbol = std::make_shared<mml::symbol>(inferedType, id, 0);
  if (current_symbol)
  {
    if (current_symbol->allow_redeclaration())
    {
      new_symbol->set_external(current_symbol->is_external());
      new_symbol->label(current_symbol->label());
      _symtab.replace(id, new_symbol);
    }
    else
      THROW("[type_checker]: Not allow it to redeclare");
  }
  else
  {
    _symtab.insert(id, new_symbol);
  }
  _parent->set_new_symbol(new_symbol);

  node->setType(new_symbol->type());
}

void mml::type_checker::do_stop_node(mml::stop_node *const node, int lvl)
{
  // EMPTY
}
void mml::type_checker::do_next_node(mml::next_node *const node, int lvl)
{
  // EMPTY
}
void mml::type_checker::do_return_node(mml::return_node *const node, int lvl)
{
  if (!_function)
  {
    THROW("[type_checker]: _function was not set");
  }

  if (!_function->is_typed(cdk::TYPE_FUNCTIONAL))
  {
    THROW("[type_checker]: CRITICAL: _function context is not FUNCTIONAL!");
  }

  auto functionType = cdk::functional_type::cast(_function->type());

  if (node->expression())
  {
    if (functionType->output()->name() == cdk::TYPE_VOID)
    {
      THROW("non-empty return in void function.");
    }

    node->expression()->accept(this, lvl + 2);

    auto type = compatible_types(functionType->output(0), node->expression()->type(), ASSIGNMENT_TYPE_COMPAT);

    if (type == nullptr)
    {
      THROW("[type_checker]: return expression incompatible with function return type");
    }
  }
  else if (functionType->output()->name() != cdk::TYPE_VOID)
  {
    THROW("empty return in non-void function");
  }
}
void mml::type_checker::do_block_node(mml::block_node *const node, int lvl)
{
  if (_typecheckingFunction)
  {
    _symtab.push();

    if (node->declarations())
    {
      node->declarations()->accept(this, lvl + 2);
    }

    if (node->instructions())
    {
      node->instructions()->accept(this, lvl + 2);
    }

    _symtab.pop();
  }
}
void mml::type_checker::do_function_node(mml::function_node *const node, int lvl)
{

  // Function node already visited, skip typecheck
  if (node->typeChecked())
  {
    auto function = std::make_shared<mml::symbol>(node->type(), "anonymous", 0);
    if (!_typecheckingFunction)
      _parent->set_new_symbol(function);
    return;
  }

  _typecheckingFunction = true;
  _symtab.push();

  auto returnType = node->type();

  // Create functional type
  std::vector<std::shared_ptr<cdk::basic_type>> params;
  if (node->params()->size() != 0)
  {
    for (auto param : node->params()->nodes())
    {
      auto decl = dynamic_cast<mml::declaration_node *>(param);
      params.push_back(decl->type());
      auto arg = std::make_shared<mml::symbol>(decl->type(), decl->identifier(), 0);
      _symtab.insert(decl->identifier(), arg);
    }
  }

  std::shared_ptr<cdk::functional_type> functionType = cdk::functional_type::create(params, returnType);

  auto function = std::make_shared<mml::symbol>(functionType, "anonymous", 0);

  if (!_typecheckingFunction)
    _parent->set_new_symbol(function);

  // Save old function reference
  std::shared_ptr<mml::symbol> function_old = _function;
  std::cout << "[type_checker]: saved function reference: " << function_old << std::endl;

  // Change context
  _function = function;
  std::cout << "[type_checker]: new function context: " << _function << std::endl;

  node->block()->accept(this, lvl + 2);

  _symtab.pop();
  _typecheckingFunction = false;

  // Restore context
  _function = function_old;
  std::cout << "[type_checker]: restored function context: " << _function << std::endl;

  node->type(function->type());
  node->setTypeChecked();
}
void mml::type_checker::do_function_call_node(mml::function_call_node *const node, int lvl)
{
  ASSERT_UNSPEC;

  std::shared_ptr<cdk::functional_type> functionType;

  if (node->function() == nullptr)
  {
    if (!_function)
      THROW("[type_checker]: cannot self-invoke outside a function");
    if (_function->name() == "_main")
      THROW("[type_checker]: cannot self-invoke main function");

    functionType = cdk::functional_type::cast(_function->type());
  }
  else
  {
    node->function()->accept(this, lvl + 2);
    cdk::rvalue_node *rvalue = dynamic_cast<cdk::rvalue_node *>(node->function());

    if (rvalue == nullptr){
      auto function_node = dynamic_cast<mml::function_node*>(node->function());
      functionType = cdk::functional_type::cast(function_node->type());
    }
    else {

      if (!rvalue->is_typed(cdk::TYPE_FUNCTIONAL))
        {
          THROW("[type_checker]: CRITICAL: rvalue is not FUNCTIONAL!")
        }
      functionType = cdk::functional_type::cast(rvalue->type());
    }

    
  }

  std::shared_ptr<cdk::basic_type> argsType;

  if (node->params()->size() > 0)
  {
    node->params()->accept(this, lvl + 2);

    auto refrence_hint_vector = std::make_shared<std::vector<bool>>();
    // Validate params
    for (size_t i = 0; i < functionType->input_length(); i++)
    {
      if (compatible_types(functionType->input(i), node->param(i)->type(), ASSIGNMENT_TYPE_COMPAT) == nullptr)
      {
        THROW("[type_checker]: incorrect argument type in call to function");
      }
      if (node->param(i)->type()->name() == cdk::TYPE_FUNCTIONAL) {
        auto function_node = dynamic_cast<mml::function_node*>(node->param(i));
        if (function_node == nullptr){
          refrence_hint_vector->push_back(true);
        }
        else {
          refrence_hint_vector->push_back(false);
        }
      }
      else {
        refrence_hint_vector->push_back(false);
      }
    }
    _parent->set_function_reference_hints(refrence_hint_vector);
  }

  node->type(functionType->output(0));
}
void mml::type_checker::do_null_node(mml::null_node *const node, int lvl)
{
  ASSERT_UNSPEC;
  node->type(cdk::reference_type::create(4, cdk::primitive_type::create(1, cdk::TYPE_VOID)));
}

void mml::type_checker::do_indexing_ptr_node(mml::indexing_ptr_node *const node, int lvl)
{
  ASSERT_UNSPEC;
  node->expression_ptr()->accept(this, lvl + 2);
  node->index()->accept(this, lvl + 2);

  if (!node->expression_ptr()->is_typed(cdk::TYPE_POINTER))
  {
    THROW("[type_checker]: pointer expression expected in pointer indexing");
  }

  if (!node->index()->is_typed(cdk::TYPE_INT))
  {
    THROW("[type_checker]: integer expected in index");
  }

  auto ref = cdk::reference_type::cast(node->expression_ptr()->type());
  node->type(ref->referenced());
}

void mml::type_checker::do_address_of_node(mml::address_of_node *const node, int lvl)
{
  ASSERT_UNSPEC;
  node->lvalue()->accept(this, lvl + 2);
  node->type(cdk::reference_type::create(4, node->lvalue()->type()));
}

void mml::type_checker::do_memory_alloc_node(mml::memory_alloc_node *const node, int lvl)
{
  ASSERT_UNSPEC;
  node->expression()->accept(this, lvl + 2);
  node->type(cdk::reference_type::create(4, cdk::primitive_type::create(1, cdk::TYPE_VOID)));
}

void mml::type_checker::do_sizeof_node(mml::sizeof_node *const node, int lvl)
{
  ASSERT_UNSPEC;
  node->expression()->accept(this, lvl + 2);
  node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
}

//---------------------------------------------------------------------------

void mml::type_checker::do_integer_node(cdk::integer_node *const node, int lvl)
{
  ASSERT_UNSPEC;
  node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
}

void mml::type_checker::do_string_node(cdk::string_node *const node, int lvl)
{
  ASSERT_UNSPEC;
  node->type(cdk::primitive_type::create(4, cdk::TYPE_STRING));
}

//---------------------------------------------------------------------------

void mml::type_checker::processUnaryExpression(cdk::unary_operation_node *const node, int lvl)
{
  node->argument()->accept(this, lvl + 2);
  node->type(node->argument()->type());
}

void mml::type_checker::do_neg_node(cdk::neg_node *const node, int lvl)
{
  processUnaryExpression(node, lvl);

  if (!node->is_typed(cdk::TYPE_INT) && !node->is_typed(cdk::TYPE_DOUBLE))
  {
    THROW("[type_check]: invalid type in logical symmetric");
  }
}

void mml::type_checker::do_identity_node(mml::identity_node *const node, int lvl)
{
  processUnaryExpression(node, lvl);

  if (!node->is_typed(cdk::TYPE_INT) && !node->is_typed(cdk::TYPE_DOUBLE))
  {
    THROW("[type_check]: invalid type in logical identity");
  }
}

void mml::type_checker::do_not_node(cdk::not_node *const node, int lvl)
{
  processUnaryExpression(node, lvl);

  if (!node->is_typed(cdk::TYPE_INT))
  {
    THROW("[type_check]: invalid type in logical negation");
  }
}

//---------------------------------------------------------------------------

void mml::type_checker::processBinaryExpression(cdk::binary_operation_node *const node, int lvl, bool allow_ptrs = false)
{
  ASSERT_UNSPEC;
  node->left()->accept(this, lvl + 2);
  node->right()->accept(this, lvl + 2);

  if (node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_INT))
  {
    node->type(node->left()->type());
    return;
  }
  else if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_POINTER))
  {
    node->type(node->right()->type());
    return;
  }

  auto type = compatible_types(node->left()->type(), node->right()->type());
  if (!type || !is_ID(node->left()))
  {
    THROW("[type_checker]: invalid types in binary expr");
  }

  node->type(type);
}

void mml::type_checker::do_add_node(cdk::add_node *const node, int lvl)
{
  processBinaryExpression(node, lvl, true);
}
void mml::type_checker::do_sub_node(cdk::sub_node *const node, int lvl)
{
  processBinaryExpression(node, lvl, true);
}
void mml::type_checker::do_mul_node(cdk::mul_node *const node, int lvl)
{
  processBinaryExpression(node, lvl);
}
void mml::type_checker::do_div_node(cdk::div_node *const node, int lvl)
{
  processBinaryExpression(node, lvl);
}
void mml::type_checker::do_mod_node(cdk::mod_node *const node, int lvl)
{
  processBinaryExpression(node, lvl);
}
void mml::type_checker::do_lt_node(cdk::lt_node *const node, int lvl)
{
  processBinaryExpression(node, lvl);
}
void mml::type_checker::do_le_node(cdk::le_node *const node, int lvl)
{
  processBinaryExpression(node, lvl);
}
void mml::type_checker::do_ge_node(cdk::ge_node *const node, int lvl)
{
  processBinaryExpression(node, lvl);
}
void mml::type_checker::do_gt_node(cdk::gt_node *const node, int lvl)
{
  processBinaryExpression(node, lvl);
}
void mml::type_checker::do_ne_node(cdk::ne_node *const node, int lvl)
{
  processBinaryExpression(node, lvl, true);
}
void mml::type_checker::do_eq_node(cdk::eq_node *const node, int lvl)
{
  processBinaryExpression(node, lvl, true);
}

//---------------------------------------------------------------------------

void mml::type_checker::do_variable_node(cdk::variable_node *const node, int lvl)
{
  ASSERT_UNSPEC;
  const std::string &id = node->name();
  std::shared_ptr<mml::symbol> symbol = _symtab.find(id);

  if (symbol != nullptr)
  {
    node->type(symbol->type());
  }
  else
  {
    THROW("undeclared variable '" + id + "'");
  }
}

void mml::type_checker::do_rvalue_node(cdk::rvalue_node *const node, int lvl)
{
  ASSERT_UNSPEC;
  try
  {
    node->lvalue()->accept(this, lvl);
    node->type(node->lvalue()->type());
  }
  catch (const std::string &id)
  {
    throw "(rvalue_node) undeclared variable  '" + id + "'";
  }
}

void mml::type_checker::do_assignment_node(cdk::assignment_node *const node, int lvl)
{
  ASSERT_UNSPEC;
  node->lvalue()->accept(this, lvl + 2);
  node->rvalue()->accept(this, lvl + 2);

  if (compatible_types(node->lvalue()->type(), node->rvalue()->type(), ASSIGNMENT_TYPE_COMPAT) == nullptr)
  {
    THROW("[type_checker]: incompatible types in assignment");
  }

  node->type(node->lvalue()->type());

  if (auto input = dynamic_cast<mml::read_node *>(node->rvalue()); input && node->lvalue()->is_typed(cdk::TYPE_DOUBLE))
  {
    input->type(node->type());
  }
  else if (auto alloc = dynamic_cast<mml::memory_alloc_node *>(node->rvalue()))
  {
    alloc->type(node->lvalue()->type());
  }
}

//---------------------------------------------------------------------------

void mml::type_checker::do_program_node(mml::program_node *const node, int lvl)
{

  auto fnMainType = cdk::functional_type::create(cdk::primitive_type::create(4, cdk::TYPE_INT));
  auto symbol = std::make_shared<mml::symbol>(fnMainType, "_main", 0);
  _symtab.insert("_main", symbol);
  _parent->set_new_symbol(symbol);
  _function = symbol;
}

void mml::type_checker::do_evaluation_node(mml::evaluation_node *const node, int lvl)
{
  node->argument()->accept(this, lvl + 2);
}

void mml::type_checker::do_print_node(mml::print_node *const node, int lvl)
{
  node->argument()->accept(this, lvl + 2);
}

//---------------------------------------------------------------------------

void mml::type_checker::do_read_node(mml::read_node *const node, int lvl)
{
  /* try
  {
    node->argument()->accept(this, lvl);
  }
  catch (const std::string &id)
  {
    throw "undeclared variable '" + id + "'";
  } */
}

//---------------------------------------------------------------------------

void mml::type_checker::do_while_node(mml::while_node *const node, int lvl)
{
  node->condition()->accept(this, lvl + 4);
}

//---------------------------------------------------------------------------

void mml::type_checker::do_if_node(mml::if_node *const node, int lvl)
{
  node->condition()->accept(this, lvl + 4);
}

void mml::type_checker::do_if_else_node(mml::if_else_node *const node, int lvl)
{
  node->condition()->accept(this, lvl + 4);
}