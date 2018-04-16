/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.iam.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class PropertiesHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static Properties properties = new Properties();

    private String propertiesFile;

    public PropertiesHelper(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    /**
     * Get the properties map or load it if needed.
     *
     * @return the properties map corresponding to the default property file.
     */
    private Properties getProperties() {

        if (propertiesFile != null) {

            try {

                ApplicationContext appContext = new ClassPathXmlApplicationContext();

                InputStream inputStream = appContext.getResource(propertiesFile).getInputStream();
                if (inputStream != null) {
                    properties.load(inputStream);
                    inputStream.close();

                    //properties.list(System.out);
                } else {
                    logger.error("Unable to load properties file");
                    throw new IOException("Unable to load properties file");
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else
            throw new RuntimeException("Properties file not found");

        return properties;
    }

    /**
     * Set the value of this property to the given one.
     *
     * @param value the new value to set.
     */
    public synchronized void updateProperty(String key, String value) {
        getProperties().setProperty(key, value);
    }

    /**
     * Return true if this property is set, false otherwise.
     *
     * @return true if this property is set, false otherwise.
     */
    public synchronized boolean isSet(String key, String defaultValue) {
        return defaultValue != null || getProperties().containsKey(key);
    }

    /**
     * unsets this property
     *
     */
    public synchronized void unSet(String key) {
        getProperties().remove(key);
    }

    /**
     * Returns the value of this property as an integer.
     * If value is not an integer, an exception will be thrown.
     *
     * @return the value of this property.
     */
    public synchronized int getValueAsInt(String key, int defaultValue) {

        String valueString = getValueAsString(key, String.valueOf(defaultValue));

        if (valueString != null) {
            try {
                return Integer.parseInt(valueString);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(key +
                                                   " is not an integer property. getValueAsInt cannot be called on this property");
            }
        } else {
            throw new IllegalArgumentException("Property " + key +
                                               " is undefined and does not declare a default value.");
        }
    }

    /**
     * Returns the value of this property as an integer.
     * If value is not an integer, an exception will be thrown.
     *
     * @return the value of this property.
     */
    public synchronized long getValueAsLong(String key, PropertyType type, String defaultValue) {
        if (type != PropertyType.INTEGER) {
            throw new IllegalArgumentException("Property " + key + " is not a " + PropertyType.INTEGER);
        }
        String valueString = getValueAsString(key, defaultValue);

        if (valueString != null) {
            try {
                return Long.parseLong(valueString);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(key +
                                                   " is not an integer property. getValueAsInt cannot be called on this property");
            }
        } else {
            throw new IllegalArgumentException("Property " + key +
                                               " is undefined and does not declare a default value.");
        }
    }

    /**
     * Returns the value of this property as a string.
     *
     * @return the value of this property.
     */
    public synchronized String getValueAsString(String key, String defaultValue) {
        Properties prop = getProperties();
        if (prop.containsKey(key)) {
            return prop.getProperty(key);
        } else {
            return defaultValue;
        }
    }

    /**
     * Returns the value of this property as a List of strings.
     *
     * @param separator the separator to use
     *
     * @return the list of values of this property.
     */
    public synchronized List<String> getValueAsList(String key, PropertyType type, String separator,
            String defaultValue) {
        if (type != PropertyType.LIST) {
            throw new IllegalArgumentException("Property " + key + " is not a " + PropertyType.LIST);
        }
        String valueString = getValueAsString(key, defaultValue);
        ArrayList<String> valueList = new ArrayList<>();
        if (valueString != null) {
            for (String val : valueString.split(Pattern.quote(separator))) {
                val = val.trim();
                if (val.length() > 0) {
                    valueList.add(val);
                }
            }
        } else {
            throw new IllegalArgumentException("Property " + key +
                                               " is undefined and does not declare a default value.");
        }
        return valueList;
    }

    /**
     * Returns the value of this property as a string.
     * If the property is not defined, then null is returned
     *
     * @return the value of this property.
     */
    public synchronized String getValueAsStringOrNull(String key) {
        Properties prop = getProperties();
        if (prop.containsKey(key)) {
            String ret = prop.getProperty(key);
            if ("".equals(ret)) {
                return null;
            }
            return ret;
        } else {
            return null;
        }
    }

    /**
     * Returns the value of this property as a boolean.
     * If value is not a boolean, an exception will be thrown.<br>
     * The behavior of this method is the same as the {@link java.lang.Boolean#parseBoolean(String s)}.
     *
     * @return the value of this property.
     */
    public synchronized boolean getValueAsBoolean(String key, PropertyType type, String defaultValue) {
        if (type != PropertyType.BOOLEAN) {
            throw new IllegalArgumentException("Property " + key + " is not a " + PropertyType.BOOLEAN);
        }
        String valueString = getValueAsString(key, defaultValue);
        /*
         * if (valueString != null) {
         * return Boolean.parseBoolean(valueString);
         * } else {
         * return false;
         * }
         */

        return valueString != null && Boolean.parseBoolean(valueString);
    }

}
