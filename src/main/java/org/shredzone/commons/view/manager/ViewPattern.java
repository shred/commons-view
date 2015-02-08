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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.shredzone.commons.view.PathContext;
import org.shredzone.commons.view.Signature;
import org.shredzone.commons.view.annotation.View;
import org.shredzone.commons.view.util.PathUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * A view pattern is a URL pattern for a view. It is used to detect which view is used for
 * handling a HTTP request, and what parameters are used.
 * <p>
 * {@link ViewPattern ViewPatterns} are immutable.
 *
 * @author Richard "Shred" Körber
 */
public class ViewPattern implements Comparable<ViewPattern> {
    private static final Pattern PATH_PART = Pattern.compile("\\$\\{([^\\}]+)\\}");

    private final String pattern;
    private final ViewInvoker invoker;
    private final Signature signature;
    private final Pattern regEx;
    private final List<Expression> expression;
    private final List<String> parameter;
    private final int weight;
    private final String qualifier;

    /**
     * Instantiates a new view pattern.
     *
     * @param anno
     *            {@link View} annotation
     * @param invoker
     *            {@link ViewInvoker} for rendering this view
     */
    public ViewPattern(View anno, ViewInvoker invoker) {
        this.invoker = invoker;
        this.pattern = anno.pattern();

        if (anno.qualifier() != null && !anno.qualifier().isEmpty()) {
            this.qualifier = anno.qualifier();
        } else {
            this.qualifier = null;
        }

        String[] sig = anno.signature();
        if (sig != null && sig.length > 0) {
            this.signature = new Signature(sig);
        } else {
            this.signature = null;
        }

        List<Expression> expList = new ArrayList<>();
        List<String> paramList = new ArrayList<>();
        StringBuilder pb = new StringBuilder();
        compilePattern(this.pattern, pb, expList, paramList);
        this.regEx = Pattern.compile(pb.toString());
        this.expression = Collections.unmodifiableList(expList);
        this.parameter = Collections.unmodifiableList(paramList);

        this.weight = computeWeight(this.pattern);
    }

    /**
     * Gets the signature stored in this {@link ViewPattern}.
     *
     * @return {@link Signature}
     */
    public Signature getSignature() {
        return signature;
    }

    /**
     * Gets the {@link ViewInvoker} to be used for rendering.
     *
     * @return {@link ViewInvoker}
     */
    public ViewInvoker getInvoker() {
        return invoker;
    }

    /**
     * Gets the weight of this {@link ViewPattern}. If more than one {@link ViewPattern}
     * matches the requested URL, the one with the highest weight is taken.
     *
     * @return weight
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Gets the view's URL pattern.
     *
     * @return URL pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Gets a regular expression {@link Pattern} to match a URL against this
     * {@link ViewPattern}. This regular expression can be used to quickly find view
     * candidates for a request URL.
     *
     * @return regular expression {@link Pattern}.
     */
    public Pattern getRegEx() {
        return regEx;
    }

    /**
     * Returns an {@link Expression} for each placeholder in the pattern. The expressions
     * are used for building an URL to this view.
     *
     * @return List of {@link Expression}
     */
    public List<Expression> getExpression() {
        return expression;
    }

    /**
     * Returns a list of parameter strings for each placeholder in the pattern.
     *
     * @return List of parameters
     */
    public List<String> getParameters() {
        return parameter;
    }

    /**
     * Returns the qualifier of this pattern.
     *
     * @return Qualifier of this pattern, or {@code null} for the default qualifier.
     */
    public String getQualifier() {
        return qualifier;
    }

    /**
     * Matches the requested URL against this {@link ViewPattern}.
     *
     * @param path
     *            the requested URL
     * @return {@code true} if this {@link ViewPattern} matches the given URL, and thus is
     *         a candidate for rendering
     */
    public boolean matches(String path) {
        return regEx.matcher(path).matches();
    }

