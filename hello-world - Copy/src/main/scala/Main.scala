@main def hello: Unit =
  println("Hello world!")
  println(msg)
  com.eed3si9n.eval.Eval()
    .evalInfer("""println("Hello, World! EVAL")""")
    .getValue(this.getClass.getClassLoader)

def msg = "I was compiled by Scala 3. :)"