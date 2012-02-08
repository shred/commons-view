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

package org.shredzone.commons.view.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.shredzone.commons.view.Signature;
import org.shredzone.commons.view.annotation.View;
import org.shredzone.commons.view.annotation.ViewGroup;
import org.shredzone.commons.view.annotation.ViewHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Manages the view handlers.
 *
 * @author Richard "Shred" Körber
 */
@Component
public class ViewManager {
    private static final Logger LOG = LoggerFactory.getLogger(ViewManager.class);

    private @Resource ApplicationContext applicationContext;
    private @Resource ConversionService conversionService;

    private Map<String, List<ViewPattern>> patternMap = new HashMap<String, List<ViewPattern>>();
    private Map<Signature, ViewPattern> signatureMap = new HashMap<Signature, ViewPattern>();
    private List<ViewPattern> patternOrder = new ArrayList<ViewPattern>();

    /**
     * Returns a collection of all defined {@link ViewPattern}.
     *
     * @return Collection of matching {@link ViewPattern}
     */
    public Collection<ViewPattern> getViewPatterns() {
        return Collections.unmodifiableCollection(patternOrder);
    }

    /**
     * Returns a collection of {@link ViewPattern} that were defined for the given view.
     *
     * @param view
     *            View name
     * @return Collection of matching {@link ViewPattern}, or {@code null} if there is no
     *         such view
     */
    public Collection<ViewPattern> getViewPatternsForView(String view) {
        List<ViewPattern> result = patternMap.get(view);
        if (result != null) {
            return Collections.unmodifiableCollection(result);
        } else {
            return null;
        }
    }

    /**
     * Returns the {@link ViewPattern} that handles the given {@link Signature}.
     *
     * @param signature
     *            {@link Signature} to find a {@link ViewPattern} for
     * @return {@link ViewPattern} found, or {@code null} if there is no such
     *         {@link ViewPattern}
     */
    public ViewPattern getViewPatternForSignature(Signature signature) {
        return signatureMap.get(signature);
    }

    /**
     * Sets up the view manager. All Spring beans are searched for {@link ViewHandler}
     * annotations.
     */
    @PostConstruct
    protected void setup() {
        Collection<Object> beans = applicationContext.getBeansWithAnnotation(ViewHandler.class).values();
        for (Object bean : beans) {
            ViewHandler vhAnno = bean.getClass().getAnnotation(ViewHandler.class);
            if (vhAnno != null) {
                for (Method method : bean.getClass().getMethods()) {
                    ViewGroup groupAnno = AnnotationUtils.findAnnotation(method, ViewGroup.class);
                    if (groupAnno != null) {
                        for (View viewAnno : groupAnno.value()) {
                            processView(bean, method, viewAnno);
                        }
                    }

                    View viewAnno = AnnotationUtils.findAnnotation(method, View.class);
                    if (viewAnno != null) {
                        processView(bean, method, viewAnno);
                    }
                }
            }
        }

        for (List<ViewPattern> pl : patternMap.values()) {
            Collections.sort(pl);
        }

        Collections.sort(patternOrder);
    }

    /**
     * Processes a {@link View}. A view name and view pattern is generated, and a
     * {@link ViewInvoker} is built.
     *
     * @param bean
     *            Spring bean to be used
     * @param method
     *            View handler method to be invoked
     * @param anno
     *            {@link View} annotation
     */
    private void processView(Object bean, Method method, View anno) {
        String name = computeViewName(method, anno);

        List<ViewPattern> vpList = patternMap.get(name);
        if (vpList == null) {
            vpList = new ArrayList<ViewPattern>();
            patternMap.put(name, vpList);
        }

        ViewInvoker invoker = new ViewInvoker(bean, method, conversionService);

        String pattern = anno.pattern();
        String[] signature = anno.signature();
        ViewPattern vp = new ViewPattern(pattern, invoker, signature);
        Signature sig = vp.getSignature();
        if (sig != null) {
            if (signatureMap.containsKey(sig)) {
                throw new IllegalStateException("Signature '" + sig + "' defined twice");
            }
            signatureMap.put(sig, vp);
        }

        patternOrder.add(vp);
        vpList.add(vp);

        LOG.info("Found view '{}' with pattern '{}'", name, pattern);
    }

    /**
     * Computes a view name. If the {@link View} annotation contains a name, it is used.
     * If no name is given, it is guessed by the method name. If the method name ends with
     * "View", it is removed.
     *
     * @param method
     *            {@link Method} of the view handler
     * @param anno
     *            {@link View} annotation
     * @return view name to be used for this view
     */
    private String computeViewName(Method method, View anno) {
        if (StringUtils.hasText(anno.name())) {
            return anno.name();
        }

        String name = method.getName();
        if (name.length() > 4 && name.endsWith("View")) {
            name = name.substring(0, name.length() - "View".length());
        }

        return name;
    }

}
