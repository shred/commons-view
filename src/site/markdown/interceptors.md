# View Interceptors

`commons-view` invokes view interceptors at different stages of request processing. Interceptors can be used e.g. for reacting on exceptions or change the templates used for rendering.

## Defining Interceptors

It is very easy to add interceptors: just define a Spring bean that implements the `ViewInterceptor` interface. For your convenience, there is a class `EmptyViewInterceptor` that implements the interface with dummy methods, so you will only need to override those you actually need.

## Interceptor Stages

Interceptors are invoked on different stages of the request processing.

### `onRequest()`

Invoked very early, when the request has just been received by the servlet. Can be used for setting common attributes or response headers.

### `onViewHandlerInvocation()`

Invoked just before a view handler is called. The `ViewContext` is given to the method, as well as the bean and method that is going to be invoked.

Possible usages:

* Filling the `ViewContext` with common values.
* Security checks before invoking the view.
* Vetoing against invoking the view (by throwing a `PageNotFoundException`)

### `onRendering()`

Invoked after the view handler has completed successfully, and returned a `String`.

Possible usages:

* Changing the name of the JSP template to be rendered
* Adding common attributes to the `HttpServletRequest` for the JSP template
* Adding common response headers
* Vetoing against rendering the JSP template

### `onErrorResponse()`

Invoked when the view handler threw an `ErrorResponseException`. The interceptor can decide if it processes the exception, or passes it on to the next interceptor.

Possible usages:

* Rendering an error page for the error code
* Modifying the respose headers on errors
