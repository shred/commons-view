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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.shredzone.commons.view.ViewContext;
import org.shredzone.commons.view.exception.ViewContextException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link ViewContext}.
 * <p>
 * This bean is request scoped.
 *
 * @author Richard "Shred" Körber
 */
@Component("viewContext")
@Scope("request")
public class ViewContextImpl implements ViewContext {

    @Resource
    private HttpServletRequest req;

    private String requestServerUrl;
    private String requestServletName;

    private Map<Class<?>, Object> typedValueMap = new HashMap<Class<?>, Object>();
    private Map<String, String> pathParts;

    /**
     * Sets up this bean's contents.
     */
    @PostConstruct
    protected void setup() {
        StringBuilder sb = new StringBuilder();
        String scheme = req.getScheme();
        int serverPort = req.getServerPort();
        sb.append(scheme).append("://").append(req.getServerName());
        if (! (("http".equals(scheme) && serverPort == 80)
            || ("https".equals(scheme) && serverPort == 443))) {
            sb.append(':').append(serverPort);
        }

        requestServerUrl = sb.toString();
        requestServletName = req.getServletPath();
    }

    @Override
    public String getRequestServerUrl() {
        return requestServerUrl;
    }

    @Override
    public String getRequestServletName() {
        return requestServletName;
    }

    @Override
    public String getParameter(String name) {
        return req.getParameter(name);
    }

    @Override
    public <T> void putTypedArgument(Class<T> type, T value) {
        if (value != null && !type.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("value must be an instance of "
                            + type.getName() + ", but is " + value.getClass().getName());
        }
        typedValueMap.put(type, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValueOfType(Class<T> type) throws ViewContextException {
        if (typedValueMap.containsKey(type)) {
            return (T) typedValueMap.get(type);
        }

        if (type.isAssignableFrom(HttpServletRequest.class)) {
            return (T) req;
        }

        if (type.isAssignableFrom(HttpSession.class)) {
            return (T) req.getSession();
        }

        if (type.isAssignableFrom(Locale.class)) {
            return (T) req.getLocale();
        }

        if (type.isAssignableFrom(OutputStream.class)) {
            try {
                HttpServletResponse resp = getValueOfType(HttpServletResponse.class);
                return (T) resp.getOutputStream();
            } catch (IOException ex) {
                throw new ViewContextException("Could not get OutputStream", ex);
            }
        }

        if (type.isAssignableFrom(PrintWriter.class)) {
            try {
                HttpServletResponse resp = getValueOfType(HttpServletResponse.class);
                return (T) resp.getWriter();
            } catch (IOException ex) {
                throw new ViewContextException("Could not get Writer", ex);
            }
        }

        if (type.isAssignableFrom(InputStream.class)) {
            try {
                return (T) req.getInputStream();
            } catch (IOException ex) {
                throw new ViewContextException("Could not get InputStream", ex);
            }
        }

        if (type.isAssignableFrom(BufferedReader.class)) {
            try {
                return (T) req.getReader();
            } catch (IOException ex) {
                throw new ViewContextException("Could not get Reader", ex);
            }
        }

        throw new ViewContextException("No value for type " + type.getName());
    }

    @Override
    public void setPathParts(Map<String, String> pathParts) {
        this.pathParts = Collections.unmodifiableMap(pathParts);
    }

    @Override
    public Map<String, String> getPathParts() {
        return pathParts;
    }

}
