package io.github.heathensoft.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Frederik Dahl
 * 23/06/2022
 */


public class FileUtils {
    
    public static final WriterUtility writer = new WriterUtility();
    public static final ReaderUtility reader = new ReaderUtility();
    
    public static final class ReaderUtility {
        
        private ReaderUtility() {}
        
        @SuppressWarnings("all")
        public ByteBuffer readToBuffer(Path path) throws IOException {
            ByteBuffer buffer;
            try (SeekableByteChannel byteChannel = Files.newByteChannel(path, StandardOpenOption.READ)) {
                buffer = ByteBuffer.allocate((int)byteChannel.size() + 1);
                //buffer = BufferUtils.createByteBuffer((int)byteChannel.size() + 1);
                while (byteChannel.read(buffer) != -1); // *intentional*
            } return buffer.flip();
        }
        
        // todo: Image, Online resources
        
        public Stream<String> readLines(Path path, Charset charset) throws IOException {
            return Files.lines(path, charset);
        }
        
        public Stream<String> readLines(Path path) throws IOException {
            return Files.lines(path);
        }
        
        public List<String> readLinesToList(Path path, Charset charset) throws IOException {
            return readLines(path,charset).collect(Collectors.toList());
        }
        
        public List<String> readLinesToList(Path path) throws IOException {
            return readLines(path).collect(Collectors.toList());
        }
        
        public String toString(Path path, Charset charset) throws IOException {
            return Files.readString(path, charset);
        }
        
        public String toString(Path path) throws IOException {
            return Files.readString(path);
        }
    }
    
    public static final class WriterUtility {
        
        private WriterUtility() {}
        
        public void write(Path path, ByteBuffer source) throws IOException {
            try (SeekableByteChannel byteChannel = Files.newByteChannel(path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING)){
                byteChannel.write(source);
            }
        }
        
        public void write(Path path, byte[] bytes) throws IOException {
            Files.write(path,bytes);
        }
        
        public void write(Path path, String string, Charset charset) throws IOException {
            Files.writeString(path,string,charset);
        }
        
        public void write(Path path, String string) throws IOException {
            Files.writeString(path,string);
        }
        
        public void write(Path path, Iterable<? extends CharSequence> lines, Charset charset) throws IOException {
            Files.write(path,lines,charset);
        }
        
        public void write(Path path, Iterable<? extends CharSequence> lines) throws IOException {
            Files.write(path,lines);
        }
        
        public void append(Path path, String string, Charset charset) throws IOException {
            Files.writeString(path,string,charset,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND);
        }
        
        public void append(Path path, String string) throws IOException {
            Files.writeString(path,string,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND);
        }
        
        public void append(Path path, byte[] bytes) throws IOException {
            Files.write(path, bytes,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND);
        }
        
        public void append(Path path, Iterable<? extends CharSequence> lines, Charset charset) throws IOException {
            Files.write(path,lines,charset,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND);
        }
        
        public void append(Path path, Iterable<? extends CharSequence> lines) throws IOException {
            append(path,lines, StandardCharsets.UTF_8);
        }
        
        public void append(Path path, ByteBuffer source) throws IOException {
            try (SeekableByteChannel byteChannel = Files.newByteChannel(path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND)){
                byteChannel.write(source.flip());
            }
        }
    }
}
