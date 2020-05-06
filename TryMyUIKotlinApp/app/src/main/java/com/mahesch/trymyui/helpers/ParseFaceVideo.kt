package com.mahesch.trymyui.helpers

import android.util.Log
import com.coremedia.iso.boxes.Container
import com.googlecode.mp4parser.FileDataSourceImpl
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.Channels


class ParseFaceVideo {
    val TAG = ParseFaceVideo::class.java.simpleName.toUpperCase()
    val   FILE_BUFFER_SIZE = 1024

    /**
     * Appends mp4 audios/videos: `anotherFileName` to `mainFileName`.
     */
    fun append(mainFileName: String, anotherFileName: String?): Boolean {
        var rvalue = false
        try {
            val targetFile = File(mainFileName)
            val anotherFile = File(anotherFileName)
            if (targetFile.exists() && targetFile.length() > 0) {
                val tmpFileName = "$mainFileName.tmp"
                append(mainFileName, anotherFileName, tmpFileName)
                copyFile(tmpFileName, mainFileName)
                rvalue = anotherFile.delete() && File(tmpFileName).delete()
            } else if (targetFile.createNewFile()) {
                copyFile(anotherFileName, mainFileName)
                rvalue = anotherFile.delete()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Append two mp4 files exception", e)
        }
        return rvalue
    }

    @Throws(IOException::class)
    private fun copyFile(from: String?, destination: String) {
        val `in` = FileInputStream(from)
        val out = FileOutputStream(destination)
        copy(`in`, out)
        `in`.close()
        out.close()
    }

    @Throws(IOException::class)
    private fun copy(`in`: FileInputStream, out: FileOutputStream) {
        val buf = ByteArray(FILE_BUFFER_SIZE)
        var len: Int
        while (`in`.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
    }

    fun append(
        firstFile: String?,
        secondFile: String?,
        newFile: String?
    ) {
        try {
            Log.e(
                TAG,
                "new FileDataSourceImpl(firstfile) " + FileDataSourceImpl(firstFile)
            )
            Log.e(
                TAG,
                "new FileDataSourceImpl(secondFile) " + FileDataSourceImpl(secondFile)
            )
            val movieB: Movie = MovieCreator.build(FileDataSourceImpl(firstFile))
            val movieA: Movie = MovieCreator.build(FileDataSourceImpl(secondFile))
            val finalMovie = Movie()
            val movieOneTracks: List<Track> = movieA.getTracks()
            val movieTwoTracks: List<Track> = movieB.getTracks()
            var i = 0
            while (i < movieOneTracks.size || i < movieTwoTracks.size) {
                finalMovie.addTrack(AppendTrack(movieTwoTracks[i], movieOneTracks[i]))
                ++i
            }
            val container: Container = DefaultMp4Builder().build(finalMovie)
            val fos =
                FileOutputStream(File(String.format(newFile!!)))
            val bb =
                Channels.newChannel(fos)
            container.writeContainer(bb)
            fos.close()
        } catch (ioe: IOException) {
            Log.e(TAG, "append ioe $ioe")
        }
    }
}
