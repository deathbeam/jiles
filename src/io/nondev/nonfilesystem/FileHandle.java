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

import java.io.*;

public class FileHandle {
    protected FileSystem fs;
    protected File file;
    protected FileHandleType type;

    public FileHandle (FileSystem fs, String fileName) {
        this(fs, new File(fileName));
    }

    public FileHandle(FileSystem fs, File file) {
        this(fs, file, FileHandleType.Absolute);
    }

    public FileHandle(FileSystem fs, String fileName, FileHandleType type) {
        this(fs, new File(fileName), type);
    }

    public FileHandle(FileSystem fs, File file, FileHandleType type) {
        this.fs = fs;
        this.file = file;
        this.type = type;
    }

    public String getPath() {
        return file.getPath().replace('\\', '/');
    }

    public String getName() {
        return file.getName();
    }

    public String getExtension() {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return "";
        return name.substring(dotIndex + 1);
    }

    public FileHandleType getType() {
        return type;
    }

    public File getFile() {
        if (type == FileHandleType.External) {
            return new File(fs.getExternalPath(), file.getPath());
        }

        return file;
    }

    public InputStream readStream() {
        if (type == FileHandleType.Classpath || 
           (type == FileHandleType.Internal && !getFile().exists()) ||
           (type == FileHandleType.Local && !getFile().exists())) {

            InputStream input = FileHandle.class.getResourceAsStream("/" + file.getPath().replace('\\', '/'));
        
            if (input == null) {
                throw new RuntimeException("File not found: " + this);
            }

            return input;
        }

        try {
            return new FileInputStream(getFile());
        } catch (Exception ex) {
            if (getFile().isDirectory()) {
                throw new RuntimeException("Cannot open a stream to a directory: " + this, ex);
            }

            throw new RuntimeException("Error reading file: " + this , ex);
        }
    }

    public String readString (String charset) {
        StringBuilder output = new StringBuilder(estimateLength());
        InputStreamReader reader = null;
        try {
            if (charset == null)
                reader = new InputStreamReader(readStream());
            else
                reader = new InputStreamReader(readStream(), charset);
            char[] buffer = new char[256];
            while (true) {
                int length = reader.read(buffer);
                if (length == -1) break;
                output.append(buffer, 0, length);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error reading layout file: " + this, ex);
        } finally {
            closeQuietly(reader);
        }
        return output.toString();
    }

    public byte[] readBytes () {
        InputStream input = readStream();
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream(Math.max(0, estimateLength()));
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            return output.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Error reading file: " + this, ex);
        } finally {
            closeQuietly(input);
        }
    }

