/*
 * Copyright 2017 Apereo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tle.web.sections.standard.js;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import com.tle.common.i18n.CurrentLocale;
import com.tle.core.guice.Bind;
import com.tle.core.javascript.JavascriptLibrary;
import com.tle.core.javascript.JavascriptModule;
import com.tle.web.sections.standard.js.modules.JSONModule;
import com.tle.web.sections.standard.js.modules.SelectModule;
import com.tle.web.sections.standard.js.modules.StandardModule;

/**
 * @author aholland
 */
@Bind
@Singleton
public class StandardJavascriptLibrary implements JavascriptLibrary
{
	private static final long serialVersionUID = 1L;

	private final Map<String, JavascriptModule> moduleMap;

	public StandardJavascriptLibrary()
	{
		List<JavascriptModule> modules = new ArrayList<JavascriptModule>();
		modules.add(new StandardModule());
		modules.add(new JSONModule());
		modules.add(new SelectModule());

		Map<String, JavascriptModule> tempModuleMap = new HashMap<String, JavascriptModule>();
		for( JavascriptModule module : modules )
		{
			tempModuleMap.put(module.getId(), module);
		}
		moduleMap = Collections.unmodifiableMap(tempModuleMap);
	}

	@Override
	public String getDisplayName()
	{
		return CurrentLocale.get("com.tle.web.sections.standard.js.standardlibrary.name"); //$NON-NLS-1$
	}

	@Override
	public String getId()
	{
		return "standard"; //$NON-NLS-1$
	}

	@Override
	public Map<String, JavascriptModule> getModules()
	{
		return moduleMap;
	}
}
