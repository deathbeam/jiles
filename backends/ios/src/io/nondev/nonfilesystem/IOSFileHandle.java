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
import org.robovm.apple.foundation.NSBundle;

public class IOSFileHandle extends FileHandle {
	static final String internalPath = NSBundle.getMainBundle().getBundlePath();

	public IOSFileHandle (FileSystem fs, String fileName) {
        super(fs, fileName);
    }

    public IOSFileHandle(FileSystem fs, File file) {
        super(fs, file);
    }

    public IOSFileHandle(FileSystem fs, String fileName, FileHandleType type) {
        super(fs, fileName, type);
    }

    public IOSFileHandle(FileSystem fs, File file, FileHandleType type) {
        super(fs, file, type);
    }

	@Override
	public FileHandle getChild (String name) {
		if (file.getPath().length() == 0) return new IOSFileHandle(fs, new File(name), type);
		return new IOSFileHandle(fs, new File(file, name), type);
	}

	@Override
	public FileHandle getParent () {
		File parent = file.getParentFile();
		if (parent == null) {
			if (type == FileHandleType.Absolute)
				parent = new File("/");
			else
				parent = new File("");
		}
		return new IOSFileHandle(fs, parent, type);
	}

	@Override
	public FileHandle getSibling (String name) {
		if (file.getPath().length() == 0) throw new RuntimeException("Cannot get the sibling of the root.");
		return new IOSFileHandle(fs, new File(file.getParent(), name), type);
	}

	@Override
	public File getFile () {
		if (type == FileHandleType.Internal) return new File(internalPath, file.getPath());
		if (type == FileHandleType.External) return new File(fs.getExternalPath(), file.getPath());
		if (type == FileHandleType.Local) return new File(fs.getLocalPath(), file.getPath());
		return file;
	}

}
