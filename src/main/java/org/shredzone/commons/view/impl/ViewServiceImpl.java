/*
 * Shredzone Commons
 *
 * Copyright (C) 2012 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.shredzone.commons.view.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shredzone.commons.view.PathContext;
import org.shredzone.commons.view.PathType;
import org.shredzone.commons.view.ViewContext;
import org.shredzone.commons.view.ViewInterceptor;
import org.shredzone.commons.view.ViewService;
import org.shredzone.commons.view.exception.ErrorResponseException;
import org.shredzone.commons.view.exception.PageNotFoundException;
import org.shredzone.commons.view.exception.ViewException;
import org.shredzone.commons.view.manager.ViewInvoker;
import org.shredzone.commons.view.manager.ViewManager;
import org.shredzone.commons.view.manager.ViewPattern;
import org.shredzone.commons.view.util.ViewPathEvaluationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Default implementation of {@link ViewService}.
 *
 * @author Richard "Shred" Körber
 */
@Component
public class ViewServiceImpl implements ViewService {

    private @Resource ViewManager viewManager;
    private @Resource ServletContext servletContext;
    private @Resource ConversionService conversionService;
    private @Resource ApplicationContext appContext;

    private Collection<ViewInterceptor> interceptors;

    @PostConstruct
    protected void setup() {
        // Cannot immediately inject to the collection, as it fails when no
        // ViewInterceptor bean was found.
        interceptors = appContext.getBeansOfType(ViewInterceptor.class).values();
    }

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String path = req.getPathInfo();
        if (path == null) {
            path = "";
        }

        for (ViewInterceptor interceptor : interceptors) {
            interceptor.onRequest(req, resp);
        }

        String renderViewName = null;

        try {
            ViewContext context = getViewContext();
            context.putTypedArgument(ServletContext.class, servletContext);
            context.putTypedArgument(HttpServletResponse.class, resp);
            renderViewName = invokeView(path);
        } catch (ErrorResponseException ex) {
            for (ViewInterceptor interceptor : interceptors) {
                if (interceptor.onErrorResponse(ex, req, resp)) {
                    return;
                }
            }

            if (ex.getMessage() != null) {
                resp.sendError(ex.getResponseCode(), ex.getMessage());
            } else {
                resp.sendError(ex.getResponseCode());
            }
            return;
        }

        if (renderViewName != null) {
            for (ViewInterceptor interceptor : interceptors) {
                String newViewName = interceptor.onRendering(renderViewName, req, resp);
                if (newViewName != null) {
                    renderViewName = newViewName;
                }
            }

            String fullViewPath = getTemplatePath(renderViewName);
            RequestDispatcher dispatcher = servletContext.getRequestDispatcher(fullViewPath);
            dispatcher.forward(req, resp);
        }
    }

    @Override
    public ViewContext getViewContext() {
        return appContext.getBean("viewContext", ViewContext.class);
    }

    @Override
    public String invokeView(String path) throws ViewException {
        ViewContext context = getViewContext();

        for (ViewPattern pattern : viewManager.getViewPatterns()) {
            Map<String, String> pathParts = pattern.resolve(path);
            if (pathParts != null) { // matched!
                context.setPathParts(pathParts);
                ViewInvoker invoker = pattern.getInvoker();

                for (ViewInterceptor interceptor : interceptors) {
                    interceptor.onViewHandlerInvocation(context, invoker.getBean(), invoker.getMethod());
                }

                return pattern.getInvoker().invoke(context);
            }
        }

        throw new PageNotFoundException("No page found at " + path);
    }

    @Override
    public String buildPath(PathContext data, String view, PathType type) {
        Collection<ViewPattern> vpList;

        if (StringUtils.hasText(view)) {
            // The given view is required...
            vpList = viewManager.getViewPatternsForView(view);
            if (vpList == null) {
                throw new IllegalArgumentException("Unknown view " + view);
            }

        } else {
            // Find a view by the signature...
            ViewPattern pattern = viewManager.getViewPatternForSignature(data.getSignature());
            if (pattern == null) {
                throw new IllegalArgumentException("No view for signature: " + data.getSignature());
            }
            vpList = Collections.singletonList(pattern);
        }

        EvaluationContext evContext = createEvaluationContext(data);

        for (ViewPattern pattern : vpList) {
            String path = pattern.evaluate(evContext, data);
            if (path != null) {
                return processPath(path, type);
            }
        }

        return null;
    }

    @Override
    public String getTemplatePath(String template) {
        if (template == null || template.isEmpty()) {
            throw new IllegalArgumentException("template name not set");
        }

        if (template.startsWith("/")) {
            template = template.substring(1);
        }

        return servletContext.getAttribute("jspPath") + template;
    }

    /**
     * Creates an {@link EvaluationContext} to be used for evaluation in this view
     * service. The default implementation creates a {@link ViewPathEvaluationContext}.
     *
     * @param context
     *            {@link PathContext} to be used as root object
     * @return {@link EvaluationContext} to be used for evaluation
     */
    protected EvaluationContext createEvaluationContext(PathContext context) {
        ViewPathEvaluationContext evContext = new ViewPathEvaluationContext(context);
        evContext.setTypeConverter(new StandardTypeConverter(conversionService));
        return evContext;
    }

    /**
     * Processes a path, prefixing the servlet name and making it absolute if requested.
     *
     * @param path
     *            relative path to be processed
     * @param type
     *            {@link PathType} to be returned
     * @return URL to this path
     */
    private String processPath(String path, PathType type) {
        if (type == PathType.VIEW) {
            return path;
        }

        StringBuilder sb = new StringBuilder();

        if (type == PathType.ABSOLUTE) {
            sb.append(getViewContext().getRequestServerUrl());
        }

        sb.append(servletContext.getContextPath());
        sb.append(getViewContext().getRequestServletName());
        sb.append(path);

        return sb.toString();
    }

}
