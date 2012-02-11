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
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.shredzone.commons.view.annotation.View;
import org.shredzone.commons.view.annotation.ViewGroup;

/**
 * Unit tests for {@link ViewPattern}.
 *
 * @author Richard "Shred" Körber
 */
public class ViewPatternTest {

    @Test
    @View(pattern = "test/${blafoo}/and/a/${path}.html")
    public void functionalTest() throws NoSuchMethodException {
        Method m = this.getClass().getMethod("functionalTest");

        ViewPattern pat = new ViewPattern(m.getAnnotation(View.class), null);
        Assert.assertEquals(107, pat.getWeight());
        Assert.assertEquals("test/${blafoo}/and/a/${path}.html", pat.getPattern());
        Assert.assertEquals("\\Qtest/\\E([^/]*)\\Q/and/a/\\E([^/]*)\\Q.html\\E", pat.getRegEx().pattern());

        String[] expected = new String[] {"blafoo", "path"};
        Assert.assertArrayEquals(expected, pat.getParameters().toArray());
        try {
            pat.getParameters().add("foo");
            Assert.fail("parameter list is modifiable");
        } catch (UnsupportedOperationException ex) {
            // We expected this exception
        }

        Map<String, String> map;

        map = pat.resolve("test/Something/and/a/1234.html");
        Assert.assertNotNull(map);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("Something", map.get("blafoo"));
        Assert.assertEquals("1234", map.get("path"));

        map = pat.resolve("test//and/a/.html");
        Assert.assertNotNull(map);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("", map.get("blafoo"));
        Assert.assertEquals("", map.get("path"));

        map = pat.resolve("test/Something/and/a/1234.htm");
        Assert.assertNull(map);

        map = pat.resolve("something");
        Assert.assertNull(map);

    }

    @Test
    @ViewGroup({
        @View(pattern = "test/${blafoo}.html"),
        @View(pattern = "test.html"),
        @View(pattern = "test/${blafoo}/and/a/${path}.html")
    })
    public void orderTest() throws NoSuchMethodException {
        Method m = this.getClass().getMethod("orderTest");
        SortedSet<ViewPattern> set = new TreeSet<ViewPattern>();

        ViewGroup groupAnno = m.getAnnotation(ViewGroup.class);
        for (View view : groupAnno.value()) {
            set.add(new ViewPattern(view, null));
        }

        Iterator<ViewPattern> it = set.iterator();
        Assert.assertEquals("test/${blafoo}/and/a/${path}.html", it.next().getPattern());
        Assert.assertEquals("test/${blafoo}.html", it.next().getPattern());
        Assert.assertEquals("test.html", it.next().getPattern());
    }

}