    public int readBytes (byte[] bytes, int offset, int size) {
        InputStream input = readStream();
        int position = 0;
        try {
            while (true) {
                int count = input.read(bytes, offset + position, size - position);
                if (count <= 0) break;
                position += count;
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error reading file: " + this, ex);
        } finally {
            closeQuietly(input);
        }
        return position - offset;
    }

    public void writeStream(InputStream input, boolean append) {
        OutputStream output = null;
        try {
            output = createWriterStream(append);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error stream writing to file: " + file + " (" + type + ")", ex);
        } finally {
            closeQuietly(input);
            closeQuietly(output);
        }

    }

    public void writeString (String string, boolean append) {
        writeString(string, append, null);
    }

    public void writeString (String string, boolean append, String charset) {
        Writer writer = null;
        try {
            if (type == FileHandleType.Classpath) throw new RuntimeException("Cannot write to a classpath file: " + file);
            if (type == FileHandleType.Internal) throw new RuntimeException("Cannot write to an internal file: " + file);
            getParent().mkdirs();
            try {
                FileOutputStream output = new FileOutputStream(getFile(), append);
                if (charset == null)
                    writer = new OutputStreamWriter(output);
                else
                    writer = new OutputStreamWriter(output, charset);
            } catch (IOException ex) {
                if (getFile().isDirectory())
                    throw new RuntimeException("Cannot open a stream to a directory: " + file + " (" + type + ")", ex);
                throw new RuntimeException("Error writing file: " + file + " (" + type + ")", ex);
            }
            writer.write(string);
        } catch (Exception ex) {
            throw new RuntimeException("Error writing file: " + file + " (" + type + ")", ex);
        } finally {
            closeQuietly(writer);
        }
    }

    public void writeBytes (byte[] bytes, boolean append) {
        OutputStream output = createWriterStream(append);
        try {
            output.write(bytes);
        } catch (IOException ex) {
            throw new RuntimeException("Error writing file: " + file + " (" + type + ")", ex);
        } finally {
            closeQuietly(output);
        }
    }

    public void writeBytes (byte[] bytes, int offset, int length, boolean append) {
        OutputStream output = createWriterStream(append);
        try {
            output.write(bytes, offset, length);
        } catch (IOException ex) {
            throw new RuntimeException("Error writing file: " + file + " (" + type + ")", ex);
        } finally {
            closeQuietly(output);
        }
    }

    public FileHandle[] list () {
        if (type == FileHandleType.Classpath) throw new RuntimeException("Cannot list a classpath directory: " + file);
        String[] relativePaths = getFile().list();
        if (relativePaths == null) return new FileHandle[0];
        FileHandle[] handles = new FileHandle[relativePaths.length];
        for (int i = 0, n = relativePaths.length; i < n; i++)
            handles[i] = getChild(relativePaths[i]);
        return handles;
    }

    public FileHandle[] list (FileFilter filter) {
        if (type == FileHandleType.Classpath) throw new RuntimeException("Cannot list a classpath directory: " + file);
        File file = getFile();
        String[] relativePaths = file.list();
        if (relativePaths == null) return new FileHandle[0];
        FileHandle[] handles = new FileHandle[relativePaths.length];
        int count = 0;
        for (int i = 0, n = relativePaths.length; i < n; i++) {
            String path = relativePaths[i];
            FileHandle child = getChild(path);
            if (!filter.accept(child.getFile())) continue;
            handles[count] = child;
            count++;
        }
        if (count < relativePaths.length) {
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            handles = newHandles;
        }
        return handles;
    }

    public FileHandle[] list (FilenameFilter filter) {
        if (type == FileHandleType.Classpath) throw new RuntimeException("Cannot list a classpath directory: " + file);
        File file = getFile();
        String[] relativePaths = file.list();
        if (relativePaths == null) return new FileHandle[0];
        FileHandle[] handles = new FileHandle[relativePaths.length];
        int count = 0;
        for (int i = 0, n = relativePaths.length; i < n; i++) {
            String path = relativePaths[i];
            if (!filter.accept(file, path)) continue;
            handles[count] = getChild(path);
            count++;
        }
        if (count < relativePaths.length) {
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            handles = newHandles;
        }
        return handles;
    }

    public FileHandle[] list (String suffix) {
        if (type == FileHandleType.Classpath) throw new RuntimeException("Cannot list a classpath directory: " + file);
        String[] relativePaths = getFile().list();
        if (relativePaths == null) return new FileHandle[0];
        FileHandle[] handles = new FileHandle[relativePaths.length];
        int count = 0;
        for (int i = 0, n = relativePaths.length; i < n; i++) {
            String path = relativePaths[i];
            if (!path.endsWith(suffix)) continue;
            handles[count] = getChild(path);
            count++;
        }
        if (count < relativePaths.length) {
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            handles = newHandles;
        }
        return handles;
    }

    public boolean isDirectory () {
        if (type == FileHandleType.Classpath) return false;
        return getFile().isDirectory();
    }

    public FileHandle getChild (String name) {
        if (file.getPath().length() == 0) return new FileHandle(fs, new File(name), type);
        return new FileHandle(fs, new File(file, name), type);
    }

    public FileHandle getSibling (String name) {
        if (file.getPath().length() == 0) throw new RuntimeException("Cannot get the sibling of the root.");
        return new FileHandle(fs, new File(file.getParent(), name), type);
    }

    public FileHandle getParent () {
        File parent = new File(file.getParent());
        if (parent == null) {
            if (type == FileHandleType.Absolute)
                parent = new File("/");
            else
                parent = new File("");
        }
        return new FileHandle(fs, parent, type);
    }

    public void mkdirs () {
        if (type == FileHandleType.Classpath) throw new RuntimeException("Cannot mkdirs with a classpath file: " + file);
        if (type == FileHandleType.Internal) throw new RuntimeException("Cannot mkdirs with an internal file: " + file);
        getFile().mkdirs();
    }

    public boolean exists () {
        switch (type) {
        case Internal:
            if (getFile().exists()) return true;
        case Classpath:
            return FileHandle.class.getResource("/" + file.getPath().replace('\\', '/')) != null;
        }
        return getFile().exists();
    }

    public boolean delete() {
        if (type == FileHandleType.Classpath) throw new RuntimeException("Cannot delete a classpath file: " + file);
        if (type == FileHandleType.Internal) throw new RuntimeException("Cannot delete an internal file: " + file);
        
        if (isDirectory()) {
            return getFile().delete();
        }

        return deleteDirectory(getFile());
    }

    public long length () {
        if (type == FileHandleType.Classpath || (type == FileHandleType.Internal && !file.exists())) {
            InputStream input = readStream();
            try {
                return input.available();
            } catch (Exception ignored) {
            } finally {
                closeQuietly(input);
            }
            return 0;
        }
        return getFile().length();
    }

    public long lastModified () {
        return getFile().lastModified();
    }

    @Override
    public boolean equals (Object obj) {
        if (!(obj instanceof FileHandle)) return false;
        FileHandle other = (FileHandle)obj;
        return type == other.type && getPath().equals(other.getPath());
    }

    @Override
    public int hashCode () {
        int hash = 1;
        hash = hash * 37 + type.hashCode();
        hash = hash * 67 + getPath().hashCode();
        return hash;
    }

    public String toString () {
        return file.getPath().replace('\\', '/') + " (" + type + ")";
    }

    private int estimateLength () {
        int length = (int)length();
        return length != 0 ? length : 512;
    }

    private boolean deleteDirectory (File file) {
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0, n = files.length; i < n; i++) {
                    if (!files[i].isDirectory()) {
                        files[i].delete();
                    } else {
                        deleteDirectory(files[i]);
                    }
                }
            }
        }

        return file.delete();
    }

    private OutputStream createWriterStream(boolean append) {
        if (type == FileHandleType.Classpath) throw new RuntimeException("Cannot write to a classpath file: " + file);
        if (type == FileHandleType.Internal) throw new RuntimeException("Cannot write to an internal file: " + file);
        getParent().mkdirs();
        try {
            return new FileOutputStream(getFile(), append);
        } catch (Exception ex) {
            if (getFile().isDirectory())
                throw new RuntimeException("Cannot open a stream to a directory: " + file + " (" + type + ")", ex);
            throw new RuntimeException("Error writing file: " + file + " (" + type + ")", ex);
        }
    }

    private OutputStream createWriterStream(boolean append, int bufferSize) {
        return new BufferedOutputStream(createWriterStream(append), bufferSize);
    }

    private void closeQuietly (Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception ignored) {
            }
        }
    }
}
