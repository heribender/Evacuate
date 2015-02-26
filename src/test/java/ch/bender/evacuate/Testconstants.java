/**
 * Copyright (c) 2015 by the original author or authors.
 *
 * This code is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */



package ch.bender.evacuate;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * TODO Hey Heri, comment this type please !
 *
 * @author Heri
 */
public final class Testconstants
{
    public static final String TEST_SANDBOX_DIR_STR = "testsandbox";
    public static Path ROOT_DIR = Paths.get( TEST_SANDBOX_DIR_STR );


    public static void deleteDirRecursive( Path aDir ) throws Exception
    {
        Files.walkFileTree( aDir, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile( Path file,
                                              BasicFileAttributes attrs )
                throws IOException
            {
                Files.delete( file );
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory( Path dir, IOException exc )
                throws IOException
            {
                Files.delete( dir );
                return FileVisitResult.CONTINUE;
            }

        } );
    }

    /**
     * createNewFile
     * <p>
     * @param aParent
     * @param aNewFileName
     * @return
     * @throws IOException
     */
    public static Path createNewFile( Path aParent, String aNewFileName )
        throws IOException
    {
        Path file1 = Paths.get( aParent.toString(), aNewFileName );
        Files.createFile( file1 );
        return file1;
    }

    /**
     * createNewFolder
     * <p>
     * @param aParent
     * @param aNewFolderStr
     * @return
     * @throws IOException
     */
    public static Path createNewFolder( Path aParent, String aNewFolderStr )
        throws IOException
    {
        Path orig = Paths.get( aParent.toString(), aNewFolderStr );
        Files.createDirectory( orig );
        return orig;
    }

    /**
     * Constructor
     *
     */
    private Testconstants()
    {
        super();
    }

}
