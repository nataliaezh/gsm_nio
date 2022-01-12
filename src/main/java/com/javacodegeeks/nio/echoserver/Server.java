package com.javacodegeeks.nio.echoserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class Server implements ChannelWriter {

    private static final int BUFFER_SIZE = 1024;

    private final int port;
    private final Map<SocketChannel, StringBuilder> session;

    public static void main(final String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Expecting one argument (1) port.");
        }

        new Server(Integer.valueOf(args[0])).start();
    }

    private Server(final int port) {
        this.port = port;
        this.session = new HashMap<>();
    }

    private void start() {
        try (Selector selector = Selector.open(); ServerSocketChannel channel = ServerSocketChannel.open()) {
            initChannel(channel, selector);

            while (!Thread.currentThread().isInterrupted()) {
                if (selector.isOpen()) {
                    final int numKeys = selector.select();
                    if (numKeys > 0) {
                        handleKeys(channel, selector.selectedKeys());
                    }
                } else {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to start server.", e);
        } finally {
            this.session.clear();
        }
    }

    private void initChannel(final ServerSocketChannel channel, final Selector selector) throws IOException {
        assert !Objects.isNull(channel) && !Objects.isNull(selector);

        channel.socket().setReuseAddress(true);
        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress(this.port));
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void handleKeys(final ServerSocketChannel channel, final Set<SelectionKey> keys) throws IOException {
        assert !Objects.isNull(keys) && !Objects.isNull(channel);

        final Iterator<SelectionKey> iterator = keys.iterator();
        while (iterator.hasNext()) {

            final SelectionKey key = iterator.next();
            try {
                if (key.isValid()) {
                    if (key.isAcceptable()) {
                        doAccept(channel, key);
                    } else if (key.isReadable()) {
                        doRead(key);
                    } else {
                        throw new UnsupportedOperationException("Key not supported by server.");
                    }
                } else {
                    throw new UnsupportedOperationException("Key not valid.");
                }
            } finally {
                if (mustEcho(key)) {
                    doEcho(key);
                    cleanUp(key);
                }

                iterator.remove();
            }
        }
    }

    private void doAccept(final ServerSocketChannel channel, final SelectionKey key) throws IOException {
        assert !Objects.isNull(key) && !Objects.isNull(channel);

        final SocketChannel client = channel.accept();
        client.configureBlocking(false);
        client.register(key.selector(), SelectionKey.OP_READ);

        // Create a session for the incoming connection
        this.session.put(client, new StringBuilder());
    }

    private void doRead(final SelectionKey key) throws IOException {
        assert !Objects.isNull(key);

        final SocketChannel client = (SocketChannel) key.channel();
        final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        final int bytesRead = client.read(buffer);
        if (bytesRead > 0) {
            this.session.get(client).append(new String(buffer.array()).trim());
        } else if (bytesRead < 0) {
            if (mustEcho(key)) {
                doEcho(key);
            }

            cleanUp(key);
        }
    }

    private void doEcho(final SelectionKey key) throws IOException {
        assert !Objects.isNull(key);

        final ByteBuffer buffer = ByteBuffer.wrap(this.session.get(key.channel()).toString().trim().getBytes());

        doWrite(buffer, (SocketChannel) key.channel());
    }

    private boolean mustEcho(final SelectionKey key) {
        assert !Objects.isNull(key);

        return (key.channel() instanceof SocketChannel) && this.session.get((SocketChannel) key.channel()).toString().contains(Constants.END_MESSAGE_MARKER);
    }

    private void cleanUp(final SelectionKey key) throws IOException {
        assert !Objects.isNull(key);

        this.session.remove((SocketChannel) key.channel());

        key.channel().close();
        key.cancel();
    }
}
