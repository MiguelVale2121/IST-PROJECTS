#include <string>
#include <sstream>
#include "targets/xml_writer.h"
#include "targets/type_checker.h"
#include ".auto/all_nodes.h" // automatically generated

//---------------------------------------------------------------------------

void mml::xml_writer::do_nil_node(cdk::nil_node *const node, int lvl)
{
  // EMPTY
}
void mml::xml_writer::do_data_node(cdk::data_node *const node, int lvl)
{
  // EMPTY
}
void mml::xml_writer::do_double_node(cdk::double_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  process_literal(node, lvl);
}

void mml::xml_writer::do_and_node(cdk::and_node *const node, int lvl)
{
  do_binary_operation(node, lvl);
}
void mml::xml_writer::do_or_node(cdk::or_node *const node, int lvl)
{
  do_binary_operation(node, lvl);
}

/* Custom functions (BEGIN) */
std::string escape(const std::string &src)
{
  std::stringstream dst;
  for (char ch : src)
  {
    switch (ch)
    {
    case '&':
      dst << "&amp;";
      break;
    case '\'':
      dst << "&apos;";
      break;
    case '"':
      dst << "&quot;";
      break;
    case '<':
      dst << "&lt;";
      break;
    case '>':
      dst << "&gt;";
      break;
    default:
      dst << ch;
      break;
    }
  }
  return dst.str();
}
/* Custom function (END) */

void mml::xml_writer::do_declaration_node(mml::declaration_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;

  os() << std::string(lvl, ' ') << "<declaration_node"
       << " isForward='" << node->isForward() << "'"
       << " isForeign='" << node->isForeign() << "'"
       << " isPublic='" << node->isPublic() << "'"
       << " isAuto='" << node->isAuto() << "'"
       << " type='" << escape(cdk::to_string(node->type())) << "'>" << std::endl;

  os() << std::string(lvl + 2, ' ') << "<identifier>" << node->identifier() << "</identifier>" << std::endl;

  if (node->expression())
  {
    openTag("expression", lvl + 2);
    node->expression()->accept(this, lvl + 2 * 2);
    closeTag("expression", lvl + 2);
  }

  closeTag(node, lvl);
}

void mml::xml_writer::do_stop_node(mml::stop_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  os() << std::string(lvl, ' ') << "<" << node->label() << " level='" << node->level() << "'>" << std::endl;
  closeTag(node, lvl);
}
void mml::xml_writer::do_next_node(mml::next_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  os() << std::string(lvl, ' ') << "<" << node->label() << " level='" << node->level() << "'>" << std::endl;
  closeTag(node, lvl);
}

void mml::xml_writer::do_return_node(mml::return_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  if (node->expression())
    node->expression()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void mml::xml_writer::do_block_node(mml::block_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);

  _symtab.push();
  if (node->declarations())
  {
    openTag("declarations", lvl + 2);
    node->declarations()->accept(this, lvl + 2 * 2);
    closeTag("declarations", lvl + 2);
  }

  if (node->instructions())
  {
    openTag("instructions", lvl + 2);
    node->instructions()->accept(this, lvl + 2 * 2);
    closeTag("instructions", lvl + 2);
  }
  _symtab.pop();

  closeTag(node, lvl);
}

void mml::xml_writer::do_function_node(mml::function_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  os() << std::string(lvl, ' ') << "<function_node type='" << escape(cdk::to_string(node->type())) << "'>" << std::endl;

  _symtab.push();

  if (node->params())
  {
    openTag("params", lvl + 2);
    node->params()->accept(this, lvl + 2 * 2);
    closeTag("params", lvl + 2);
  }

  node->block()->accept(this, lvl + 2);

  _symtab.pop();

  closeTag(node, lvl);
}
void mml::xml_writer::do_function_call_node(mml::function_call_node *const node, int lvl)
{
  os() << std::string(lvl, ' ') << "<" << node->label() << ">" << std::endl;

  openTag("params", lvl + 2);
  if (node->params())
    node->params()->accept(this, lvl + 4);
  closeTag("params", lvl + 2);

  openTag("function_expression", lvl + 2);
  if (node->function())
    node->function()->accept(this, lvl + 4);
  closeTag("function_expression", lvl + 2);

  closeTag(node, lvl);
}
void mml::xml_writer::do_null_node(mml::null_node *const node, int lvl)
{
  openTag(node, lvl);
  closeTag(node, lvl);
}

void mml::xml_writer::do_identity_node(mml::identity_node *const node, int lvl)
{
  do_unary_operation(node, lvl);
}

void mml::xml_writer::do_indexing_ptr_node(mml::indexing_ptr_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);

  openTag("base", lvl + 2);
  node->expression_ptr()->accept(this, lvl + 2 * 2);
  closeTag("base", lvl + 2);

  openTag("index", lvl + 2);
  node->index()->accept(this, lvl + 2 * 2);
  closeTag("index", lvl + 2);

  closeTag(node, lvl);
}

