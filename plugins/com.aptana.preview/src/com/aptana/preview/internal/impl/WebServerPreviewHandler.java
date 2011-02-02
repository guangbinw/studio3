/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.preview.internal.impl;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;

import com.aptana.preview.IPreviewHandler;
import com.aptana.preview.PreviewConfig;
import com.aptana.preview.ProjectPreviewUtil;
import com.aptana.preview.SourceConfig;
import com.aptana.webserver.core.AbstractWebServerConfiguration;
import com.aptana.webserver.core.WebServerCorePlugin;

/**
 * @author Max Stepanov
 * 
 */
public class WebServerPreviewHandler implements IPreviewHandler {

	/*
	 * (non-Javadoc)
	 * @see com.aptana.preview.IPreviewHandler#handle(com.aptana.preview.SourceConfig)
	 */
	public PreviewConfig handle(SourceConfig config) throws CoreException {
		AbstractWebServerConfiguration serverConfiguration = ProjectPreviewUtil.getServerConfiguration(config.getProject());
		if (serverConfiguration != null) {
			URL url = serverConfiguration.resolve(config.getFileStore());
			if (url != null) {
				return new PreviewConfig(url);
			}
		} else {
			for (AbstractWebServerConfiguration configuration : WebServerCorePlugin.getDefault().getServerConfigurationManager().getServerConfigurations()) {
				URL url = configuration.resolve(config.getFileStore());
				if (url != null) {
					return new PreviewConfig(url);
				}
			}
		}
		return null;
	}
}