# Define Views

Views are used to process a HTTP request and prepare to send a respose. The view handler that is in charge of sending the response is found by its URL pattern. Usually the response is a rendered JSP, but it could also be a binary stream.

In `commons-view`, views are Spring beans and are defined entirely by annotations.

First of all, the Spring bean class must be annotated with `@ViewHandler`. This is required so `commons-view` will recognize the bean as view handler and look at its methods for single view handlers.

## A Very Simple View

A view handler method is defined by a `@View` annotation and contains the URL pattern. If the requested URL matches the pattern, the view handler is invoked.

This is an example of a very simple view handler:

<pre>
@View(pattern = "/helloworld.html")
public String helloworldView() {
    return "helloworld.jsp";
}
</pre>

This view is invoked when the `commons-view` servlet is invoked with an URL like `http://www.example.com/viewservlet/helloworld.html`. Since the view returns a String, the rendering of the response is delegated to the returned JSP file. If the view would return `null` (or `void`), `commons-view` assumes that it already took care of sending a respose itself (e.g. by sending a binary stream), so no JSP would be rendered.

## HTTP Parameters

HTTP parameters can be passed in to the handler as invocation parameters. A type conversion is done if necessary (using Spring's `SimpleTypeConverter`).

```java
@View(pattern = "/showDocument.html")
public String documentView(
    @Parameter("page") Long page
) {
    // some sanity checks on page...
    return "document" + page + ".jsp";
}
```

An example invocation URL would be `http://www.example.com/viewservlet/showDocument.html?page=13`.

You can see that the `page` parameter is automatically converted to a `Long`. If the parameter is omitted, a "404 page not found" error message is shown. However, this behavior can be changed, as we will see soon.

## Path Placeholders

A speciality of `commons-view` is that the view path pattern can contain placeholders. The URL part of the placeholder can be passed in as parameter, too (also with type conversion if necessary):

```java
@View(pattern = "/${planet}/hello.html")
public String helloplanetView(
    @PathPart("planet") String planet,
    HttpServletRequest req
) {
    req.setAttribute("planet", planet);
    return "helloplanet.jsp";
}
```

An example invocation URL would be `http://www.example.com/viewservlet/venus/hello.html`, which would give "venus" as `planet` parameter.

A path part can contain placeholders mixed with text (like `"/hello${planet}.html"`) or even multiple placeholders (like `"/hello_${planet}_${moon}.html"`).

The placeholder never matches '/' characters, so `http://www.example.com/viewservlet/jupiter/io/hello.html` would _not_ give "jupiter/io" as `planet`, but it would not match this view pattern at all.

If a path part is ommitted (`http://www.example.com/viewservlet//hello.html`, note the double slash), `commons-view` does not attempt to find another view pattern that would match. A "404 page not found" error message is shown instead. You can change that behavior too.

## Optional Parameters

If at least one of the parameters or path placeholders is omitted, the handler will not be invoked and a "404 page not found" error message is rendered instead. Optional parameters need to be annotated with `@Optional` and will then give `null` if the parameter was omitted.

```java
@View(pattern = "/hello${name}.html")
public String helloplanetView(
    `Optional `PathPart("name") String name
) {
    return "helloplanet.jsp";
}
```

In this example, `name` is `null` if `/hello.html` is invoked, and `"moon"` if `/hellomoon.html` is invoked.

## View Groups

It is possible to match multiple URL patterns to a view handler by using the `@ViewGroup` annotation. The view handler is invoked when one of the patterns matched. Since Java 8, the `@ViewGroup` annotation is not required any more, multiple `@View` annotations can be added to the handler instead.

By using qualifiers, the handler is able to detect which of the view patterns actually matched.

```java
@ViewGroup({
  @View(pattern = "/helloworld.html"),
  @View(pattern = "/hellomars.html", qualifier = "mars")
})
public String helloworldView(
    @Qualifier String qualifier
) {
    if ("mars".equals(qualifier)) {
        return "hellomars.jsp";
    }
    return "helloworld.jsp";
}
```

Qualifiers are optional. A good use case is handling AJAX requests. The view handler prepares the rendering of a page, but finally use different templates depending on whether the standard URL (full page is rendered) or an AJAX URL (page is partially rendered) was requested.

## Parameter Types

As you saw in an example a few lines up, you can use method parameter types like `HttpServletRequest`. `commons-view` will resolve the type automatically, and pass in the according value when the handler is invoked.

These types can be passed in:

* `HttpServletRequest`
* `HttpServletResponse`
* `ServletContext`
* `HttpSession`
* `Locale`
* `InputStream`
* `BufferedReader`
* `OutputStream`
* `PrintStream`

You can also pass in further types by adding them to the `ViewContext` (via its `putTypedArgument()` method) in a [View Interceptor](./interceptors.html).

## Pattern Matching

Due to the nature of placeholders, it is possible that two or more view patterns actually match the request URL. In this case, a heuristic decides which handler is to be invoked. Basically, it prefers constant parts of the pattern over placeholders. It seems to work quite well in practice, but it still can lead to an unexpected view handler being invoked.

For this reason, it is strongly recommended to avoid conflicting patterns, for example by directory-like pattern prefixes.

Remember: Since placeholder never match slashes, the number of slashes in the pattern must match those of the requested URL in all cases.
