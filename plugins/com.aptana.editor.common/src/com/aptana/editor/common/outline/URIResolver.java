package com.aptana.editor.common.outline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.util.IOUtil;

/**
 * Resolves paths that may be relative to a base URI (filesystem, remote, etc)
 * 
 * @author cwilliams
 */
class URIResolver implements IPathResolver
{

	private URI baseURI;

	public URIResolver(URI baseURI)
	{
		this.baseURI = baseURI;
	}

	@Override
	public String resolveSource(String path, IProgressMonitor monitor) throws Exception
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		URI uri;
		try
		{
			// try to parse as a URI
			uri = URI.create(path);
			String scheme = uri.getScheme();
			if (scheme == null)
			{
				// no scheme, means it's relative to base URI, or an absolute file path?
				uri = baseURI.resolve(path);
			}
		}
		catch (IllegalArgumentException e)
		{
			// fails to parse, try resolving against base URI
			uri = baseURI.resolve(path);
			// TODO What if it fails here, then what do we do?
		}
		sub.worked(5);
		// get the filesystem that can handle the URI
		IFileSystem fileSystem = EFS.getFileSystem(uri.getScheme());
		IFileStore store = fileSystem.getStore(uri);
		// grab down a local copy TODO What if file is already a local file?
		File aFile = store.toLocalFile(EFS.CACHE, sub.newChild(90));
		if (aFile == null || !aFile.exists())
		{
			// Need to pass up correct original filename and says that's the one that doesn't exist
			throw new FileNotFoundException(uri.toString());
		}
		// now read in the local copy
		return IOUtil.read(new FileInputStream(aFile));
	}
}
