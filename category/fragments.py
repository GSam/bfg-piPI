
def _qualname(obj):
    """Get the fully-qualified name of an object (including module)."""
    return obj.__module__ + '.' + obj.__qualname__

def _declaring_class(obj):
    """Get the name of the class that declared an object."""
    name = _qualname(obj)
    return name[:name.rfind('.')]

# Stores the actual visitor methods
_methods = {}

# Delegating visitor implementation
def _visitor_impl(self, arg):
    """Actual visitor method implementation."""
    method = _methods[(_qualname(type(self)), type(arg))]
    return method(self, arg)

# The actual @visitor decorator
def visitor(arg_type):
    """Decorator that creates a visitor method."""

    def decorator(fn):
        declaring_class = _declaring_class(fn)
        _methods[(declaring_class, arg_type)] = fn

        # Replace all decorated methods with _visitor_impl
        return _visitor_impl

    return decorator


class Lion: pass
class Tiger: pass
class Bear: pass

class ZooVisitor:
    @visitor(Lion)
    def visit(self, animal):
        return "Lions"

    @visitor(Tiger)
    def visit(self, animal):
        return "tigers"

    @visitor(Bear)
    def visit(self, animal):
        return "and bears, oh my!"

animals = [Lion(), Tiger(), Bear()]
visitor = ZooVisitor()
print(', '.join(visitor.visit(animal) for animal in animals))
# Prints "Lions, tigers, and bears, oh my!"
