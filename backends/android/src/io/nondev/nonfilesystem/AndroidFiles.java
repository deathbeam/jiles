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

import android.content.res.AssetManager;
import android.os.Environment;

/** @author mzechner
 * @author Nathan Sweet */
public class AndroidFiles implements Files {
	protected final String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	protected final String localpath;
	protected final AssetManager assets;

	public AndroidFiles (AssetManager assets) {
		this.assets = assets;
		localpath = sdcard;
	}

	public AndroidFiles (AssetManager assets, String localpath) {
		this.assets = assets;
		this.localpath = localpath.endsWith("/") ? localpath : localpath + "/";
	}

	public AssetManager getAssetManager() {
		return assets;
	}

	@Override
	public FileHandle getFileHandle (String path, FileType type) {
		return new AndroidFileHandle(this, path, type);
	}

	@Override
	public FileHandle classpath (String path) {
		return new AndroidFileHandle(this, path, FileType.Classpath);
	}

	@Override
	public FileHandle internal (String path) {
		return new AndroidFileHandle(this, path, FileType.Internal);
	}

	@Override
	public FileHandle external (String path) {
		return new AndroidFileHandle(this, path, FileType.External);
	}

	@Override
	public FileHandle absolute (String path) {
		return new AndroidFileHandle(this, path, FileType.Absolute);
	}

	@Override
	public FileHandle local (String path) {
		return new AndroidFileHandle(this, path, FileType.Local);
	}

	@Override
	public String getExternalStoragePath () {
		return sdcard;
	}

	@Override
	public boolean isExternalStorageAvailable () {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	@Override
	public String getLocalStoragePath () {
		return localpath;
	}

	@Override
	public boolean isLocalStorageAvailable () {
		return true;
	}
}
