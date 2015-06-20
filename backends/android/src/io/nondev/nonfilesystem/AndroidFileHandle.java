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
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import io.nondev.nonfilesystem.Files.FileType;

/** @author mzechner
 * @author Nathan Sweet */
public class AndroidFileHandle extends FileHandle {
	protected AssetManager assets() {
		return ((AndroidFiles)files).getAssetManager();
	}

	public AndroidFileHandle (Files files, String fileName) {
		super(files, fileName);
	}

	public AndroidFileHandle (Files files, File file) {
		super(files, file);
	}

	public AndroidFileHandle (Files files, String fileName, FileType type) {
		super(files, fileName, type);
	}

	public AndroidFileHandle (Files files, File file, FileType type) {
		super(files, file, type);
	}

	public FileHandle child (String name) {
		name = name.replace('\\', '/');
		if (file.getPath().length() == 0) return new AndroidFileHandle(files, new File(name), type);
		return new AndroidFileHandle(files, new File(file, name), type);
	}

	public FileHandle sibling (String name) {
		name = name.replace('\\', '/');
		if (file.getPath().length() == 0) throw new RuntimeException("Cannot get the sibling of the root.");
		return new AndroidFileHandle(files, new File(file.getParent(), name), type);
	}

	public FileHandle parent () {
		File parent = file.getParentFile();
		if (parent == null) {
			if (type == FileType.Absolute)
				parent = new File("/");
			else
				parent = new File("");
		}
		return new AndroidFileHandle(files, parent, type);
	}

	public InputStream read () {
		if (type == FileType.Internal) {
			try {
				return assets().open(file.getPath());
			} catch (IOException ex) {
				throw new RuntimeException("Error reading file: " + file + " (" + type + ")", ex);
			}
		}
		return super.read();
	}

	public FileHandle[] list () {
		if (type == FileType.Internal) {
			try {
				String[] relativePaths = assets().list(file.getPath());
				FileHandle[] handles = new FileHandle[relativePaths.length];
				for (int i = 0, n = handles.length; i < n; i++)
					handles[i] = new AndroidFileHandle(files, new File(file, relativePaths[i]), type);
				return handles;
			} catch (Exception ex) {
				throw new RuntimeException("Error listing children: " + file + " (" + type + ")", ex);
			}
		}
		return super.list();
	}

	public FileHandle[] list (FileFilter filter) {
		if (type == FileType.Internal) {
			try {
				String[] relativePaths = assets().list(file.getPath());
				FileHandle[] handles = new FileHandle[relativePaths.length];
				int count = 0;
				for (int i = 0, n = handles.length; i < n; i++) {
					String path = relativePaths[i];
					FileHandle child = new AndroidFileHandle(files, new File(file, path), type);
					if (!filter.accept(child.file())) continue;
					handles[count] = child;
					count++;
				}
				if (count < relativePaths.length) {
					FileHandle[] newHandles = new FileHandle[count];
					System.arraycopy(handles, 0, newHandles, 0, count);
					handles = newHandles;
				}
				return handles;
			} catch (Exception ex) {
				throw new RuntimeException("Error listing children: " + file + " (" + type + ")", ex);
			}
		}
		return super.list(filter);
	}

	public FileHandle[] list (FilenameFilter filter) {
		if (type == FileType.Internal) {
			try {
				String[] relativePaths = assets().list(file.getPath());
				FileHandle[] handles = new FileHandle[relativePaths.length];
				int count = 0;
				for (int i = 0, n = handles.length; i < n; i++) {
					String path = relativePaths[i];
					if (!filter.accept(file, path)) continue;
					handles[count] = new AndroidFileHandle(files, new File(file, path), type);
					count++;
				}
				if (count < relativePaths.length) {
					FileHandle[] newHandles = new FileHandle[count];
					System.arraycopy(handles, 0, newHandles, 0, count);
					handles = newHandles;
				}
				return handles;
			} catch (Exception ex) {
				throw new RuntimeException("Error listing children: " + file + " (" + type + ")", ex);
			}
		}
		return super.list(filter);
	}

	public FileHandle[] list (String suffix) {
		if (type == FileType.Internal) {
			try {
				String[] relativePaths = assets().list(file.getPath());
				FileHandle[] handles = new FileHandle[relativePaths.length];
				int count = 0;
				for (int i = 0, n = handles.length; i < n; i++) {
					String path = relativePaths[i];
					if (!path.endsWith(suffix)) continue;
					handles[count] = new AndroidFileHandle(files, new File(file, path), type);
					count++;
				}
				if (count < relativePaths.length) {
					FileHandle[] newHandles = new FileHandle[count];
					System.arraycopy(handles, 0, newHandles, 0, count);
					handles = newHandles;
				}
				return handles;
			} catch (Exception ex) {
				throw new RuntimeException("Error listing children: " + file + " (" + type + ")", ex);
			}
		}
		return super.list(suffix);
	}

	public boolean isDirectory () {
		if (type == FileType.Internal) {
			try {
				return assets().list(file.getPath()).length > 0;
			} catch (IOException ex) {
				return false;
			}
		}
		return super.isDirectory();
	}

	public boolean exists () {
		if (type == FileType.Internal) {
			String fileName = file.getPath();
			try {
				assets().open(fileName).close(); // Check if file exists.
				return true;
			} catch (Exception ex) {
				// This is SUPER slow! but we need it for directories.
				try {
					return assets().list(fileName).length > 0;
				} catch (Exception ignored) {
				}
				return false;
			}
		}
		return super.exists();
	}

	public long length () {
		if (type == FileType.Internal) {
			AssetFileDescriptor fileDescriptor = null;
			try {
				fileDescriptor = assets().openFd(file.getPath());
				return fileDescriptor.getLength();
			} catch (IOException ignored) {
			} finally {
				if (fileDescriptor != null) {
					try {
						fileDescriptor.close();
					} catch (IOException e) {
					}
					;
				}
			}
		}
		return super.length();
	}

	public long lastModified () {
		return super.lastModified();
	}

	public File file () {
		if (type == FileType.Local) return new File(files.getLocalStoragePath(), file.getPath());
		return super.file();
	}

}
