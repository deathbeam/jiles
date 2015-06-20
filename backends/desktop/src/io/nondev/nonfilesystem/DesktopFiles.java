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

import java.io.File;

import io.nondev.nonfilesystem.Files.FileType;

/** @author mzechner
 * @author Nathan Sweet */
public final class DesktopFiles implements Files {
	static public final String externalPath = System.getProperty("user.home") + File.separator;
	static public final String localPath = new File("").getAbsolutePath() + File.separator;
	
	@Override
	public FileHandle getFileHandle (String fileName, FileType type) {
		return new DesktopFileHandle(this, fileName, type);
	}

	@Override
	public FileHandle classpath (String path) {
		return new DesktopFileHandle(this, path, FileType.Classpath);
	}

	@Override
	public FileHandle internal (String path) {
		return new DesktopFileHandle(this, path, FileType.Internal);
	}

	@Override
	public FileHandle external (String path) {
		return new DesktopFileHandle(this, path, FileType.External);
	}

	@Override
	public FileHandle absolute (String path) {
		return new DesktopFileHandle(this, path, FileType.Absolute);
	}

	@Override
	public FileHandle local (String path) {
		return new DesktopFileHandle(this, path, FileType.Local);
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
