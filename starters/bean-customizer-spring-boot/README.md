# bean-customizer-spring-boot

A quick library for spring to add a slightly more friendly way to drive bean post processors.

Usage:

Declare a bean that implements the `BeanCustomizer` interface. 

The `getType` method returns the type of Bean the customizer wants to customize. 

Callbacks will be made to `postProcessBeforeInit` and to `postProcessAfterInit` passing the bean to process.
As with regular BeanPostProcessors, you can adjust the bean, or switch it for another (keep the type the same).

Why this instead of BeanPostProcessors directly? 

Because I didn't like ending up with many processors each chewing through every bean hoping this was one they needed.
This way, the `BeanCustomizer` is only invoked with the type it asks for, and only a single processor is registerd 
regardless of the number of customizers required.

Why isn't the type/generics better? / Why is it still Object?

Because I couldn't figure that part out, it gets kinda fuzzy when you want to have a BeanCustomizer<T> that has a 
method that returns T's class, and methods that accept/return T, and then the post processor itself has to cast to
different T's depending on the class of the Bean being passed to a corresponding Customizer (because the processor 
only ever see's it as Object), which made it easier to leave it all as Object for now. I accept PR's for how to do this
properly ;p