    /**
     * Resolves a requested URL path. For each placeholder in the view pattern, the
     * placeholder name and its value in the URL path is returned in a map.
     *
     * @param path
     *            the requested URL to be resolved
     * @return Map containing the placeholder names and its values
     */
    public Map<String, String> resolve(String path) {
        Matcher m = regEx.matcher(path);
        if (!m.matches()) {
            return null;
        }

        if (m.groupCount() != parameter.size()) {
            throw new IllegalStateException("regex group count " + m.groupCount()
                    + " does not match parameter count " + parameter.size());
        }

        // TODO: only use decode when #encode() was used
        return IntStream.range(0, parameter.size()).collect(
                    HashMap::new,
                    (map, ix) -> map.put(parameter.get(ix), PathUtils.decode(m.group(ix + 1))),
                    Map::putAll
        );
    }

    /**
     * Evaluates the given {@link EvaluationContext} and builds an URL to the appropriate
     * view.
     *
     * @param context
     *            {@link EvaluationContext} to be used
     * @param data
     *            {@link PathContext} containing all data required for building the URL
     * @return URL that was built, or {@code null} if the {@link PathContext} did not
     *         contain all necessary data for building the URL
     */
    public String evaluate(EvaluationContext context, PathContext data) {
        StringBuilder sb = new StringBuilder();
        for (Expression expr : expression) {
            String value = expr.getValue(context, data, String.class);
            if (value == null) {
                // A part resolved to null, so this ViewPattern is unable
                // to build a path from the given PathData.
                return null;
            }
            sb.append(value);
        }

        // Remove ugly double slashes
        int pos;
        while ((pos = sb.indexOf("//")) >= 0) {
            sb.deleteCharAt(pos);
        }

        return sb.toString();
    }

    /**
     * Compiles a view pattern. Generates a parameter list, a list of expressions for
     * building URLs to this view, and a regular expression for matching URLs against this
     * view pattern.
     *
     * @param pstr
     *            the view pattern
     * @param pattern
     *            {@link StringBuilder} to assemble the regular expression in
     * @param expList
     *            List of {@link Expression} to assemble expressions in
     * @param paramList
     *            List to assemble parameters in
     */
    private void compilePattern(String pstr, StringBuilder pattern,
            List<Expression> expList, List<String> paramList) {
        ExpressionParser parser = new SpelExpressionParser();
        int previous = 0;

        Matcher m = PATH_PART.matcher(pstr);
        while (m.find()) {
            String fixedPart = pstr.substring(previous, m.start());
            if (fixedPart.indexOf('\'') >= 0) {
                throw new IllegalArgumentException("path parameters must not contain \"'\"");
            }

            String expressionPart = m.group(1);

            pattern.append(Pattern.quote(fixedPart));
            pattern.append("([^/]*)");

            paramList.add(expressionPart);

            expList.add(parser.parseExpression('\'' + fixedPart + '\''));
            expList.add(parser.parseExpression(expressionPart));

            previous = m.end();
        }

        String postPart = pstr.substring(previous);
        pattern.append(Pattern.quote(postPart));
        expList.add(parser.parseExpression('\'' + postPart + '\''));
    }

    /**
     * Computes the weight of the pattern. The weight is computed by a score where every
     * path delimiter '/' counts 10, constant character counts 5 and every path parameter
     * counts 1.
     *
     * @param pstr
     *            view pattern to weight
     * @return weight of this pattern
     */
    private int computeWeight(String pstr) {
        int count = 0;
        int pos = 0;
        while (pos < pstr.length()) {
            char ch = pstr.charAt(pos);
            if (ch == '/') {
                count += 10;

            } else if (ch == '$' && pos + 1 < pstr.length() && pstr.charAt(pos + 1) == '{') {
                int end = pstr.indexOf('}', pos);
                if (end >= 0) {
                    pos = end;
                    count += 1;
                }

            } else {
                count += 5;
            }

            pos++;
        }
        return count;
    }

    @Override
    public int compareTo(ViewPattern o) {
        return o.getWeight() - getWeight();
    }

}