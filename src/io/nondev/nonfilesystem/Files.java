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

/** Provides standard access to the filesystem, classpath, Android SD card, and Android assets directory.
 * @author mzechner
 * @author Nathan Sweet */
public interface Files {
	/** Indicates how to resolve a path to a file.
	 * @author mzechner
	 * @author Nathan Sweet */
	public enum FileType {
		/** Path relative to the root of the classpath. Classpath files are always readonly. Note that classpath files are not
		 * compatible with some functionality on Android, such as {@link Audio#newSound(FileHandle)} and
		 * {@link Audio#newMusic(FileHandle)}. */
		Classpath,

		/** Path relative to the asset directory on Android and to the application's root directory on the desktop. On the desktop,
		 * if the file is not found, then the classpath is checked. This enables files to be found when using JWS or applets.
		 * Internal files are always readonly. */
		Internal,

		/** Path relative to the root of the SD card on Android and to the home directory of the current user on the desktop. */
		External,

		/** Path that is a fully qualified, absolute filesystem path. To ensure portability across platforms use absolute files only
		 * when absolutely (heh) necessary. */
		Absolute,

		/** Path relative to the private files directory on Android and to the application's root directory on the desktop. */
		Local;
	}

	/** Returns a handle representing a file or directory.
	 * @param type Determines how the path is resolved.
	 * @throws RuntimeException if the type is classpath or internal and the file does not exist.
	 * @see FileType */
	public FileHandle getFileHandle (String path, FileType type);

	/** Convenience method that returns a {@link FileType#Classpath} file handle. */
	public FileHandle classpath (String path);

	/** Convenience method that returns a {@link FileType#Internal} file handle. */
	public FileHandle internal (String path);

	/** Convenience method that returns a {@link FileType#External} file handle. */
	public FileHandle external (String path);

	/** Convenience method that returns a {@link FileType#Absolute} file handle. */
	public FileHandle absolute (String path);

	/** Convenience method that returns a {@link FileType#Local} file handle. */
	public FileHandle local (String path);

	/** Returns the external storage path directory. This is the SD card on Android and the home directory of the current user on
	 * the desktop. */
	public String getExternalStoragePath ();

	/** Returns true if the external storage is ready for file IO. Eg, on Android, the SD card is not available when mounted for use
	 * with a PC. */
	public boolean isExternalStorageAvailable ();

	/** Returns the local storage path directory. This is the private files directory on Android and the directory of the jar on the
	 * desktop. */
	public String getLocalStoragePath ();

	/** Returns true if the local storage is ready for file IO. */
	public boolean isLocalStorageAvailable ();
}
