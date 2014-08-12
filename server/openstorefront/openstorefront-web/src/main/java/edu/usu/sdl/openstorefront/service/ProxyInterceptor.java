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

package edu.usu.sdl.openstorefront.service;

import java.lang.reflect.Method;

/**
 *
 * @author dshurtleff
 */
public interface ProxyInterceptor
{
	/**
	 * Runs before the Method call
	 * @param proxy
	 * @param m
	 * @param args 
	 * @param context 
	 * @return  true to skip the actual call to the method
	 */
	boolean before(Object proxy, Method m, Object[] args,  ProxyContext context);
	void after(Object proxy, Method m, Object[] args,  ProxyContext context);
	void handleException(Object proxy, Method m, Object[] args,  ProxyContext context);
	void requiredAfterRun(Object proxy, Method m, Object[] args,  ProxyContext context);
}
