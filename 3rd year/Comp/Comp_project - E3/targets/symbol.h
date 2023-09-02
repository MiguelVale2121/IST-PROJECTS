#ifndef __MML_TARGETS_SYMBOL_H__
#define __MML_TARGETS_SYMBOL_H__

#include <string>
#include <memory>
#include <cdk/types/basic_type.h>
#include <cdk/types/structured_type.h>

namespace mml
{

  class symbol
  {
    std::shared_ptr<cdk::basic_type> _type;
    std::string _name;
    long _value;     // hack!
    int _offset = 0; // 0 (zero) means global variable/function

    // Label for anonymous functions
    std::string _fnLabel;

    bool _allow_redeclaration = true;
    bool _is_external = false;

  public:
    symbol(std::shared_ptr<cdk::basic_type> type, const std::string &name, long value) : _type(type), _name(name), _value(value)
    {
    }

    virtual ~symbol()
    {
      // EMPTY
    }

    void type(std::shared_ptr<cdk::basic_type> type)
    {
      _type = type;
    }
    std::shared_ptr<cdk::basic_type> type() const
    {
      return _type;
    }
    bool is_typed(cdk::typename_type name) const
    {
      return _type->name() == name;
    }
    const std::string &name() const
    {
      return _name;
    }
    long value() const
    {
      return _value;
    }
    long value(long v)
    {
      return _value = v;
    }
    int offset() const
    {
      return _offset;
    }
    void set_offset(int offset)
    {
      _offset = offset;
    }
    bool global() const
    {
      return _offset == 0;
    }
    const std::string &label() const
    {
      return _fnLabel;
    }
    void label(std::string label)
    {
      _fnLabel = label;
    }
    bool allow_redeclaration() const
    {
      return _allow_redeclaration;
    }
    void set_allow_redeclaration(bool allow_redeclaration)
    {
      _allow_redeclaration = allow_redeclaration;
    }
    bool is_external() const
    {
      return _is_external;
    }
    void set_external(bool external)
    {
      _is_external = external;
    }
  };

} // mml

#endif
