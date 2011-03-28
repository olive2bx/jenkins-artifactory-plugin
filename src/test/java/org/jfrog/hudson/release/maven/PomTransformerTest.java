/*
 * Copyright (C) 2011 JFrog Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jfrog.hudson.release.maven;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import hudson.maven.ModuleName;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Tests the pom version change transformations.
 *
 * @author Yossi Shaul
 */
@Test
public class PomTransformerTest {

    public void transformSimplePom() throws Exception {
        File pomFile = getResourceAsFile("/poms/parentonly/pom.xml");
        HashMap<ModuleName, String> modules = Maps.newHashMap();
        modules.put(new ModuleName("org.jfrog.test", "parent"), "2.2");

        new PomTransformer(new ModuleName("org.jfrog.test", "one"), modules, "").invoke(pomFile, null);

        String pomStr = Files.toString(pomFile, Charset.defaultCharset());
        Document expected = PomTransformer.createSaxBuilder().build(
                getResourceAsFile("/poms/parentonly/pom.expected.xml"));
        String expectedStr = new XMLOutputter().outputString(expected);

        assertEquals(pomStr, expectedStr);
    }

    public void transformMultiPom() throws Exception {
        File pomFile = getResourceAsFile("/poms/multi/pom.xml");
        Map<ModuleName, String> modules = Maps.newHashMap();
        modules.put(new ModuleName("org.jfrog.test.nested", "nested1"), "3.6");
        modules.put(new ModuleName("org.jfrog.test.nested", "nested2"), "3.6");
        modules.put(new ModuleName("org.jfrog.test.nested", "two"), "3.6");

        new PomTransformer(new ModuleName("org.jfrog.test.nested", "two"), modules, "").invoke(pomFile, null);

        String pomStr = Files.toString(pomFile, Charset.defaultCharset());
        Document expected = PomTransformer.createSaxBuilder().build(getResourceAsFile("/poms/multi/pom.expected.xml"));
        String expectedStr = new XMLOutputter().outputString(expected);

        assertEquals(pomStr, expectedStr);
    }

    public void transformScm() throws Exception {
        File pomFile = getResourceAsFile("/poms/scm/pom.xml");
        HashMap<ModuleName, String> modules = Maps.newHashMap();
        modules.put(new ModuleName("org.jfrog.test", "parent"), "1");

        new PomTransformer(new ModuleName("org.jfrog.test", "one"), modules,
                "http://subversion.jfrog.org/test/tags/1").invoke(pomFile, null);

        String pomStr = Files.toString(pomFile, Charset.defaultCharset());
        Document expected = PomTransformer.createSaxBuilder().build(
                getResourceAsFile("/poms/scm/pom.expected.xml"));
        String expectedStr = new XMLOutputter().outputString(expected);

        assertEquals(pomStr, expectedStr);
    }

    private File getResourceAsFile(String path) {
        URL resource = getClass().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found: " + path);
        }
        return new File(resource.getFile());
    }
}