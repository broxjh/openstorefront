/*
 * Copyright 2014 Space Dynamics Laboratory - Utah State University Research Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.usu.sdl.openstorefront.service.manager;

import edu.usu.sdl.openstorefront.exception.OpenStorefrontRuntimeException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provide single access to system properties
 *
 * @author dshurtleff
 */
public class PropertiesManager
{

	private static final Logger log = Logger.getLogger(PropertiesManager.class.getName());

	public static final String PW_PROPERTY = ".pw";

	public static final String KEY_USE_REST_PROXY = "service.rest.proxy";
	public static final String KEY_DB_CONNECT_MIN = "db.connectionpool.min";
	public static final String KEY_DB_CONNECT_MAX = "db.connectionpool.max";
	public static final String KEY_DB_USER = "db.user";
	public static final String KEY_DB_AT = "db.pw";
	public static final String KEY_MAX_ERROR_TICKETS = "errorticket.max";
	public static final String KEY_SOLR_URL = "solr.server.url";

	public static final String KEY_OPENAM_URL = "openam.url";
	public static final String KEY_LOGOUT_URL = "logout.url";
	public static final String KEY_OPENAM_HEADER_USERNAME = "openam.header.username";
	public static final String KEY_OPENAM_HEADER_FIRSTNAME = "openam.header.firstname";
	public static final String KEY_OPENAM_HEADER_LASTNAME = "openam.header.lastname";
	public static final String KEY_OPENAM_HEADER_EMAIL = "openam.header.email";
	public static final String KEY_OPENAM_HEADER_GROUP = "openam.header.group";
	public static final String KEY_OPENAM_HEADER_LDAPGUID = "openam.header.ldapguid";
	public static final String KEY_OPENAM_HEADER_ORGANIZATION = "openam.header.organization";
	public static final String KEY_OPENAM_HEADER_ADMIN_GROUP = "openam.header.admingroupname";

	public static final String KEY_TOOLS_USER = "tools.login.user";
	public static final String KEY_TOOLS_CREDENTIALS = "tools.login.pw";

	public static final String KEY_JIRA_POOL_SIZE = "jira.connectionpool.size";
	public static final String KEY_JIRA_CONNECTION_WAIT_TIME = "jira.connection.wait.seconds";
	public static final String KEY_JIRA_URL = "jira.server.url";
	public static final String KEY_JOB_WORKING_STATE_OVERRIDE = "job.working.state.override.minutes";

	public static final String KEY_MAIL_SERVER = "mail.smtp.url";
	public static final String KEY_MAIL_SERVER_USER = "mail.server.user";
	public static final String KEY_MAIL_SERVER_PW = "mail.server.pw";
	public static final String KEY_MAIL_SERVER_PORT = "mail.smtp.port";
	public static final String KEY_MAIL_USE_SSL = "mail.use.ssl";
	public static final String KEY_MAIL_USE_TLS = "mail.use.tls";
	public static final String KEY_MAIL_FROM_NAME = "mail.from.name";
	public static final String KEY_MAIL_FROM_ADDRESS = "mail.from.address";
	public static final String KEY_MAIL_REPLY_NAME = "mail.reply.name";
	public static final String KEY_MAIL_REPLY_ADDRESS = "mail.reply.address";
	public static final String KEY_MESSAGE_KEEP_DAYS = "message.archive.days";
	public static final String KEY_MESSAGE_MIN_QUEUE_MINUTES = "message.queue.minmintues";
	public static final String KEY_MESSAGE_MAX_RETRIES = "message.maxretires";
	public static final String KEY_MESSAGE_RECENT_CHANGE_DAYS = "message.recentchanges.days";

	public static final String KEY_APPLICATION_TITLE = "app.title";
	public static final String KEY_MAX_TASK_POOL_SIZE = "task.pool.size";

	private static Properties properties;
	private static final String PROPERTIES_FILENAME = FileSystemManager.getConfig("openstorefront.properties").getPath();

	public static String getApplicationVersion()
	{
		String key = "app.version";
		return getValue(key);
	}

	public static String getValue(String key)
	{
		return getProperties().getProperty(key);
	}

	/**
	 * Note: this will trim the value....the extra spaces can cause issues
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getValue(String key, String defaultValue)
	{
		String value = getProperties().getProperty(key, defaultValue);
		if (value != null) {
			value = value.trim();
		}
		return value;
	}

	public static void setProperty(String key, String value)
	{
		getProperties().setProperty(value, value);
		saveProperties();
	}

	public static Map<String, String> getAllProperties()
	{
		Map<String, String> propertyMap = new HashMap<>();
		for (String key : getProperties().stringPropertyNames()) {
			propertyMap.put(key, getProperties().getProperty(key));
		}
		return propertyMap;
	}

	private static Properties getProperties()
	{
		if (properties == null) {
			loadProperties();
		}
		return properties;
	}

	private static void loadProperties()
	{
		ReentrantLock lock = new ReentrantLock();
		lock.lock();
		try {
			if (Paths.get(PROPERTIES_FILENAME).toFile().createNewFile()) {
				log.log(Level.WARNING, "Open Storefront properties file was missing from location a new file was created.  Location: {0}", PROPERTIES_FILENAME);
			}
			try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(PROPERTIES_FILENAME))) {
				properties = new Properties();
				properties.load(bin);
			} catch (IOException e) {
				throw new OpenStorefrontRuntimeException(e);
			}

			try (InputStream in = FileSystemManager.getApplicatioResourceFile("/filter/version.properties")) {
				Properties versionProperties = new Properties();
				versionProperties.load(in);
				properties.putAll(versionProperties);
			} catch (IOException e) {
				throw new OpenStorefrontRuntimeException(e);
			}
		} catch (IOException e) {
			throw new OpenStorefrontRuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	private static void saveProperties()
	{
		ReentrantLock lock = new ReentrantLock();
		lock.lock();
		try (BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(PROPERTIES_FILENAME))) {
			properties.store(bout, "Open Storefront Properties");
		} catch (IOException e) {
			throw new OpenStorefrontRuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

}
