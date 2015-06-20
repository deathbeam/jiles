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

public class IOSFileHandle extends FileHandle {
	public IOSFileHandle (Files files, String fileName) {
		super(files, fileName);
	}

	public IOSFileHandle (Files files, File file) {
		super(files, file);
	}

	public IOSFileHandle (Files files, String fileName, FileType type) {
		super(files, fileName, type);
	}

	public IOSFileHandle (Files files, File file, FileType type) {
		super(files, file, type);
	}

	@Override
	public FileHandle child (String name) {
		if (file.getPath().length() == 0) return new IOSFileHandle(files, new File(name), type);
		return new IOSFileHandle(files, new File(file, name), type);
	}

	@Override
	public FileHandle parent () {
		File parent = file.getParentFile();
		if (parent == null) {
			if (type == FileType.Absolute)
				parent = new File("/");
			else
				parent = new File("");
		}
		return new IOSFileHandle(files, parent, type);
	}

	@Override
	public FileHandle sibling (String name) {
		if (file.getPath().length() == 0) throw new RuntimeException("Cannot get the sibling of the root.");
		return new IOSFileHandle(files, new File(file.getParent(), name), type);
	}

	@Override
	public File file () {
		if (type == FileType.Internal) return new File(IOSFiles.internalPath, file.getPath());
		if (type == FileType.External) return new File(IOSFiles.externalPath, file.getPath());
		if (type == FileType.Local) return new File(IOSFiles.localPath, file.getPath());
		return file;
	}

}
