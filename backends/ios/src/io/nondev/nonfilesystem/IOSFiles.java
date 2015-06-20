/*******************************************************************************
 * Original source:
 * Copyright 2011 See https://github.com/libgdx/libgdx/blob/master/AUTHORS
 * under the Apache License included as LICENSE-LibGDX
 *
 * Modifications:
 * Copyright 2015 Thomas Slusny
 * Rewrote entire LibGDX filesystem to be non-LibGDX dependent. These
 * modifications are licensed under below license:
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package io.nondev.nonfilesystem;

import org.robovm.apple.foundation.NSBundle;

public class IOSFiles implements Files {
	// TODO: Use NSSearchPathForDirectoriesInDomains instead?
	// $HOME should point to the app root dir.
	static final String appDir = System.getenv("HOME");
	static final String externalPath = appDir + "/Documents/";
	static final String localPath = appDir + "/Library/local/";
	static final String internalPath = NSBundle.getMainBundle().getBundlePath();

	public IOSFiles () {
		new FileHandle(this, externalPath).mkdirs();
		new FileHandle(this, localPath).mkdirs();
	}

	@Override
	public FileHandle getFileHandle (String fileName, FileType type) {
		return new IOSFileHandle(this, fileName, type);
	}

	@Override
	public FileHandle classpath (String path) {
		return new IOSFileHandle(this, path, FileType.Classpath);
	}

	@Override
	public FileHandle internal (String path) {
		return new IOSFileHandle(this, path, FileType.Internal);
	}

	@Override
	public FileHandle external (String path) {
		return new IOSFileHandle(this, path, FileType.External);
	}

	@Override
	public FileHandle absolute (String path) {
		return new IOSFileHandle(this, path, FileType.Absolute);
	}

	@Override
	public FileHandle local (String path) {
		return new IOSFileHandle(this, path, FileType.Local);
	}

	@Override
	public String getExternalStoragePath () {
		return externalPath;
	}

	@Override
	public boolean isExternalStorageAvailable () {
		return true;
	}

	@Override
	public String getLocalStoragePath () {
		return localPath;
	}

	@Override
	public boolean isLocalStorageAvailable () {
		return true;
	}
}