void mml::xml_writer::do_address_of_node(mml::address_of_node *const node, int lvl)
{
  openTag(node, lvl);
  node->lvalue()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void mml::xml_writer::do_memory_alloc_node(mml::memory_alloc_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->expression()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void mml::xml_writer::do_sizeof_node(mml::sizeof_node *const node, int lvl)
{
  openTag(node, lvl);
  node->expression()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void mml::xml_writer::do_sequence_node(cdk::sequence_node *const node, int lvl)
{
  os() << std::string(lvl, ' ') << "<sequence_node size='" << node->size() << "'>" << std::endl;
  for (size_t i = 0; i < node->size(); i++)
    node->node(i)->accept(this, lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void mml::xml_writer::do_integer_node(cdk::integer_node *const node, int lvl)
{
  process_literal(node, lvl);
}

void mml::xml_writer::do_string_node(cdk::string_node *const node, int lvl)
{
  process_literal(node, lvl);
}

//---------------------------------------------------------------------------

void mml::xml_writer::do_unary_operation(cdk::unary_operation_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->argument()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void mml::xml_writer::do_not_node(cdk::not_node *const node, int lvl)
{
  do_unary_operation(node, lvl);
}

void mml::xml_writer::do_neg_node(cdk::neg_node *const node, int lvl)
{
  do_unary_operation(node, lvl);
}

//---------------------------------------------------------------------------

void mml::xml_writer::do_binary_operation(cdk::binary_operation_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->left()->accept(this, lvl + 2);
  node->right()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void mml::xml_writer::do_add_node(cdk::add_node *const node, int lvl)
{
  do_binary_operation(node, lvl);
}
void mml::xml_writer::do_sub_node(cdk::sub_node *const node, int lvl)
{
  do_binary_operation(node, lvl);
}
void mml::xml_writer::do_mul_node(cdk::mul_node *const node, int lvl)
{
  do_binary_operation(node, lvl);
}
void mml::xml_writer::do_div_node(cdk::div_node *const node, int lvl)
{
  do_binary_operation(node, lvl);
}
void mml::xml_writer::do_mod_node(cdk::mod_node *const node, int lvl)
{
  do_binary_operation(node, lvl);
}
void mml::xml_writer::do_lt_node(cdk::lt_node *const node, int lvl)
{
  do_binary_operation(node, lvl);
}
void mml::xml_writer::do_le_node(cdk::le_node *const node, int lvl)
{
  do_binary_operation(node, lvl);
}
void mml::xml_writer::do_ge_node(cdk::ge_node *const node, int lvl)
{
  do_binary_operation(node, lvl);
}
void mml::xml_writer::do_gt_node(cdk::gt_node *const node, int lvl)
{
  do_binary_operation(node, lvl);
}
void mml::xml_writer::do_ne_node(cdk::ne_node *const node, int lvl)
{
  do_binary_operation(node, lvl);
}
void mml::xml_writer::do_eq_node(cdk::eq_node *const node, int lvl)
{
  do_binary_operation(node, lvl);
}

//---------------------------------------------------------------------------

void mml::xml_writer::do_variable_node(cdk::variable_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  os() << std::string(lvl, ' ') << "<" << node->label() << ">" << node->name() << "</" << node->label() << ">" << std::endl;
}

void mml::xml_writer::do_rvalue_node(cdk::rvalue_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->lvalue()->accept(this, lvl + 4);
  closeTag(node, lvl);
}

void mml::xml_writer::do_assignment_node(cdk::assignment_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);

  node->lvalue()->accept(this, lvl + 2);
  reset_new_symbol();

  node->rvalue()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void mml::xml_writer::do_program_node(mml::program_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;

  _function = new_symbol();
  openTag(node, lvl);
  node->statements()->accept(this, lvl + 4);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void mml::xml_writer::do_evaluation_node(mml::evaluation_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->argument()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void mml::xml_writer::do_print_node(mml::print_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  os() << std::string(lvl, ' ') << "<print_node newline='" << ((node->new_line()) ? "true" : "false") << "'>" << std::endl;
  node->argument()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void mml::xml_writer::do_read_node(mml::read_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void mml::xml_writer::do_while_node(mml::while_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("condition", lvl + 2);
  node->condition()->accept(this, lvl + 4);
  closeTag("condition", lvl + 2);
  openTag("block", lvl + 2);
  node->block()->accept(this, lvl + 4);
  closeTag("block", lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void mml::xml_writer::do_if_node(mml::if_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("condition", lvl + 2);
  node->condition()->accept(this, lvl + 4);
  closeTag("condition", lvl + 2);
  openTag("then", lvl + 2);
  node->block()->accept(this, lvl + 4);
  closeTag("then", lvl + 2);
  closeTag(node, lvl);
}

void mml::xml_writer::do_if_else_node(mml::if_else_node *const node, int lvl)
{
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("condition", lvl + 2);
  node->condition()->accept(this, lvl + 4);
  closeTag("condition", lvl + 2);
  openTag("then", lvl + 2);
  node->thenblock()->accept(this, lvl + 4);
  closeTag("then", lvl + 2);
  openTag("else", lvl + 2);
  node->elseblock()->accept(this, lvl + 4);
  closeTag("else", lvl + 2);
  closeTag(node, lvl);
}
