package com.mahesch.trymyui.helpers

import com.googlecode.mp4parser.DataSource
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.WritableByteChannel

class FileDataSourceImpl : DataSource {
    var fc: FileChannel
    var filename: String

    constructor(f: File) {
        fc = FileInputStream(f).channel
        filename = f.name
    }

    constructor(f: String?) {
        val file = File(f)
        fc = FileInputStream(file).channel
        filename = file.name
    }

    constructor(fc: FileChannel) {
        this.fc = fc
        filename = "unknown"
    }

    constructor(fc: FileChannel, filename: String) {
        this.fc = fc
        this.filename = filename
    }

    @Synchronized
    @Throws(IOException::class)
    override fun read(byteBuffer: ByteBuffer?): Int {
        return fc.read(byteBuffer)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun size(): Long {
        return fc.size()
    }

    @Synchronized
    @Throws(IOException::class)
    override fun position(): Long {
        return fc.position()
    }

    @Synchronized
    @Throws(IOException::class)
    override fun position(nuPos: Long) {
        fc.position(nuPos)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun transferTo(
        startPosition: Long,
        count: Long,
        sink: WritableByteChannel?
    ): Long {
        return fc.transferTo(startPosition, count, sink)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun map(startPosition: Long, size: Long): ByteBuffer {
        //LOG.logDebug(startPosition + " " + size);
        return fc.map(FileChannel.MapMode.READ_ONLY, startPosition, size)
    }

    @Throws(IOException::class)
    override fun close() {
        fc.close()
    }

    override fun toString(): String {
        return filename
    }


}
