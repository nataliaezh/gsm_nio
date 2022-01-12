package com.javacodegeeks.nio.echoserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public final class Client implements ChannelWriter {

    private final InetSocketAddress hostAddress;

    public static void main(final String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Expecting two arguments in order (1) port (2) message eg: 9999 \"Hello world\".");
        }

       // new Client(Integer.valueOf(args[0])).start(args[1]);
        new Client(Integer.valueOf(args[0])).start(Constants.TEXT_FIRST_SEGMENT);
    }

    private Client(final int port) {
        this.hostAddress = new InetSocketAddress(port);
    }

    private void start(final String message) {
        assert StringUtils.isNotEmpty(message);
           try (SocketChannel client = SocketChannel.open(this.hostAddress)) {

            final ByteBuffer buffer = ByteBuffer.wrap((message + Constants.END_MESSAGE_MARKER).trim().getBytes());

            doWrite(buffer, client);

            buffer.flip();

            final StringBuilder echo = new StringBuilder();
            doRead(echo, buffer, client);


            System.out.println(String.format("Message :%s \nПолучены данные:\n %s", message, echo.toString().replace(Constants.END_MESSAGE_MARKER, StringUtils.EMPTY)));
        } catch (IOException e) {
            throw new RuntimeException("Unable to communicate with server.", e);
        }
    }

    private void doRead(final StringBuilder data, final ByteBuffer buffer, final SocketChannel channel) throws IOException {
        assert !Objects.isNull(data) && !Objects.isNull(buffer) && !Objects.isNull(channel);

        while (channel.read(buffer) != -1) {
            data.append(new String(buffer.array()).trim());
            buffer.clear();
        }
    }
}
