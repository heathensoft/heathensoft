package io.github.heathensoft.graphics.resources;

import io.github.heathensoft.common.FileUtils;
import io.github.heathensoft.graphics.Image;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Frederik Dahl
 * 23/06/2022
 */


public class ResourceLoader {
    
    public static final FileUtils.WriterUtility filerWriter = FileUtils.writer;
    public static final FileUtils.ReaderUtility fileReader = FileUtils.reader;
    public static final ResourceUtility resource = new ResourceUtility();
    
    public final static class ResourceUtility {
        
        private final Class<?> c = ResourceLoader.class;
        
        private ResourceUtility() {}
        
        
        public ByteBuffer toBuffer(String file, int byteSize) throws IOException {
            ByteBuffer result;
            try (InputStream is = stream(file)){
                if (is == null) throw new IOException("Unable to read: " + file);
                try (ReadableByteChannel bc = Channels.newChannel(is)){
                    result = BufferUtils.createByteBuffer(Math.max(128,byteSize));
                    while (true) {
                        int bytes = bc.read(result);
                        if (bytes == -1) break;
                        if (result.remaining() == 0) {
                            byteSize = result.capacity() * 2;
                            ByteBuffer b = BufferUtils.createByteBuffer(byteSize);
                            result = b.put(result.flip());
                        }
                    }
                }
            } // not sure if we need to slice. should at least check if its necessary
            return MemoryUtil.memSlice(result.flip());
        }
        
        // todo:
        /*
        public boolean copy(String file, Path to) throws IOException {
            try (InputStream is = stream(file)){
                if (is == null) throw new IOException("Unable to read: " + file);
                Files.copy(is,to);
            }
        }
        
         */
        
        public Image image(String file, int size, boolean flip) throws IOException {
            return new Image(toBuffer(file,size),flip);
        }
        
        public Image image(String file, boolean flip) throws IOException {
            int size = 1024 * 16; // 16kb default. It doesn't matter. Well it does, but it doesn't.
            return image(file,size,flip);
        }
        
        public Image image(String file,int size) throws IOException {
            return image(file,size,false);
        }
        
        public Image image(String file) throws IOException {
            return image(file,false);
        }
        
        public List<String> asLines(String file, Charset charset) throws IOException {
            List<String> result;
            try (InputStream is = stream(file)){
                if (is == null) throw new IOException("Unable to read: " + file);
                Stream<String> stream = new BufferedReader(new InputStreamReader(is,charset)).lines();
                result = stream.collect(Collectors.toList());
            }
            return result;
        }
        
        public List<String> asLines(String file) throws IOException {
            return asLines(file, StandardCharsets.UTF_8);
        }
        
        public String toString(String file, Charset charset) throws IOException {
            StringBuilder builder = new StringBuilder();
            try (InputStream is = stream(file)){
                if (is == null) throw new IOException("Unable to read: " + file);
                BufferedReader bf = new BufferedReader(new InputStreamReader(is,charset));
                String line;
                while ((line = bf.readLine()) != null) {
                    builder.append(line).append(System.lineSeparator());
                }
            }
            return builder.toString();
        }
        
        public String toString(String file) throws IOException {
            return toString(file,StandardCharsets.UTF_8);
        }
        
        private InputStream stream(String file) {
            return c.getClassLoader().getResourceAsStream(file);
        }
    }
}
