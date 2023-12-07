package com.n4no.webpencoder.webp.stream;

import com.n4no.webpencoder.webp.utils.Logs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Bartlomiej Tadych, b4rtaz
 */
public class MemorySeekableOutputStream implements SeekableOutputStream {

    private byte[] _buffer;
    private int _pos;

    public MemorySeekableOutputStream(ByteArrayOutputStream bos) {
        _buffer = bos.toByteArray();
    }

    @Override
    public void setPosition(int position) throws IOException {
        _pos = position;
    }

    @Override
    public void write(byte[] bytes, int length) throws IOException {
        int min = _pos + length;
        if (_buffer == null || _buffer.length < min) {
            byte[] b = new byte[min];
            if (_buffer != null)
                System.arraycopy(_buffer, 0, b, 0, _buffer.length);
            _buffer = b;
        }
        for (int i = 0; i < length; i++)
            _buffer[_pos++] = bytes[i];

        Logs.d(this,"Buffer size is: " + _buffer.length);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public byte[] getBytes() throws IOException{

        return _buffer;
    }
}
